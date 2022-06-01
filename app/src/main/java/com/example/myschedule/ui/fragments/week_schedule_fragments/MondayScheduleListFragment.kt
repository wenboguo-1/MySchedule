package com.example.myschedule.ui.fragments.week_schedule_fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated
import com.example.myschedule.databinding.FragmentMondayListBinding
import com.example.myschedule.ui.adapters.ScheduleAdapter
import com.example.myschedule.ui.viewModule.MyScheduleViewModel
import com.example.myschedule.util.operation.Delete
import com.example.myschedule.util.operation.Insert
import com.example.myschedule.util.operation.Refresh
import com.example.myschedule.util.operation.Update


private const val ARGUMENT = "LIST_MONDAY"

class MondayScheduleListFragment : Fragment(){
    private lateinit var binding: FragmentMondayListBinding
    private lateinit var recyclerView: RecyclerView
    private val viewModel: MyScheduleViewModel by viewModels({ requireParentFragment() })
    private lateinit var listOfMondaySchedule:MutableList<ScheduleWeekly>
    private lateinit var adapter: ScheduleAdapter
    private var x1 = 0f
    private var x2 = 0f
    val MIN_DISTANCE = 150

    companion object {
        @JvmStatic
        fun newInstance() = MondayScheduleListFragment()

     }
    override fun onStop() {
        super.onStop()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            listOfMondaySchedule =  it.getParcelableArrayList<ScheduleWeekly>(ARGUMENT) as MutableList<ScheduleWeekly>
        }
        binding = FragmentMondayListBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewMonday
        viewModel.operation = Refresh(0)
        adapter = ScheduleAdapter(mutableListOf(), requireContext(),viewModel.onRowItemUpdateInterface)
        recyclerView.adapter = adapter

        this.setGesture()
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.onUpdateScheduleListMonday.observe(viewLifecycleOwner) {
             val position = viewModel.operation!!.index
             when(viewModel.operation){
                 is Refresh -> adapter.updateSchedule(it)
                 is Delete -> adapter.deleteScheduleItem(position)
                 is Update -> adapter.updateScheduleItem(position,it[position])
                 is Insert -> adapter.insertNewScheduleItem()
             }

        }

    }


    private fun setGesture(){

        val gesture = GestureDetector(
            activity,
            object : SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean {
                    return false
                }

                override fun onFling(
                    e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if(e1!= null && e2 != null) {
                        val SWIPE_MIN_DISTANCE = 80
                        val SWIPE_MAX_OFF_PATH = 150
                        val SWIPE_THRESHOLD_VELOCITY = 250
                        try {
                            if (kotlin.math.abs(e1?.y - e2?.y) > SWIPE_MAX_OFF_PATH) return false
                            if (e1?.x - e2?.x > SWIPE_MIN_DISTANCE
                                && kotlin.math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {
                                viewModel.onTuesdayClicked()

                            } else if (e2?.x - e1?.x > SWIPE_MIN_DISTANCE
                                && kotlin.math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                            ) {
                                viewModel.onSundayClicked()
                            }
                        } catch (e: Exception) {
                            // nothing
                        }
                    }else{
                        return true
                    }
                    return false
                }
            })
        recyclerView.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP + DOWN,0 ){
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
                val list = viewModel.onUpdateScheduleListMonday?.value!!
                viewModel.setObserverUpdateListPositionForWeek(list)

            }

        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

}

