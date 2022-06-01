package com.example.myschedule.ui.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.data.module.CalendarScheduleInfo
import com.example.myschedule.ui.activities.CalendarViewActivity
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(calendarScheduleInfoList:MutableList<CalendarScheduleInfo>, onItemClickListener: CalendarViewActivity,onProgressDialogDone: OnProgressDialogDone,
deviceHeight:Int,deviceWidth:Int,context: Context):RecyclerView.Adapter<CalendarAdapter.CalenderViewHolder>(){
    private val calendarScheduleInfoList = calendarScheduleInfoList
    private val itemListener = onItemClickListener
    private val progressDialogListener = onProgressDialogDone
    private val deviceHeight = deviceHeight
    private val deviceWidth = deviceWidth

    private lateinit var calendarViewHolder:CalenderViewHolder
    private val localDate: LocalDate by lazy { LocalDate.now() }
    class CalenderViewHolder(view: View, listener:OnItemListener,deviceHeight: Int,deviceWidth: Int): RecyclerView.ViewHolder(view),View.OnClickListener{
         private val view = view
         private val onItemClicked = listener
         private val deviceHeight = deviceHeight
         private val deviceWidth = deviceWidth
         val dayOfMonth: TextView = view.findViewById(R.id.daysInMonth)
         private val name1:TextView = view.findViewById(R.id.studentName1)
         private val name2:TextView = view.findViewById(R.id.studentName2)
         private val name3:TextView = view.findViewById(R.id.studentName3)
         private val name4:TextView = view.findViewById(R.id.studentName4)
         private val scheduleNothing:TextView = view.findViewById(R.id.scheduleNothing)
         fun setOnClickListed(){
             this.view.setOnClickListener(this)
         }
        override fun onClick(v: View?) {
            onItemClicked.onItemClicked(position = layoutPosition, dayOfMonth = dayOfMonth.text.toString())
        }
        fun getListOfTextViews():MutableList<TextView>{
            return mutableListOf(dayOfMonth,name1,name2,name3,name4,scheduleNothing)
        }
        fun setTextViewSize(){

            this.name1.textSize = (deviceHeight/170).toFloat()
            this.name2.textSize = (deviceHeight/170).toFloat()
            this.name3.textSize = (deviceHeight/170).toFloat()
            this.name4.textSize = (deviceHeight/170).toFloat()
            this.scheduleNothing.textSize = (deviceHeight/160).toFloat()
        }

    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {

          val inflater = LayoutInflater.from(parent.context)
          val view = inflater.inflate(R.layout.calendar_cell,parent,false)
          val layoutParams = view.layoutParams
          layoutParams.height =   (parent.height * 0.166666).toInt()
          this.calendarViewHolder = CalenderViewHolder(view,itemListener,this.deviceHeight,this.deviceWidth)
          this.calendarViewHolder.setOnClickListed()
          this.calendarViewHolder.setTextViewSize()
          return this.calendarViewHolder
     }

     @RequiresApi(Build.VERSION_CODES.O)
     override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
           val item = calendarScheduleInfoList[position]
           if(item.dayOfMonth.isNotEmpty()){
               if(item.dayOfMonth == localDate.dayOfMonth.toString()){
                   holder.dayOfMonth.setTextColor(Color.parseColor("#B50101"))
               }
               holder.dayOfMonth.text = item.dayOfMonth
               val scheduleList = item.scheduleList

               for( i in 0 until scheduleList?.size!!){
                   when{
                       i == 4 -> holder.getListOfTextViews()[5].text = "``````"
                       i < 4 -> holder.getListOfTextViews()[i+1].text = scheduleList[i].studentName
                       else ->
                           break
                   }
               }
           }
         if(position == 41){
              this.progressDialogListener.dismissDialog()
         }

     }
     override fun getItemCount(): Int {
          return this.calendarScheduleInfoList.size
     }

    interface OnItemListener{
        fun onItemClicked(position:Int,dayOfMonth:String)
    }
    interface OnProgressDialogDone{
        fun dismissDialog()
    }


}