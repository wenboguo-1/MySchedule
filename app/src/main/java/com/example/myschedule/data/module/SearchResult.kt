package com.example.myschedule.data.module

data class SearchResult(val searchResultList:MutableList<Schedule>,val totalHour:Double,val numOfUnpaid:Double,val numOfAbsent:Double)