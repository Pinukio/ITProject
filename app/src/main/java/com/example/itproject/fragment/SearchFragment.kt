package com.example.itproject.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itproject.R
import com.example.itproject.SearchItem
import com.example.itproject.activity.SetActivity
import com.example.itproject.adapter.SearchAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SearchFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    private val array_finished : ArrayList<Boolean> = ArrayList()
    private lateinit var db : FirebaseFirestore
    private val list : ArrayList<SearchItem> = ArrayList()
    private lateinit var recycler : RecyclerView
    private var adapter : SearchAdapter = SearchAdapter(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_search, container, false)

        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(context!!)
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        alertBuilder.setCancelable(false)
        dialog = alertBuilder.create()

        val searchBtn : ImageView = view.findViewById(R.id.Search_searchbtn)
        val edit : EditText = view.findViewById(R.id.Search_edit)
        recycler = view.findViewById(R.id.Search_recycler)
        db = FirebaseFirestore.getInstance()

        searchBtn.setOnClickListener {
            dialog.show()
            setRecycler(edit.text.toString())
        }

        edit.setOnEditorActionListener { v, actionId, event ->
            searchBtn.performClick()
        }
        return view
    }

    private fun setRecycler(text : String) {
        val currentEmail : String = FirebaseAuth.getInstance().currentUser!!.email!!
        db.collection("users").get()
            .addOnSuccessListener {
                array_finished.clear()
                it.forEachIndexed { index, doc ->
                    if(doc.id != currentEmail) {
                        array_finished.add(false)
                        list.clear()
                        val MEGABYTE: Long = 1024 * 1024
                        val profileRef = FirebaseStorage.getInstance().reference.child("${doc.id}/profile.jpg")
                        var bitmap : Bitmap?

                        profileRef.getBytes(MEGABYTE).addOnCompleteListener { it_ ->
                            bitmap = if(it_.isSuccessful) {
                                BitmapFactory.decodeByteArray(it_.result, 0, it_.result!!.size)
                            } else {
                                null
                            }
                            setArray(text, doc.id, bitmap, doc["name"].toString(), index)
                        }
                    }
                    else
                        array_finished.add(true)
                }
            }
    }

    private fun setArray(text : String, email : String, profile : Bitmap?, name : String, index : Int) {
        db.collection("users").document(email).collection("sets").get()
            .addOnSuccessListener {
                for(doc in it) {
                    if(doc.id.contains(text)) {
                        list.add(SearchItem(doc.id, doc["subtitle"].toString(), name, email, profile))
                    }
                }
                array_finished[index] = true
                if(!array_finished.contains(false)) {
                    recycler.layoutManager = LinearLayoutManager(context!!)
                    adapter.changeList(list)
                    recycler.adapter = adapter
                    dialog.dismiss()
                }
            }
    }

    fun moveToSet(title : String, subtitle : String, email : String) {
        val intent = Intent(activity, SetActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("subtitle", subtitle)
        intent.putExtra("email", email)
        startActivity(intent)
    }

}