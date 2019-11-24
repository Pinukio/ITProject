package com.example.itproject

import android.content.Context
import android.hardware.input.InputManager
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
import androidx.recyclerview.widget.RecyclerView
import com.example.itproject.MakeSetAdapter.OnItemCheckListener
import kotlinx.android.synthetic.main.activity_make_set.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MakeSetActivity : AppCompatActivity() {

    private var array_word : ArrayList<String>? = null
    private var list : MutableList<Model>? = null
    private var array_meaning : ArrayList<String>? = null
    private var adapter : MakeSetAdapter? = null
    private var dialog : AlertDialog? = null
    private var array_null : ArrayList<Int>? = null // 뜻이 리턴되지 않은 단어들의 인덱스 저장
    private lateinit var onItemClick : OnItemCheckListener
    private var array_selected : ArrayList<Int>? = null
    /*private lateinit var nav : SlidingRootNav
    private lateinit var builder : SlidingRootNavBuilder*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_set)

        val intent = intent
        array_word = intent.getStringArrayListExtra("array_word")
        array_meaning = ArrayList()
        array_null = ArrayList()
        array_selected = ArrayList()

        /*builder = SlidingRootNavBuilder(this)
        setDragStateListener()
        nav = makeNav()
*/
        /*MakeSet_menu.setOnClickListener {
            if(nav.isMenuClosed)
                nav.openMenu()
            else
                nav.closeMenu()
        }

        MakeSet_background.setOnClickListener {
            if(nav.isMenuOpened)
                nav.closeMenu()
        }*/

        MakeSet_back.setOnClickListener {
            finish()
        }

        onItemClick = object:OnItemCheckListener {

            override fun onItemCheck(index : Int) {
                array_selected!!.add(index)
            }

            override fun onItemUncheck(index: Int) {
                array_selected!!.remove(index)
            }
        }

        if(array_word != null) {

            val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
            alertBuilder.setCancelable(false)
            dialog = alertBuilder.create()
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

                array_word = ArrayList()
                array_meaning = ArrayList()

                /*for(i in 1..adapter!!.getLastIndex()) {
                    val word = MakeSet_recycler.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_word).text.toString()
                    val meaning = MakeSet_recycler.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning).text.toString()
                    array_word!!.add(word)
                    array_meaning!!.add(meaning)
                }*/

                postAndNotifyAdapter(Handler(), MakeSet_recycler)

                /*val title : String = MakeSet_recycler.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).text.toString()
                val subtitle : String = MakeSet_recycler.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_subtitle).text.toString()

                if(title.isEmpty()) {
                    Toast.makeText(applicationContext, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(0, "title")
                }
                else if(array_word!!.contains("")) {
                    var index = array_word!!.indexOf("") + 1
                    Toast.makeText(applicationContext, "단어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(index, "word")
                }
                else if(array_meaning!!.contains("")) {
                    var index = array_meaning!!.indexOf("") + 1
                    Toast.makeText(applicationContext, "뜻을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    moveFocus(index, "meaning")
                }*/

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

    /*fun makeNav() : SlidingRootNav {

        val dm : DisplayMetrics = applicationContext.resources.displayMetrics
        val width = (dm.widthPixels * 0.35).toInt()
        return builder.withMenuLayout(R.layout.menu)
            .withDragDistancePx(width)
            .withRootViewScale(0.6f)
            .withRootViewElevation(10)
            .withRootViewYTranslation(0)
            .inject()
    }

    fun setDragStateListener() {

        val listener : DragStateListener = object : DragStateListener{

            override fun onDragEnd(isMenuOpened: Boolean) {
                if(isMenuOpened)
                    MakeSet_background.visibility = View.VISIBLE
                else
                    MakeSet_background.visibility = View.INVISIBLE
            }

            override fun onDragStart() {
            }

        }
        builder.addDragStateListener(listener)
    }*/

    /*private fun removeItem(position : Int) {
        array_word!!.removeAt(position)
        array_meaning!!.removeAt(position)
    }

    fun deleteItems(array_selected : ArrayList<Int>){
        if(array_selected.size > 1)
            Collections.sort(array_selected, AscendingInteger())
        array_selected.forEach {
            removeItem(it)
        }
    }

    internal inner class AscendingInteger : Comparator<Int> { //오름차순으로 정렬
        override fun compare(a: Int, b: Int): Int {
            return b.compareTo(a)
        }
    }*/

    fun moveFocus(position : Int, what : String) {
        val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(what == "word") {
            MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_word).requestFocus()
            imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_word), 0)
        }
        else if(what == "meaning") {
            MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning).requestFocus()
            imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning), 0)
        }
        else if(what == "title") {
            MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).requestFocus()
            imm.showSoftInput(MakeSet_recycler.findViewHolderForAdapterPosition(position)!!.itemView.findViewById<EditText>(R.id.MakeSet_title), 0)
        }
        MakeSet_recycler.scrollToPosition(position)
    }

    private fun postAndNotifyAdapter(handler : Handler, recyclerView : RecyclerView) {
        handler.post {
            if(recyclerView.findViewHolderForAdapterPosition(adapter!!.getLastIndex()) != null) {
                for(i in 1..adapter!!.getLastIndex()) {
                    Log.i("Hello", i.toString())
                    Log.i("World", adapter!!.getLastIndex().toString())
                    //val word = MakeSet_recycler.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_word).text.toString()
                    val word = recyclerView.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_word).text.toString()
                    //val meaning = MakeSet_recycler.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning).text.toString()
                    val meaning = recyclerView.findViewHolderForAdapterPosition(i)!!.itemView.findViewById<EditText>(R.id.MakeSet_meaning).text.toString()
                    array_word!!.add(word)
                    array_meaning!!.add(meaning)
                }
                    //val title : String = MakeSet_recycler.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).text.toString()
                    val title : String = recyclerView.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_title).text.toString()
                    //val subtitle : String = MakeSet_recycler.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_subtitle).text.toString()
                    val subtitle : String = recyclerView.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<EditText>(R.id.MakeSet_subtitle).text.toString()

                    if(title.isEmpty()) {
                        Toast.makeText(applicationContext, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        moveFocus(0, "title")
                    }
                    else if(array_word!!.contains("")) {
                        var index = array_word!!.indexOf("") + 1
                        Toast.makeText(applicationContext, "단어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        moveFocus(index, "word")
                    }
                    else if(array_meaning!!.contains("")) {
                        var index = array_meaning!!.indexOf("") + 1
                        Toast.makeText(applicationContext, "뜻을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        moveFocus(index, "meaning")
                    }
            }
            else {
                postAndNotifyAdapter(handler, recyclerView)
            }
        }
    }
}
