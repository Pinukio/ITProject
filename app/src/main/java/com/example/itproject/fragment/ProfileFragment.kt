package com.example.itproject.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.itproject.R
import com.example.itproject.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private lateinit var dialog : AlertDialog
    private lateinit var image : ImageView
    private lateinit var ad : AlertDialog
    private lateinit var email : String
    private lateinit var profileRef : StorageReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.frame_profile, container, false)
        val user = FirebaseAuth.getInstance().currentUser!!

        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(context!!)
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        alertBuilder.setCancelable(false)
        dialog = alertBuilder.create()
        dialog.show()
        email = user.email!!
        val storageRef = FirebaseStorage.getInstance().reference
        profileRef = storageRef.child( "${email}/profile.jpg")

        (activity as MainActivity).getFixBtn().setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(context!!)

            val editProfile = inflater.inflate(R.layout.dialog_edit_profile, null)
            editProfile.findViewById<TextView>(R.id.editProfile_image).setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
                dialog.show()
            }
            editProfile.findViewById<TextView>(R.id.editProfile_name).setOnClickListener {
                val builder_ : AlertDialog.Builder = AlertDialog.Builder(context!!)
                val updateName = inflater.inflate(R.layout.dialog_update_name, null)
                val edit : EditText = updateName.findViewById(R.id.updateName_edit)
                builder_.setView(updateName)
                builder_.setNegativeButton("취소") {dialog, _ ->
                    dialog.cancel()
                }
                builder_.setPositiveButton("변경") {d, _ ->

                    val text = edit.text.toString()
                    if(text.isNotBlank()) {
                        val map : HashMap<String, Any> = hashMapOf("name" to edit.text.toString())
                        dialog.show()
                        ad.dismiss()
                        FirebaseFirestore.getInstance().collection("users").document(email).update(map)
                            .addOnSuccessListener {
                                Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                                view.findViewById<TextView>(R.id.Profile_name).text = text
                                dialog.dismiss()
                            }
                    }
                    else {
                        Toast.makeText(context, "닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }
                builder_.show()
            }
            builder.setView(editProfile)
            ad = builder.create()
            ad.show()
        }

        image = view.findViewById<CircleImageView>(R.id.Profile_image)
        view.findViewById<TextView>(R.id.Profile_email).text = email
        view.findViewById<TextView>(R.id.Profile_size_sets).text = "${(activity as MainActivity).getSetsSize()}개"
        FirebaseFirestore.getInstance().collection("users").document(email).get()
            .addOnSuccessListener {
                view.findViewById<TextView>(R.id.Profile_name).text = it["name"].toString()
                setImage()
            }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, data!!.data)
            uploadFile(bitmap)
        }
    }

    private fun uploadFile(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
        val data: ByteArray = baos.toByteArray()
        profileRef.putBytes(data)
            .addOnSuccessListener {
                ad.dismiss()
                setImage()
            }
    }

    private fun setImage() {
        val MEGABYTE: Long = 1024 * 1024
        profileRef.getBytes(MEGABYTE).addOnCompleteListener {
            if(it.isSuccessful) {
                val bitmap = BitmapFactory.decodeByteArray(it.result, 0, it.result!!.size)
                image.setImageBitmap(bitmap)
            }
            else {
                image.setImageResource(R.drawable.profile_user)
            }
            dialog.dismiss()
            Toast.makeText(context!!, "변경되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
