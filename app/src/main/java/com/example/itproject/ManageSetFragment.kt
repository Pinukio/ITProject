package com.example.itproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ManageSetFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    private val array_title : ArrayList<String> = ArrayList()
    private lateinit var db : FirebaseFirestore
    private lateinit var tmp : CollectionReference
    private var count = 0
    private val list : ArrayList<MSModel> = ArrayList()
    private lateinit var recycler : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_manage_set, container, false)
        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(context!!)
        val inflater : LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        alertBuilder.setCancelable(false)
        dialog = alertBuilder.create()
        dialog.show()
        db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        tmp = db.collection("users").document(email).collection("sets")
        recycler = view.findViewById(R.id.ManageSet_recycler)
        makeTitleArray()
        return view
    }

    private fun makeTitleArray() {
        tmp.get()
            .addOnSuccessListener {
                for(doc in it) {
                    /*count++
                    if(array_title.size < count) {
                        for(i in 0 until count)
                            array_title.add("")
                    }
                    else {
                        array_title[count - 1] = doc.id
                    }*/
                    array_title.add(doc.id)
                }
                list.add(MSModel(MSModel.TOP))
                makeList(0)
            }
    }

    private fun makeList(i : Int) {
        val title = array_title[i]
        tmp.document(title).get()
            .addOnSuccessListener {
                val subtitle = it["subtitle"].toString()
                val progress = (it["progress"] as Long).toInt()
                list.add(MSModel(MSModel.CARD_TYPE, title, subtitle, progress))
                if(i < array_title.size - 1)
                    makeList(i + 1)
                else {
                    val adapter = ManageSetAdapter(list)
                    recycler.layoutManager = LinearLayoutManager(context)
                    recycler.adapter = adapter
                }
            }
    }
}