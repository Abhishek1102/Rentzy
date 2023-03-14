package com.example.rentzy.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mygreetingsapp.helper.AppConstant
import com.example.rentzy.R
import com.example.rentzy.helper.SecurePreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firebaseUser: FirebaseUser
    lateinit var firestore: FirebaseFirestore

    lateinit var iv_profile: ImageView
    lateinit var tv_username: TextView
    lateinit var tv_usernumber: TextView
    lateinit var tv_useremail: TextView
    lateinit var tv_userlocation: TextView
    lateinit var iv_back: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        iv_profile = view.findViewById(R.id.iv_userProfileImage)
        tv_username = view.findViewById(R.id.tv_userName)
        tv_usernumber = view.findViewById(R.id.tv_userNumber)
        tv_useremail = view.findViewById(R.id.tv_userEmail)
        tv_userlocation = view.findViewById(R.id.tv_userLocation)
        iv_back = view.findViewById(R.id.iv_profileBack)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        firestore = FirebaseFirestore.getInstance()

        initView()

        iv_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun initView() {

            Glide.with(requireContext()).load(firebaseUser.photoUrl).into(iv_profile)
            tv_username.setText(SecurePreferences.getStringPreference(context,AppConstant.USER_NAME))
            tv_usernumber.setText(SecurePreferences.getStringPreference(context,AppConstant.USER_NUMBER))
            tv_useremail.setText(SecurePreferences.getStringPreference(context,AppConstant.USER_EMAIL))
            tv_userlocation.setText("India")

            firestore.collection("user_info").whereEqualTo("Email",SecurePreferences.getStringPreference(context,AppConstant.USER_EMAIL)).get()
                .addOnSuccessListener { documents->
                    for (document in documents){
                        Log.d("Tag","${document.id}=>${document.data}")
                    }
                }.addOnFailureListener {
                    Log.d("Tag","Error getting documents: ",it)
                }


        googleSignInClient =
            GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
    }
}