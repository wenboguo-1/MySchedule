package com.example.myschedule.data.room
import androidx.room.*
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.StudentInfo

@Dao
interface ScheduleWeeklyDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeeklySchedule(weeklyScheduleWeekly: ScheduleWeekly)

    @Query("SELECT * FROM schedule_weekly where ((:timeStamp - timeStampWeekly) % 604800) = 0 AND (:timeStamp - timeStampWeekly) >= 0 ")
    suspend fun fetchAllWeeklySchedule(timeStamp:Long):MutableList<ScheduleWeekly>

    @Query("SELECT * FROM schedule_weekly")
    suspend fun fetchData():MutableList<ScheduleWeekly>

    @Query("SELECT coalesce( max(id),0) FROM schedule_weekly")
    suspend fun fetchMaxId():Int
    @Query("DELETE from schedule_weekly WHERE id = :id ")
    suspend fun deleteScheduleWeeklyById(id:Int)
    @Delete
    suspend fun deleteScheduleWeekly(scheduleWeekly: ScheduleWeekly)

    @Update
    suspend fun updateScheduleWeekly(scheduleWeekly: ScheduleWeekly)
    @Query("Update schedule_weekly SET position = :position where id = :id")
    suspend fun updateScheduleWeeklyPosition(position:Int,id:Int)
    @Query("SELECT studentName, id FROM schedule_weekly")
    suspend fun getScheduleNames():MutableList<StudentInfo>
    @Query("SELECT * FROM schedule_weekly WHERE  studentName =:name" )
    suspend fun fetchScheduleTimeDetail(name:String):MutableList<ScheduleWeekly>
    @Query("DELETE FROM schedule_weekly")
    suspend fun deleteAllSchedules()
    @Query("UPDATE schedule_weekly SET timeStampWeekly = timeStampWeekly + :difference,timeStampDaily = timeStampDaily + :difference " +
            "where id = :id ")
    suspend fun moveTo(difference:Int,id:Int)
}