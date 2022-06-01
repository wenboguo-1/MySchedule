package com.example.myschedule.data.module

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_weekly_update",
      indices = [Index("timeStampWeekly")]
)
data class ScheduleWeeklyUpdated(
    override var id:Int,
    override var userName:String?,
    override var studentName:String?,
    override var routine:String?,
    override var year:String?,
    override var month:String?,
    override var dayOfMonth:String?,
    override var startTime:String?,
    override var endTime:String?,
    override var note:String?,
    override var isPaid:Boolean,
    override var isAbsent:Boolean,
    override var timeStampWeekly:Long,
    override var timeStampDaily:Long,
    override var position:Int
      ):Schedule(id,userName,studentName,routine,year,month,dayOfMonth,startTime,endTime,note, isPaid, isAbsent, timeStampWeekly, timeStampDaily,position),Parcelable{

             @PrimaryKey(autoGenerate = true)
             var scheduleUpdatedId:Int = 0


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
      ) {
          scheduleUpdatedId = parcel.readInt()
      }

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
            parcel.writeInt(id)
            parcel.writeInt(position)
      }

      override fun describeContents(): Int {
            return 0
      }

      companion object CREATOR : Parcelable.Creator<ScheduleWeeklyUpdated> {
            override fun createFromParcel(parcel: Parcel): ScheduleWeeklyUpdated {
                  return ScheduleWeeklyUpdated(parcel)
            }

            override fun newArray(size: Int): Array<ScheduleWeeklyUpdated?> {
                  return arrayOfNulls(size)
            }
      }


}
