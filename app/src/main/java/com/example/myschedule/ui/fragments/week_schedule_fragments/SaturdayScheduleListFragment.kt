package com.example.myschedule.ui.fragments.week_schedule_fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated
import com.example.myschedule.databinding.FragmentSaturdayScheduleListBinding
import com.example.myschedule.ui.adapters.ScheduleAdapter
import com.example.myschedule.ui.viewModule.MyScheduleViewModel
import com.example.myschedule.util.operation.Delete
import com.example.myschedule.util.operation.Insert
import com.example.myschedule.util.operation.Refresh
import com.example.myschedule.util.operation.Update
import kotlin.math.abs

private const val ARGUMENT = "LIST_SATURDAY"
class SaturdayScheduleListFragment : Fragment(){
    private lateinit var binding:FragmentSaturdayScheduleListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:ScheduleAdapter
    private val viewModel:MyScheduleViewModel by viewModels({ requireParentFragment() })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaturdayScheduleListBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerViewSaturday
        viewModel.operation = Refresh(0)
        recyclerView.adapter = ScheduleAdapter(mutableListOf(), requireContext(),viewModel.onRowItemUpdateInterface)
        this.adapter = recyclerView.adapter as ScheduleAdapter
        val gesture = GestureDetector(
            activity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return false
                }
                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val SWIPE_MIN_DISTANCE = 80
                    val SWIPE_MAX_OFF_PATH = 150
                    val SWIPE_THRESHOLD_VELOCITY = 250
                    if(e1!= null && e2 != null) {
                        try {
                            if (abs(e1?.y - e2?.y) > SWIPE_MAX_OFF_PATH) return false
                            if (e1?.x - e2?.x > SWIPE_MIN_DISTANCE
                                && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {

                            } else if (e2?.x - e1?.x > SWIPE_MIN_DISTANCE
                                && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {
                                viewModel.onFridayClicked()
                            }
                        } catch (e: Exception) {
                            print(e.message)
                        }
                    }else{
                       return true
                    }
                    return false
                }
            })
        recyclerView.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP + ItemTouchHelper.DOWN,0 ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.onItemMove(viewHolder?.adapterPosition,target?.adapterPosition)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                val list = viewModel.onUpdateScheduleListSaturday?.value!!
                viewModel.setObserverUpdateListPositionForWeek(list)

            }

        })
        touchHelper.attachToRecyclerView(recyclerView)
        viewModel.onUpdateScheduleListSaturday.observe(viewLifecycleOwner){
            val position = viewModel.operation!!.index
            when(viewModel.operation){
                is Refresh -> adapter.updateSchedule(it)
                is Delete -> adapter.deleteScheduleItem(position)
                is Update -> adapter.updateScheduleItem(position,it[position])
                is Insert -> adapter.insertNewScheduleItem()
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = SaturdayScheduleListFragment()
    }



}