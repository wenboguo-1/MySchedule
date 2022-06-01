package com.example.myschedule.ui.fragments
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import com.example.myschedule.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import com.example.myschedule.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import com.example.myschedule.databinding.LoginFragment2Binding
import com.example.myschedule.databinding.RegisterFragment2Binding
import com.example.myschedule.ui.activities.MainActivity
import com.example.myschedule.ui.viewModule.MainViewModel
import com.example.myschedule.ui.viewModule.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.myschedule.data.module.User
import com.example.myschedule.ui.activities.CalendarActivity
import com.example.myschedule.ui.activities.CalendarViewActivity

class RegisterFragment : Fragment() {

    private lateinit var binding: RegisterFragment2Binding
    private lateinit var progressDialog: ProgressDialog
    companion object {
        fun newInstance() = RegisterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  DataBindingUtil.inflate(inflater,R.layout.register_fragment2,container,false)
        binding.lifecycleOwner = requireActivity()
       return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.registerViewModel = viewModel

        binding.registerButt.setOnClickListener{
            startActivity(Intent(requireActivity(),CalendarViewActivity::class.java))
            requireActivity().finish()
        }

         viewModel.userRegisterName.observe(viewLifecycleOwner, Observer {
             viewModel.nameErrorMessage.value = if(it.isEmpty()) "Your name can not be empty" else ""
        })

        viewModel.userRegisterPassword.observe(viewLifecycleOwner, Observer {
            when {
                it.isEmpty() -> viewModel.passwordErrorMessage.value =  "Your password can not be empty"
                it.length <= 6 -> viewModel.passwordErrorMessage.value = "At least 7 characters"
                else -> viewModel.passwordErrorMessage.value = ""
            }
        })
        viewModel.userRegisterEmail.observe(viewLifecycleOwner, Observer {
             val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
             viewModel.emailErrorMessage.value = if(isEmailValid) "" else "Your email address is not the valid"
        })
        viewModel._areAllUserInputsValid.observe(viewLifecycleOwner, Observer {

           if(it) {
               progressDialog = ProgressDialog(context,R.style.MyAlertDialogStyle)
               progressDialog.setCanceledOnTouchOutside(false)
               progressDialog.show()
               val userName = viewModel.userRegisterName.value
               val userEmail = viewModel.userRegisterEmail.value
               val userPassword = viewModel.userRegisterPassword.value
               val user = User(userName!!, userEmail!!, userPassword)
               viewModel.addUser(user)
           }
        })

        viewModel._isUserExist.observe(viewLifecycleOwner, Observer {
               progressDialog.dismiss()
               if(it){
                   Log.e(null,"2345")
                   Toast.makeText(requireActivity(),"User name or email has been registered",Toast.LENGTH_LONG).show()
               }else{
                   Log.e(null,"45678")
                   Toast.makeText(requireActivity(),"Register has been Successful",Toast.LENGTH_LONG).show()
               }
        })
    }
}