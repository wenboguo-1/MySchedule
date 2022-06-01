package com.example.myschedule.data.room

import androidx.room.*
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated

@Dao
interface ScheduleWeeklyUpdatedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeeklyScheduleUpdated(scheduleWeeklyUpdated:ScheduleWeeklyUpdated)
    @Query("Select * from schedule_weekly_update where timeStampDaily = :timeStamp")
    suspend fun fetchAllWeeklyScheduleUpdated(timeStamp:Long):MutableList<ScheduleWeeklyUpdated>
    @Update
    suspend fun updateScheduleWeeklyUpdated(scheduleWeeklyUpdated: ScheduleWeeklyUpdated)
    @Query("DELETE FROM schedule_weekly_update WHERE :id = id")
    suspend fun deleteScheduleWeeklyUpdated(id:Int)
    @Delete
    suspend fun deleteScheduleWeeklyUpdated(scheduleWeeklyUpdated: ScheduleWeeklyUpdated)
    @Query("SELECT coalesce( max(scheduleUpdatedId),0) FROM schedule_weekly_update")
    suspend fun fetchWeeklyUpdatedScheduleMaxId():Int
    @Query("SELECT COUNT(*) FROM schedule_weekly_update WHERE :id = scheduleUpdatedId")
    suspend fun isScheduleUpdatedExist(id:Int):Int
    @Query("Update schedule_weekly_update SET position = :position WHERE :id = id")
    suspend fun updateScheduleUpdatedById(id:Int,position:Int)
    @Query("SELECT * from schedule_weekly_update WHERE id = :id AND timeStampDaily = :timeStamp AND studentName = :name")
    suspend fun fetchScheduleTimeDetail(id:Int,timeStamp: Long,name:String):ScheduleWeeklyUpdated
    @Query("DELETE FROM schedule_weekly_update")
    suspend fun deleteAllSchedules()

}