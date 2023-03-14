package com.example.rentzy.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.rentzy.R
import com.example.rentzy.adapter.DashboardAdapter
import com.example.rentzy.adapter.ImagesSliderAdapter
import com.example.rentzy.helper.GravitySnapHelper
import com.example.rentzy.model.DashboardModel
import com.example.rentzy.model.Slider_Model
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var rv_slider: RecyclerView
    lateinit var rv_dashboard: RecyclerView
    private lateinit var slider_adapter: ImagesSliderAdapter
    private lateinit var dashboardAdapter: DashboardAdapter
    lateinit var iv_bell:ImageView
    lateinit var iv_profile:ImageView
    lateinit var sv_dashboard:SearchView

    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    lateinit var sliderList: MutableList<Slider_Model>
    lateinit var dashboardList: MutableList<DashboardModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        rv_slider = view.findViewById(R.id.rv_slider)
        rv_dashboard = view.findViewById(R.id.rv_dashboard)
        iv_bell = view.findViewById(R.id.iv_bell)
        iv_profile = view.findViewById(R.id.iv_profile)
        sv_dashboard = view.findViewById(R.id.searchview_dashboard)

        initView()
        getImages()
        getListData()

        iv_bell.setOnClickListener {
            replacefragment(NotificationFragment(),"NotificationFragment")
        }

        iv_profile.setOnClickListener {
            replacefragment(ProfileFragment(),"ProfileFragment")
        }

        sv_dashboard.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filter(p0)
                return true
            }

        })


        return view
    }

    private fun initView() {

        sliderList = ArrayList()
        dashboardList = ArrayList()

        //initalizing firestorage and firestore
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()


        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN
        )

        rv_slider.setHasFixedSize(true)
        rv_slider.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        slider_adapter = ImagesSliderAdapter(context, sliderList)
        rv_slider.adapter = slider_adapter

        rv_dashboard.setHasFixedSize(true)
        rv_dashboard.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //for recyclerview item to scroll perfectly
        val snapHelper: SnapHelper = GravitySnapHelper(Gravity.CENTER)
        snapHelper.attachToRecyclerView(rv_slider)

    }

    private fun filter(newtext: String?) {
        if (newtext != null) {
            val filteredList = ArrayList<DashboardModel>()
            for (i in dashboardList) {
                if (i.house_name.lowercase(Locale.ROOT).contains(newtext)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_LONG).show()
            } else {
                dashboardAdapter.setFilteredList(filteredList)
            }
        }
    }

    private fun getListData() {
        firebaseFirestore.collection("house_info")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = documents.toObjects(DashboardModel::class.java)
                    //onclicked is called and model is passed to it
                    dashboardAdapter = DashboardAdapter(context, user, ::onClicked)
                    rv_dashboard.adapter = dashboardAdapter
                }
            }.addOnFailureListener {
                Log.d("Fail_List", it.localizedMessage)
                toast("error", "Fail to load data")
            }
    }

    private fun onClicked(dashboardModel: DashboardModel) {
//        Toast.makeText(context, "clicked item is ${dashboardModel.house_name}", Toast.LENGTH_SHORT).show()
        //passing data in model to another fragment
        replacefragment(HouseProfileFragment(dashboardModel),"HouseProfileFragment")

    }

    private fun getImages() {
        firebaseFirestore.collection("image_slider")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = documents.toObjects(Slider_Model::class.java)
                    slider_adapter = ImagesSliderAdapter(context, user)
                    rv_slider.adapter = slider_adapter
                }
            }
            .addOnFailureListener {
                Log.d("Fail_Slider", it.localizedMessage)
            }
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
//        ft.setCustomAnimations(
//            R.anim.slide_in_left, R.anim.slide_out_left,
//            R.anim.slide_out_right, R.anim.slide_in_right
//        )
        ft.replace(R.id.frame_full, fragment1!!, tag).addToBackStack(null).commit()
    }

}