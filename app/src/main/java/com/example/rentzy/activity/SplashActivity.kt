package com.example.rentzy.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.rentzy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {

    @BindView(R.id.progressbar)
    lateinit var pb: ProgressBar
    lateinit var mAuth:FirebaseAuth

    // Splash Screen
    private val SPLASH_SCREEN_TIME_OUT = 3000
    // after 3000ms next activity will be started

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This method is used so that your splash activity can cover the entire screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        mAuth = FirebaseAuth.getInstance()

        Handler().postDelayed(
            {

                var user: FirebaseUser? = mAuth.currentUser
                if(user!=null) {
                    startActivity(Intent(this,MainActivity::class.java))
                    this.finish()
                }
                else{
                    startActivity(Intent(this,AuthenticationActivity::class.java))
                    this.finish()

                }

//                startActivity(Intent(this, AppTourActivity::class.java))
//                finish()

            }, 3000
        )

    }


}