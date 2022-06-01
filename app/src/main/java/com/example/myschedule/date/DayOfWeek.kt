package com.example.myschedule.date

sealed class DayOfWeek
data class Monday(val day:String) :DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Tuesday(val day:String) : DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Wednesday(val day:String) :DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Thursday(val day:String) :DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Friday(val day:String) :DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Saturday(val day:String):DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
data class Sunday(val day:String):DayOfWeek(){
    override fun toString(): String {
        return day
    }
}
