package com.example.myschedule.data.module

import androidx.room.Entity

@Entity(tableName = "schedule_delete_history",primaryKeys = ["id", "timeStampDaily"])
    data class DeleteHistory(
                            var id:Int = 0,
                            var userName:String?,
                            var studentName:String?,
                            var routine:String?,
                            var year:String?,
                            var month:String?,
                            var dayOfMonth:String?,
                            var startTime:String?,
                            var endTime:String?,
                            var note:String?,
                            var isPaid:Boolean = false,
                            var isAbsent:Boolean = false,
                            var timeStampWeekly:Long,
                            var timeStampDaily:Long)
