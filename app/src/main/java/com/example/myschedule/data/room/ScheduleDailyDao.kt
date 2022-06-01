package com.example.myschedule.data.room

import androidx.room.*
import com.example.myschedule.data.module.ScheduleDaily
import com.example.myschedule.data.module.StudentInfo

@Dao
interface ScheduleDailyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newDailySchedule(scheduleDaily:ScheduleDaily)
    @Query("SELECT * FROM schedule_daily where :timeStamp = timeStampDaily")
    suspend fun fetchDailySchedules(timeStamp:Long):MutableList<ScheduleDaily>
    @Update
    suspend fun updateScheduleDaily(scheduleDaily: ScheduleDaily)
    @Delete
    suspend fun deleteScheduleDaily(schedule:ScheduleDaily)
    @Query("Select coalesce( max(id),0) FROM schedule_daily")
    suspend fun fetchDailySchedulMaxId():Int
    @Query ("SELECT studentName,id from schedule_daily")
    suspend fun getScheduleNames():MutableList<StudentInfo>
    @Query("SELECT * from schedule_daily WHERE studentName = :name  AND timeStampDaily = :timeStamp")
    suspend fun fetchScheduleDailyById(name:String,timeStamp: Long):ScheduleDaily
    @Query("DELETE FROM schedule_daily")
    suspend fun deleteAllSchedules()

}