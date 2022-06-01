package com.example.myschedule.ui.viewModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myschedule.data.module.Schedule

class SearchHistoryViewModel:ViewModel() {
    private var searchBy:SearchBy = All
    private val _searchResultList:MutableLiveData<MutableList<Schedule>> = MutableLiveData()
    val searchResultList:LiveData<MutableList<Schedule>> = _searchResultList


    fun setList(list:MutableList<Schedule>){
        this._searchResultList.postValue(list)
    }

    fun getSearchByOption():SearchBy{
        return searchBy
    }
    fun setSearchByOption(option:SearchBy){
         this.searchBy = option
    }
}


sealed class SearchBy
object All : SearchBy()
object  Absent:SearchBy()
object Unpaid:SearchBy()