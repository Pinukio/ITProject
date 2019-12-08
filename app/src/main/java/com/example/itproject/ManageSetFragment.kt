package com.example.itproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.top_manage_set.view.*

class ManageSetFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    private val array_title : ArrayList<String> = ArrayList()
    private lateinit var db : FirebaseFirestore
    private lateinit var tmp : CollectionReference
    private val list : ArrayList<MSModel> = ArrayList()
    private lateinit var recycler : RecyclerView
    private var adapter : ManageSetAdapter? = null

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
        /*view.ManageSet_trash.setOnClickListener {
            adapter!!.deleteItems(dialog)
        }*/
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
        var title : String
        if(array_title.size != 0) {
            title = array_title[i]
            tmp.document(title).get()
                .addOnSuccessListener {
                    val subtitle = it["subtitle"].toString()
                    val progress = (it["progress"] as Long).toInt()
                    list.add(MSModel(MSModel.CARD_TYPE, title, subtitle, progress))
                    if(i < array_title.size - 1)
                        makeList(i + 1)
                    else {
                        /*val adapter = ManageSetAdapter(list)
                        recycler.layoutManager = LinearLayoutManager(activity)
                        recycler.adapter = adapter
                        dialog.dismiss()*/
                        setRecycler()
                    }
                }
        }
        else {
            setRecycler()
        }
    }

    private fun setRecycler() {

        val onItemCheck : ManageSetAdapter.OnItemCheckListener = object : ManageSetAdapter.OnItemCheckListener {

            override fun onItemCheck(index : Int) {
                adapter!!.setSelectedArray(index, true)
            }

            override fun onItemUncheck(index: Int) {
                adapter!!.setSelectedArray(index, false)
            }

        }
        adapter = ManageSetAdapter(list, onItemCheck)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        dialog.dismiss()
    }
}