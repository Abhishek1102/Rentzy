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
import com.example.rentzy.adapter.NotificationAdapter
import com.example.rentzy.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class NotificationFragment : Fragment() {

    lateinit var rv_notification: RecyclerView
    lateinit var iv_notificationback: ImageView

    lateinit var notificationList: MutableList<NotificationModel>
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var storageRef: StorageReference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        //initialization
        notificationList = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()


        rv_notification = view.findViewById(R.id.rv_notification)
        iv_notificationback = view.findViewById(R.id.iv_notificationBack)

        initView()
        getListData()

        return view
    }

    private fun initView() {

        rv_notification.setHasFixedSize(true)
        rv_notification.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        iv_notificationback.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun getListData() {
        firebaseFirestore.collection("notification_info")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = documents.toObjects(NotificationModel::class.java)
                    notificationAdapter = NotificationAdapter(context,user)
                    rv_notification.adapter = notificationAdapter
                }
            }.addOnFailureListener {
                Log.d("Fail_List", it.localizedMessage)
//                toast("error", "Fail to load data")
            }
    }

    fun replacefragment(fragment1: Fragment?, tag: String?) {
        val ft = requireFragmentManager().beginTransaction()
//        ft.setCustomAnimations(
//            R.anim.slide_in_left, R.anim.slide_out_left,
//            R.anim.slide_out_right, R.anim.slide_in_right
//        )
        ft.replace(R.id.frame_main, fragment1!!, tag).addToBackStack(null).commit()
    }

}