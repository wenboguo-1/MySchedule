package com.example.myschedule.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myschedule.data.module.User

@Dao
interface UserDao {

      @Insert(onConflict = OnConflictStrategy.IGNORE)
      suspend fun addUser(user:User)

      @Query("Select exists(Select * from user_table where userEmail = :userEmail and userPassword = :userPassword)")
      suspend fun isUserExist(userEmail:String, userPassword:String):Boolean

      @Query("Select exists(Select * from user_table where userEmail = :email or userName = :name )")
      suspend fun isNameOrEmailRegistered(name:String,email:String):Boolean

      @Query("Select exists(Select * from user_table where userEmail = :email and userPassword = :password)")
      suspend fun isUserAuthenticated(email:String,password:String):Boolean

      @Query("Select * from user_table")
      suspend fun getAllUsers():List<User>
}