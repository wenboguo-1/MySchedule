package com.example.myschedule.ui.viewModule

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated
import com.example.myschedule.data.repository.ScheduleDailyRepository
import com.example.myschedule.data.repository.ScheduleWeeklyRepository
import com.example.myschedule.data.repository.ScheduleWeeklyUpdatedRepository
import com.example.myschedule.data.room.*
import com.example.myschedule.date.Date
import com.example.myschedule.ui.fragments.week_schedule_fragments.*
import com.example.myschedule.util.OnRowItemUpdateInterface
import com.example.myschedule.util.operation.Delete
import com.example.myschedule.util.operation.Insert
import com.example.myschedule.util.operation.Operation
import com.example.myschedule.util.operation.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.mapOf
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
@RequiresApi(Build.VERSION_CODES.O)
class MyScheduleViewModel(application: Application) : AndroidViewModel(application){

    private val _observerUpdateListPositionForWeek :MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    val observerUpdateListPositionForWeek:LiveData<MutableList<Schedule>> = _observerUpdateListPositionForWeek
    var position:Int = 0
    private val dayOfWeek : MutableLiveData<Week> = MutableLiveData()
    private val dayMap = mutableMapOf<Week,TextView>()
    var currentDayOfWeek: Week ?= null
    val onRowItemUpdateInterface by lazy {
        object : OnRowItemUpdateInterface {
            override fun onIsPaidClick(position: Int) {

               _onIsPaidClicked.value = position
            }

            override fun onIsAbsentClick(position: Int) {
                 _onAbsentClicked.value = position
            }

            override fun onUpdatedClick(position: Int) {
                 _onUpdatedClicked.value = position
            }

            override fun onDeleteClick(position:Int){
                 _onDeleteClick.value = position
            }

            override fun onMoveToClick(position: Int) {
                _onMoveToClick.value = position;
            }
        }
    }
    private val _onDeleteClick:MutableLiveData<Int> = MutableLiveData()
    val onDeleteClick:LiveData<Int> = _onDeleteClick
    private val _onAbsentClicked:MutableLiveData<Int> = MutableLiveData()
    val onAbsentClicked:LiveData<Int> = _onAbsentClicked
    private val isOnMonthOfYearClicked:MutableLiveData<Boolean> = MutableLiveData()
    private var preDayOfWeek: Week? = null
    var operation: Operation ?= null
    private val scheduleWeeklyUpdatedDao:ScheduleWeeklyUpdatedDao by lazy { ScheduleWeeklyUpdatedDatabase.getScheduleWeeklyUpdatedDatabase(context = application).getScheduleWeeklyUpdatedDao()  }
    private val scheduleDailyDao:ScheduleDailyDao by lazy{ScheduleDailyDatabase.getScheduleDailyDatabase(application).scheduleDailyDao() }
    private val scheduleWeeklyUpdatedRepository:ScheduleWeeklyUpdatedRepository by lazy{
        ScheduleWeeklyUpdatedRepository(scheduleWeeklyUpdatedDao)
    }
    private val scheduleDailyRepository:ScheduleDailyRepository by lazy{
        ScheduleDailyRepository(scheduleDailyDao)
    }
    private val scheduleWeeklyDao:ScheduleWeeklyDao by lazy{ScheduleWeeklyDatabase.getScheduleWeeklyDatabase(context = application).scheduleWeeklyDao()}
    private val scheduleWeeklyRepository:ScheduleWeeklyRepository by lazy {
        ScheduleWeeklyRepository(scheduleWeeklyDao,scheduleWeeklyUpdatedDao,scheduleDailyDao)
    }

