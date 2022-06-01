package com.example.myschedule.ui.bindingAdapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.google.android.material.textfield.TextInputLayout

/*
    Show an error for Name text input layout
 */
@BindingAdapter("nameErrorText")
fun TextInputLayout?.showNameErrorMessage( errorMessage:String?){
    this?.error = errorMessage
}

@BindingAdapter("emailErrorMessage")
fun TextInputLayout?.setEmailErrorMessage(message:String?){
    this?.error =  message
}
@BindingAdapter("nameErrorTextLogin")
fun TextInputLayout?.setLoginEmailErrorMessage(message:String?){
    this?.error = message
}
@BindingAdapter("passwordErrorMessage")
fun TextInputLayout?.setPasswordMessage(message:String?){
    this?.error = message
}
@BindingAdapter("passwordErrorMessageLogin")
fun TextInputLayout?.setLoginPasswordErrorMessage(message: String?){
    this?.error = message
}
@BindingAdapter("shouldRegisterTextBeBold")
fun TextView?.changeRegisterTextView(isBold:Boolean){
    this?.setTypeface(this?.typeface, if(isBold) Typeface.BOLD else Typeface.NORMAL)
    this?.setTextColor(Color.parseColor(if(isBold) "#000000" else "#FBFBFB") );
}

@BindingAdapter("shouldLoginTextBeBold")
fun TextView?.changeLoginTextView(isBold:Boolean){
    this?.setTypeface(this?.typeface, if(isBold) Typeface.BOLD else Typeface.NORMAL)
    this?.setTextColor(Color.parseColor(if(isBold) "#000000" else "#FBFBFB") );
}







