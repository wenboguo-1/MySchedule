package com.example.myschedule.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myschedule.R
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated
import com.example.myschedule.data.user.UserInfo
import com.example.myschedule.databinding.CustomDialogBinding
import com.example.myschedule.databinding.ListViewBinding
import com.example.myschedule.databinding.MyScheduleFragmentBinding
import com.example.myschedule.databinding.NewShceduleBottomSheetBinding
import com.example.myschedule.date.*
import com.example.myschedule.date.Date
import com.example.myschedule.ui.activities.UserPageActivity
import com.example.myschedule.ui.viewModule.MyScheduleViewModel
import com.example.myschedule.util.operation.NoAction
import com.example.myschedule.util.operation.Update
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.sql.Timestamp
import java.text.Format
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class MyScheduleFragment : Fragment(), View.OnTouchListener {

    private lateinit var binding : MyScheduleFragmentBinding
    private lateinit var dialogBinding:NewShceduleBottomSheetBinding
    private lateinit var daysOfWeek: List<DayOfWeek>
    private lateinit var dayMap: Map<MyScheduleViewModel.Week,TextView>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var weekList:ArrayList<String>
    private lateinit var listView: ListView
    private lateinit var bindingCustom:CustomDialogBinding
    private  var move_to_position: Int = 0
    private lateinit var layout:View
    private lateinit var dialog: Dialog
    companion object {
        fun newInstance() = MyScheduleFragment()
    }
    private val viewModel: MyScheduleViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = layoutInflater.inflate(R.layout.custom_dialog,null)
        listView = layout.findViewById(R.id.List)
        dialog = context?.let { Dialog(it) }!!
        dialog?.setContentView(layout)
        dialog?.setTitle("Heart attack and shock")
        dialog?.setCancelable(true)

        viewModel.observerUpdateListPositionForWeek.observe(requireActivity()){ list ->
            viewModel.updateSchedulePositionOfOneDay(list)

        }
        viewModel.onIsPaidClicked.observe(requireActivity()
        ) {  position ->
            viewModel.operation = Update(position)
            val list = viewModel.getCurrentDayOfOnUpdateListSchedule().value
            val isPaid = list?.get(position)?.isPaid!!
            context?.let {
                val message by lazy {
                     if(isPaid){
                         "Are you sure you want to mark the schedule as an unpaid?"
                     }else{
                         "Are you sure you want to mark the schedule as a paid?"
                     }
                }
                AlertDialog.Builder(it).apply {
                     setMessage(message).setPositiveButton("Yes"){
                         _,_ ->
                          list?.get(position)?.isPaid = !isPaid!!
                          val schedule =  list?.get(position)
                          viewModel.getCurrentDayOfOnUpdateListSchedule().value = list
                         if (schedule != null) {
                             updateSchedule(schedule)
                         }

                     }.setNegativeButton("No"){
                         _,_ ->
                         list?.get(position)?.isPaid = isPaid!!
                         viewModel.getCurrentDayOfOnUpdateListSchedule().value = list
                     }
                    show()
                }
              }
        }

        viewModel.onUpdatedClicked.observe(requireActivity()){

            val schedule = viewModel.getCurrentDayOfOnUpdateListSchedule().value?.get(it)
            viewModel.setUpdatedScheduleName(scheduleName = schedule?.studentName!!)
            viewModel.startTime.value = schedule?.startTime
            viewModel.endTime.value = schedule?.endTime
            viewModel.addOrUpdatedText.value = "Updated"
            dialogBinding.isEveryWeek.isChecked = schedule.routine == "Weekly"
            bottomSheetDialog.show()

        }

        viewModel.onMoveToClick.observe(requireActivity()){ position ->

            this.move_to_position = position
            weekList = viewModel.getWeekList()
            val arrayAdapter =
                context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1,weekList) }
            listView.adapter = arrayAdapter
            dialog?.show()

        }

        listView?.setOnItemClickListener { _, _, position, _ ->
            dialog.dismiss()
            context?.let { context ->
                val message = "Are you sure you want change to ${weekList[position]}?"
                AlertDialog.Builder(context).apply {
                    setMessage(message).setPositiveButton("Yes"){
                            _ , _ ->
                        val to = viewModel.convertDayToNumber(weekList[position])
                        val temp = viewModel.getDayAdded()
                        val cur = if (temp == 0) 7 else  temp
                        Log.e("to cur ",weekList[position])
                        val difference = (cur - to) * 24 * 60 * 60
                        viewModel.moveTo(difference,move_to_position)


                    }.setNegativeButton("No"){
                            _,_ ->


                    }

                    show()
                }
            }
        }
        viewModel.onAbsentClicked.observe(requireActivity()){  position ->

              viewModel.operation = Update(position)
              val list = viewModel.getCurrentDayOfOnUpdateListSchedule().value
              val isAbsent = list?.get(position)?.isAbsent
              context?.let { context ->
                  val message = "Are you sure you want to make this change?"
                   AlertDialog.Builder(context).apply {
                        setMessage(message).setPositiveButton("Yes"){
                            _ , _ ->
                            list?.get(position)?.isAbsent = !isAbsent!!
                            val schedule =  list?.get(position)
                            viewModel.getCurrentDayOfOnUpdateListSchedule().value = list
                            if (schedule != null) {
                                updateSchedule(schedule)
                            }


                        }.setNegativeButton("No"){
                                _,_ ->

                            list?.get(position)?.isAbsent = isAbsent!!
                            viewModel.getCurrentDayOfOnUpdateListSchedule().value = list
                        }

                       show()
                   }



              }

        }

        viewModel.onDeleteClick.observe(requireActivity()){
            this.setOnDeleteScheduleAlert(it)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.actionBar?.hide()
        val bottomSheet = layoutInflater.inflate(R.layout.new_shcedule_bottom_sheet, null)
        dialogBinding = NewShceduleBottomSheetBinding.inflate(layoutInflater,
            bottomSheet as ViewGroup?,false)
        bindingCustom =  CustomDialogBinding.inflate(inflater)
        bottomSheetDialog = context?.let { it1 -> BottomSheetDialog(it1,R.style.BottomSheetDialogThem).apply {
            setOnDismissListener{
                viewModel.newScheduleName.value = ""
                viewModel.scheduleNote.value = ""
            }
        } }!!
        bottomSheetDialog.setContentView(dialogBinding.bottomSheetContainer)

        binding =  DataBindingUtil.inflate(inflater,R.layout.my_schedule_fragment,container,false)
        binding.lifecycleOwner = this
        dialogBinding.lifecycleOwner = this
        daysOfWeek = Date.daysOfMonth
        setMonthOfYear(Date.currentMonth)
        setDaysOfWeekTextView()
        initMap()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.myScheduleViewModel = viewModel
        dialogBinding.viewModel = viewModel

        viewModel.initCurrentDayOfWeek()
        viewModel.initScheduleView()
        savedInstanceState ?: run{
            childFragmentManager.beginTransaction().replace(R.id.schedule_list_fragment,viewModel.mapDaySelectedWithFragments[viewModel.currentDayOfWeek]!!).commit()

        }
        viewModel.getIsOnMonthOfYearClicked.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().finish()
                activity?.overridePendingTransition(
                    R.anim.activity1_slide_in,
                    R.anim.activity1_slide_out
                )
            }
        }
        viewModel._newScheduleName.observe(viewLifecycleOwner
        ) { t ->
            dialogBinding.addButt.setTextColor(Color.parseColor(if (t?.isEmpty()!!) "#CCCCCC" else "#B50101"))
        }
        viewModel.getDaySelected.observe(viewLifecycleOwner) { value ->
            lifecycleScope.launch(Dispatchers.Main){
            viewModel.getPreDayOfWeek()?.let {
                dayMap[it]?.background = null
                val tempMap = viewModel.mapDaySelectedWithFragments
                when (animationFragmentToRightOrLeft(it, value)) {
                    0 -> {
                        replaceFragment(
                            tempMap[value]!!,
                            R.anim.activity1_slide_in,
                            R.anim.activity1_slide_out
                        )
                    }
                    1 -> {
                        replaceFragment(
                            tempMap[value]!!,
                            R.anim.activity_slide_in,
                            R.anim.activity_slide_out
                        )
                    }
                }

            }
                viewModel.currentDayOfWeek = value
                viewModel.setPreDayOfWeek(value)
                dayMap[value]?.background =
                    resources.getDrawable(R.drawable.circle_day_month_background)
            }


        }
        binding.addNewSchedule.setOnClickListener{

             this.addNewScheduleButtonSheet()
        }

        dialogBinding.startTime.setOnClickListener{
            this.showTimePicker("Select a start time",0)
        }
        dialogBinding.endTime.setOnClickListener{
            this.showTimePicker("Select a end time",1)
        }


        dialogBinding.cancel.setOnClickListener{
            viewModel.newScheduleName.value = ""
            viewModel.scheduleNote.value = ""
            bottomSheetDialog.dismiss()
        }

        dialogBinding.addButt.setOnClickListener{
            val position = viewModel.onUpdatedClicked?.value
             if(dialogBinding.addButt.currentTextColor == Color.parseColor("#B50101")) {
                 this.setNewSchedule(position)
                 bottomSheetDialog.dismiss()
             }
        }

    }

    private fun updateSchedule(schedule:Schedule){

        when(schedule){
            is ScheduleWeeklyUpdated -> {
                viewModel.updateSchedleWeeklyUpdate(schedule)
            }
            is ScheduleWeekly -> {
                val s = viewModel.getNewUpdatedScheduleWeekly(schedule)
                viewModel.addNewScheduleUpdate(s)
            }
            is ScheduleDaily-> {
                viewModel.updateScheduleDaily(schedule)
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addNewScheduleButtonSheet(){
        this.initialValueOfBottomSheet()
        viewModel.addOrUpdatedText.value = "Add"
        bottomSheetDialog.dismissWithAnimation
        bottomSheetDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initialValueOfBottomSheet(){
        val hour = Calendar.getInstance( Locale.getDefault()).time.hours
        val min = Calendar.getInstance( Locale.getDefault()).time.minutes
        viewModel.position =  hour * 24 + min

        viewModel.startTime.value = this.getTimeFormat(hour,min)
        viewModel.endTime.value = this.getTimeFormat(hour,min)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNewSchedule(position:Int?){
        val calendar = viewModel.getCalendar()
        var schedule:Schedule
        calendar.add(Calendar.DAY_OF_YEAR,viewModel.getDayAdded())
        val format1 = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        val name = viewModel.newScheduleName.value

        val note = viewModel.scheduleNote.value
        val startTime = viewModel.startTime.value
        val endTime = viewModel.endTime.value
        val year = SimpleDateFormat("yyyy").format(calendar.time)
        val month = (calendar.time.month + 1).toString()
        val dayOfMonth = calendar.time.date.toString()
        val isDayLightTime =  TimeZone.getTimeZone( ZoneId.systemDefault().id).inDaylightTime(calendar.time)
        val timeStamp = viewModel.getTimeStamp(isDayLightTime, timestamp = Timestamp.valueOf("$format1 00:00:00"))
        val timeStampDaily = if(isDayLightTime) Timestamp.valueOf("$format1 00:00:00").time + 3600000 else Timestamp.valueOf("$format1 00:00:00").time
        val isWeekly = dialogBinding.isEveryWeek.isChecked
        val addOrUpdated = viewModel.addOrUpdatedText.value
        var isPaid = false
        var isAbsent = false
        if(addOrUpdated.equals("Add")){

            if(isWeekly){

                schedule = ScheduleWeekly(0, "Bob",name,"Weekly",year,month,dayOfMonth, startTime,endTime,"",isPaid,isAbsent,timeStamp,timeStampDaily,viewModel.position)
                viewModel.addNewScheduleWeekly(schedule)
            }else{
                schedule = ScheduleDaily(0,"Bob",name,"Daily",year,month,dayOfMonth,startTime,endTime,"",isPaid,isAbsent,timeStamp,timeStampDaily,viewModel.position)
                viewModel.addNewScheduleDaily(schedule)

            }
            viewModel.position = -1
        }else{
            val currentScheduleSelected = viewModel.getCurrentDayOfOnUpdateListSchedule()?.value?.get(position!!)
            isPaid = position?.let { viewModel.getCurrentDayOfOnUpdateListSchedule().value?.get(it)?.isPaid }!!
            isAbsent  = viewModel.getCurrentDayOfOnUpdateListSchedule().value?.get(position)?.isAbsent !!
            viewModel.position = if(viewModel.position < 0 ) currentScheduleSelected?.position!! else viewModel.position

            when(currentScheduleSelected){
                is ScheduleWeekly -> {
                    if(isWeekly){
                        val preWeeklyTimestamp = currentScheduleSelected?.timeStampWeekly
                        val newScheduleWeekly = preWeeklyTimestamp?.let {
                            ScheduleWeekly(currentScheduleSelected.id,
                                UserInfo.userName,name,"Weekly",year,month,dayOfMonth,startTime,endTime,note,isPaid!!,isAbsent!!,
                                it,timeStampDaily, viewModel.position )
                        }
                        if (newScheduleWeekly != null) {
                            this.setOnUpdateClickDialog(position,newScheduleWeekly)
                        }
                    }else{
                        schedule = ScheduleDaily(0,UserInfo.userName,name,"Daily",year,month,dayOfMonth,startTime,endTime,note,isPaid,isAbsent,timeStampDaily,timeStampDaily, viewModel.position )
                        this.setOnChangeWeeklyToDailyDialog(position,schedule)
                    }
                }
                is ScheduleWeeklyUpdated -> {
                     if(isWeekly){

                         val scheduleWeeklyUpdated = viewModel.getCurrentDayOfOnUpdateListSchedule()?.value?.get(position) as ScheduleWeeklyUpdated

                         schedule = ScheduleWeeklyUpdated(
                             position?.let { viewModel.getCurrentDayOfOnUpdateListSchedule()?.value?.get(it)?.id }!!,
                             UserInfo.userName,name,"Weekly",year,month,dayOfMonth,startTime,endTime,note,isPaid!!,isAbsent!!,currentScheduleSelected.timeStampWeekly,timeStampDaily,position).apply {
                                 scheduleUpdatedId = scheduleWeeklyUpdated.scheduleUpdatedId
                           }
                         this.setOnUpdateClickDialog(position,schedule)
                     }else{
                         schedule = ScheduleDaily(0,UserInfo.userName,name,"Daily",year,month,dayOfMonth,startTime,endTime,note,isPaid,isAbsent,timeStamp,timeStampDaily,position)
                         this.setOnChangeWeeklyToDailyDialog(position,schedule)
                     }
                }
                is ScheduleDaily -> {
                    if(isWeekly){
                        schedule = ScheduleWeekly(0, UserInfo.userName,name,"Weekly",year,month,dayOfMonth, startTime,endTime,note,isPaid,isAbsent,timeStamp,timeStampDaily,position)
                        viewModel.deleteScheduleDaily(schedule,position)
                    }else{
                        schedule = ScheduleDaily(currentScheduleSelected.id,UserInfo.userName,name,"Daily",year,month,dayOfMonth,startTime,endTime,note,isPaid,isAbsent,timeStamp,timeStampDaily,currentScheduleSelected.position)
                        viewModel.updateScheduleDaily(schedule = schedule,position)
                    }
                }
            }
        }
    }
    private fun setOnDeleteScheduleAlert(position:Int){
        val schedule = viewModel.getCurrentDayOfOnUpdateListSchedule().value?.get(position!!)
        val message = "WARNING: If you delete the schedule you selected, all schedules " +
                "associated each week will be deleted as well, or you can mark it as " +
                "Absent. Delete it anyway?"
        context?.let {
            AlertDialog.Builder(it).apply {
                when(schedule){
                    is ScheduleWeekly  -> {
                         setMessage(message).setPositiveButton("Yes"){
                                  _,_ ->
                                    viewModel.deleteScheduleWeekly(schedule,position,schedule.id)
                                 }.setNegativeButton("No"){
                                     _,_ ->
                                 }
                    }
                    is ScheduleWeeklyUpdated -> {

                        setMessage(message).setPositiveButton("Yes"){
                            _,_->
                             val id = schedule.id
                             viewModel.deleteScheduleWeeklyUpdated(id,position,false)
                             viewModel.deleteScheduleWeeklyById(id,position)

                        }.setNegativeButton("No"){
                            _,_ ->
                        }
                    }
                    is ScheduleDaily -> {
                        setMessage("Are your sure you want to delete this schedule? ").setPositiveButton("Yes"){
                            _,_ ->
                             viewModel.deleteScheduleDailyWithoutAddingNewWeeklySchedule(schedule,position)
                        }.setNegativeButton("No"){
                            _,_ ->

                        }
                    }
                }
               show()
            }
        }
    }




    private fun setOnUpdateClickDialog(position: Int,schedule: Schedule){

        val message = "Do you want to update the schedule for "

        val scheduleWeekly = ScheduleWeekly(schedule.id,schedule.userName,schedule.studentName,schedule.routine,schedule.year,
            schedule.month,schedule.dayOfMonth,schedule.startTime,schedule.endTime,schedule.note,schedule.isPaid,schedule.isAbsent,schedule.timeStampWeekly,schedule.timeStampDaily,position)
        val scheduleWeeklyUpdated = ScheduleWeeklyUpdated(schedule.id,schedule.userName,schedule.studentName,schedule.routine,schedule.year,
            schedule.month,schedule.dayOfMonth,schedule.startTime,schedule.endTime,schedule.note,schedule.isPaid,schedule.isAbsent,schedule.timeStampWeekly,schedule.timeStampDaily,position)

        context?.let {
            AlertDialog.Builder(it).apply {
                setMessage(message)
                setPositiveButton("Only Today") { _, _ ->
                    when(schedule){
                        is ScheduleWeeklyUpdated -> {

                            viewModel.updatedScheduleWeeklyUpdated(schedule, position)
                        }
                        is ScheduleWeekly -> {
                            viewModel.addNewScheduleWeeklyUpdate(scheduleWeeklyUpdated,position)
                        }
                    }

                }
                setNegativeButton("every week") { _, _ ->
                    when(schedule){
                        is ScheduleWeekly -> {
                            val timestampWeek = viewModel.getCurrentDayOfOnUpdateListSchedule()?.value?.get(position)?.timeStampWeekly!!
                            val timestampWeekDay = viewModel.getCurrentDayOfOnUpdateListSchedule()?.value?.get(position)?.timeStampDaily!!
                            val newScheduleWeekly = ScheduleWeekly(schedule.id,schedule.userName,schedule.studentName,schedule.routine,schedule.year,
                                schedule.month,schedule.dayOfMonth,schedule.startTime,schedule.endTime,schedule.note,schedule.isPaid,schedule.isAbsent,timestampWeek,timestampWeekDay,position)
                            viewModel.updateScheduleWeekly(newScheduleWeekly,position)
                        }
                        is ScheduleWeeklyUpdated -> {
                            viewModel.deleteScheduleWeeklyUpdated(schedule.id,position,false)
                            viewModel.updateScheduleWeekly(scheduleWeekly,position)
                        }

                    }

                }
                show()
            }
        }
    }

    private fun setOnChangeWeeklyToDailyDialog(position:Int,schedule:ScheduleDaily){
        val message = "Are you sure you want to change weekly to daily? "
        context?.let {
            AlertDialog.Builder(it).apply {
                setMessage(message).setPositiveButton("Yes"){
                    _,_ ->
                    viewModel.changeScheduleWeeklyToDaily(position,schedule!!)
                }.setNegativeButton("No"){
                    _,_ ->

                }
                show()
            }
        }

    }
    private fun showTimePicker(title:String,startOrEnd:Int){
        val timePicker = MaterialTimePicker.Builder().setTimeFormat(CLOCK_12H).setHour(12).setMinute(0).setTitleText(title).build()
        timePicker.show(childFragmentManager,"TAG")
        timePicker.addOnPositiveButtonClickListener{
            val hour = timePicker.hour
            val min = timePicker.minute
            viewModel.position = hour * 60 + min
            when(startOrEnd){
                0 -> viewModel.startTime.value = this.getTimeFormat(hour,min)
                else -> viewModel.endTime.value = this.getTimeFormat(hour,min)
            }
        }

    }
    private fun getTimeFormat(hour:Int, min:Int):String{
         val time = Time(hour,min,0)
         val formatter:Format = SimpleDateFormat("h:mm a")

         return formatter.format(time)
    }


    private fun setMonthOfYear(month:String){
        binding.monthOfYear.text = month
    }

    /**
     * Initialize the map
     */
    private fun initMap(){
         viewModel.mapDayWithTextView(MyScheduleViewModel.Monday,binding.dayOfMonday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Tuesday,binding.dayOfTuesday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Wednesday,binding.dayOfWednesday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Thursday,binding.dayOfThursday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Friday,binding.dayOfFriday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Saturday,binding.dayOfSaturday)
         viewModel.mapDayWithTextView(MyScheduleViewModel.Sunday,binding.dayOfSunday)
         this.dayMap = viewModel.getDayMap
    }
    private fun setDaysOfWeekTextView(){
        for(i in daysOfWeek.indices){

            when(daysOfWeek[i]){
                is Monday -> binding.dayOfMonday.text = daysOfWeek[i].toString()
                is Tuesday -> binding.dayOfTuesday.text = daysOfWeek[i].toString()
                is Wednesday -> binding.dayOfWednesday.text = daysOfWeek[i].toString()
                is Thursday -> binding.dayOfThursday.text = daysOfWeek[i].toString()
                is Friday -> binding.dayOfFriday.text = daysOfWeek[i].toString()
                is Saturday -> binding.dayOfSaturday.text = daysOfWeek[i].toString()
                is Sunday -> binding.dayOfSunday.text = daysOfWeek[i].toString()
            }
        }
    }

    private fun replaceFragment(fragment:Fragment,leftOrRight_slide_in:Int, leftOrRight_slide_out: Int){
             childFragmentManager.beginTransaction().apply {
                     setCustomAnimations(
                         leftOrRight_slide_in,
                         leftOrRight_slide_out
                     ).replace(R.id.schedule_list_fragment, fragment).addToBackStack(null).commit()
             }
    }



    private fun animationFragmentToRightOrLeft(preDayOfWeek:MyScheduleViewModel.Week, currentSelectedDayOfWeek:MyScheduleViewModel.Week): Int {
         val map = viewModel.mapScheduleListFragment
         val preDay = map[preDayOfWeek]!!
         val currentDay =  map[currentSelectedDayOfWeek]!!
         when {
            preDay > currentDay -> return 0
            preDay < currentDay -> return 1
        }
        return -1
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
  /*
    override fun onStop() {

            val elementPositionList = viewModel.getAllCopyOfScheduleList()
            val scheduleLists = viewModel.getAllScheduleLists()

            for (i in 0..6) {

                for (j in 0 until scheduleLists[i].size) {

                    val schedulePosition = scheduleLists[i][j].position

                    //if (schedulePosition != elementPositionList[i][j]) {
                        Log.e("schedulePosition", "$schedulePosition -> ${scheduleLists[i][j].studentName}"  )
                        Log.e("ElementPosition","${elementPositionList[i][j]}")
                        when (val schedule = scheduleLists[i][j]) {
                            is ScheduleWeeklyUpdated -> viewModel.updatedScheduleWeeklyUpdated(
                                schedule,
                                j
                            )
                            is ScheduleDaily -> viewModel.updateScheduleDaily(schedule, j)
                            is ScheduleWeekly -> {
                                Log.e("heihei","asdasdas")
                                viewModel.udWeeklyScheduleWhenActivityStopped(schedule, j)}
                        }
                    //}
                }
            }
        super.onStop()
    }

   */
    /*
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        if(!isFragmentCreated){
            viewModel.initCurrentDayOfWeek()
            daysOfWeek = Date.daysOfMonth
            viewModel.initScheduleView()
            childFragmentManager.beginTransaction().replace(R.id.schedule_list_fragment,viewModel.mapDaySelectedWithFragments[viewModel.currentDayOfWeek]!!).setReorderingAllowed(true).commit()
            this.setMonthOfYear(Date.currentMonth)
            this.setDaysOfWeekTextView()
            this.initMap()

            Log.e("CurrentDayOfWeek ", " ${viewModel.currentDayOfWeek}")

        }else{
            isFragmentCreated = false
        }
        super.onResume()
    }

     */

}