package com.example.myschedule.ui.viewModule

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
     val fragmentChanging:MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
     val shouldRegisterTextBold :MutableLiveData<Boolean> by lazy{MutableLiveData<Boolean>()}
     val shouldLoginTextBold : MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>().apply { postValue(true) }}
    companion object{
        fun newInstant() = MainViewModel()
    }

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
             postValue(false)
        }
    }


    fun setIsLoading(isLoading:Boolean){
        this.isLoading.value = isLoading
    }

    fun onRegisterFragmentClick(){
        if(this.fragmentChanging?.value != 2){
            this.fragmentChanging?.value = 2
            this.shouldRegisterTextBold?.value = true
            this.shouldLoginTextBold?.value = false
        }

    }
    fun onLoginFragmentClick(){
        if(this.fragmentChanging?.value != 1){
            this.fragmentChanging?.value = 1
            this.shouldRegisterTextBold?.value = false
            this.shouldLoginTextBold?.value = true
        }
    }

}