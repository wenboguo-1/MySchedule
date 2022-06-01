package com.example.myschedule.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myschedule.data.module.ScheduleDaily

@Database(entities = [ScheduleDaily::class], version =20, exportSchema = false)
abstract class ScheduleDailyDatabase:RoomDatabase() {
    abstract fun scheduleDailyDao(): ScheduleDailyDao

    companion object{
        @Volatile
        private var INSTANCE:ScheduleDailyDatabase ?= null

        fun getScheduleDailyDatabase(context: Context):ScheduleDailyDatabase{

            val instance = INSTANCE
            if(instance != null){
                return instance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDailyDatabase::class.java,
                    "schedule_daily"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }


}