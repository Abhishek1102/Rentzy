package com.example.rentzy.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import com.airbnb.lottie.LottieAnimationView
import com.example.rentzy.R
import com.example.rentzy.activity.AuthenticationActivity
import com.example.rentzy.activity.MainActivity

class Tour_Third : Fragment() {

    lateinit var lv_next:LinearLayout
    lateinit var iv_backTour:ImageView
    lateinit var tv_skipTour:TextView
    lateinit var lottieAnimationHouse:LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tour__third, container, false)

        lv_next = view.findViewById(R.id.lv_next)
        iv_backTour = view.findViewById(R.id.iv_back_tour)
        tv_skipTour = view.findViewById(R.id.tv_skip_tour)
        lottieAnimationHouse = view.findViewById(R.id.animation_view)

        lv_next.setOnClickListener {
            startActivity(Intent(context, AuthenticationActivity::class.java))
            requireActivity().finish()
        }

        iv_backTour.setOnClickListener {
            designfragment(Tour_Second(),"Tour_Second")
        }

        tv_skipTour.setOnClickListener {
            startActivity(Intent(context, AuthenticationActivity::class.java))
            requireActivity().finish()
        }

        lottieAnimationHouse.loop(false)
        lottieAnimationHouse.playAnimation()

        return view

    }

    private fun designfragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.add(R.id.frame_tour, fragment, tag).addToBackStack(null)
        fragmentTransaction.commit()
    }

}