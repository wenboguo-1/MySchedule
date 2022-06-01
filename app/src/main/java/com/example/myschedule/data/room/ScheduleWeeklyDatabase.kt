package com.example.myschedule.data.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.ScheduleWeekly

@Database(entities = [ScheduleWeekly::class], version =20, exportSchema = false)
abstract class ScheduleWeeklyDatabase:RoomDatabase() {

     abstract fun scheduleWeeklyDao(): ScheduleWeeklyDao

    companion object{
        @Volatile
        private var INSTANCE:ScheduleWeeklyDatabase ?= null

        fun getScheduleWeeklyDatabase(context:Context):ScheduleWeeklyDatabase{

            val instance = INSTANCE
            if(instance != null){
                return instance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(

                    context.applicationContext,
                    ScheduleWeeklyDatabase::class.java,
                    "schedule_weekly"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }

}