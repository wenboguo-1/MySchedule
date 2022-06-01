package com.example.myschedule.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi

import com.example.myschedule.R
import com.example.myschedule.databinding.ActivityUserPageBinding
import com.example.myschedule.ui.fragments.MyScheduleFragment


class UserPageActivity : AppCompatActivity() {

    private val disableBackPress = true
    private lateinit var binding:ActivityUserPageBinding
    private val myScheduleFragment:MyScheduleFragment by lazy {

        MyScheduleFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?: kotlin.run {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,myScheduleFragment).commit()
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBackPressed() {
        if(!disableBackPress){
            super.onBackPressed()
        }
    }
}