package com.example.myschedule.data.module

import android.os.Parcel
import android.os.Parcelable

open class Schedule(
    open var id:Int,
    open var userName:String?,
    open var studentName:String?,
    open var routine:String?,
    open var year:String?,
    open var month:String?,
    open var dayOfMonth:String?,
    open var startTime:String?,
    open var endTime:String?,
    open var note:String?,
    open var isPaid:Boolean = false,
    open var isAbsent:Boolean = false,
    open var timeStampWeekly:Long,
    open var timeStampDaily:Long,
    open var position:Int):Parcelable {
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
      parcel.writeInt(position)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<Schedule> {
      override fun createFromParcel(parcel: Parcel): Schedule {
         return Schedule(parcel)
      }

      override fun newArray(size: Int): Array<Schedule?> {
         return arrayOfNulls(size)
      }
   }
}