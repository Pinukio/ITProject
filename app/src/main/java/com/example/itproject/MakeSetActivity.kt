package com.example.itproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itproject.MakeSetAdapter.OnItemCheckListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_make_set.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MakeSetActivity : AppCompatActivity() {

    private var array_word : ArrayList<String>? = null
    private var list : MutableList<Model>? = null
    private var array_meaning : ArrayList<String>? = null
    private var adapter : MakeSetAdapter? = null
    private var dialog : AlertDialog? = null
    private var array_null : ArrayList<Int>? = null // 뜻이 리턴되지 않은 단어들의 인덱스 저장
    private lateinit var onItemClick : OnItemCheckListener
    private var array_selected : ArrayList<Int>? = null
    private var acti : MakeSetActivity? = null
    private var count = 0
    private var title = ""
    private var subtitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_set)

        val intent = intent
        array_word = intent.getStringArrayListExtra("array_word")
        array_meaning = ArrayList()
        array_null = ArrayList()
        array_selected = ArrayList()

        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        alertBuilder.setCancelable(false)
        dialog = alertBuilder.create()

        MakeSet_back.setOnClickListener {
            finish()
        }

        onItemClick = object:OnItemCheckListener {

            override fun onItemCheck(index : Int) {
                array_selected!!.add(index)
                adapter!!.setSelectedArray(index, true)
            }

            override fun onItemUncheck(index: Int) {
                array_selected!!.remove(index)
                adapter!!.setSelectedArray(index, false)
            }
        }

        if(array_word != null) {
            acti = this
            dialog!!.show()

            for(i in 0 until array_word!!.size)
                array_meaning!!.add("")

            val retrofitService=RetrofitService.create()
            array_word!!.forEachIndexed { index, s ->
                retrofitService.getWordRetrofit(s).enqueue(object : Callback<WordRepo> {
                    override fun onFailure(call: Call<WordRepo>, t: Throwable) {}
                    override fun onResponse(call: Call<WordRepo>, response: Response<WordRepo>) {
                        val wordrepo: WordRepo? = response.body()
                        if (wordrepo?.meaning?.korean == null) {
                            array_meaning!![index] = ""
                            array_null!!.add(index)
                        }
                        else
                        array_meaning!![index] = wordrepo.meaning.korean
                        MakeListTask().execute()

                    }
                })
            }
        }

        else {
            list = mutableListOf<Model>().apply {
                add(Model(Model.TITLE_TYPE, "", ""))
            }
            adapter = MakeSetAdapter(list!!, onItemClick, true)
            for(i in 0..1)
            adapter!!.addItem()
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(this)
        }

        MakeSet_trash.setOnClickListener {
            adapter!!.deleteItems(array_selected!!)
            array_selected = ArrayList()
        }

        MakeSet_checkbtn.setOnClickListener {
            if(adapter != null) {

                array_word = adapter!!.getWords()
                array_meaning = adapter!!.getMeanings()
                title = adapter!!.getTitleText()
                subtitle = adapter!!.getSubtitleText()

                if(title.isEmpty()) {
                    Toast.makeText(applicationContext, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(0, "title")
                }

                else if(array_word!!.size == 0) {
                    Toast.makeText(applicationContext, "카드를 만들어 주세요.", Toast.LENGTH_SHORT).show()
                }
                else if(array_word!!.contains("")) {
                    var index = array_word!!.indexOf("") + 1
                    Log.i("흠음", array_word!!.size.toString())
                    Toast.makeText(applicationContext, "단어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(index, "word")
                }
                else if(array_meaning!!.contains("")) {
                    var index = array_meaning!!.indexOf("") + 1
                    Toast.makeText(applicationContext, "뜻을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(index, "meaning")
                }

                else {
                    dialog!!.show()
                    addToDB(0)

                    /*for(i in 0 until array_word!!.size) {
                        //val map : HashMap<String, String> = HashMap()
                        //map["word"] = array_word!![i]
                        //map["meaning"] = array_meaning!![i]
                        val map = hashMapOf(
                            "word" to array_word!![i],
                            "meaning" to array_meaning!![i],
                            "star" to false
                        )

                        tmp.collection("_").document(i.toString()).set(map)
                            .addOnSuccessListener {
                                if(i == array_word!!.size - 1) {
                                    dialog!!.dismiss()
                                    val intent = Intent(this, SetActivity::class.java)
                                    //intent.putExtra("array_word", array_word)
                                    //intent.putExtra("array_meaning", array_meaning)
                                    intent.putExtra("title", title)
                                    intent.putExtra("subtitle", subtitle)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        count++
                    }
                    //subMap["subtitle"] = subtitle
                    //subMap["size"] = count
                    val subMap = hashMapOf(
                        "subtitle" to subtitle,
                        "size" to count
                    )

                    tmp.set(subMap)*/

                }


            }
        }

        MakeSet_addbtn.setOnClickListener {
            val lastIndex = adapter!!.addItem()
            MakeSet_recycler.scrollToPosition(lastIndex)
        }
    }

    inner class MakeListTask : AsyncTask<Void, Void, MutableList<Model>>() {

        override fun doInBackground(vararg params: Void?): MutableList<Model> {

            list = mutableListOf<Model>().apply {
                add(Model(Model.TITLE_TYPE, "", ""))
                for(i in 0 until array_meaning!!.size) {
                    add(Model(Model.CARD_TYPE, array_word!![i], array_meaning!![i]))
                }
            }
            return list!!

        }

        override fun onPostExecute(result: MutableList<Model>) {
            super.onPostExecute(result)

            adapter = MakeSetAdapter(result, onItemClick, false)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(applicationContext)

            var tmp = false

            for(i in 0 until array_meaning!!.size) {
                if(array_meaning!![i] == "") { //뜻이 없을 경우 예외처리

                    if(array_null!!.contains(i)) //api에서 뜻이 리턴되지 않은 경우
                        tmp = true
                }
                else
                    tmp = true
            }

            if(tmp)
                dialog!!.dismiss()
        }
    }

    fun moveFocus(position : Int, what : String) {
        val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        MakeSet_recycler.scrollToPosition(position)
        Handler().postDelayed({
            if(what == "word") {
                MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_word).requestFocus()
                imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_word), 0)
            } else if(what == "meaning") {
                MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning).requestFocus()
                imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning), 0)
            } else if(what == "title") {
                MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).requestFocus()
                imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_title), 0)
            }
        }, 100)
    }


    fun addToDB(i : Int) {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val email: String = FirebaseAuth.getInstance().currentUser!!.email.toString()

        val tmp = db.collection("users").document(email).collection("sets").document(title)
        val map = hashMapOf(
            "word" to array_word!![i],
            "meaning" to array_meaning!![i],
            "star" to false
        )

        tmp.collection("_").document(i.toString()).set(map)
            .addOnSuccessListener {
                count++
                if(i == array_word!!.size - 1) {
                    dialog!!.dismiss()
                    val intent = Intent(this, SetActivity::class.java)
                    val subMap = hashMapOf(
                        "subtitle" to subtitle,
                        "size" to count,
                        "progress" to 0
                    )
                    tmp.set(subMap)

                    val sf : SharedPreferences = getSharedPreferences("count_sets", Context.MODE_PRIVATE)
                    val sf_countBefore : SharedPreferences = getSharedPreferences("count_sets_before", Context.MODE_PRIVATE)
                    val et : SharedPreferences.Editor = sf.edit()
                    val count_before : Int = sf_countBefore.getInt("sets_before", 0)
                    et.putInt("sets", count_before + 1)
                    et.apply()

                    intent.putExtra("title", title)
                    intent.putExtra("subtitle", subtitle)
                    startActivity(intent)
                    finish()
                }
                else {
                    addToDB(i + 1)
                }
            }
    }
}
