package com.example.myschedule.data.room

import android.content.Context
import androidx.room.*
import com.example.myschedule.data.module.DeleteHistory
import com.example.myschedule.data.module.ScheduleDaily

@Dao
interface ScheduleDeleteHistoryDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newDeleteHistory(deleteHistory: DeleteHistory)


}