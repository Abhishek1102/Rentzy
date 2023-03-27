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
import com.bumptech.glide.Glide.init
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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {

    lateinit var lv_LoginByEmail: LinearLayout
    lateinit var lv_LoginByMobile: LinearLayout
    lateinit var lv_GoToSignUp: LinearLayout
    lateinit var tv_login: TextView
    lateinit var tv_email: TextView
    lateinit var tv_forgotPassword: TextView
    lateinit var tv_mobilenumber: TextView
    lateinit var edt_Lemail: EditText
    lateinit var edt_Lpassword: EditText
    lateinit var edt_Lphone: EditText
    lateinit var iv_password_hide: ImageView
    lateinit var iv_password_show: ImageView
    lateinit var iv_google: ImageView
    lateinit var tv_LemailorusernameValidation: TextView
    lateinit var tv_LpasswordValidation: TextView
    lateinit var tv_LmobilenumberValidation: TextView

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firestore: FirebaseFirestore
    lateinit var user_Id:String
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var user_mobileNumber: String
    var valid_LoginMobileNumber: Boolean = false
    var valid_LoginEmail: Boolean = false
    var valid_LoginPassword: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        lv_LoginByEmail = view.findViewById(R.id.lv_LoginByEmail)
        lv_LoginByMobile = view.findViewById(R.id.lv_LoginByMobile)
        lv_GoToSignUp = view.findViewById(R.id.lv_GoToSignUp)
        tv_login = view.findViewById(R.id.tv_login)
        tv_email = view.findViewById(R.id.tv_email)
        tv_mobilenumber = view.findViewById(R.id.tv_mobile)
        tv_forgotPassword = view.findViewById(R.id.tv_forgotPassword)
        edt_Lemail = view.findViewById(R.id.edt_Lemail)
        edt_Lpassword = view.findViewById(R.id.edt_Lpassword)
        iv_password_hide = view.findViewById(R.id.iv_password_hide)
        iv_password_show = view.findViewById(R.id.iv_password_show)
        iv_google = view.findViewById(R.id.iv_google)
        edt_Lphone = view.findViewById(R.id.edt_Lphone)
        tv_LemailorusernameValidation = view.findViewById(R.id.tv_LoginemailOrusernameValidation)
        tv_LpasswordValidation = view.findViewById(R.id.tv_LoginpasswordValidation)
        tv_LmobilenumberValidation = view.findViewById(R.id.tv_LoginphoneValidation)


        init()
        gAuthenticate()
        onTypeValidation()

        return view

    }

    private fun init() {

        tv_email.setOnClickListener {
            lv_LoginByEmail.visibility = View.VISIBLE
            lv_LoginByMobile.visibility = View.GONE

            tv_email.setBackground(resources.getDrawable(R.drawable.app_main_gradient))
            tv_mobilenumber.setBackground(resources.getDrawable(R.drawable.gray_rect))

            tv_email.setTextColor(resources.getColor(R.color.white))
            tv_mobilenumber.setTextColor(resources.getColor(R.color.font_color_gray))

        }

        tv_mobilenumber.setOnClickListener {
            lv_LoginByEmail.visibility = View.GONE
            lv_LoginByMobile.visibility = View.VISIBLE

            tv_email.setBackground(resources.getDrawable(R.drawable.gray_rect))
            tv_mobilenumber.setBackground(resources.getDrawable(R.drawable.app_main_gradient))

            tv_email.setTextColor(resources.getColor(R.color.font_color_gray))
            tv_mobilenumber.setTextColor(resources.getColor(R.color.white))

        }

        lv_GoToSignUp.setOnClickListener {
            replacefragment(RegistrationFragment(), "RegistrationFragment")
        }

        //to show or hide password
        iv_password_show.setOnClickListener {
            iv_password_show.visibility = View.GONE
            iv_password_hide.visibility = View.VISIBLE

            edt_Lpassword.setTransformationMethod(null)

        }
        iv_password_hide.setOnClickListener {
            iv_password_show.visibility = View.VISIBLE
            iv_password_hide.visibility = View.GONE

            edt_Lpassword.setTransformationMethod(PasswordTransformationMethod())

        }

        iv_google.setOnClickListener {
            // Initialize sign in intent
            val intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }

        tv_login.setOnClickListener {

            if (valid_LoginEmail && valid_LoginPassword) {
                loginUser()
            }
            else if (valid_LoginMobileNumber) {
                addUserwithNumber()
            } else {
                toast("error", "please enter valid credentials to continue")
            }
        }

    }

    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(
            edt_Lemail.text.toString(),
            edt_Lpassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("success", "Login Successfull")

                SecurePreferences.savePreferences(context,AppConstant.USER_EMAIL,edt_Lemail.text.toString())
//                SecurePreferences.savePreferences(context,AppConstant.USER_NAME,firebaseUser.displayName)
//                SecurePreferences.savePreferences(context,AppConstant.USER_NUMBER,firebaseUser.phoneNumber)
//                SecurePreferences.savePreferences(context,AppConstant.USER_IMAGE,firebaseUser.photoUrl.toString())

                firebaseUser = firebaseAuth.currentUser!!
                firestore = FirebaseFirestore.getInstance()

                firestore.collection("user_info").whereEqualTo("Email",SecurePreferences.getStringPreference(context,AppConstant.USER_EMAIL)).get()
                    .addOnSuccessListener { documents->
                        for (document in documents){
                            Log.d("Tag","${document.id}=>${document.data}")
                            SecurePreferences.savePreferences(context,AppConstant.USER_NAME,document.get("Name").toString())
                        }
                    }.addOnFailureListener {
                        Log.d("Tag","Error getting documents: ",it)
                    }

                Handler().postDelayed({
                    SecurePreferences.savePreferences(activity, AppConstant.IS_LOGIN, true)
                    startActivity(Intent(activity, MainActivity::class.java))
                    requireActivity().finish()
                }, 1000)
            } else {
                Log.d("TAG", task.exception?.message.toString())
//                Toast.makeText(context,"Login Failed " + task.exception,Toast.LENGTH_LONG).show()
                toast("error", "Login Failed")
            }
        }
    }

    private fun gAuthenticate() {

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


        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser = firebaseAuth.currentUser
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

    private fun onTypeValidation() {

        edt_Lpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_Lpassword.text.toString().isEmpty()) {
                    tv_LpasswordValidation.setText("Please enter password")
                    tv_LpasswordValidation.visibility = View.VISIBLE
                    valid_LoginPassword = false
                } else {
                    tv_LpasswordValidation.visibility = View.GONE
                    valid_LoginPassword = true
                }
            }

        })

        edt_Lemail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_Lemail.text.toString().trim().isEmpty()) {
                    tv_LemailorusernameValidation.visibility = View.VISIBLE
                    tv_LemailorusernameValidation.setText("please enter credentials to continue")
                    valid_LoginEmail = false
                } else {
                    tv_LemailorusernameValidation.visibility = View.GONE
                    valid_LoginEmail = true
                }
            }

        })

        edt_Lphone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edt_Lphone.text.toString().trim().isEmpty()) {
                    tv_LmobilenumberValidation.setText("please enter mobile number")
                    tv_LmobilenumberValidation.visibility = View.VISIBLE
                    valid_LoginMobileNumber = false
                } else {
                    tv_LmobilenumberValidation.visibility = View.GONE
                    valid_LoginMobileNumber = true
                }
            }

        })

    }

    private fun addUserwithNumber() {
        user_mobileNumber = "+91" + edt_Lphone.text.toString().trim()
        SecurePreferences.savePreferences(activity, AppConstant.MOBILE_NUMBER, user_mobileNumber)

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
                    startActivity(Intent(context, MainActivity::class.java))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        toast("error", "Entered code is invalid")
                        // The verification code entered was invalid
                    }
                    // Update UI
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
                                    // When task is successful
                                    // Redirect to profile activity
                                    firebaseUser = firebaseAuth.currentUser!!
                                    SecurePreferences.savePreferences(context,AppConstant.USER_IMAGE,firebaseUser.photoUrl.toString())
                                    SecurePreferences.savePreferences(context,AppConstant.USER_NAME,firebaseUser.displayName.toString())
                                    SecurePreferences.savePreferences(context,AppConstant.IS_LOGIN,true)
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

    private fun displayToast(s: String) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
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

    fun replacefragment(fragment1: Fragment?, tag: String?) {
        val ft = requireFragmentManager().beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_in_left, R.anim.slide_out_left,
            R.anim.slide_out_right, R.anim.slide_in_right
        )
        ft.replace(R.id.frame_authentication, fragment1!!, tag).addToBackStack(null).commit()
    }
}