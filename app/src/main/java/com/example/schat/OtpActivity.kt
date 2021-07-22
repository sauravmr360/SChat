package com.example.schat

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER="phonenumber"
class OtpActivity : AppCompatActivity(), View.OnClickListener {
    var phonenumber:String?=null
    lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mverificationid:String?=null
    var mresendtoken:PhoneAuthProvider.ForceResendingToken?=null
    private lateinit var progressDialog: ProgressDialog
    private var countDownTimer:CountDownTimer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        intiliazeviews()
        startverification()

    }

    private fun startverification() {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phonenumber!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        showTimer(60000)
        progressDialog=createProgressDialog("Sending a verification code",false)
        progressDialog.show()
    }

    private fun showTimer(millisecondinfuture:Long) {
        resendBtn.isEnabled=false
        countDownTimer=object : CountDownTimer(millisecondinfuture,1000){
            override fun onTick(millisUntilFinished: Long) {
                //on ticking the timer
                counterTv.isVisible=true
                counterTv.text=getString(R.string.counter,millisUntilFinished/1000)
            }

            override fun onFinish() {
                //on compeletion of time
                counterTv.isVisible=false
                resendBtn.isEnabled=true
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(countDownTimer!=null)
        {
            countDownTimer!!.cancel()
        }
    }

    private fun intiliazeviews() {
        phonenumber=intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text=getString(R.string.verify_number,phonenumber)
        setspannablestring()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)
        //callback for sms code
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                if(::progressDialog.isInitialized)
                {
                    progressDialog.dismiss()
                }
                val smscode=credential.smsCode
                if(!smscode.isNullOrBlank())
                {
                    sentcodeEt.setText(smscode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
                notifyUserAndRetry("Your phone number might be wrong or connection error. Retry Again!")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                if(::progressDialog.isInitialized)
                {
                    progressDialog.dismiss()
                }
                counterTv.isVisible=false
                mverificationid = verificationId
                mresendtoken = token
                // ...
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val mauth=FirebaseAuth.getInstance()
        mauth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    startActivity(Intent(this,SignUpActivity::class.java))
                }
                else
                {
                    notifyUserAndRetry("Your Phone no. Verification failed. Try Again!!")
                }
            }

    }

    private fun setspannablestring() {
        val span=SpannableString(getString(R.string.waiting_text,phonenumber))
        val clickableSpan= object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.color=ds.linkColor
            }

            override fun onClick(widget: View) {
                //send back
                showLoginActivity()
            }

        }
        span.setSpan(clickableSpan,span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod=LinkMovementMethod.getInstance()
        waitingTv.text=span
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoginActivity() {
        var i=Intent(this, LoginActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        //setting flags to clear backstack
        startActivity(i)
    }

    override fun onBackPressed() {
        //I removed super.onBackpressed to remove the back button function
    }

    override fun onClick(v: View?) {
        when(v)
        {
            verificationBtn->{
                val code=sentcodeEt.text.toString()
                if(code.isNotEmpty() && !mverificationid.isNullOrBlank())
                {
                    progressDialog=createProgressDialog("Please wait....",false)
                    progressDialog.show()
                    val credential=PhoneAuthProvider.getCredential(mverificationid!!,code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
            resendBtn->{
                if(mresendtoken!=null)
                {
                    showTimer(60000)
                    progressDialog=createProgressDialog("Sending a verification code",false)
                    progressDialog.show()

                    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phonenumber!!)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callback)
                        .setForceResendingToken(mresendtoken!!)
                        // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            }
        }
    }
}

fun Context.createProgressDialog(message:String,isCancelable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }

}