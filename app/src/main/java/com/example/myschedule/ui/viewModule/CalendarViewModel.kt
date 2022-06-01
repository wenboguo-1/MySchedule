package com.example.myschedule.ui.viewModule

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myschedule.data.module.*
import com.example.myschedule.data.network.NetworkResponse
import com.example.myschedule.data.repository.ScheduleDailyRepository
import com.example.myschedule.data.repository.ScheduleWeeklyRepository
import com.example.myschedule.data.repository.ScheduleWeeklyUpdatedRepository
import com.example.myschedule.data.repository.UserInfoRepository
import com.example.myschedule.data.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class CalendarViewModel(application: Application):AndroidViewModel(application){
     private var daysInMonth = 0
     private var year = 0
     private var month = 0
     private val ADD_TIME = 3600000
     private val ADD_TIME1 = 8 * 60 * 60 * 1000
     private val dayInMilSecond = 24 * 60 * 60 * 1000
     private var _scheduleNameList: MutableLiveData<MutableList<StudentInfo>> = MutableLiveData()
     private val _searchResultList:MutableLiveData<SearchResult> = MutableLiveData()
     val searchResultList:LiveData<SearchResult> = _searchResultList
     val scheduleNameList:LiveData<MutableList<StudentInfo>>  = _scheduleNameList
     private val scheduleWeeklyRepository: ScheduleWeeklyRepository by lazy {
        val scheduleWeeklyDao: ScheduleWeeklyDao = ScheduleWeeklyDatabase.getScheduleWeeklyDatabase(context = application).scheduleWeeklyDao()
        ScheduleWeeklyRepository(scheduleWeeklyDao,scheduleWeeklyUpdateDao,scheduleDailyDao)
    }
     private val scheduleWeeklyUpdateDao by lazy {
         ScheduleWeeklyUpdatedDatabase.getScheduleWeeklyUpdatedDatabase(context = application).getScheduleWeeklyUpdatedDao()
     }
    private val scheduleDailyDao by lazy{
        ScheduleDailyDatabase.getScheduleDailyDatabase(application).scheduleDailyDao()
    }
    private val scheduleWeeklyUpdateRepository by lazy {
        ScheduleWeeklyUpdatedRepository(scheduleWeeklyUpdateDao)
    }
    private val scheduleDailyRepository by lazy {
        ScheduleDailyRepository(scheduleDailyDao)
    }
     private val _listOfScheduleList:MutableLiveData<MutableList<MutableList<Schedule>>> by lazy { MutableLiveData()}
     val listOfScheduleList:LiveData<MutableList<MutableList<Schedule>>> = _listOfScheduleList
     fun setDate(daysInMonth:Int,year:Int, month:Int){
         this.daysInMonth = daysInMonth
         this.year = year
         this.month = month
     }
    private fun getCalendar(dayOfMonth:Int, year:Int, month: Int):Calendar{
        return Calendar.getInstance().apply {
            this[Calendar.DAY_OF_MONTH] = dayOfMonth
            this[Calendar.MONTH] = month - 1
            this[Calendar.YEAR] = year
        }
    }

     @RequiresApi(Build.VERSION_CODES.O)
     fun fetchScheduleList(){
         viewModelScope.launch(Dispatchers.IO){
             val scheduleList:MutableList<StudentInfo> = scheduleWeeklyRepository.getAllScheduleNames()
             val map:MutableSet<String> = mutableSetOf()

             _scheduleNameList.postValue(scheduleList.filter { value ->
                 if(map.contains(value.studentName)){
                     false
                 }else {
                     map.add(value.studentName)
                     true
                 }
             } as MutableList<StudentInfo>)
             viewModelScope.launch(Dispatchers.IO){
                 _listOfScheduleList.postValue(fetchWeeklySchedules(daysInMonth,year,month,true))
              }

            // viewModelScope.launch(Dispatchers.IO) {
             //    val useRepo = UserInfoRepository();
              //   val a = useRepo.newUserInfo(UserInfo("wenboguo001@gmail.com", "daxiong"))
              //   if (a.isSuccessful) {
               //      Log.e("sdsd ", "${a.body}")
              //   } else {
                  //   Log.e("sdsd", "sdsd")
             //    }

            // }
         }



     }
    private suspend fun fetchWeeklySchedules(dayOfMonth: Int,year: Int,month: Int,shouldSort:Boolean):MutableList<MutableList<Schedule>>{
         val resultList = mutableListOf<MutableList<Schedule>>()
         for( i in 1..dayOfMonth){
            val dateFormat = "$year-$month-$i 00:00:00"
            val timeStamp = Timestamp.valueOf(dateFormat)
            val isDayLightTime =  TimeZone.getTimeZone( ZoneId.systemDefault().id).inDaylightTime(getCalendar(year = year, month = month, dayOfMonth =i).time)
            val time = getTimeBasedOnDayLightTime(isDayLightTime,month,timeStamp,i)
            val scheduleList = scheduleWeeklyRepository.fetchWeeklySchedule(timeStamp = time)
            scheduleList?.forEach { it.timeStampDaily = time }
            if(shouldSort) scheduleList?.sortBy { it.position }
            resultList.add(scheduleList!!)
        }
        return resultList
    }

   private fun getTimeBasedOnDayLightTime(isDayLightTime:Boolean,month:Int,timeStamp: Timestamp,dayOfMonth: Int):Long{
        return  if(isDayLightTime){
            // 夏令时和冬令时的处理
            if((month == 3 && (dayOfMonth - 14) < 0 )) {
                timeStamp.time
            }else{
                timeStamp.time + ADD_TIME
            }
        }else {
            if(month == 11 && (dayOfMonth - 7) < 0 ){
                timeStamp.time + ADD_TIME
            }else
                timeStamp.time
        }
    }

    fun findScheduleInfoDetail(startDate:Long,endDate:Long,position:Int){
        var start = startDate
        val name = this.scheduleNameList.value?.get(position)?.studentName!!
        val resList:MutableList<Schedule> = mutableListOf()
        var numOfHourAbsent = BigDecimal(0.0)
        var numOfUnpaid = 0.0
        var totalHour = BigDecimal(0.0)
        viewModelScope.launch(Dispatchers.IO) {
            val scheduleWeekly = scheduleWeeklyRepository.fetchAScheduleById(name!!)
            while (start <= endDate) {
                scheduleWeekly?.let { list ->
                    list.forEach {
                        val id = it.id
                        if ( ((start + ADD_TIME1) - it.timeStampWeekly) >= 0L && (it.timeStampWeekly - (start + ADD_TIME1)) % 604800 == 0L) {
                            val schedule:ScheduleWeeklyUpdated =
                                scheduleWeeklyUpdateRepository.fetchAScheduleUpdateByDetail(
                                    id,
                                    timeStamp = start + ADD_TIME1,
                                    name
                                )
                            if (schedule == null) {
                                val schedule1: ScheduleWeekly = it.copy()
                                totalHour = (totalHour.add (parseTime(it.endTime!!).minus(parseTime(it.startTime!!))))
                                schedule1.timeStampDaily = start + ADD_TIME1
                                resList.add(schedule1)
                            }else {
                                totalHour = (totalHour.add (parseTime(it.endTime!!).minus(parseTime(it.startTime!!))))
                                numOfHourAbsent =  numOfHourAbsent.add(if (schedule.isAbsent) parseTime(schedule.endTime!!).minus(parseTime(schedule.startTime!!)) else BigDecimal(0.0))
                                resList.add(schedule)
                            }
                        }
                    }
                }
                val scheduleDaily = scheduleDailyRepository.fetchDailyScheduleById(name, timeStamp = start + ADD_TIME1)
                scheduleDaily?.let {
                    totalHour = totalHour.add((parseTime(it.endTime!!).minus(parseTime(it.startTime!!)) ))
                    numOfHourAbsent = numOfHourAbsent.add(if (it.isAbsent) parseTime(it.endTime!!).minus(parseTime(it.startTime!!)) else BigDecimal(0.0))
                    resList.add(scheduleDaily)
                }
                start += dayInMilSecond
            }
            _searchResultList.postValue(SearchResult(resList,totalHour.toDouble(),numOfUnpaid,numOfHourAbsent.toDouble()))
        }
    }

    fun fetchMonthlyScheduleRecord(monthLength:Int,month: Int,year: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchWeeklySchedules(monthLength,year,month,false)
            val resList = mutableListOf<Schedule>()
            var numOfHourAbsent = BigDecimal(0.0)
            var numOfUnpaid = BigDecimal(0.0)
            var totalHour = BigDecimal(0.0)
            list.forEach{
                elements ->
                  elements.forEach {
                      element ->
                            val startTime = parseTime(element?.startTime!!)
                            val endTime = parseTime(element?.endTime!!)
                      totalHour =   totalHour.add(endTime - startTime)

                      numOfHourAbsent =  numOfHourAbsent.add(if (element.isAbsent) (endTime.minus(startTime)) else BigDecimal(0.0))
                      resList.add(element)


                  }
            }

              _searchResultList.postValue(SearchResult(resList,totalHour.toDouble(),numOfUnpaid.toDouble(),numOfHourAbsent.toDouble()))
        }
    }

    private fun parseTime(time:String):BigDecimal{
        var hour = BigDecimal(0.0)
        var mins = BigDecimal(0.0)
        var i = 0
        val v = 60.0
        while(time[i++].isDigit()) hour = time.substring(0,i).toBigDecimal()
        var j = i
        while(time[j++].isDigit()) mins = if( time.substring(i,j).isNotEmpty()) time.substring(i,j).toBigDecimal() else BigDecimal(0.0)
        Log.e("hour " ,"${time.substring(j,time.length)}")
        Log.e("heihei" ,"${hour}")
        Log.e("heihei" ,"${mins}")

        hour = when( time.substring(j,time.length)){

            "AM" -> if(hour.toDouble() == 12.0) BigDecimal(0) else hour
            else -> if(hour.toDouble() != 12.0) hour + BigDecimal(12.0)  else hour

        }


        return hour.add ((mins.divide(v.toBigDecimal(),1,RoundingMode.HALF_UP)))
    }

    fun clearAllSchedule(){
        viewModelScope.launch(Dispatchers.IO) {
            scheduleWeeklyRepository.deleteAllSchedules()
             val emptyList = mutableListOf<MutableList<Schedule>>()
             for(i in 0..31) emptyList.add(mutableListOf())
             _listOfScheduleList.postValue(emptyList)
        }
    }


}