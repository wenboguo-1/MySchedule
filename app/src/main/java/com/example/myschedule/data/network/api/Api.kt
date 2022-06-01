package com.example.myschedule.data.network.api

import android.icu.text.IDNA
import com.example.myschedule.data.module.UserInfo
import com.example.myschedule.data.network.NetworkResponse

import retrofit2.http.GET

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.Exception

interface Api {

    @GET("posts")
    suspend fun getUserInfo(): Response<List<List<UserInfo>>>

    @POST("posts")
    suspend fun newUserInfo(@Body userInfo: UserInfo):Response<String>




}