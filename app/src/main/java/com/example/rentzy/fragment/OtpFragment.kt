package com.example.rentzy.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mygreetingsapp.helper.AppConstant
import com.example.mygreetingsapp.helper.AppConstant.Companion.OTP
import com.example.rentzy.R
import com.example.rentzy.activity.MainActivity
import com.example.rentzy.helper.SecurePreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class OtpFragment : Fragment() {

    lateinit var otp_1: EditText
    lateinit var otp_2: EditText
    lateinit var otp_3: EditText
    lateinit var otp_4: EditText
    lateinit var otp_5: EditText
    lateinit var otp_6: EditText
    lateinit var tv_otp_number: TextView
    lateinit var tv_submit: TextView
    lateinit var tv_countdown: TextView
    lateinit var tv_resendotp: TextView

    private lateinit var RESENDED_OTP: String
    private lateinit var OTP: String
    lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    var START_MILLI_SECONDS = 60000L

    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        otp_1 = view.findViewById(R.id.otp_1)
        otp_2 = view.findViewById(R.id.otp_2)
        otp_3 = view.findViewById(R.id.otp_3)
        otp_4 = view.findViewById(R.id.otp_4)
        otp_5 = view.findViewById(R.id.otp_5)
        otp_6 = view.findViewById(R.id.otp_6)
        tv_otp_number = view.findViewById(R.id.tv_otpNumber)
        tv_submit = view.findViewById(R.id.tv_submit)
        tv_countdown = view.findViewById(R.id.tv_countdown)
        tv_resendotp = view.findViewById(R.id.tv_resendotp)

        auth = FirebaseAuth.getInstance()

        tv_otp_number.text = SecurePreferences.getStringPreference(context, "mobile_number")

        OTP = SecurePreferences.getStringPreference(context, AppConstant.OTP)

        init()



        return view

    }

    private fun init() {
        otp_1.addTextChangedListener(GenericTextWatcher(otp_2, otp_1))
        otp_2.addTextChangedListener(GenericTextWatcher(otp_3, otp_1))
        otp_3.addTextChangedListener(GenericTextWatcher(otp_4, otp_2))
        otp_4.addTextChangedListener(GenericTextWatcher(otp_5, otp_3))
        otp_5.addTextChangedListener(GenericTextWatcher(otp_6, otp_4))
        otp_6.addTextChangedListener(GenericTextWatcher(otp_6, otp_5))

        phoneNumber = SecurePreferences.getStringPreference(context, AppConstant.MOBILE_NUMBER)

        if (isRunning){
            pauseTimer()
        }
        else{
            time_in_milli_seconds = 60000L
            startTimer(time_in_milli_seconds)
        }

        tv_resendotp.setOnClickListener {
            resendVerificationCode()
            resendOTPvVisibility()
            resetTimer()
        }

        tv_submit.setOnClickListener {
            val typed_OTP = (otp_1.text.toString().trim()
                    + otp_2.text.toString().trim()
                    + otp_3.text.toString().trim()
                    + otp_4.text.toString().trim()
                    + otp_5.text.toString().trim()
                    + otp_6.text.toString().trim())

            if (typed_OTP.isNotEmpty()) {
                if (typed_OTP.length == 6) {

                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typed_OTP
                    )

                    Toast.makeText(context, "OTP " + OTP, Toast.LENGTH_LONG).show()
                    Toast.makeText(context, "typed otp " + typed_OTP, Toast.LENGTH_LONG).show()
                    signInWithPhoneAuthCredential(credential)
                } else {
                    toast("error", "Please enter Correct OTP")
                }
            } else {
                toast("warning", "Please enter OTP")
            }

//            if (full_otp.equals("1234", ignoreCase = false)) {
//
//                toast("success", "Otp verified")
//
//                Handler().postDelayed({
//                    startActivity(Intent(context, MainActivity::class.java))
//                    requireActivity().finish()
//                }, 2000)
//
//            } else {
//                toast("error", "Otp is invalid")
//            }
        }

    }

    private fun resendOTPvVisibility() {
        otp_1.setText("")
        otp_2.setText("")
        otp_3.setText("")
        otp_4.setText("")
        otp_5.setText("")
        otp_6.setText("")

//        when clicked once resend tv will be gone and disabled
        tv_resendotp.visibility = View.GONE
        tv_resendotp.isEnabled = false

        //after 60 seconds resend tv will be visible and enabled too
        Handler(Looper.myLooper()!!).postDelayed({
            tv_resendotp.visibility = View.VISIBLE
            tv_resendotp.isEnabled = true
        }, 60000)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                toast("success", "Authentication Successfull")
                pauseTimer()
                sendToMain()

            } else {
                // Sign in failed, display a message and update the UI
                Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
                // Update UI
            }
        }
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
            toast("error", "Verification failed")
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            RESENDED_OTP = verificationId
            resendToken = token
        }
    }

    private fun sendToMain() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun pauseTimer() {
        countdown_timer.cancel()
        isRunning = false
    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                toast("error","Time's up")
                replacefragment(LoginFragment(),"LoginFragment")
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

        isRunning = true

    }

    private fun resetTimer() {
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        tv_countdown.text = "$minute:$seconds"
    }

    private fun toast(type: String, desc: String) {
        val inflater = layoutInflater
        val layout: View =
            inflater.inflate(R.layout.custom_toast, requireView().findViewById(R.id.lv_customtoast))
        val iv_messageimage = layout.findViewById<ImageView>(R.id.iv_messageimage)
        val tv_messagetitle = layout.findViewById<TextView>(R.id.tv_messagetitle)
        val tv_messagedesc = layout.findViewById<TextView>(R.id.tv_messagedesc)
        val lv_message = layout.findViewById<LinearLayout>(R.id.lv_message)
        if (type.equals("success", ignoreCase = true)) //success
        {
            lv_message.setBackgroundColor(resources.getColor(R.color.green2))
            //            iv_messageimage.setBackground(getResources().getDrawable(R.drawable.gradient_green));
            iv_messageimage.setImageResource(R.drawable.tick)
            tv_messagetitle.text = "Success"
            tv_messagedesc.text = desc
            tv_messagedesc.text = desc
        } else if (type.equals("info", ignoreCase = true)) //info
        {
            lv_message.setBackgroundColor(resources.getColor(R.color.blue))
            iv_messageimage.setImageResource(R.drawable.info)
            tv_messagetitle.text = "Info"
            tv_messagedesc.text = desc
            tv_messagedesc.text = desc
        } else if (type.equals("error", ignoreCase = true)) {
            lv_message.setBackgroundColor(resources.getColor(R.color.red_2))
            iv_messageimage.setImageResource(R.drawable.danger)
            tv_messagetitle.text = "Danger"
            tv_messagedesc.text = desc
        } else if (type.equals("warning", ignoreCase = true)) {
            lv_message.setBackgroundColor(resources.getColor(R.color.orange4))
            iv_messageimage.setImageResource(R.drawable.exclamation)
            tv_messagetitle.text = "Warning"
            tv_messagedesc.text = desc
        }
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.setView(layout)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    class GenericTextWatcher(private val etNext: EditText, private val etPrev: EditText) :
        TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            if (text.length == 1) {
                etNext.requestFocus()
            } else if (text.length == 0) {
                etPrev.requestFocus()
            }
        }
    }

    fun replacefragment(fragment1: Fragment?, tag: String?) {
        val ft = requireFragmentManager().beginTransaction()
        ft.replace(R.id.frame_authentication, fragment1!!, tag).addToBackStack(null).commit()
    }

}