    private val _onMoveToClick:MutableLiveData<Int> = MutableLiveData();
    private val _onUpdateScheduleListMonday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListTuesday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListWednesday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListThursday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListFriday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListSaturday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onUpdateScheduleListSunday:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    private val _onIsPaidClicked:MutableLiveData<Int> = MutableLiveData()
    val onMoveToClick:LiveData<Int> = _onMoveToClick
    val addOrUpdatedText:MutableLiveData<String> = MutableLiveData()
    val onIsPaidClicked:LiveData<Int> = _onIsPaidClicked
    val onUpdateScheduleListMonday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListMonday
    val onUpdateScheduleListTuesday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListTuesday
    val onUpdateScheduleListWednesday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListWednesday
    val onUpdateScheduleListThursday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListThursday
    val onUpdateScheduleListFriday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListFriday
    val onUpdateScheduleListSaturday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListSaturday
    val onUpdateScheduleListSunday:LiveData<MutableList<Schedule>> = _onUpdateScheduleListSunday
    val newScheduleName = MutableLiveData<String>()
    val _newScheduleName:LiveData<String> = newScheduleName
    val scheduleNote:MutableLiveData<String> = MutableLiveData("")
    val startTime:MutableLiveData<String> = MutableLiveData("")
    var endTime:MutableLiveData<String> = MutableLiveData("")
    val mapScheduleListFragment:Map<Week,Int> by lazy {
          mapOf(Sunday to 1, Monday to 2, Tuesday to 3, Wednesday to 4, Thursday to 5, Friday to 6, Saturday to 7 )
    }
    private val _mapOfWeekToSchedule:Map<Int,MutableLiveData<MutableList<Schedule>>> by lazy {
        mapOf(0 to _onUpdateScheduleListSunday,1 to _onUpdateScheduleListMonday,2 to _onUpdateScheduleListTuesday,3 to _onUpdateScheduleListWednesday,
        4 to _onUpdateScheduleListThursday,5 to _onUpdateScheduleListFriday,6 to _onUpdateScheduleListSaturday)
    }


    private val _copyOfSchedule:MutableList<MutableList<Int>> by lazy { mutableListOf() }
    private  var mapOfDateFormat:MutableMap<Int,Long> ?= null
    private val _onUpdatedClicked:MutableLiveData<Int> = MutableLiveData()
    val onUpdatedClicked:LiveData<Int> = _onUpdatedClicked

    fun initCurrentDayOfWeek(){
        currentDayOfWeek = when(Date.currentDayOfWeek){
            is com.example.myschedule.date.Monday -> Monday
            is com.example.myschedule.date.Tuesday -> Tuesday
            is com.example.myschedule.date.Wednesday -> Wednesday
            is com.example.myschedule.date.Thursday -> Thursday
            is com.example.myschedule.date.Friday -> Friday
            is com.example.myschedule.date.Saturday -> Saturday
            is com.example.myschedule.date.Sunday -> Sunday
        }
        dayOfWeek.postValue(currentDayOfWeek)
    }

