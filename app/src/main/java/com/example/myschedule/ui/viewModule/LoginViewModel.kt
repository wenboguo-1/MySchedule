package com.example.myschedule.ui.viewModule

import android.app.Application
import android.util.Log
import android.util.MutableDouble
import androidx.lifecycle.*
import com.example.myschedule.data.module.User
import com.example.myschedule.data.repository.UserRepository
import com.example.myschedule.data.room.UserDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val userEmailInput = MutableLiveData<String>()
    val emailErrorMessage = MutableLiveData<String>()
    val passwordErrorMessage = MutableLiveData<String>()
    val userPasswordInput = MutableLiveData<String>()
    private val userRepository:UserRepository
    private val isUserExists = MutableLiveData<Boolean>()
    var getAllUsers = MutableLiveData<List<User>>()
    val _isUserExists:LiveData<Boolean> = isUserExists

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }
    fun onLoginClicked(){

            when{
                emailErrorMessage.value == null && userEmailInput.value != null && userPasswordInput.value != null && passwordErrorMessage.value == null -> {
                    viewModelScope.launch {
                        val userEmail = userEmailInput.value
                        val userPassword = userPasswordInput.value
                        isUserExists.postValue(userRepository.isUserAuthenticated(userEmail!!,userPassword!!))
                    }
                }
                userEmailInput.value == null || userEmailInput.value!!.isEmpty() || emailErrorMessage.value !=  null -> userEmailInput.value = ""
                userPasswordInput.value == null || userPasswordInput.value!!.isEmpty() == null || passwordErrorMessage.value !=null -> userPasswordInput.value = ""
            }
     }




    fun setEmailErrorMessage( message:String?){
        this.emailErrorMessage.value = message
    }

}