package com.example.rentzy.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.R
import com.example.rentzy.activity.AuthenticationActivity
import com.example.rentzy.activity.SplashActivity
import com.example.rentzy.helper.SecurePreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MoreFragment : Fragment() {

    lateinit var lv_termsandcondition: LinearLayout
    lateinit var lv_settings: LinearLayout
    lateinit var lv_faq: LinearLayout
    lateinit var lv_contactus: LinearLayout
    lateinit var lv_logout: LinearLayout
    lateinit var lv_changelanguage:LinearLayout
    lateinit var lv_darkmode:LinearLayout

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        lv_termsandcondition = view.findViewById(R.id.lv_termsandconditions)
        lv_contactus = view.findViewById(R.id.lv_contactus)
        lv_faq = view.findViewById(R.id.lv_faq)
        lv_settings = view.findViewById(R.id.lv_settings)
        lv_logout = view.findViewById(R.id.lv_logout)
        lv_changelanguage = view.findViewById(R.id.lv_changelanguage)
        lv_darkmode = view.findViewById(R.id.lv_darkMode)

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN
        )


        initView()

        return view
    }

    private fun initView() {
        lv_termsandcondition.setOnClickListener {
            replacefragment(TermsandConditionsFragment(), "TermsandConditionsFragment")
        }

        lv_settings.setOnClickListener {
            replacefragment(SettingsFragment(), "SettingsFragment")
        }

        lv_faq.setOnClickListener {
            replacefragment(FaqFragment(), "FaqFragment")
        }

        lv_contactus.setOnClickListener {
            replacefragment(ContactusFragment(), "Contactus Fragment")
        }

        lv_darkmode.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
//            if (SecurePreferences.getBooleanPreference(context, AppConstant.is_dark_mode_on)) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                SecurePreferences.savePreferences(context, AppConstant.is_dark_mode_on, false)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                SecurePreferences.savePreferences(context, AppConstant.is_dark_mode_on, true)
//            }
        }

        lv_changelanguage.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.custom_dialog)
            val title = dialog.findViewById<TextView>(R.id.txt_dialog_title)
            val msg = dialog.findViewById<TextView>(R.id.txt_dialog_msg)
            val eng = dialog.findViewById<TextView>(R.id.txt_positive)
            val guj = dialog.findViewById<TextView>(R.id.txt_negative)

            title.setText(R.string.change_language)
            msg.setText(getString(R.string.ask_change_language))
            eng.setText(getString(R.string.english))
            guj.setText(getString(R.string.gujarati))

            eng.setOnClickListener {
                SecurePreferences.saveLanguagePreferences(activity, AppConstant.LANGUAGE, "en")
                AppConstant.setLanguage(activity)
                SecurePreferences.savePreferences(activity, AppConstant.IS_LOGIN, true)
                requireActivity().finishAffinity()
                val i = Intent(activity, SplashActivity::class.java)
                startActivity(i)
            }
            guj.setOnClickListener {
                SecurePreferences.saveLanguagePreferences(activity, AppConstant.LANGUAGE, "gu")
                AppConstant.setLanguage(activity)
                SecurePreferences.savePreferences(activity, AppConstant.IS_LOGIN, true)
                requireActivity().finishAffinity()
                val i = Intent(activity, SplashActivity::class.java)
                startActivity(i)
            }
            //Creating dialog box
            //Creating dialog box
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }

        lv_logout.setOnClickListener {

            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.custom_dialog)
            val title = dialog.findViewById<TextView>(R.id.txt_dialog_title)
            val msg = dialog.findViewById<TextView>(R.id.txt_dialog_msg)
            val yes = dialog.findViewById<TextView>(R.id.txt_positive)
            val no = dialog.findViewById<TextView>(R.id.txt_negative)


            title.setText(R.string.logout)
            msg.setText(getString(R.string.sure_to_logout))
            yes.setText(getString(R.string.yes))
            no.setText(getString(R.string.no))

            yes.setOnClickListener {

                AppConstant.showProgressDialog(context)
                Handler().postDelayed({
                    // Sign out from google
                    googleSignInClient.signOut().addOnCompleteListener { task ->
                        // Check condition
                        if (task.isSuccessful) {
                            // When task is successful
                            // Sign out from firebase
                            firebaseAuth.signOut()

                            // Display Toast
                            Toast.makeText(activity, "Logout successful", Toast.LENGTH_SHORT).show()

                            SecurePreferences.clearSecurepreference(context)
                            SecurePreferences.savePreferences(context,AppConstant.IS_LOGIN,false)

                            // Finish activity
                            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                },2000)
            }
            no.setOnClickListener {
                dialog.dismiss()
            }
            //Creating dialog box
            //Creating dialog box
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }
    }

    fun replacefragment(fragment1: Fragment?, tag: String?) {
        val ft = requireFragmentManager().beginTransaction()
        ft.replace(R.id.frame_full, fragment1!!, tag).addToBackStack(null).commit()
    }
}