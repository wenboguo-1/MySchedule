package com.example.myschedule.ui.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myschedule.databinding.ActivityMainBinding
import com.example.myschedule.ui.fragments.LoginFragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myschedule.R
import com.example.myschedule.ui.fragments.RegisterFragment
import com.example.myschedule.ui.viewModule.LoginViewModel
import com.example.myschedule.ui.viewModule.MainViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel : MainViewModel
  //private lateinit var bottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>


    companion object{
        fun newInstance() = MainActivity()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.lifecycleOwner = this


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_main,LoginFragment()).addToBackStack(null).commit()

        }
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel = mainViewModel
        /*
              Do something when user's input validation was passed
         */

        mainViewModel.fragmentChanging.observe(this, Observer {
            when(it){
                1 -> {
                    replaceFragment(LoginFragment.newInstance())

                }
                2 -> {
                    replaceFragment(RegisterFragment.newInstance())
                }
            }
        })
    }
    /*
        Fragment transaction
     */
    private fun replaceFragment(fragment:Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main,fragment);
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}