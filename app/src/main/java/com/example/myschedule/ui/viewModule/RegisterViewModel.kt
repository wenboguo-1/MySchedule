package com.example.myschedule.ui.viewModule

import android.app.Application
import android.app.ProgressDialog
import android.util.Log
import androidx.lifecycle.*
import com.example.myschedule.data.module.User
import com.example.myschedule.data.repository.UserRepository
import com.example.myschedule.data.room.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel( application:Application) : AndroidViewModel (application) {

     val nameErrorMessage =  MutableLiveData<String>()
     val emailErrorMessage= MutableLiveData<String>()
     val passwordErrorMessage =  MutableLiveData<String>()
     val user:MutableLiveData<User> =  MutableLiveData<User>()
     val userRegisterPassword = MutableLiveData<String>()
     val userRegisterEmail = MutableLiveData<String>()
     val userRegisterName = MutableLiveData<String>()
     private val areAllUserInputsValid = MutableLiveData<Boolean>()
     private val isUserExist = MutableLiveData<Boolean>()
     val _isUserExist:LiveData<Boolean> = isUserExist
     val _areAllUserInputsValid:LiveData<Boolean> = areAllUserInputsValid
     private val userRepository: UserRepository

     init {
          nameErrorMessage.value = ""
          emailErrorMessage.value = ""
          passwordErrorMessage.value = ""
          val userDao = UserDatabase.getDatabase(application).userDao()
          userRepository = UserRepository(userDao)
     }

     fun addUser(user:User){
          viewModelScope.launch(Dispatchers.IO){
                 val res = userRepository.isNameOrEmailRegister(user.userName!!,user.userEmail!!)

                 isUserExist.postValue(res)
                 userRepository.addNewUser(user)
          }
     }


     /*
          Validate user's input
      */
     fun onRegisterButtonClicked() {

          if( nameErrorMessage.value?.isEmpty()!! and emailErrorMessage.value?.isEmpty()!! and passwordErrorMessage.value?.isEmpty()!!)
            when{
                 userRegisterName.value == null -> {
                      areAllUserInputsValid.value = false
                      userRegisterName.postValue("")
                 }
                 userRegisterEmail.value == null -> {
                      areAllUserInputsValid.value = false
                      emailErrorMessage.value = "Can not be empty"
                 }
                 userRegisterPassword.value == null -> {
                      areAllUserInputsValid.value = false
                      passwordErrorMessage.value = "Can not be empty"
                 }
                 else -> areAllUserInputsValid.value = true

            }


     }



}

