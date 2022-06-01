package com.example.myschedule.ui.fragments
import com.example.myschedule.R
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myschedule.databinding.LoginFragment2Binding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.myschedule.Message.Message
import com.example.myschedule.ui.activities.CalendarActivity
import com.example.myschedule.ui.activities.MainActivity
import com.example.myschedule.ui.viewModule.LoginViewModel
import com.example.myschedule.ui.viewModule.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class LoginFragment : Fragment() {
    private lateinit var _binding: LoginFragment2Binding
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private lateinit var   progressDialog :ProgressDialog
    companion object {
        fun newInstance() = LoginFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        _binding = DataBindingUtil.inflate(inflater,R.layout.login_fragment2,container,false)
        _binding.lifecycleOwner = requireActivity()
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        _binding.loginViewModel = viewModel
        viewModel.userEmailInput.observe(viewLifecycleOwner, Observer {
              when{
                  it.isEmpty() -> viewModel.setEmailErrorMessage(Message.EMAIL_ERROR_MESSAGE_EMPRY)
                  !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() -> viewModel.setEmailErrorMessage(Message.EMAIL_ERROR_MESSAGE)
                  else -> viewModel.setEmailErrorMessage(null)

               }
        })
        viewModel.userPasswordInput.observe(viewLifecycleOwner, Observer {
             val temp = viewModel.passwordErrorMessage
             when{
                 it.isEmpty() -> temp.value = Message.PASSWERO_ERROR_MESSAGE_EMPTY
                 else -> temp.value = null
             }
        })
        viewModel._isUserExists.observe(viewLifecycleOwner, Observer {
             if(it){
                startActivity(Intent(requireContext(),CalendarActivity::class.java))
                 requireActivity().finish()
             }else{
                 Toast.makeText(requireActivity(),"Login is failed", Toast.LENGTH_LONG).show()
             }
        })

        viewModel.getAllUsers.observe(requireActivity() , Observer {

            for( i in it.indices){

                Log.e("User info", "${it[i].userName + " " + it[i].userEmail + " " + it[i].userPassword}")
            }
        })


    }


    /**
     * Use gmail api to allow user to sign in with Gmail
     */
    private fun gmail_sign(){
       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestEmail()
           .build()
       val mGoogleSignInClient = this.activity?.let { GoogleSignIn.getClient(it, gso) }
       val signInIntent: Intent? = mGoogleSignInClient?.let { it.signInIntent }
       startActivityForResult(signInIntent,123456)

   }



}