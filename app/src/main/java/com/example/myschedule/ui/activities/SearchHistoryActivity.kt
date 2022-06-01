package com.example.myschedule.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.data.module.Schedule
import com.example.myschedule.data.module.SearchResult
import com.example.myschedule.ui.adapters.SearchResultAdapter
import com.example.myschedule.ui.viewModule.*
import com.google.android.material.textfield.TextInputLayout

class SearchHistoryActivity : AppCompatActivity() {
    private val NUM_OF_HOURS = "NUM_OF_HOURS"
    private val NUM_OF_ABSENTS = "NUM_OF_ABSENTS"
    private val NUM_OF_UNPAID = "NUM_OF_UNPAID"
    private val SEARCH_INFO_LIST = "SEARCH_INFO_LIST"
    private val FROM_DATE = "FROM_DATE"
    private val TO_DATE = "TO_DATE"
    private lateinit var searchResult:SearchResult
    private lateinit var viewModel:SearchHistoryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private lateinit var hourDetail:TextView
    private lateinit var absentDetail:TextView
    private lateinit var dateDetail:TextView
    private val myIntent:Intent by lazy { intent }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)

        viewModel = ViewModelProvider(this)[SearchHistoryViewModel::class.java]
        val dropdown = findViewById<AutoCompleteTextView>(R.id.menu1)
        val items = arrayOf("All", "Unpaid", "Absent")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        searchResult = SearchResult(myIntent.getParcelableArrayListExtra<Schedule>(SEARCH_INFO_LIST) as ArrayList<Schedule>,myIntent.getDoubleExtra(NUM_OF_HOURS,0.0),
        myIntent.getDoubleExtra(NUM_OF_UNPAID, 0.0),myIntent.getDoubleExtra(NUM_OF_ABSENTS,0.0))
        viewModel.setList(searchResult.searchResultList)
        this.recyclerView = findViewById(R.id.searchResultRecyclerView)
        this.hourDetail = findViewById(R.id.hourDetail)
        this.absentDetail = findViewById(R.id.absentDetail)
        this.dateDetail = findViewById(R.id.dateDetail)
        this.adapter = SearchResultAdapter(searchResult.searchResultList)
        this.recyclerView.adapter = this.adapter
        this.hourDetail.text = "Total hours: ${searchResult.totalHour}"
        this.dateDetail.text = "${intent.getStringExtra(FROM_DATE)} - ${intent.getStringExtra(TO_DATE)}"
        this.absentDetail.text = "Number of Absents: ${searchResult.numOfAbsent}"
        viewModel.searchResultList.observe(this){
            when(viewModel.getSearchByOption()){
                is All -> {
                    this.adapter.setList(it)
                }
                is Unpaid -> this.adapter.setList(it.filter { element ->
                      element.isPaid

                } as MutableList<Schedule>)
                is Absent -> this.adapter.setList(it.filter { element ->
                    element.isAbsent
                } as MutableList<Schedule>)
            }
        }

        dropdown.setAdapter(arrayAdapter)

        dropdown.setOnItemClickListener { parent, view, position, id ->
              when(position){
                  0 ->{
                      viewModel.setSearchByOption(All)
                  }
                  1 -> {
                      viewModel.setSearchByOption(Unpaid)

                  }
                  else -> {
                      viewModel.setSearchByOption(Absent)
                  }
              }

            viewModel.setList(viewModel.searchResultList.value!!)
        }
    }
}