package com.example.itproject

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_make_set.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeSetActivity : AppCompatActivity() {

    private var array_word : ArrayList<String>? = null
    private var list : MutableList<Model>? = null
    private var array_meaning : ArrayList<String>? = null
    private var adapter : MakeSetAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_set)

        val intent = intent
        array_word = intent.getStringArrayListExtra("array_word")
        array_meaning = ArrayList()

        if(array_word != null) {
            for(i in 0 until array_word!!.size)
                array_meaning!!.add("")

            val retrofitService=RetrofitService.create()
            array_word!!.forEachIndexed { index, s ->
                retrofitService.getWordRetrofit(s).enqueue(object : Callback<WordRepo> {
                    override fun onFailure(call: Call<WordRepo>, t: Throwable) {}
                    override fun onResponse(call: Call<WordRepo>, response: Response<WordRepo>) {
                        val wordrepo: WordRepo? = response.body()
                        array_meaning!![index] = wordrepo?.meaning?.korean!!
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

            val adapter = MakeSetAdapter(list!!)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(this)
        }

        MakeSet_check.setOnClickListener {
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
                    Log.i(array_word!![i], array_meaning!![i])
                }
            }
            return list!!

        }

        override fun onPostExecute(result: MutableList<Model>) {
            super.onPostExecute(result)

            adapter = MakeSetAdapter(result)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(applicationContext)
        }
    }
}
