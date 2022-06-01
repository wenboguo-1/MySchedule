package com.example.myschedule.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.databinding.ScheduleRowBinding
import com.example.myschedule.util.ItemTouchHelperAdapter
import com.example.myschedule.util.OnRowItemUpdateInterface
import kotlinx.coroutines.coroutineScope
import java.util.*

class ScheduleAdapter( scheduleItemList:MutableList<Schedule>, val mContext:Context, private val onItemClicked: OnRowItemUpdateInterface):RecyclerView.Adapter<ScheduleAdapter.ViewHolder>()
,ItemTouchHelperAdapter{
      private var lastPosition = -1
      private var scheduleList:MutableList<Schedule> = scheduleItemList
     inner class ViewHolder(val scheduleRowBinding: ScheduleRowBinding, private val onItemClicked: OnRowItemUpdateInterface) :
            RecyclerView.ViewHolder(scheduleRowBinding.root){
                 fun getIsAbsentCheckBox() = scheduleRowBinding.absentCheckBox
                 fun getIsPaidCheckBox() = scheduleRowBinding.paymentCheckBox
                 fun getItemMenu() = scheduleRowBinding.itemMenu
                 fun setOnIsPaidClicked() = onItemClicked.onIsPaidClick(layoutPosition)
                 fun setOnIsAbsentClicked() = onItemClicked.onIsAbsentClick(layoutPosition)

      }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ScheduleRowBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            ), onItemClicked!!
        )
    }
      @SuppressLint("DiscouragedPrivateApi")
      override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
          val schedule= scheduleList[position]
          this.setAnimation(viewHolder.itemView,position)
          schedule.apply {
               setItemView(viewHolder, name = this.studentName!!,isAbsent,isPaid,routine!!,startTime!!,endTime!!,note!!)
           }
         viewHolder.getIsPaidCheckBox().setOnClickListener{
             viewHolder.setOnIsPaidClicked()
         }
         viewHolder.getIsAbsentCheckBox().setOnClickListener{
             viewHolder.setOnIsAbsentClicked()
         }
         viewHolder.getItemMenu().setOnClickListener{
             PopupMenu(mContext,it).apply {
                 menuInflater.inflate(R.menu.option_menu,this.menu)
                 this.show()
                 val popupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                 popupMenu.isAccessible = true
                 val menu = popupMenu.get(this)
                 menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java).invoke(menu,true)
                 this.setOnMenuItemClickListener { menuItem ->
                     when(menuItem.itemId){
                         R.id.update -> onItemClicked.onUpdatedClick(viewHolder.layoutPosition)
                         R.id.delete -> onItemClicked.onDeleteClick(viewHolder.layoutPosition)
                         else -> onItemClicked.onMoveToClick(viewHolder.layoutPosition)
                     }
                     true
                 }
             }
         }


      }


      private fun setItemView(viewHolder:ViewHolder,name:String,isAbsent:Boolean,isPaid:Boolean,routine:String,startTime:String,endTime:String,note:String){

          viewHolder.scheduleRowBinding.userName.text = name
          viewHolder.scheduleRowBinding.absentCheckBox.isChecked = isAbsent
          viewHolder.scheduleRowBinding.paymentCheckBox.isChecked = isPaid
          viewHolder.scheduleRowBinding.routine.text = routine
          viewHolder.scheduleRowBinding.scheduleStartTime.text = startTime
          viewHolder.scheduleRowBinding.scheduleEndTime.text = endTime
          viewHolder.scheduleRowBinding.scheduleNote.text = note
      }

      fun updateSchedule(list:MutableList<Schedule>){
          this.scheduleList= list
          notifyDataSetChanged()
      }

      fun insertNewScheduleItem(){
          notifyItemInserted(this.scheduleList.size + 1)
      }

      fun deleteScheduleItem(position: Int){
          notifyItemRemoved(position)
      }

     fun updateScheduleItem(position:Int,schedule:Schedule){
         notifyItemChanged(position,schedule)
      }

      // Return the size of your dataset (invoked by the layout manager)
      override fun getItemCount() = scheduleList.size
      override fun onItemMoved(from: Int, to: Int ) {
           val tempSchedule = scheduleList[from]
           scheduleList.removeAt(from)
           scheduleList.add(to,tempSchedule)
           notifyItemMoved(from,to)

      }

      override fun onItemSwiped(position: Int) {
          scheduleList.removeAt(position)
          notifyItemRemoved(position)
      }


    fun onItemMove(fromPosition: Int?, toPosition: Int?): Boolean {
        fromPosition?.let {
            toPosition?.let {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(scheduleList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(scheduleList, i, i - 1)
                    }
                }
                Log.e("item ", "$fromPosition $toPosition")
                notifyItemMoved(fromPosition, toPosition)
                return true
            }
        }

        return false
    }
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(mContext, R.anim.fragment_animatioin_first_time_in)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }


}