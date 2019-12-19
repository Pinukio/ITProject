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
    private var lastIndex_card : Int = 0
    private var isFromOther : Boolean = false
    private lateinit var array_star : BooleanArray
    private var fix = true
    private lateinit var adapter : SetAdapter
    companion object {
        lateinit var ac : SetActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)

        if(intent != null) {

            val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
            alertBuilder.setCancelable(false)
            dialog = alertBuilder.create()
            dialog!!.show()

            db = FirebaseFirestore.getInstance()
            if(intent.getStringExtra("email") == null) {
                email = FirebaseAuth.getInstance().currentUser!!.email!!
            }
            else {
                email = intent.getStringExtra("email")!!
                isFromOther = true
                SetActivity_fixBtn.setImageResource(R.drawable.share__white)
                fix = false
            }
            title = intent.getStringExtra("title")!!
            subtitle = intent.getStringExtra("subtitle")!!
            tmp = db.collection("users").document(email).collection("sets").document(title)
            tmp.get()
                .addOnSuccessListener {
                    size = (it["size"] as Long).toInt()
                    progress = (it["progress"] as Double).toFloat()
                    array_incorrect = it["array_incorrect"] as ArrayList<Int>
                    lastIndex = (it["lastIndex"] as Long).toInt()
                    lastIndex_card = (it["lastIndex_card"] as Long).toInt()
                    array_star = BooleanArray(size)
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
                val intent = Intent(this, MakeSetActivity::class.java)
                intent.putStringArrayListExtra("array_word", array_word)
                intent.putStringArrayListExtra("array_meaning", array_meaning)
                intent.putExtra("title", title)
                intent.putExtra("subtitle", subtitle)
                intent.putExtra("array_star", array_star)
                intent.putExtra("fix", fix)
                startActivity(intent)
            }

            ac = this
        }

    }

    private fun setArray(i : Int) {
        tmp.collection("_").document(i.toString()).get()
            .addOnSuccessListener {
                array_word[i] = it["word"].toString()
                array_meaning[i] = it["meaning"].toString()
                array_star[i] = it["star"] as Boolean
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
        var profile : String
        db.collection("users").document(email).get()
            .addOnSuccessListener {
                name = it["name"].toString()
                profile = it["profile"].toString()
                adapter =
                    SetAdapter(list!!, name, profile, this)
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
        if(lastIndex_card != array_word.size) {
            val tmp_word : ArrayList<String> = ArrayList()
            val tmp_meaning : ArrayList<String> = ArrayList()
            val tmp_star = BooleanArray(array_word.size - lastIndex_card)
            for((index, i) in (lastIndex_card until array_word.size).withIndex()) {
                tmp_word.add(array_word[i])
                tmp_meaning.add(array_meaning[i])
                tmp_star[index] = array_star[i]
            }
            intent.putStringArrayListExtra("array_word", tmp_word)
            intent.putStringArrayListExtra("array_meaning", tmp_meaning)
            intent.putExtra("array_star", tmp_star)
            intent.putExtra("lastIndex_card", lastIndex_card)
        }
        else {
            intent.putStringArrayListExtra("array_word", array_word)
            intent.putStringArrayListExtra("array_meaning", array_meaning)
            intent.putExtra("array_star", array_star)

        }
        intent.putExtra("title", title)
        startActivity(intent)
    }

    fun getIsFromOther() : Boolean {
        return isFromOther
    }

    fun setStar(index : Int, b : Boolean) {
        array_star[index] = b
    }

    fun getStar(index : Int) : Boolean {
        return array_star[index]
    }

    fun changeStar(i : Int, b : Boolean) {
        adapter.changeStar(i, b)
    }

    fun setLastIndexCard(i : Int) {
        lastIndex_card = i
    }

    fun getWords() : ArrayList<String> {
        return array_word
    }

    fun getMeanings() : ArrayList<String> {
        return array_meaning
    }

    fun getStars() : BooleanArray {
        return array_star
    }
}
