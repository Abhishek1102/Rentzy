package com.example.mygreetingsapp.helper

import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.rentzy.R
import java.util.*

class AppConstant {

    companion object {

        var IS_LOGIN = "is_login"
        var NAME = "name"
        var EMAIL = "email"
        var PASSWORD = "password"
        var MOBILE_NUMBER = "mobile_number"
        var OTP = "otp"

        var USER_NAME = ""
        var USER_NUMBER = ""
        var USER_EMAIL = ""
        var USER_IMAGE = ""

        //api
        var REGISTER_API = "https://abhitestsite.000webhostapp.com/Greetings_Api/signup.php"
        var LOGIN_API = "https://abhitestsite.000webhostapp.com/Greetings_Api/login.php"

        var LANGUAGE = "user_language"

        lateinit var dialog:ProgressDialog

        fun setLanguage(activity: Context?) {
            val sharedPreferences =
                activity!!.getSharedPreferences("LANGUAGE_SETTING", Context.MODE_PRIVATE)
            val languageToLoad: String
            languageToLoad =
                if (sharedPreferences.getString(AppConstant.LANGUAGE, "en").equals("en", ignoreCase = true)) {
                    "en" // your language
                } else {
                    "gu"
                }
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            activity.resources.updateConfiguration(
                config,
                activity.resources.displayMetrics
            )
        }

        fun showProgressDialog(context: Context?) {
            dialog = ProgressDialog(context)
            if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.isIndeterminate = true
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()
            dialog.setContentView(R.layout.custom_progress)
        }

        fun hideProgressDialog() {
            if (dialog != null && dialog.isShowing) dialog.dismiss()
        }


    }
}