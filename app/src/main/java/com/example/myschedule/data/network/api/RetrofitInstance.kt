package com.example.myschedule.data.network.api

import com.example.myschedule.data.module.UserInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
         Retrofit.Builder().baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api:Api by lazy {
        retrofit.create(Api::class.java)
    }
}
