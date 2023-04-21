package com.example.rentzy.fragment

import android.app.Activity
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Log.d
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
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
    lateinit var iv_bell: ImageView
    lateinit var iv_profile: ImageView
    lateinit var sv_dashboard: SearchView

    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    lateinit var sliderList: MutableList<Slider_Model>

    private var interstitialAd: InterstitialAd? = null
    private var adcount = 0

    var mList = ArrayList<DashboardModel>()

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

        MobileAds.initialize(requireContext()){
            loadadds()
        }

        initView()
        getImages()
        getListData()

        iv_bell.setOnClickListener {
            replacefragment(NotificationFragment(), "NotificationFragment")
        }

        iv_profile.setOnClickListener {
            replacefragment(ProfileFragment(), "ProfileFragment")
        }

        sv_dashboard.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })


        return view
    }

    private fun loadadds() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                    Toast.makeText(context, "Ad failed to load", Toast.LENGTH_SHORT).show()
                    d("Ads", "fail to load error ${p0.message}")
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    Toast.makeText(context, "Ad Loaded successfully", Toast.LENGTH_SHORT).show()
                    interstitialAd = p0
                }

            })
    }

    private fun showadds() {
        if (interstitialAd != null) {
            //after every 3 clicks ad is displayed
            if (adcount%3==0){
                interstitialAd?.show(context as Activity)

                interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        Toast.makeText(context, "Ad was clicked", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        interstitialAd = null
                        Toast.makeText(context, "Ad failed to show", Toast.LENGTH_SHORT).show()
                        d("Ads", "fail to show ads ${p0.message}")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        Toast.makeText(context, "Ad was dismissded", Toast.LENGTH_SHORT).show()
                        loadadds()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Toast.makeText(context, "Ad showed fullscreen content", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                Toast.makeText(context, "no ads to display yet", Toast.LENGTH_SHORT).show()
            }

        } else {
            d("Ads", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<DashboardModel>()
            //searching for result in list
            for (i in mList) {
                if (i.house_name.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            //checking if list is empty then toast will be shown
            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_LONG).show()
            }
            //if list is not empty i.e. search result is available then list is sent to adapter
            else {
                dashboardAdapter.setFilteredList(filteredList)
            }
        }
    }


    private fun initView() {

        sliderList = ArrayList()

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

    private fun getListData() {
        firebaseFirestore.collection("house_info")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = documents.toObjects(DashboardModel::class.java)
                    //onclicked is called and model is passed to it
                    dashboardAdapter = DashboardAdapter(context, user, ::onClicked)
                    mList = user as ArrayList<DashboardModel>
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
        replacefragment(HouseProfileFragment(dashboardModel), "HouseProfileFragment")
        showadds()
        adcount+=1
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