package com.example.myschedule.data.repository

import androidx.lifecycle.LiveData
import com.example.myschedule.data.module.User
import com.example.myschedule.data.room.UserDao

class UserRepository(private val userDao: UserDao) {
     suspend fun addNewUser(user:User){
          userDao.addUser(user)
     }
     suspend fun isUserExist(userEmail:String, userPassword:String):Boolean {
          return userDao.isUserExist(userEmail,userPassword)
     }
     suspend fun isNameOrEmailRegister(userName:String, userEmail:String):Boolean{
          return userDao.isNameOrEmailRegistered(userName,userEmail)
     }
     suspend fun isUserAuthenticated(userEmail: String,userPassword: String):Boolean{
          return userDao.isUserAuthenticated(userEmail,userPassword)
     }
     suspend fun getAllUsers():List<User>{
           return userDao.getAllUsers()
     }
}