package com.example.myschedule.util

interface OnRowItemUpdateInterface {

    fun onIsPaidClick(position: Int)
    fun onIsAbsentClick(position: Int)
    fun onUpdatedClick(position:Int)
    fun onDeleteClick(position:Int)
    fun onMoveToClick(position:Int)
}