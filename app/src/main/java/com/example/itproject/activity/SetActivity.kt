package com.example.itproject.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itproject.Model
import com.example.itproject.R
import com.example.itproject.adapter.SetAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set.*

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
    private var progress = 0f
    private lateinit var array_incorrect : ArrayList<Int>
    private var lastIndex : Int = 0

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
                    progress = (it["progress"] as Double).toFloat()
                    array_incorrect = it["array_incorrect"] as ArrayList<Int>
                    lastIndex = (it["lastIndex"] as Long).toInt()
                    for(i in 0 until size) {
                        array_word.add("")
                        array_meaning.add("")
                        setArray(i)
                    }

                }

            SetActivity_back.setOnClickListener {
                finish()
            }

            SetActivity_fixBtn.setOnClickListener {
                val intent_ = Intent(this, MakeSetActivity::class.java)
                intent_.putStringArrayListExtra("array_word", array_word)
            }
        }

    }

    private fun setArray(i : Int) {
        tmp.collection("_").document(i.toString()).get()
            .addOnSuccessListener {
                array_word[i] = it["word"].toString()
                array_meaning[i] = it["meaning"].toString()
                if(!array_word.contains("")) {
                    setRecycler()
                    dialog!!.dismiss()
                }
            }
    }

    private fun setRecycler() {
        SetActivity_recycler.layoutManager = LinearLayoutManager(this)
        list = mutableListOf<Model>().apply {
            add(
                Model(
                    Model.TITLE_TYPE,
                    title,
                    subtitle
                )
            )
            for(i in 0 until array_word.size) {
                add(
                    Model(
                        Model.CARD_TYPE,
                        array_word[i],
                        array_meaning[i]
                    )
                )
            }
        }
        var name: String
        db.collection("users").document(email).get()
            .addOnSuccessListener {
                name = it["name"].toString()
                val adapter =
                    SetAdapter(list!!, name, this)
                SetActivity_recycler.adapter = adapter
            }
    }

    fun moveToStudy() {
        val intent = Intent(this, StudyActivity::class.java)
        intent.putStringArrayListExtra("array_word", array_word)
        intent.putStringArrayListExtra("array_meaning", array_meaning)
        intent.putIntegerArrayListExtra("array_incorrect", array_incorrect)
        intent.putExtra("title", title)
        intent.putExtra("progress", progress)
        intent.putExtra("lastIndex", lastIndex)
        startActivity(intent)
    }

    fun moveToCard() {
        val intent = Intent(this, CardActivity::class.java)
        intent.putStringArrayListExtra("array_word", array_word)
        intent.putStringArrayListExtra("array_meaning", array_meaning)
        startActivity(intent)
    }
}
