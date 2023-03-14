package com.example.rentzy.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.rentzy.R

class SettingsFragment : Fragment() {

    lateinit var iv_back:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        iv_back =view.findViewById(R.id.iv_settingsBack)

        iv_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}