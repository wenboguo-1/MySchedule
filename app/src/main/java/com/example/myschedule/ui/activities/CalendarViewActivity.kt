package com.example.myschedule.ui.activities


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.data.module.CalendarScheduleInfo
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.databinding.ActivityCalendarViewBinding
import com.example.myschedule.date.*
import com.example.myschedule.date.Date
import com.example.myschedule.ui.adapters.CalendarAdapter
import com.example.myschedule.ui.viewModule.CalendarViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class  CalendarViewActivity : AppCompatActivity(),CalendarAdapter.OnItemListener,CalendarAdapter.OnProgressDialogDone{

    private var isSign = false
    private val SIGNIN_CODE = 940511
    private var localDate: LocalDate = LocalDate.now()
    private lateinit var recyclerView: RecyclerView
    private lateinit var calenderScheduleInfoList: MutableList<CalendarScheduleInfo>
    private lateinit var binding: ActivityCalendarViewBinding
    private lateinit var viewModel: CalendarViewModel
    private var deviceHeight: Int? = null
    private var deviceWidth: Int? = null
    private var studentInfoList: MutableList<String>? = null
    private var userAdapter: ArrayAdapter<String>? = null
    private var fromDate = ""
    private var toDate = ""
    private var builder: MaterialDatePicker.Builder<Pair<Long, Long>>? = null
    private var isUserSign = false
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(
            this,
            ProgressDialog.THEME_HOLO_DARK
        )
    }


    override fun onStart() {
         super.onStart()
        val lastSign =  GoogleSignIn.getLastSignedInAccount(this)
        lastSign?.let {
            this.isSign = true
        }

    }

    companion object {
        fun newInstance() = CalendarViewActivity()
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            this.showDialogDialog()
            val metrics = this.resources.displayMetrics
            val deviceWidth = metrics.widthPixels
            val deviceHeight = metrics.heightPixels
            this.deviceHeight = deviceHeight
            this.deviceWidth = deviceWidth
            binding = ActivityCalendarViewBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.calenderView.monthAndYear.text =
                "${convertMonth(this.localDate.monthValue)} ${localDate.year}"
            binding.calenderView.goPreMonth.textSize = (deviceHeight / 45).toFloat()
            binding.calenderView.goNextMonth.textSize = (deviceHeight / 45).toFloat()


            binding.sliderView.scheduleListName.setOnItemClickListener { _, _, position, _ ->

                builder = MaterialDatePicker.Builder.dateRangePicker()
                val picker = builder?.build()!!
                picker.show(supportFragmentManager, "Select date range")
                picker.addOnPositiveButtonClickListener {
                    showDialogDialog()
                    convertTimeToStringFormat(it.first, it.second)
                    viewModel.findScheduleInfoDetail(it.first, it.second, position)
                    this.binding.slideMenu.switchMenu()
                }


            }

            binding.sliderView.searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.sliderView.searchView.clearFocus()
                    if (studentInfoList?.contains(query)!!) {
                        userAdapter?.filter?.filter(query)

                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    userAdapter?.filter?.filter(newText)
                    return false
                }


            })

            binding.calenderView.sliderButton.setOnClickListener {
                binding.slideMenu.switchMenu()
            }



            binding.calenderView.dropdownMenu.setOnClickListener {

                PopupMenu(this, it).apply {
                    menuInflater.inflate(R.menu.calendar_menu, this.menu)
                    this.show()
                    val popupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                    popupMenu.isAccessible = true
                    val menu = popupMenu.get(this)
                    menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(menu, true)
                    this.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.go_to -> {
                                val picker =
                                    MaterialDatePicker.Builder.datePicker() as MaterialDatePicker.Builder<*>
                                picker.setTitleText("Select date")
                                val builder = picker.build()
                                builder.addOnPositiveButtonClickListener {
                                    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                                    utc.timeInMillis = it as Long
                                    val month = SimpleDateFormat("M").format(utc.time).toInt()
                                    val dayOfMonth =
                                        SimpleDateFormat("dd").format(utc.time).toInt() + 1
                                    val year = SimpleDateFormat("yyyy").format(utc.time).toInt()
                                    changeMonthWhenPickerIsClicked(year, month, dayOfMonth)
                                    builder.dismiss()

                                }
                                builder.show(supportFragmentManager, "Asdsd")
                            }
                            R.id.search_month -> {
                                showDialogDialog()
                                fromDate =
                                    "${convertMonth1(localDate.monthValue)}/01/${localDate.year}"
                                toDate =
                                    "${convertMonth1(localDate.monthValue)}/${localDate.lengthOfMonth()}/${localDate.year}"
                                viewModel.fetchMonthlyScheduleRecord(
                                    localDate.lengthOfMonth(),
                                    localDate.monthValue,
                                    localDate.year
                                )
                            }
                            R.id.clear_schedules -> {
                                clearScheduleDialog()
                                studentInfoList = mutableListOf()
                            }
                            R.id.today_schedules -> {
                                showDialogDialog()
                                localDate = LocalDate.now()
                                binding.calenderView.monthAndYear.text =
                                    "${convertMonth(localDate.monthValue)} ${localDate.year}"
                                viewModel.setDate(
                                    getDaysInMonth(),
                                    localDate.year,
                                    localDate.monthValue
                                )
                                viewModel.fetchScheduleList()

                            }
                            else -> {
                                showDialogDialog()
                                changeCurrentMonth(0)
                            }
                        }
                        true
                    }
                }


            }


            recyclerView = binding.calenderView.calendarViewRecyclerView

            viewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
            viewModel.setDate(this.getDaysInMonth(), localDate.year, localDate.monthValue)
            viewModel.fetchScheduleList()
            viewModel.scheduleNameList.observe(this) {
                this.
                studentInfoList= it.map { v -> v.studentName } as MutableList<String>
                this.userAdapter = ArrayAdapter(
                    this, android.R.layout.simple_list_item_1,
                    this.studentInfoList!!
                )
                binding.sliderView.scheduleListName.adapter = userAdapter

            }

            binding.calenderView.goNextMonth.setOnClickListener {
                showDialogDialog()
                lifecycleScope.launch(Dispatchers.Main) {
                    changeCurrentMonth(1)
                }

            }

            binding.calenderView.goPreMonth.setOnClickListener {
                showDialogDialog()
                lifecycleScope.launch(Dispatchers.Main) {
                    changeCurrentMonth(-1)
                }
            }


            viewModel.listOfScheduleList.observe(this) {


                   calenderScheduleInfoList = mutableListOf()
                   val firstOfMonth = localDate.withDayOfMonth(1)
                   val daysInMonth = getDaysInMonth()
                   val dayOfWeek =
                       if (firstOfMonth.dayOfWeek.value == 7) 0 else firstOfMonth.dayOfWeek.value
                   for (i in 1..42) {
                       if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                           calenderScheduleInfoList.add(CalendarScheduleInfo("", null))
                       } else {
                           calenderScheduleInfoList.add(
                               (CalendarScheduleInfo(
                                   (i - dayOfWeek).toString(),
                                   it[i - dayOfWeek - 1]
                               ))
                           )
                       }
                   }
                   setMonthView()

            }


            viewModel.searchResultList.observe(this) {

                if (it.searchResultList.isEmpty()) {
                    Toast.makeText(this, "No search result found", Toast.LENGTH_LONG).show()
                    dismissDialog()
                } else {
                    val intent = Intent(this, SearchHistoryActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        "SEARCH_INFO_LIST",
                        it.searchResultList as ArrayList<Schedule>
                    )
                    intent.putExtra("NUM_OF_HOURS", it.totalHour)
                    intent.putExtra("NUM_OF_ABSENTS", it.numOfAbsent)
                    intent.putExtra("NUM_OF_UNPAID", it.numOfUnpaid)
                    intent.putExtra("FROM_DATE", fromDate)
                    intent.putExtra("TO_DATE", toDate)
                    startActivity(intent)
                    dismissDialog()
                }
            }


    }

    @SuppressLint("SetTextI18n")
    private fun changeCurrentMonth(changeInMonth: Int) {
        this.localDate =
            if (changeInMonth > 0) this.localDate.plusMonths(1) else if(changeInMonth < 0) this.localDate.minusMonths(1) else this.localDate
        this.binding.calenderView.monthAndYear.text =
            "${convertMonth(this.localDate.monthValue)} ${localDate.year}"
        viewModel.setDate(getDaysInMonth(), this.localDate.year, this.localDate.monthValue)
        viewModel.fetchScheduleList()
    }

    private fun changeMonthWhenPickerIsClicked(year: Int, month: Int, dayOfMonth: Int) {
        this.showDialogDialog()
        this.localDate = this.getNewLocalDate(year, month, dayOfMonth)
        this.binding.calenderView.monthAndYear.text = "${convertMonth(month)} ${localDate.year}"

        viewModel.setDate(getDaysInMonth(), year, month)
        viewModel.fetchScheduleList()

    }

    private fun getDaysInMonth(): Int {
        return YearMonth.from(localDate).lengthOfMonth()
    }

    private fun showDialogDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setTitle("Wait")
        progressDialog.setMessage("I am trying, please waite")
        progressDialog.show()
        progressDialog.setCancelable(false)
    }

    private fun setMonthView() {

        val calendarAdapter = CalendarAdapter(
            this.calenderScheduleInfoList,
            this,
            this,
            this.deviceHeight!!,
            this.deviceWidth!!,
            this
        )
        val layoutManager = GridLayoutManager(applicationContext, 7)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = calendarAdapter


    }


    private fun getNewLocalDate(year: Int, month: Int, dayOfMonth: Int): LocalDate {

        return LocalDate.of(year, month, dayOfMonth)
    }

    override fun onItemClicked(position: Int, dayOfMonth: String) {
       // if(isSign) {
            if (dayOfMonth.isNotEmpty()) {
                val calendar =
                    this.getCalendar(localDate.year, localDate.monthValue - 1, dayOfMonth.toInt())
                var format1 = SimpleDateFormat("d")
                val startDay = format1.format(calendar.time).toInt()
                Date.startDate = startDay
                calendar.add(Calendar.DAY_OF_YEAR, 6)
                val endDay = format1.format(calendar.time).toInt()
                this.setMonth(calendar.time)
                Date.dayOfMonthSelected = dayOfMonth.toInt()
                Date.daysOfMonth = getDaysOfWeek(startDay, endDay)
                Date.endDate = endDay
                Date.year = localDate.year
                Date.month = localDate.monthValue - 1
                Date.dayOfMonth = dayOfMonth.toInt()
                val intent = Intent(this, UserPageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
       // }//else{
         //   gmail_sign()
      //  }
    }

    /**
     * @return The object of the Calendar that contains the value of year, month, and dayOfMonth selected by the user
     * @param year The year selected by user in Calendar
     */
    private fun getCalendar(year: Int, month: Int, dayOfMonth: Int): Calendar {
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
    private fun getDaysOfWeek(startDay: Int, endDay: Int): MutableList<DayOfWeek> {
        val daysOfWeek: MutableList<DayOfWeek> = ArrayList()
        var counter = 7
        var endDay = endDay
        var startDay = startDay
        if (endDay < startDay) {
            while (endDay >= 1) {
                getADayOfWeek(counter, endDay)?.let {

                    if (endDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                    daysOfWeek.add(it)
                }
                counter -= 1
                endDay -= 1
            }
            var temp = 0
            while (counter >= 1) {

                getADayOfWeek(++temp, startDay)?.let {
                    if (startDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                    daysOfWeek.add(it)
                }
                counter -= 1
                startDay += 1
            }

        } else {
            while (counter >= 1) {
                getADayOfWeek(counter, endDay)?.let {
                    if (endDay == Date.dayOfMonthSelected) Date.currentDayOfWeek = it
                    daysOfWeek.add(it)
                }
                counter -= 1
                endDay -= 1

            }
        }
        return daysOfWeek
    }

    private fun getADayOfWeek(dayOfWeek: Int, dayOfMonth: Int): DayOfWeek? {
        val res = when (dayOfWeek) {
            1 -> Sunday(dayOfMonth.toString())
            7 -> Saturday(dayOfMonth.toString())
            6 -> Friday(dayOfMonth.toString())
            5 -> Thursday(dayOfMonth.toString())
            4 -> Wednesday(dayOfMonth.toString())
            3 -> Tuesday(dayOfMonth.toString())
            2 -> Monday(dayOfMonth.toString())

            else -> {
                null
            }
        }
        return res
    }

    private fun setMonth(dateMonth: java.util.Date) {
        val sf = SimpleDateFormat("MMMM")
        Date.currentMonth = sf.format(dateMonth)
    }

    private fun convertMonth(month: Int): String {
        val res = when (month) {
            1 -> "Jan."
            2 -> "Feb."
            3 -> "Mar."
            4 -> "Apr."
            5 -> "May."
            6 -> "June."
            7 -> "July."
            8 -> "Aug."
            9 -> "Sept."
            10 -> "Oct."
            11 -> "Nov."
            else -> "Dec."

        }
        return res
    }

    private fun convertMonth1(month: Int): String {
        val res = when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "Aug"
            9 -> "Sept"
            10 -> "Oct"
            11 -> "Nov"
            else -> "Dec"

        }
        return res
    }

    override fun dismissDialog() {
        this.progressDialog.dismiss()
    }

    private fun clearScheduleDialog() {
        this?.let {
            AlertDialog.Builder(it).apply {
                setMessage("NOTE: All schedules will not be recovered when they are cleared! Clear anyway").setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    showDialogDialog()

                    viewModel.clearAllSchedule()
                }.setNegativeButton("No") { _, _ ->
                }
                show()
            }

        }
    }

    private fun convertTimeToStringFormat(fromTimeStamp: Long, toTimeStamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fromTimeStamp + (24 * 60 * 60 * 1000)
        this.fromDate = SimpleDateFormat("MMM/dd/yyyy").format(calendar.time)
        calendar.timeInMillis = toTimeStamp + (24 * 60 * 60 * 1000)
        this.toDate = SimpleDateFormat("MMM/dd/yyyy").format(calendar.time)
    }

    /**
     * Use gmail api to allow user to sign in with Gmail
     */
    private fun gmail_sign(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()



        val mGoogleSignInClient = this?.let {

            GoogleSignIn.getClient(it, gso) }
        val signInIntent: Intent? = mGoogleSignInClient?.let { it.signInIntent }
        startActivityForResult(signInIntent,SIGNIN_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGNIN_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
                Log.e("1234","1234")
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("0k","${account.idToken}")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }
}