    fun getCurrentSchedulePosition():Int{
        return this.getCurrentDayOfOnUpdateListSchedule().value?.size!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initScheduleView(){
        viewModelScope.launch(Dispatchers.IO) {
            val mutableMap = mutableMapOf<Int, Long>()
            val calendar = getCalendar()
            viewModelScope.launch(Dispatchers.IO) {
                 for (i in 0..6) {
                     val month = SimpleDateFormat("M") .format(calendar.time).toInt()
                     val dayOfMonth =  SimpleDateFormat("dd") .format(calendar.time).toInt()
                     val format = SimpleDateFormat("yyyy-MM-dd").format(calendar.time) + " 00:00:00"

                     val isDayLightTime =  TimeZone.getTimeZone( ZoneId.systemDefault().id).inDaylightTime(calendar.time)
                     val timestamp = Timestamp.valueOf(format)
                     val time = if(isDayLightTime) { //处理夏令时
                         if (month == 3 && (dayOfMonth - 14) < 0) {
                             timestamp.time
                         }else{
                             timestamp.time + 3600000
                         }
                     }else{
                         if(month == 11 && (dayOfMonth - 7 ) < 0){
                             timestamp.time + 3600000
                         }else{
                             timestamp.time
                         }
                     }
                     mutableMap[i] = time
                     val temp = scheduleWeeklyRepository
                     val scheduleList = temp.fetchWeeklySchedule(timeStamp = time)
                     _mapOfWeekToSchedule[i]?.postValue(scheduleList)
                     calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                mapOfDateFormat = mutableMap

            }
        }

    }

    fun setObserverUpdateListPositionForWeek(list:MutableList<Schedule>){
        this._observerUpdateListPositionForWeek.postValue(list)
    }
    fun getCalendar():Calendar{
        return Calendar.getInstance().apply {
            var currentMonth = convertMonthToIn(Date.currentMonth) - 1
            when(Date.startDate > Date.endDate){
                true -> {
                    if(currentMonth == 0){
                        currentMonth = 11
                        Date.year =   Date.year - 1
                    }else{
                        currentMonth -= 1
                    }
                }
            }
            this[Calendar.DAY_OF_MONTH] = Date.startDate
            this[Calendar.MONTH] = currentMonth
            this[Calendar.YEAR] = Date.year

        }
    }


    fun setUpdatedScheduleName(scheduleName:String){
        this.newScheduleName.value = scheduleName
    }

    /*
       return current day of week schedule list
     */
    fun getCurrentDayOfOnUpdateListSchedule():MutableLiveData<MutableList<Schedule>>{
      return  when (currentDayOfWeek){
            is Monday -> _onUpdateScheduleListMonday
            is Tuesday -> _onUpdateScheduleListTuesday
            is Wednesday -> _onUpdateScheduleListWednesday
            is Thursday -> _onUpdateScheduleListThursday
            is Friday -> _onUpdateScheduleListFriday
            is Saturday -> _onUpdateScheduleListSaturday
            else -> {
                return _onUpdateScheduleListSunday
            }

        }
    }

    val mapDaySelectedWithFragments:Map<Week,Fragment> by lazy {
         mapOf(Monday to MondayScheduleListFragment.newInstance(),
               Tuesday to TuesdayScheduleListFragment.newInstance(),
               Wednesday to WednesdayScheduleListFragment.newInstance(),
               Thursday to ThursdayScheduleListFragment.newInstant(),
               Friday to FridayScheduleListFragment.newInstance(),
               Saturday to SaturdayScheduleListFragment.newInstance(),
               Sunday to SundayScheduleListFragment.newInstant())
    }
    val getDayMap:Map<Week,TextView> = dayMap
    val getIsOnMonthOfYearClicked:LiveData<Boolean> = isOnMonthOfYearClicked
    val getDaySelected : LiveData<Week> = dayOfWeek
    fun setPreDayOfWeek(dayOfWeek:Week){
        this.preDayOfWeek = dayOfWeek
    }
    fun getPreDayOfWeek()= this.preDayOfWeek

    private fun updatedScheduleData(schedule:Schedule) {

        val list = getCurrentDayOfOnUpdateListSchedule().value
        list?.add(schedule)
        getCurrentDayOfOnUpdateListSchedule().value = list
    }
    private fun updateScheduleList(schedule:Schedule,position:Int){
        val list = getCurrentDayOfOnUpdateListSchedule().value
        list?.set(position,schedule)
        getCurrentDayOfOnUpdateListSchedule().postValue(list)
    }
        /**
     * Map the day of week with text view
     * @param dayOfWeek sub-class of the week that represents the day of weeks
     * @param view Text view that corresponds with day of weeks
     */
     fun mapDayWithTextView(dayOfWeek:Week,view:TextView){

        this.dayMap[dayOfWeek] = view
     }
    /*
       Return current day to be added to current calendar for the new schedule
     */
    fun getDayAdded():Int = when (this.currentDayOfWeek) {
        is Monday ->  1
        is Tuesday -> 2
        is Wednesday -> 3
        is Thursday -> 4
        is Friday -> 5
        is Saturday -> 6
        else -> 0
    }

    fun moveTo(difference:Int,position: Int){
        val id = this.getCurrentDayOfOnUpdateListSchedule().value?.get(position)?.id
        viewModelScope.launch {
            if (id != null) {
                Log.e("time ",difference.toString())
                scheduleWeeklyRepository.moveTo(difference,id)
            }
        }
    }
    fun addNewScheduleWeekly(scheduleWeekly:ScheduleWeekly){

        val list = getCurrentDayOfOnUpdateListSchedule().value
        var index_to_be_inserted = 0
        if(list?.isNotEmpty() == true) {
            for (i in 0 until list?.size!!) {
                if (list[i].position >= scheduleWeekly.position) {
                    list.add(i, scheduleWeekly)
                    index_to_be_inserted = i
                    break
                }
            }
        }else{
            list?.add(scheduleWeekly)
        }
         getCurrentDayOfOnUpdateListSchedule().value = list
         this.operation = Insert(index_to_be_inserted)
         viewModelScope.launch {
             scheduleWeeklyRepository.newWeeklySchedule(scheduleWeekly)
             scheduleWeekly.id = scheduleWeeklyRepository?.fetchMaxId()!!
         }
    }
     fun updatedScheduleWeeklyUpdated(schedule:ScheduleWeeklyUpdated,position:Int){
         this.operation = Update(position)
         this.updateScheduleList(schedule,position)
        viewModelScope.launch {
            scheduleWeeklyUpdatedRepository.updateScheduleWeeklyUpdated(schedule)
        }
     }
    fun addNewScheduleDaily(schedule:ScheduleDaily){
        this.operation = Insert(getCurrentDayOfOnUpdateListSchedule().value?.size!!)

          this.updatedScheduleData(schedule)
          viewModelScope.launch(Dispatchers.IO) {
              scheduleDailyRepository.newScheduleDaily(schedule)
              schedule.id = scheduleDailyRepository.fetchDayilyScheduleMaxId()
          }
    }

    fun getWeekList():ArrayList<String>{
        var res:ArrayList<String> = ArrayList();
        for(i in 1..7){
            if(i == getDayAdded()){
                continue
            }
            when(i){
                1 -> res?.add("Monday")
                2 -> res.add("Tuesday")
                3 -> res.add("Wednesday")
                4 -> res.add("Thursday")
                5 -> res.add("Friday")
                6 -> res.add(("Saturday"))
                else -> res.add("Sunday")
            }

        }
        return res
    }
    fun deleteScheduleWeekly(schedule: Schedule,position: Int,id:Int){
        this.deleteScheduleWeekly(position)
        viewModelScope.launch(Dispatchers.IO) {
            scheduleWeeklyRepository.deleteScheduleWeekly(schedule)
            scheduleWeeklyUpdatedRepository.deleteScheduleUpdated(id)
        }
    }

    fun deleteScheduleWeeklyUpdated(id:Int,position: Int,shouldResetList:Boolean){
        if(shouldResetList) this.deleteScheduleWeekly(position)

        viewModelScope.launch(Dispatchers.IO   ){
            scheduleWeeklyUpdatedRepository.deleteScheduleUpdated(id)
        }
    }
    fun deleteScheduleDaily(scheduleWeekly:ScheduleWeekly,position:Int){


         val scheduleList = getCurrentDayOfOnUpdateListSchedule()?.value
         val scheduleDaily = getCurrentDayOfOnUpdateListSchedule()?.value?.get(position!!) as ScheduleDaily
         scheduleList?.removeAt(position)
         Log.e("schedule rotouin ", "${scheduleWeekly.routine}")
         scheduleList?.add(position,scheduleWeekly)

         this.getCurrentDayOfOnUpdateListSchedule().postValue(scheduleList)
         viewModelScope.launch(Dispatchers.IO) {
             scheduleDailyRepository.deleteDaily(scheduleDaily)
             scheduleWeeklyRepository.newWeeklySchedule(scheduleWeekly)
             scheduleWeekly.id = scheduleWeeklyRepository.fetchMaxId()
         }

    }



    fun getAllScheduleLists():List<List<Schedule>>{

         val resList:List<List<Schedule>> by lazy {
             listOf(
                 this.onUpdateScheduleListSunday.value as List<Schedule>,
                 this.onUpdateScheduleListMonday.value as List<Schedule>,
                 this.onUpdateScheduleListTuesday.value as List<Schedule>,
                 this.onUpdateScheduleListWednesday.value as List<Schedule>,
                 this.onUpdateScheduleListThursday.value as List<Schedule>,
                 this.onUpdateScheduleListFriday.value as List<Schedule>,
                 this.onUpdateScheduleListSaturday.value as List<Schedule>)
                }
         return resList
    }

   private fun deleteScheduleWeekly(position: Int){
        this.operation = Delete(position)
        val list = getCurrentDayOfOnUpdateListSchedule().value
        list?.removeAt(position)
        getCurrentDayOfOnUpdateListSchedule().postValue(list)
    }

    fun changeScheduleWeeklyToDaily(position: Int,schedule: Schedule){
        schedule.note = ""
        this.operation = Update(position)
        val weekScheduleId = getCurrentDayOfOnUpdateListSchedule().value?.get(position)?.id!!
        this.updateScheduleList(schedule,position)


        viewModelScope.launch(Dispatchers.IO) {
            scheduleDailyRepository.newScheduleDaily(schedule as ScheduleDaily)
            scheduleWeeklyRepository.deletwScheduleWeeklyById(weekScheduleId)
            scheduleWeeklyUpdatedRepository.deleteScheduleUpdated(weekScheduleId)
            getCurrentDayOfOnUpdateListSchedule().value?.get(position)?.id = scheduleDailyRepository.fetchDayilyScheduleMaxId()

        }

    }
    fun updateScheduleDaily(schedule:ScheduleDaily,position:Int){
        this.operation = Update(position)

        this.updateScheduleList(schedule,position)
        viewModelScope.launch {
            scheduleDailyRepository.updateScheduleDaily(schedule)
        }
    }
    fun addNewScheduleWeeklyUpdate(schedule:ScheduleWeeklyUpdated,position:Int){
        this.operation = Update(position)
        this.updateScheduleList(schedule,position)
        viewModelScope.launch(Dispatchers.IO) {
            scheduleWeeklyUpdatedRepository.newScheduleWeeklyUpdated(schedule)
            schedule.scheduleUpdatedId = scheduleWeeklyUpdatedRepository.fetchWeeklyUpdatedScheduleMaxId()

        }
    }

    fun deleteScheduleDailyWithoutAddingNewWeeklySchedule(schedule:ScheduleDaily,position:Int){
        this.operation = Delete(position)
        val list = getCurrentDayOfOnUpdateListSchedule().value
        list?.removeAt(position)
        this.getCurrentDayOfOnUpdateListSchedule().postValue(list)
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDailyRepository.deleteDaily(schedule)
        }
    }
    fun updateScheduleWeekly(scheduleWeekly: ScheduleWeekly){
        viewModelScope.launch(Dispatchers.IO) {
            scheduleWeeklyRepository.updateScheduleWeekly(scheduleWeekly)
        }
    }
    fun updateSchedleWeeklyUpdate(scheduleWeeklyUpdated: ScheduleWeeklyUpdated){
        viewModelScope.launch(Dispatchers.IO){
            scheduleWeeklyUpdatedRepository.updateScheduleWeeklyUpdated(scheduleWeeklyUpdated)
        }
    }

    fun updateScheduleDaily(scheduleDaily: ScheduleDaily){
        viewModelScope.launch(Dispatchers.IO){
            scheduleDailyRepository.updateScheduleDaily(scheduleDaily)
        }
    }
    fun updateScheduleWeekly(scheduleWeekly: ScheduleWeekly,position: Int){
        this.operation = Update(position)
        this.updateScheduleList(scheduleWeekly,position)
        viewModelScope.launch(Dispatchers.IO) {

            scheduleWeeklyRepository.updateScheduleWeekly(scheduleWeekly)
        }
    }

    fun deleteScheduleWeeklyById(id:Int,position:Int){
        this.operation = Delete(position)
        val list =  getCurrentDayOfOnUpdateListSchedule().value
        list?.removeAt(position)
        getCurrentDayOfOnUpdateListSchedule().value = list

        viewModelScope.launch(Dispatchers.IO) {
            scheduleWeeklyRepository.deletwScheduleWeeklyById(id)
        }
    }
    fun setOnMonthOfYearClicked(){
        this.isOnMonthOfYearClicked.postValue(true)
    }

    fun addNewScheduleUpdate(schedule:ScheduleWeeklyUpdated){
        viewModelScope.launch(Dispatchers.IO){
            scheduleWeeklyUpdatedRepository.newScheduleWeeklyUpdated(schedule)
        }
    }


    fun getNewUpdatedScheduleWeekly(s: ScheduleWeekly):ScheduleWeeklyUpdated{
          return ScheduleWeeklyUpdated(s.id,s.userName,s.studentName,s.routine,s.year,s.month,s.dayOfMonth,
           s.startTime,s.endTime,s.note,s.isPaid,s.isAbsent,s.timeStampWeekly,getTimeStamp(),s.position)
    }

    private fun getTimeStamp():Long{
         val calendar = this.getCalendar();
         calendar.add(android.icu.util.Calendar.DAY_OF_YEAR,this.getDayAdded())
         val format1 = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
         val isDayLightTime =  TimeZone.getTimeZone( ZoneId.systemDefault().id).inDaylightTime(calendar.time)
         return  getTimeStamp(isDayLightTime, timestamp = Timestamp.valueOf("$format1 00:00:00"))
    }

    /*
 fun updateSchedulePositionOfEachWeek(scheduleList:MutableList<Schedule>){
        var position = 0
        viewModelScope.launch {
            scheduleList.forEach { schedule ->
                when (schedule) {
                    is ScheduleDaily -> {
                        schedule.position = position++
                        updateScheduleDaily(schedule)
                    }
                    is ScheduleWeeklyUpdated -> {
                        schedule.position = position++
                        scheduleWeeklyUpdatedRepository.updateScheduleUpdatedById(schedule.position,schedule.id)
                        scheduleWeeklyRepository.updateScheduleWeeklyPosition(schedule.id,position-1)

                    }
                    is ScheduleWeekly -> {
                        schedule.position = position++
                        updateScheduleWeekly(schedule)
                    }
                }
            }
        }

    }

     */

  fun updateSchedulePositionOfOneDay(scheduleList:MutableList<Schedule>){
        var position = 0;
        viewModelScope.launch(Dispatchers.IO) {
            scheduleList.forEach {
                schedule ->
                  when(schedule){
                      is ScheduleDaily -> {
                          schedule.position = position++
                          updateScheduleDaily(schedule)
                      }
                      is ScheduleWeekly -> {
                          val newUpdatedSchedule =  getNewUpdatedScheduleWeekly(schedule)
                          newUpdatedSchedule.position = position++
                          addNewScheduleWeeklyUpdate(newUpdatedSchedule,position-1)
                      }
                      is ScheduleWeeklyUpdated -> {
                          schedule.position = position++
                          updatedScheduleWeeklyUpdated(schedule,position-1)
                      }
                  }
            }
        }
    }


    fun getTimeStamp(isDayTime:Boolean,timestamp: Timestamp):Long  {
        return if(isDayTime){
            if(Date.month == 2 && (Date.dayOfMonth - 14) < 0 ){
                timestamp.time
            }else{
                timestamp.time + 3600000
            }
        }else{
            if(Date.month == 10 && (Date.dayOfMonth - 7) < 0){
                timestamp.time + 3600000
            }else{
                timestamp.time
            }
        }

    }
    private fun convertMonthToIn(month:String):Int{
          return  when(month){
                "January" -> 1
                "February" -> 2
                "March" -> 3
                "April" -> 4
                "May" -> 5
                "June" -> 6
                "July" -> 7
                "August" -> 8
                "September" -> 9
                "October" -> 10
                "November" -> 11
                else ->12
           }
    }

    fun convertDayToNumber(s:String):Int{

        return  when(s){
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            else -> 7
        }
    }

    /**
     * Set up onClicked listener for card view of MySchedule fragment
     */
    fun onMondayClicked(){ dayOfWeek.postValue(Monday)}
    fun onTuesdayClicked(){ dayOfWeek.postValue(Tuesday) }
    fun onWednesdayClicked(){ dayOfWeek.postValue(Wednesday) }
    fun onThursdayClicked(){ dayOfWeek.postValue(Thursday) }
    fun onFridayClicked(){ dayOfWeek.postValue(Friday) }
    fun onSaturdayClicked(){ dayOfWeek.postValue(Saturday) }
    fun onSundayClicked(){ dayOfWeek.postValue(Sunday) }
    sealed class Week
    object Monday : Week()
    object Tuesday: Week()
    object Wednesday : Week()
    object Thursday: Week()
    object Friday : Week()
    object Saturday : Week()
    object Sunday: Week()



}