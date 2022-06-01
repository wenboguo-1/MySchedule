package com.example.myschedule.Message

sealed class Message {
     companion object{
         val NAME_ERROR_MESSAGE = "Your account name can not be empty"
         val EMAIL_ERROR_MESSAGE = "Email is not valid"
         val EMAIL_ERROR_MESSAGE_EMPRY = "Email can not be empty"
         val PASSWORD_ERROR_MESSAGE_LENGTH = "must be at least 7 characters"
         val PASSWERO_ERROR_MESSAGE_EMPTY = "Your password can not be empty"
     }

}