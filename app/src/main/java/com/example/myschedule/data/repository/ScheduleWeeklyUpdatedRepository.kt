package com.example.myschedule.data.repository

import com.example.myschedule.data.module.ScheduleWeeklyUpdated
import com.example.myschedule.data.room.ScheduleWeeklyUpdatedDao

class ScheduleWeeklyUpdatedRepository(scheduleWeeklyUpdatedDao: ScheduleWeeklyUpdatedDao) {
    private val dao = scheduleWeeklyUpdatedDao

    suspend fun newScheduleWeeklyUpdated(scheduleWeeklyUpdated: ScheduleWeeklyUpdated){
        dao.insertWeeklyScheduleUpdated(scheduleWeeklyUpdated)
    }
    suspend fun deleteScheduleUpdated(scheduleWeeklyUpdated: ScheduleWeeklyUpdated){
        dao.deleteScheduleWeeklyUpdated(scheduleWeeklyUpdated)
    }
    suspend fun deleteScheduleUpdated(id:Int){
        dao.deleteScheduleWeeklyUpdated(id)
    }

    suspend fun updateScheduleWeeklyUpdated(scheduleWeeklyUpdated: ScheduleWeeklyUpdated){
        dao.updateScheduleWeeklyUpdated(scheduleWeeklyUpdated)
    }

    suspend fun fetchWeeklyUpdatedScheduleMaxId():Int{
        return dao.fetchWeeklyUpdatedScheduleMaxId()
    }

    suspend fun isScheduleUpdatedExist(id:Int):Int{
        return dao.isScheduleUpdatedExist(id)
    }
    suspend fun updateScheduleUpdatedById(position:Int,id:Int){
        dao.updateScheduleUpdatedById(id,position)
    }
    suspend fun fetchAScheduleUpdateByDetail(id:Int,timeStamp:Long,name:String):ScheduleWeeklyUpdated{
        return dao.fetchScheduleTimeDetail(id,timeStamp,name)
    }
}