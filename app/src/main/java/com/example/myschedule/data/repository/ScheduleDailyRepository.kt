package com.example.myschedule.data.repository

import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.room.ScheduleDailyDao

class ScheduleDailyRepository(scheduleDailyDao: ScheduleDailyDao) {
    private val dao = scheduleDailyDao
    suspend fun newScheduleDaily(scheduleDaily:ScheduleDaily){
        dao.newDailySchedule(scheduleDaily)
    }
    suspend fun fetchDailySchedules(timeStamp:Long):MutableList<Schedule>{
        return dao.fetchDailySchedules(timeStamp) as MutableList<Schedule>
    }
    suspend fun updateScheduleDaily(schedule:ScheduleDaily){
        dao.updateScheduleDaily(schedule)
    }
    suspend fun deleteDaily(scheduleDaily: ScheduleDaily){
        dao.deleteScheduleDaily(scheduleDaily)
    }

    suspend fun fetchDayilyScheduleMaxId():Int{
        return dao.fetchDailySchedulMaxId()
    }

    suspend fun fetchDailyScheduleById(name:String,timeStamp: Long):ScheduleDaily{
        return dao.fetchScheduleDailyById(name,timeStamp)
    }

}