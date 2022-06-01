package com.example.myschedule.data.repository

import com.example.myschedule.data.module.UserInfo
import com.example.myschedule.data.network.NetworkResponse
import com.example.myschedule.data.network.api.RetrofitInstance
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception

class UserInfoRepository {
    @SerializedName("posts")
    suspend fun getUserInfo(): NetworkResponse<List<List<UserInfo>>> {
        return safeApiCall {
            RetrofitInstance.api.getUserInfo()
        }
    }


    suspend fun newUserInfo(userInfo: UserInfo):NetworkResponse<String>{
        return safeApiCall {
            RetrofitInstance.api.newUserInfo(userInfo)
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> Response<T>) : NetworkResponse<T>{
        return try{
            NetworkResponse.success(apiCall.invoke())
        }catch (e: Exception){
            NetworkResponse.failure(e)
        }
    }
}