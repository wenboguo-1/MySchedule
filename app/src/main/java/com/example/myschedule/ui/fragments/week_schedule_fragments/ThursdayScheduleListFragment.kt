package com.example.myschedule.ui.fragments.week_schedule_fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.FragmentThursdayScheduleListBinding
import com.example.myschedule.ui.adapters.ScheduleAdapter
import com.example.myschedule.ui.viewModule.MyScheduleViewModel
import com.example.myschedule.util.operation.Delete
import com.example.myschedule.util.operation.Insert
import com.example.myschedule.util.operation.Refresh
import com.example.myschedule.util.operation.Update
import kotlin.math.abs

private const val ARGUMENT = "LIST_THURSDAY"
class ThursdayScheduleListFragment : Fragment(){
    private val viewModel:MyScheduleViewModel by viewModels({ requireParentFragment() })
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding:FragmentThursdayScheduleListBinding
    private lateinit var adapter: ScheduleAdapter


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThursdayScheduleListBinding.inflate(inflater,container,false)
        recyclerView = binding.recyclerViewThurdday
        viewModel.operation = Refresh(0)
        recyclerView.adapter = ScheduleAdapter(mutableListOf(),requireContext(),viewModel.onRowItemUpdateInterface)
        adapter = recyclerView.adapter as ScheduleAdapter
        this.setGesture()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.e("sda","gaga")
        viewModel.onUpdateScheduleListThursday.observe(viewLifecycleOwner){
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
         fun newInstant() = ThursdayScheduleListFragment()

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setGesture(){
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
                    val SWIPE_MAX_OFF_PATH = 200
                    val SWIPE_THRESHOLD_VELOCITY = 250
                    if( e1!= null && e2 != null) {
                        try {
                            if (abs(e1?.y - e2?.y) > SWIPE_MAX_OFF_PATH) return false
                            if (e1?.x - e2?.x > SWIPE_MIN_DISTANCE
                                && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {
                                viewModel.onFridayClicked()

                            } else if (e2?.x - e1?.x > SWIPE_MIN_DISTANCE
                                && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {
                                viewModel.onWednesdayClicked()
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
        recyclerView.setOnTouchListener { v, event -> gesture?.onTouchEvent(event) }
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
                val list = viewModel.onUpdateScheduleListThursday?.value!!
                viewModel.setObserverUpdateListPositionForWeek(list)

            }


        })
        touchHelper.attachToRecyclerView(recyclerView)
    }





}