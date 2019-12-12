package com.example.itproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set.*
import java.io.Serializable

class SetActivity : AppCompatActivity() {

    private var dialog : AlertDialog? = null
    private var size : Int = 0
    private val array_word : ArrayList<String> = ArrayList()
    private val array_meaning : ArrayList<String> = ArrayList()
    private lateinit var tmp : DocumentReference
    private var list : MutableList<Model>? = null
    private var title : String = ""
    private var subtitle : String = ""
    private lateinit var db : FirebaseFirestore
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)

        val intent = intent

        if(intent != null) {

            val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
            alertBuilder.setCancelable(false)
            dialog = alertBuilder.create()
            dialog!!.show()

            db = FirebaseFirestore.getInstance()
            email = FirebaseAuth.getInstance().currentUser!!.email!!
            title = intent.getStringExtra("title")!!
            subtitle = intent.getStringExtra("subtitle")!!
            tmp = db.collection("users").document(email).collection("sets").document(title)
            tmp.get()
                .addOnSuccessListener {
                    size = (it["size"] as Long).toInt()
                    for(i in 0 until size) {
                        array_word.add("")
                        array_meaning.add("")
                    }
                    if(size != 0)
                        setArray(0)
                }

            SetActivity_back.setOnClickListener {
                finish()
            }
        }

    }

    private fun setArray(i : Int) {
        tmp.collection("_").document(i.toString()).get()
            .addOnSuccessListener {
                array_word[i] = it["word"].toString()
                array_meaning[i] = it["meaning"].toString()
                if(i == size - 1) {
                    setRecycler()
                    dialog!!.dismiss()
                }
                else {
                    setArray(i + 1)
                }
            }
    }

    private fun setRecycler() {
        SetActivity_recycler.layoutManager = LinearLayoutManager(this)
        list = mutableListOf<Model>().apply {
            add(Model(Model.TITLE_TYPE, title, subtitle))
            for(i in 0 until array_word.size) {
                add(Model(Model.CARD_TYPE, array_word[i], array_meaning[i]))
            }
        }
        var name: String
        db.collection("users").document(email).get()
            .addOnSuccessListener {
                name = it["name"].toString()
                val adapter = SetAdapter(list!!, name)
                SetActivity_recycler.adapter = adapter
            }

    }
}
