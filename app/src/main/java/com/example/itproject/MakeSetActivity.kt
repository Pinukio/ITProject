package com.example.itproject

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itproject.MakeSetAdapter.OnItemCheckListener
import kotlinx.android.synthetic.main.activity_make_set.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeSetActivity : AppCompatActivity() {

    private var array_word : ArrayList<String>? = null
    private var list : MutableList<Model>? = null
    private var array_meaning : ArrayList<String>? = null
    private var adapter : MakeSetAdapter? = null
    private var dialog : AlertDialog? = null
    private var array_null : ArrayList<Int>? = null // 뜻이 리턴되지 않은 단어들의 인덱스 저장
    private lateinit var onItemClick : OnItemCheckListener
    private var array_selected : ArrayList<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_set)

        val intent = intent
        array_word = intent.getStringArrayListExtra("array_word")
        array_meaning = ArrayList()
        array_null = ArrayList()
        array_selected = ArrayList()

        onItemClick = object:OnItemCheckListener {

            override fun onItemCheck(index : Int) {
                array_selected!!.add(index)
            }

            override fun onItemUncheck(index: Int) {
                array_selected!!.remove(index)
            }

        }

        if(array_word != null) {

            val builder : AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_loading, null))
            builder.setCancelable(false)
            dialog = builder.create()
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
                for (i in 0..1)
                add(Model(Model.CARD_TYPE, "", ""))
            }


            adapter = MakeSetAdapter(list!!, onItemClick)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(this)
        }

        MakeSet_trash.setOnClickListener {
            adapter!!.deleteItems(array_selected!!)
            array_selected = ArrayList()
        }

        MakeSet_checkbtn.setOnClickListener {
            if(adapter != null) {
                val title : String = MakeSet_recycler.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).text.toString()
                val subtitle : String = MakeSet_recycler.findViewHolderForAdapterPosition(1)!!.itemView.findViewById<EditText>(R.id.MakeSet_subtitle).text.toString()
            }
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

            adapter = MakeSetAdapter(result, onItemClick)
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
}
