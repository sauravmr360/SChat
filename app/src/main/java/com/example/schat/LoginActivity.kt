package com.example.schat

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    private lateinit var countrycode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberEt.addTextChangedListener {
            if(!(it.isNullOrEmpty()||it.length<10))
            {
                nextBtn.isEnabled=true
            }
        }

        nextBtn.setOnClickListener {
          countrycode=ccpicker.selectedCountryCodeWithPlus
            phoneNumber=countrycode+phoneNumberEt.text.toString()

            notifyUser()
        }
    }

    private fun notifyUser() {

        MaterialAlertDialogBuilder(this)
            .setMessage("We will be verifying the phone number:$phoneNumber"+"\n"
            +"Is this ok or Would you like to Edit the number?")
            .setPositiveButton("Ok",DialogInterface.OnClickListener { dialog, which ->
                showOtpActivity()
            })
            .setNegativeButton("Edit",DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setCancelable(false)
            .create()
            .show()

    }

    private fun showOtpActivity() {

        var i=Intent(this, OtpActivity::class.java)
        i.putExtra(PHONE_NUMBER,phoneNumber)
        startActivity(i)
        finish()
    }
}