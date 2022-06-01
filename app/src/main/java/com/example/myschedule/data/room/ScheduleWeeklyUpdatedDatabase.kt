package com.example.myschedule.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleWeekly
import com.example.myschedule.data.module.ScheduleWeeklyUpdated

@Database(entities = [ScheduleWeeklyUpdated::class], version = 20, exportSchema = false)
abstract class ScheduleWeeklyUpdatedDatabase:RoomDatabase() {
     abstract fun getScheduleWeeklyUpdatedDao():ScheduleWeeklyUpdatedDao

     companion object{
         @Volatile

         private var INSTANCE: ScheduleWeeklyUpdatedDatabase? = null
         fun getScheduleWeeklyUpdatedDatabase(context:Context):ScheduleWeeklyUpdatedDatabase{
              val instance = INSTANCE

                if(instance != null )
                   return instance
             synchronized(this){
                 val instance = Room.databaseBuilder(
                     context.applicationContext,
                     ScheduleWeeklyUpdatedDatabase::class.java,
                     "schedule_weekly_update"
                 ).fallbackToDestructiveMigration().build()
                 INSTANCE = instance
                 return instance
             }
         }
     }
}