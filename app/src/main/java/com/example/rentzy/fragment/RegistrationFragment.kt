package com.example.rentzy.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.R
import com.example.rentzy.activity.MainActivity
import com.example.rentzy.helper.SecurePreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class RegistrationFragment : Fragment() {

    lateinit var lv_RegisterByEmail: LinearLayout
    lateinit var lv_RegisterByPhonenumber: LinearLayout
    lateinit var lv_GoToLogin: LinearLayout
    lateinit var lv_Remail: LinearLayout
    lateinit var tv_register: TextView
    lateinit var tv_email: TextView
    lateinit var tv_mobileNumber: TextView
    lateinit var edt_email: EditText
    lateinit var edt_password: EditText
    lateinit var edt_Rphone: EditText
    lateinit var edt_Rusername: EditText
    lateinit var iv_password_hide: ImageView
    lateinit var iv_password_show: ImageView
    lateinit var iv_google: ImageView
    lateinit var lengthValidation: TextView
    lateinit var uppercaseValidation: TextView
    lateinit var lowercaseValidation: TextView
    lateinit var digitValidation: TextView
    lateinit var symbolValidation: TextView
    lateinit var usernameVaildation: TextView
    lateinit var emailValidation: TextView
    lateinit var mobilenumberValidation: TextView

    lateinit var googleSignInClient: GoogleSignInClient
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var ref: DocumentReference
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String
    lateinit var user_mobileNumber: String

    var validUserName: Boolean = false
    var validEmail: Boolean = false
    var validPhoneNumber: Boolean = false
    var validPassword: Boolean = false

    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        lv_RegisterByEmail = view.findViewById(R.id.lv_RegisterByEmail)
        lv_RegisterByPhonenumber = view.findViewById(R.id.lv_RegisterByMobile)
        lv_GoToLogin = view.findViewById(R.id.lv_GoToLogin)
        lv_Remail = view.findViewById(R.id.lv_Remail)
        tv_register = view.findViewById(R.id.tv_register)
        tv_email = view.findViewById(R.id.tv_Remail)
        tv_mobileNumber = view.findViewById(R.id.tv_Rmobile)
        edt_email = view.findViewById(R.id.edt_Remail)
        edt_Rphone = view.findViewById(R.id.edt_Rphone)
        edt_password = view.findViewById(R.id.edt_Rpassword)
        edt_Rusername = view.findViewById(R.id.edt_Rusername)
        iv_password_hide = view.findViewById(R.id.iv_password_hide)
        iv_password_show = view.findViewById(R.id.iv_password_show)
        iv_google = view.findViewById(R.id.iv_google)
        lengthValidation = view.findViewById(R.id.tv_password_lengthValidation)
        uppercaseValidation = view.findViewById(R.id.tv_password_uppercaseValidation)
        lowercaseValidation = view.findViewById(R.id.tv_password_lowercaseValidation)
        digitValidation = view.findViewById(R.id.tv_password_digitValidation)
        symbolValidation = view.findViewById(R.id.tv_password_symbolValidation)
        usernameVaildation = view.findViewById(R.id.tv_usernameValidation)
        emailValidation = view.findViewById(R.id.tv_emailValidation)
        mobilenumberValidation = view.findViewById(R.id.tv_mobilenumberValidation)

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        initView()
        onTypeValidation()

        gAuthh()

        return view

    }

    private fun gAuthh() {

        //SHA1 Key is nescessary in this type of authenctication

        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken("1090190503526-1on2826t0j18ccsv37cpu6gvsaoauc3b.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(), googleSignInOptions
        )

        // Initialize firebase user
        var firebaseUser = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in
            // redirect to profile activity
            startActivity(
                Intent(activity, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            requireActivity().finish()
        }
    }

    private fun initView() {

        tv_email.setOnClickListener {
            lv_RegisterByPhonenumber.visibility = View.GONE
            lv_Remail.visibility = View.VISIBLE

            tv_email.setBackground(resources.getDrawable(R.drawable.app_main_gradient))
            tv_mobileNumber.setBackground(resources.getDrawable(R.drawable.gray_rect))

            tv_email.setTextColor(resources.getColor(R.color.white))
            tv_mobileNumber.setTextColor(resources.getColor(R.color.font_color_gray))

        }

        tv_mobileNumber.setOnClickListener {
            lv_RegisterByPhonenumber.visibility = View.VISIBLE
            lv_Remail.visibility = View.GONE

            tv_email.setBackground(resources.getDrawable(R.drawable.gray_rect))
            tv_mobileNumber.setBackground(resources.getDrawable(R.drawable.app_main_gradient))

            tv_email.setTextColor(resources.getColor(R.color.font_color_gray))
            tv_mobileNumber.setTextColor(resources.getColor(R.color.white))

        }

        lv_GoToLogin.setOnClickListener {
            replacefragment(LoginFragment(), "LoginFragment")
        }

        tv_register.setOnClickListener {

            if (validUserName && validEmail && validPassword) {

                addUser()


            } else if (validUserName && validPhoneNumber && validPassword) {
                addUserwithNumber()
            } else {
                toast("error", "Please enter valid credentials to Sign up!")
//                Toast.makeText(context, "Please enter valid credentials to sign up", Toast.LENGTH_SHORT).show()
            }
        }

        //to show or hide password
        iv_password_show.setOnClickListener {
            iv_password_show.visibility = View.GONE
            iv_password_hide.visibility = View.VISIBLE

            edt_password.setTransformationMethod(null)

        }
        iv_password_hide.setOnClickListener {
            iv_password_show.visibility = View.VISIBLE
            iv_password_hide.visibility = View.GONE

            edt_password.setTransformationMethod(PasswordTransformationMethod())

        }

        iv_google.setOnClickListener {
            // Initialize sign in intent
            val intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }

    }

    private fun addUserwithNumber() {
        user_mobileNumber = "+91" + edt_Rphone.text.toString().trim()
        SecurePreferences.savePreferences(
            context,
            AppConstant.USER_NUMBER,
            user_mobileNumber
        )

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(user_mobileNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
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
            toast("error", "Verfication Failed")
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

            SecurePreferences.savePreferences(context, AppConstant.OTP, verificationId)
            replacefragment(OtpFragment(), "OtpFragment")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    toast("success", "Authentication Successfull")
                    SecurePreferences.savePreferences(context,AppConstant.IS_LOGIN,true)
                    startActivity(Intent(context, MainActivity::class.java))
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


    private fun addUser() {
        username = edt_Rusername.text.toString().trim()
        email = edt_email.text.toString().trim()
        password = edt_password.text.toString().trim()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    SecurePreferences.savePreferences(context, AppConstant.USER_NAME, username)
                    SecurePreferences.savePreferences(context, AppConstant.USER_EMAIL, email)

                    toast("success", "Registration Successfull")

                    //making user info entry in firestore database
                    userId = firebaseAuth.currentUser!!.uid
                    ref = firebaseFirestore.collection("user_info").document(userId)
                    val reg_entry: MutableMap<String, Any> = HashMap()
                    reg_entry["Name"] = edt_Rusername.getText().toString()
                    reg_entry["Email"] = edt_email.getText().toString()
                    reg_entry["Password"] = edt_password.getText().toString()

                    ref.set(reg_entry).addOnSuccessListener {
                        Log.d("success", "User Registered")
                    }
                        .addOnFailureListener {
                            Log.d("failure", it.localizedMessage)
                            toast("error", "Something went wrong")
                        }

                    Handler().postDelayed({
                        SecurePreferences.savePreferences(activity, AppConstant.IS_LOGIN, true)
                        startActivity(Intent(activity, MainActivity::class.java))
                        requireActivity().finish()
                    }, 1000)
                } else {
                    toast("error", "Registration Failed\nEmail used by another account")
                    Log.d("TAG", task.exception?.message.toString())
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100
            // Initialize task
            val signInAccountTask = GoogleSignIn
                .getSignedInAccountFromIntent(data)

            // check condition
            if (signInAccountTask.isSuccessful) {
                // When google sign in successful
                // Initialize string
                val s = "Google sign in successful"
                // Display Toast
                displayToast(s)
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount = signInAccountTask
                        .getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        val authCredential = GoogleAuthProvider
                            .getCredential(
                                googleSignInAccount.idToken, null
                            )
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(
                                requireActivity()
                            ) { task ->
                                // Check condition
                                if (task.isSuccessful) {

                                    //making user info entry in firestore database
                                    firebaseUser = firebaseAuth.currentUser!!
                                    userId = firebaseAuth.currentUser!!.uid
                                    ref = firebaseFirestore.collection("user_info").document(userId)
                                    val reg_entry: MutableMap<String, Any> = HashMap()
                                    reg_entry["Name"] = firebaseUser.displayName.toString()
                                    reg_entry["Email"] = firebaseUser.email.toString()

                                    SecurePreferences.savePreferences(context,AppConstant.USER_EMAIL,firebaseUser.email.toString())
                                    SecurePreferences.savePreferences(context,AppConstant.USER_NAME,firebaseUser.displayName.toString())

                                    ref.set(reg_entry).addOnSuccessListener {
                                        Log.d("success", "User Registered")
                                    }
                                        .addOnFailureListener {
                                            Log.d("failure", it.localizedMessage)
                                            toast("error", "Something went wrong")
                                        }

                                    // When task is successful
                                    // Redirect to profile activity
                                    startActivity(
                                        Intent(activity, MainActivity::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    requireActivity().finish()
                                    // Display Toast
                                    displayToast("Firebase authentication successful")
                                } else {
                                    // When task is unsuccessful
                                    // Display Toast
                                    Toast.makeText(
                                        context,
                                        "Authentication Failed" + task.exception!!.localizedMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            } else {
                displayToast("Unsuccessful task")
            }
        }
    }

    fun textWatcher(edt: EditText, textView: TextView) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt.text.length > 0) textView.visibility = View.VISIBLE
                else textView.visibility = View.GONE
            }
        })
    }


    private fun onTypeValidation() {
        edt_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val password: String
                password = edt_password.text.toString().trim()
                if (password.length >= 8) {
                    lengthValidation.setTextColor(resources.getColor(R.color.green))
                    lengthValidation.visibility = View.GONE
                    validPassword = true
                } else {
                    lengthValidation.setTextColor(resources.getColor(R.color.red1))
                    lengthValidation.visibility = View.VISIBLE
                    validPassword = false
                }
                if (password.matches(".*[A-Z].*".toString().toRegex())) {
                    uppercaseValidation.setTextColor(resources.getColor(R.color.green))
                    uppercaseValidation.visibility = View.GONE
                    validPassword = true
                } else {
                    uppercaseValidation.setTextColor(resources.getColor(R.color.red1))
                    uppercaseValidation.visibility = View.VISIBLE
                    validPassword = false
                }
                if (password.matches(".*[0-9].*".toString().toRegex())) {
                    digitValidation.setTextColor(resources.getColor(R.color.green))
                    digitValidation.visibility = View.GONE
                    validPassword = true
                } else {
                    digitValidation.setTextColor(resources.getColor(R.color.red1))
                    digitValidation.visibility = View.VISIBLE
                    validPassword = false
                }
                if (password.matches(".*[a-z].*".toString().toRegex())) {
                    lowercaseValidation.setTextColor(resources.getColor(R.color.green))
                    lowercaseValidation.visibility = View.GONE
                    validPassword = true
                } else {
                    lowercaseValidation.setTextColor(resources.getColor(R.color.red1))
                    lowercaseValidation.visibility = View.VISIBLE
                    validPassword = false
                }
                if (password.matches(".*[!@#$%^&*+=/?].*".toRegex())) {
                    symbolValidation.setTextColor(resources.getColor(R.color.green))
                    symbolValidation.visibility = View.GONE
                    validPassword = true
                } else {
                    symbolValidation.setTextColor(resources.getColor(R.color.red1))
                    symbolValidation.visibility = View.VISIBLE
                    validPassword = false
                }

            }
        })

        edt_Rusername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_Rusername.text.toString()
                        .trim().length < 4 || edt_Rusername.text.toString()
                        .trim().length > 20
                ) {
                    usernameVaildation.text = "please enter valid username"
                    usernameVaildation.visibility = View.VISIBLE
                    validUserName = false
                } else {
                    usernameVaildation.visibility = View.GONE
                    validUserName = true
                }
            }

        })

        edt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_email.text.toString().matches(emailPattern.toRegex())) {
                    emailValidation.text = "please enter valid email"
                    emailValidation.visibility = View.VISIBLE
                    validEmail = false
                }
                if (edt_email.text.toString().isEmpty()) {
                    emailValidation.text = "please enter email"
                    emailValidation.visibility = View.VISIBLE
                    validEmail = false
                } else {
                    emailValidation.visibility = View.GONE
                    validEmail = true
                }
            }

        })

        edt_Rphone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_Rphone.text.trim().length == 10) {
                    mobilenumberValidation.visibility = View.GONE
                    validPhoneNumber = true
                } else {
                    mobilenumberValidation.text = "enter valid mobile number"
                    mobilenumberValidation.visibility = View.VISIBLE
                    validPhoneNumber = false
                }
            }

        })

    }

    private fun toast(type: String, desc: String) {
        val inflater = layoutInflater
        val layout: View =
            inflater.inflate(
                R.layout.custom_toast,
                requireView().findViewById(R.id.lv_customtoast)
            )
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


    private fun displayToast(s: String) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    fun replacefragment(fragment1: Fragment?, tag: String?) {
        val ft = requireFragmentManager().beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_in_left, R.anim.slide_out_left,
            R.anim.slide_out_right, R.anim.slide_in_right
        )
        ft.replace(R.id.frame_authentication, fragment1!!, tag).addToBackStack(null).commit()
    }
}