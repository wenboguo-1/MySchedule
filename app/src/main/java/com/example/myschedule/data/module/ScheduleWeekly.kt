package com.example.myschedule.data.module

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "schedule_weekly")
 data class ScheduleWeekly(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override var id:Int = 0,
    override var userName:String?,
    override var studentName:String?,
    override var routine:String?,
    override var year:String?,
    override var month:String?,
    override var dayOfMonth:String?,
    override var startTime:String?,
    override var endTime:String?,
    override var note:String?,
    override var isPaid:Boolean = false,
    override var isAbsent:Boolean = false,
    override var timeStampWeekly:Long,
    override var timeStampDaily:Long,
    override var position:Int
):Schedule(id,userName,studentName,routine,year,month,dayOfMonth,startTime,endTime,note, isPaid, isAbsent, timeStampWeekly, timeStampDaily,position),Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userName)
        parcel.writeString(studentName)
        parcel.writeString(routine)
        parcel.writeString(year)
        parcel.writeString(month)
        parcel.writeString(dayOfMonth)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(note)
        parcel.writeByte(if (isPaid) 1 else 0)
        parcel.writeByte(if (isAbsent) 1 else 0)
        parcel.writeLong(timeStampWeekly)
        parcel.writeLong(timeStampDaily)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleWeekly> {
        override fun createFromParcel(parcel: Parcel): ScheduleWeekly {
            return ScheduleWeekly(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleWeekly?> {
            return arrayOfNulls(size)
        }
    }
}