package com.example.myschedule.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.data.module.Schedule
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SearchResultAdapter(list:MutableList<Schedule>):RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>()  {
     private var searchResultList = list
     inner class SearchResultViewHolder(view: View): RecyclerView.ViewHolder(view){
          private val view = view
          val attendanceStatus: TextView = view.findViewById(R.id.searchAttendanceStatus)
          val paymentStatus: TextView = view.findViewById(R.id.searchPaymentStatus)
          val studentName: TextView = view.findViewById(R.id.searchStudentName)
          val date: TextView = view.findViewById(R.id.searchDate)
          val time: TextView = view.findViewById(R.id.searchTime)
     }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
          val inflater = LayoutInflater.from(parent.context)
          val view = inflater.inflate(R.layout.search_history_item,parent,false)
          return SearchResultViewHolder(view)
     }

     override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
          val item = searchResultList[position]

          holder.studentName.text = item.studentName
          holder.paymentStatus.text =  if(item.isPaid) "Payment status: Paid " else  "Payment status: Unpaid"
          holder.attendanceStatus.text = if(item.isAbsent) "Attendance status: Absent" else "Attendance status: Attended"
          holder.time.text = item.startTime + "-" + item.endTime
          val calendar = Calendar.getInstance()
          calendar.timeInMillis = item.timeStampDaily
          holder.date.text = SimpleDateFormat("MM/dd/yyyy").format(calendar.time)


     }

     override fun getItemCount(): Int {
        return   this.searchResultList.size
     }

     fun setList(list:MutableList<Schedule>){
          searchResultList = list

          notifyDataSetChanged()
     }


}