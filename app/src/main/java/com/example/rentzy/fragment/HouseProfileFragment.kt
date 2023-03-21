package com.example.rentzy.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.R
import com.example.rentzy.model.DashboardModel

//taking data as constructor
class HouseProfileFragment(private var dashboardModell: DashboardModel) : Fragment() {

    lateinit var iv_profilehouse_image: ImageView
    lateinit var tv_profilehouse_name: TextView
    lateinit var tv_profilehouse_rating: TextView
    lateinit var tv_profilehouse_rent: TextView
    lateinit var tv_profilehouse_description: TextView
    lateinit var tv_profilehouse_bedroom: TextView
    lateinit var tv_profilehouse_kitchen: TextView
    lateinit var tv_profilehouse_bathroom: TextView
    lateinit var tv_profilehouse_swimmingpool: TextView
    lateinit var tv_profilehouse_parking: TextView
    lateinit var tv_profilehouse_address: TextView
    lateinit var tv_profilehouse_ownername: TextView
    lateinit var card_booknow:CardView

    lateinit var dashboardModel:DashboardModel
    lateinit var dashboardList:MutableList<DashboardModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_house_profile, container, false)

        iv_profilehouse_image = view.findViewById(R.id.iv_profilehouse_Image)
        tv_profilehouse_name = view.findViewById(R.id.tv_profilehouse_Name)
        tv_profilehouse_rating = view.findViewById(R.id.tv_porofilehouse_Rating)
        tv_profilehouse_rent = view.findViewById(R.id.tv_profilehouse_Rent)
        tv_profilehouse_description = view.findViewById(R.id.tv_profilehouse_Description)
        tv_profilehouse_bedroom = view.findViewById(R.id.tv_profilehouse_Bedroom)
        tv_profilehouse_kitchen = view.findViewById(R.id.tv_profilehouse_Kitchen)
        tv_profilehouse_bathroom = view.findViewById(R.id.tv_profilehouse_Bathroom)
        tv_profilehouse_swimmingpool = view.findViewById(R.id.tv_profilehouse_Swimmingpool)
        tv_profilehouse_parking = view.findViewById(R.id.tv_profilehouse_Parking)
        tv_profilehouse_address = view.findViewById(R.id.tv_profilehouse_Address)
        tv_profilehouse_ownername = view.findViewById(R.id.tv_prfilehouse_ownername)
        card_booknow = view.findViewById(R.id.card_booknow)

        //storing data in this file's model using this keyword
        this.dashboardModel = dashboardModell

        //hide progress dialog
        AppConstant.hideProgressDialog()

        initView()

        return view
    }

    private fun initView() {

        Glide.with(requireContext()).load(dashboardModell.house_image).into(iv_profilehouse_image)
        tv_profilehouse_name.setText(dashboardModel.house_name)
        tv_profilehouse_rating.setText(dashboardModel.house_rating)
        tv_profilehouse_rent.setText("\u20b9" + dashboardModel.house_rent)
        tv_profilehouse_description.setText(dashboardModel.house_description)
        tv_profilehouse_address.setText(dashboardModel.house_address)
        tv_profilehouse_bedroom.setText(dashboardModel.house_bedroom)
        tv_profilehouse_kitchen.setText(dashboardModel.house_kitchen)
        tv_profilehouse_bathroom.setText(dashboardModel.house_bathroom)
        tv_profilehouse_parking.setText(dashboardModel.house_parking)
        tv_profilehouse_swimmingpool.setText(dashboardModel.house_swimmingpool)
        tv_profilehouse_ownername.setText(dashboardModel.house_ownername)

        card_booknow.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            callIntent.data = Uri.parse("tel:" + dashboardModel.house_ownernumber)
            requireActivity().startActivity(callIntent)
        }

    }
}