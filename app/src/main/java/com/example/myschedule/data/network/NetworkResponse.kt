package com.example.myschedule.data.network

import retrofit2.Response
import java.lang.Exception


data class NetworkResponse <T> (
    val status:Status,
    val response:Response<T>?,
    val exception: Exception?

    ){

    companion object{
        fun <T> success(data:Response<T>): NetworkResponse<T>{
            return NetworkResponse(status = Status.Success, response = data, exception = null)
        }
        fun <T> failure(exception: Exception): NetworkResponse<T>{
            return NetworkResponse(status = Status.Failure, response =null, exception = exception)
        }
    }
    sealed class Status{
        object Success: Status()
        object Failure : Status()
    }
    val failed : Boolean get() = this.status == Status.Failure
    val isSuccessful:Boolean get() = !failed && this.response?.isSuccessful == true
    val body: T get() =  this.response!!.body()!!
}