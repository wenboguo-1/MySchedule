package com.example.myschedule.data.repository


import android.util.Log
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.StudentInfo
import com.example.myschedule.data.room.ScheduleDailyDao
import com.example.myschedule.data.room.ScheduleWeeklyDao
import com.example.myschedule.data.room.ScheduleWeeklyUpdatedDao

class ScheduleWeeklyRepository(scheduleWeeklyDao:ScheduleWeeklyDao,scheduleWeeklyUpdatedDao:ScheduleWeeklyUpdatedDao,
     scheduleDailyDao: ScheduleDailyDao) {


    private val daoWeekly = scheduleWeeklyDao
    private val daoWeeklyUpdated = scheduleWeeklyUpdatedDao
    private val daoScheduleDaily =  scheduleDailyDao
    suspend fun newWeeklySchedule(scheduleWeekly:Schedule){
        daoWeekly.insertWeeklySchedule(scheduleWeekly as ScheduleWeekly)
    }

    suspend fun moveTo(difference:Int,id:Int){
        daoWeekly.moveTo(difference,id)
    }
    suspend fun fetchWeeklySchedule(timeStamp: Long): MutableList<Schedule>? {
        val weeklySchedule = daoWeekly.fetchAllWeeklySchedule(timeStamp = timeStamp) as MutableList<Schedule>

        val scheduleUpdatedList =
            daoWeeklyUpdated.fetchAllWeeklyScheduleUpdated(timeStamp = timeStamp) as MutableList<Schedule>
        val scheduleDailyList = daoScheduleDaily.fetchDailySchedules(timeStamp = timeStamp)
        val map = mutableMapOf<Int, Int>()

        when {
            weeklySchedule.isEmpty() and scheduleDailyList.isEmpty() -> return mutableListOf()
            scheduleUpdatedList.isEmpty() and scheduleDailyList.isEmpty() ->{
                weeklySchedule.sortBy { it.position }
                return weeklySchedule
            }
            else -> {

                for (i in 0 until scheduleUpdatedList.size) {
                    map[scheduleUpdatedList[i].id] = i
                }
                for (i in 0 until weeklySchedule?.size!!) {

                    if (weeklySchedule[i].id in map) {
                        val index = map[weeklySchedule[i].id]!!
                        map[weeklySchedule[i].id]?.let { scheduleUpdatedList.set( index,scheduleUpdatedList[index]) }
                    } else {
                        scheduleUpdatedList.add(weeklySchedule[i])
                    }
                }
                scheduleUpdatedList.addAll(scheduleDailyList)
            }
        }
        scheduleUpdatedList.sortBy { it.position }

        return scheduleUpdatedList
    }

    suspend fun fetchMaxId():Int{
       return  daoWeekly.fetchMaxId()
    }
    suspend fun deleteScheduleWeekly(scheduleWeekly: Schedule){
       daoWeekly.deleteScheduleWeekly(scheduleWeekly = scheduleWeekly as ScheduleWeekly)
    }

    suspend fun updateScheduleWeekly(scheduleWeekly: ScheduleWeekly){

            daoWeekly.updateScheduleWeekly(scheduleWeekly)

    }

    suspend fun deletwScheduleWeeklyById(id:Int){
        daoWeekly.deleteScheduleWeeklyById(id)
    }

    suspend fun getAllScheduleNames():MutableList<StudentInfo>{
         val res = mutableListOf<StudentInfo>()
         res.addAll(daoWeekly.getScheduleNames())
         res.addAll(daoScheduleDaily.getScheduleNames())
         return res
    }

    suspend fun fetchAScheduleById(name:String): MutableList<ScheduleWeekly>{
        return daoWeekly.fetchScheduleTimeDetail(name)

    }

    suspend fun deleteAllSchedules(){
        daoScheduleDaily.deleteAllSchedules()
        daoWeekly.deleteAllSchedules()
        daoWeeklyUpdated.deleteAllSchedules()
    }



}