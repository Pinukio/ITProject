package com.example.itproject

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    //private var itemTouchHelper : ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_set)

        val intent = intent
        array_word = intent.getStringArrayListExtra("array_word")
        array_meaning = ArrayList()
        array_null = ArrayList()


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

            adapter = MakeSetAdapter(list!!)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(this)
            //itemTouchHelper = ItemTouchHelper(simpleCallback)
            //itemTouchHelper!!.attachToRecyclerView(MakeSet_recycler)
        }

        MakeSet_trash.setOnClickListener {
            adapter!!.deleteItems()
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

            adapter = MakeSetAdapter(result)
            MakeSet_recycler.adapter = adapter
            MakeSet_recycler.layoutManager = LinearLayoutManager(applicationContext)
            //itemTouchHelper = ItemTouchHelper(simpleCallback)
            //itemTouchHelper!!.attachToRecyclerView(MakeSet_recycler)

            var tmp = false

            for(i in 0 until array_meaning!!.size) {
                if(array_meaning!![i] == "") {

                    for(j in 0 until array_null!!.size)
                        if(i == array_null!![j])
                            tmp = true
                }
                else
                    tmp = true
            }

            if(tmp)
                dialog!!.dismiss()
        }
    }

    fun updateList() {
        list = mutableListOf<Model>().apply {
            add(Model(Model.TITLE_TYPE, "", ""))
            for(i in 0 until array_meaning!!.size) {
                add(Model(Model.CARD_TYPE, array_word!![i], array_meaning!![i]))
            }
        }

        adapter = MakeSetAdapter(list!!)
    }

    /*val simpleCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            //타이틀 부분을 제외해야 함
            if(array_word != null) {

                if(array_word!!.size != 0) {
                    *//*val index = position - 1
                    array_word!!.removeAt(index)
                    array_meaning!!.removeAt(index)

                    for(i in 0..index)
                        if(array_null!![i] == index)
                            array_null!!.remove(index)*//*

                    adapter!!.removeItem(position)
                }

            }

            else {
                adapter!!.removeItem(position)
            }

        }

    }*/

}
