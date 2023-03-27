package com.example.rentzy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.R
import com.example.rentzy.fragment.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    @BindView(R.id.frame_authentication)
    lateinit var frame_authentication:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        addfragment(LoginFragment(),"Login Fragment")

    }

    private fun addfragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_authentication, fragment, tag)
        fragmentTransaction.commit()
    }
}