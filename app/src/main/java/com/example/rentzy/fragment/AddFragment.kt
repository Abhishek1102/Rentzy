package com.example.rentzy.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.rentzy.R
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.net.URL


class AddFragment : Fragment() {

    lateinit var edt_ownerName: EditText
    lateinit var edt_ownerNumber: EditText
    lateinit var edt_houseName: EditText
    lateinit var edt_houseAddress: EditText
    lateinit var edt_houseDescription: EditText
    lateinit var edt_houseRent: EditText
    lateinit var edt_noofBathroom: EditText
    lateinit var edt_noofBedroom: EditText
    lateinit var edt_noofParking: EditText
    lateinit var edt_noofPool: EditText
    lateinit var edt_noofKitchen: EditText
    lateinit var tv_add: TextView
    lateinit var iv_addImage: ImageView
    lateinit var iv_previewImage: ImageView

    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var reference: DocumentReference
    lateinit var selected_image: Uri
    var image_url = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        edt_ownerName = view.findViewById(R.id.edt_ownerName)
        edt_ownerNumber = view.findViewById(R.id.edt_ownerNumber)
        edt_houseName = view.findViewById(R.id.edt_houseName)
        edt_houseAddress = view.findViewById(R.id.edt_houseAddress)
        edt_houseDescription = view.findViewById(R.id.edt_houseDescription)
        edt_noofBathroom = view.findViewById(R.id.edt_houseBathroom)
        edt_houseRent = view.findViewById(R.id.edt_houseRent)
        edt_noofBedroom = view.findViewById(R.id.edt_bedroom)
        edt_noofParking = view.findViewById(R.id.edt_houseParking)
        edt_noofPool = view.findViewById(R.id.edt_housePool)
        edt_noofKitchen = view.findViewById(R.id.edt_houseKitchen)
        tv_add = view.findViewById(R.id.tv_add)
        iv_addImage = view.findViewById(R.id.iv_addImage)
        iv_previewImage = view.findViewById(R.id.iv_previewImage)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = Firebase.storage.reference

        initView()



        return view
    }

    private fun initView() {

        tv_add.setOnClickListener {
            productValidation()
        }

        iv_addImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
//            imagePickerActivityResult.launch(galleryIntent)
            startActivityForResult(galleryIntent, 200)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            //selected image by user is stored in uri variable i.e. selected_image which is pass on to uploaddata() for uploading to firebase storage
            selected_image = data.data!!
            iv_previewImage.setImageURI(selected_image)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun uploadData() {
        // getting URI of selected Image
        val imageUri: Uri = selected_image

        // val fileName = imageUri?.pathSegments?.last()

        // extract the file name with extension
        val sd = getFileName(requireContext(), imageUri)

        // Upload Task with upload to directory 'file'
        // and name of the file remains same
        val uploadTask = storageReference.child("house_image/$sd").putFile(imageUri)

        // On success, download the file URL and display it
        uploadTask.addOnSuccessListener {
            // using glide library to display the image
            storageReference.child("house_image/$sd").downloadUrl.addOnSuccessListener {

                //storing uploaded photo url in image_url
                image_url = it.toString()
                Log.d("imageurl", it.toString())
                Log.e("Firebase", "download passed")


                //creating hashmap to store inputed data and to upload it in the firestore database
                val reg_house = HashMap<String, Any>()
                reg_house["house_address"] = edt_houseAddress.text.toString().trim()
                reg_house["house_bathroom"] = edt_noofBathroom.text.toString().trim()
                reg_house["house_bedroom"] = edt_noofBedroom.text.toString().trim()
                reg_house["house_description"] = edt_houseDescription.text.toString().trim()
                reg_house["house_kitchen"] = edt_noofKitchen.text.toString().trim()
                reg_house["house_name"] = edt_houseName.text.toString().trim()
                reg_house["house_ownername"] = edt_ownerName.text.toString().trim()
                reg_house["house_ownernumber"] = edt_ownerNumber.text.toString().trim()
                reg_house["house_parking"] = edt_noofParking.text.toString().trim()
                reg_house["house_rent"] = edt_houseRent.text.toString().trim()
                reg_house["house_swimmingpool"] = edt_noofPool.text.toString().trim()
                //uploading url to the database to show in the app
                reg_house["house_image"] = image_url
//        reg_house["house_id"] = edt_houseAddress.text.toString().trim()

                //uploading reg_house hashmap in database collection
                firestore.collection("house_info").add(reg_house).addOnSuccessListener {
                    Log.d("success", "Data Registered" + reg_house)
                    toast("success", "House Added")
                    Handler().postDelayed({
                        replacefragment(DashboardFragment(), "DashboardFragment")
                    }, 1000)
                }.addOnFailureListener {
                    Log.d("failed", "Data failed to registered " + it.localizedMessage)
                    toast("error", "Something went wrong")
                }


            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading " + it.localizedMessage)
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail " + it.localizedMessage)
        }



    }

    private fun productValidation(){
        if (edt_ownerName.text.isEmpty()){
            edt_ownerName.requestFocus()
            edt_ownerName.setError("please enter valid name!")
        }else if (edt_ownerNumber.text.isEmpty()){
            edt_ownerNumber.requestFocus()
            edt_ownerNumber.setError("plese enter number!")
        }else if (edt_ownerNumber.text.toString().length != 10){
            edt_ownerNumber.requestFocus()
            edt_ownerNumber.setError("plese enter valid number!")
        }else if (edt_houseName.text.isEmpty()){
            edt_houseName.requestFocus()
            edt_houseName.setError("plese enter valid name!")
        }else if (edt_houseAddress.text.isEmpty()){
            edt_houseAddress.requestFocus()
            edt_houseAddress.setError("plese enter valid address!")
        }else if (edt_houseRent.text.isEmpty()){
            edt_houseRent.requestFocus()
            edt_houseRent.setError("plese enter valid data!")
        }else if (edt_noofBathroom.text.isEmpty()){
            edt_noofBathroom.requestFocus()
            edt_noofBathroom.setError("plese enter valid data!")
        }else if (edt_noofKitchen.text.isEmpty()){
            edt_noofKitchen.requestFocus()
            edt_noofKitchen.setError("plese enter valid data!")
        }else if (edt_noofPool.text.isEmpty()){
            edt_noofPool.requestFocus()
            edt_noofPool.setError("plese enter valid data!")
        }else if (edt_noofParking.text.isEmpty()){
            edt_noofParking.requestFocus()
            edt_noofParking.setError("plese enter valid data!")
        }else if (edt_houseDescription.text.isEmpty()){
            edt_houseDescription.requestFocus()
            edt_houseDescription.setError("plese enter description!")
        }
        else{
            uploadData()
        }

    }

    private fun toast(type: String, desc: String) {
        val inflater = layoutInflater
        val layout: View =
            inflater.inflate(
                R.layout.custom_toast,
                requireView().findViewById(R.id.lv_customtoast)
            )
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
        ft.replace(R.id.frame_main, fragment1!!, tag).addToBackStack(null).commit()
    }

}