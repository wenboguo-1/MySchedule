package com.example.myschedule.util

interface ItemTouchHelperAdapter {
      fun onItemMoved(from:Int,to:Int)
      fun onItemSwiped(position:Int)
}