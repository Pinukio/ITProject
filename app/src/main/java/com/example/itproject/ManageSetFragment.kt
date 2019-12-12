package com.example.itproject

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ManageSetFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    private val array_title : ArrayList<String> = ArrayList()
    private lateinit var db : FirebaseFirestore
    private lateinit var tmp : CollectionReference
    private val list : ArrayList<MSItem> = ArrayList()
    private lateinit var recycler : RecyclerView
    private var adapter : ManageSetAdapter? = null
    private var allItemsChecked : Boolean = false
    private lateinit var cb : CheckBox

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
        /*view.findViewById<FloatingActionButton>(R.id.ManageSet_addbtn).setOnClickListener {
            startActivity(Intent(activity, MakeSetActivity::class.java))
            //activity!!.supportFragmentManager.beginTransaction().remove(this)
            //(activity!! as MainActivity).setToolbarTitle("")
        }*/
        val search : EditText = view.findViewById(R.id.ManageSet_edit)
        search.addTextChangedListener(MyTextWatcher())

        makeTitleArray()
        cb = view.findViewById(R.id.ManageSet_checkAll)
        cb.setOnClickListener {
            if(!allItemsChecked) {
                adapter!!.checkAllCb()
                allItemsChecked = true
            }
            else {
                adapter!!.unCheckAllCb()
                allItemsChecked = false
            }
        }
        view.findViewById<ImageView>(R.id.ManageSet_trash).setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(context!!)
            builder.setMessage("정말로 삭제하시겠습니까?")
            builder.setPositiveButton("예"
            ) { _, _ ->
                adapter!!.deleteItems()
                search.setText("")
            }
            builder.setNegativeButton("아니요"
            ) { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        }
        return view
    }

    private fun makeTitleArray() {
        tmp.get()
            .addOnSuccessListener {
                for(doc in it) {
                    array_title.add(doc.id)
                }
                makeList(0)
            }
    }

    private fun makeList(i : Int) {
        val title : String
        if(array_title.size != 0) {
            title = array_title[i]
            tmp.document(title).get()
                .addOnSuccessListener {
                    val subtitle = it["subtitle"].toString()
                    val progress = (it["progress"] as Long).toInt()
                    list.add(MSItem(title, subtitle, progress))
                    if(i < array_title.size - 1)
                        makeList(i + 1)
                    else {
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
        //val imm : InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        adapter = ManageSetAdapter(list, onItemCheck, this)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        dialog.dismiss()
    }

    inner class MyTextWatcher() : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            adapter!!.filter(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    fun changeCbState(b : Boolean) {
        allItemsChecked = b
        cb.isChecked = b
    }

}