package com.example.myschedule.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myschedule.R
import com.example.myschedule.databinding.ActivityCalanderBinding
import com.example.myschedule.date.*
import com.example.myschedule.date.Date
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity(){
    private lateinit var binding:ActivityCalanderBinding
    private val disableBackPress = true
    private val metrics = resources.displayMetrics
    private val deviceWidth = metrics.widthPixels
    private val deviceHeight = metrics.heightPixels
    private val mapMonth:Map<Int,String> by lazy{
        mapOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_calander)
        binding.lifecycleOwner = this

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val calendar = getCalendar(year,month,dayOfMonth)


            var format1 = SimpleDateFormat("d")
            val startDay = format1.format(calendar.time).toInt()
            calendar.add(Calendar.DAY_OF_YEAR,6)
            val endDay = format1.format(calendar.time).toInt()
            this.setMonth(calendar.time)
            Date.dayOfMonthSelected = dayOfMonth
            Date.daysOfMonth = getDaysOfWeek(startDay,endDay)
            Date.startDate = startDay
            Date.endDate = endDay
            Date.year = year
            Date.month = month
            Date.dayOfMonth = dayOfMonth
            val intent = Intent(this,UserPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            this.finish()

        }

    }



    private fun setMonth(dateMonth:java.util.Date){
        val sf = SimpleDateFormat("MMMM")
        Date.currentMonth = sf.format(dateMonth)
    }

    /**
     * @return The object of the Calendar that contains the value of year, month, and dayOfMonth selected by the user
     * @param year The year selected by user in Calendar
     */
    private fun getCalendar(year:Int, month:Int, dayOfMonth:Int):Calendar{
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        calendar[Calendar.MONTH] = month
        calendar[Calendar.YEAR] = year
        calendar[Calendar.WEEK_OF_YEAR] = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return calendar
    }

    /**
     * @return All days of a month for a week
     * @param  startDay start day of a month in the week of a month
     * @param endDay end day of a month in the week of a month
     */
    private fun getDaysOfWeek(startDay:Int, endDay:Int):MutableList<DayOfWeek>{
        val daysOfWeek:MutableList<DayOfWeek> = ArrayList()
        var counter = 7
        var endDay = endDay
        var startDay = startDay
         if(endDay < startDay ){
             while(endDay >= 1){
                 getADayOfWeek(counter,endDay)?.let {

                     if( endDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                     daysOfWeek.add(it)
                 }
                 counter -= 1
                 endDay -= 1
             }
             var temp = 0
             while(counter >= 1){

                 getADayOfWeek(++temp,startDay)?.let {
                     if(startDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                     daysOfWeek.add(it) }
                 counter -= 1
                 startDay += 1
             }

         }else{
              while(counter >= 1){
                  getADayOfWeek(counter,endDay)?.let {
                      if( endDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                      daysOfWeek.add(it) }
                  counter -= 1
                  endDay-=1

              }
         }
        return daysOfWeek
    }

    /**
     * @return Day of week
     *
     *@param dayOfWeek Represent the day of week such as Monday, Tuesday and so on
     *@param dayOfMonth Represent the day of a month that user selected
     */
    private fun getADayOfWeek(dayOfWeek:Int, dayOfMonth:Int): DayOfWeek? {
      val res = when(dayOfWeek){
            1 -> Sunday(dayOfMonth.toString())
            7 -> Saturday(dayOfMonth.toString())
            6 -> Friday(dayOfMonth.toString())
            5-> Thursday(dayOfMonth.toString())
            4 -> Wednesday(dayOfMonth.toString())
            3 -> Tuesday(dayOfMonth.toString())
            2 -> Monday(dayOfMonth.toString())

           else -> {
               null
           }
      }
       return res
    }

    override fun onBackPressed() {
        if(!disableBackPress){
            super.onBackPressed()
        }
    }


}
