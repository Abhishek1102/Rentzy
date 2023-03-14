package com.example.rentzy.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentzy.R
import com.example.rentzy.adapter.FaqAdapter
import com.example.rentzy.model.FaqModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FaqFragment : Fragment() {

    lateinit var iv_back:ImageView
    lateinit var rv_faq:RecyclerView

    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var faqAdapter: FaqAdapter
    lateinit var faqList: MutableList<FaqModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        //initialization
        faqList = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        iv_back = view.findViewById(R.id.iv_faqback)
        rv_faq = view.findViewById(R.id.rv_faq)

        initView()
        getlistData()

        return view
    }

    private fun getlistData() {
        firebaseFirestore.collection("faq_info")
            .get().addOnSuccessListener { documents->
                for (document in documents){
                    val user = documents.toObjects(FaqModel::class.java)
                    faqAdapter = FaqAdapter(context,user)
                    rv_faq.adapter = faqAdapter
                }
            }.addOnFailureListener {
                Log.d("Fail_List",it.localizedMessage)
            }
    }

    private fun initView() {
        rv_faq.setHasFixedSize(true)
        rv_faq.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        iv_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}