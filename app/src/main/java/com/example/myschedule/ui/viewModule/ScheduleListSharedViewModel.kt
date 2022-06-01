package com.example.myschedule.ui.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myschedule.data.module.ScheduleWeekly

class ScheduleListSharedViewModel:ViewModel() {
      private val mondayScheduleTestList:MutableLiveData<List<ScheduleWeekly>> by lazy { MutableLiveData<List<ScheduleWeekly>>() }


}