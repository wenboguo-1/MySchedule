package com.example.myschedule.data.room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myschedule.data.module.DeleteHistory


@Database(entities = [DeleteHistory::class], version =20, exportSchema = false)
abstract class ScheduleDeleteHistoryDatabase: RoomDatabase() {
    abstract fun scheduleScheduleHistoryDao(): ScheduleDeleteHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDeleteHistoryDatabase? = null

        fun getScheduleDeleteHistoryDatabase(context: Context): ScheduleDeleteHistoryDatabase {

            val instance = INSTANCE
            if (instance != null) {
                return instance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDeleteHistoryDatabase::class.java,
                    "schedule_delete_history"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}