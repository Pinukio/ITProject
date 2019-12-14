package com.example.itproject.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itproject.R
import com.example.itproject.adapter.StudyAdapter
import kotlinx.android.synthetic.main.activity_study.*

class StudyActivity : AppCompatActivity() {

    private lateinit var array_word : ArrayList<String>
    private lateinit var adapter : StudyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val intent = intent

        if(intent != null) {
            array_word = intent.getStringArrayListExtra("array_word")!!
            val array_meaning : ArrayList<String> = intent.getStringArrayListExtra("array_meaning")!!
            Study_progress.progressBackgroundColor = Color.LTGRAY
            Study_recycler.layoutManager = LinearLayoutManager(applicationContext)
            adapter =
                StudyAdapter(array_meaning, this)
            Study_recycler.adapter = adapter
            Study_back.setOnClickListener {
                finish()
            }
            setWord(0)
        }
    }

    fun setWord(index : Int) {
        Study_word.text = array_word[index]
        //adapter.setSelections(index)
    }
}
