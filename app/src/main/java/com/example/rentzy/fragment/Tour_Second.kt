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
import com.example.rentzy.R
import com.example.rentzy.activity.MainActivity
import com.example.rentzy.helper.Utils

class Tour_Second : Fragment() {

    lateinit var lv_next:LinearLayout
    lateinit var iv_backTour:ImageView
    lateinit var tv_skipTour:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tour__second, container, false)

        lv_next = view.findViewById(R.id.lv_next)
        tv_skipTour = view.findViewById(R.id.tv_skip_tour)
        iv_backTour = view.findViewById(R.id.iv_back_tour)

        Utils.blackIconStatusBar(activity, R.color.white)

        lv_next.setOnClickListener {
            designfragment(Tour_Third(),"Tour_Second")
        }

        tv_skipTour.setOnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }

        iv_backTour.setOnClickListener {
            designfragment(Tour_First(),"Tour_First")
        }

        return view
    }

    private fun designfragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.add(R.id.frame_tour, fragment, tag).addToBackStack(null)
        fragmentTransaction.commit()
    }

}