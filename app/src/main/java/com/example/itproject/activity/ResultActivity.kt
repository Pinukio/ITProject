package com.example.itproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itproject.R
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        Result_back.setOnClickListener {
            finish()
        }

        val intent = intent
        if(intent != null) {
            val progress = intent.getFloatExtra("progress", 0f)
            val progressText = "${progress.toInt()}%"
            val array_incorrect : ArrayList<Int> = intent.getIntegerArrayListExtra("array_incorrect")!!
            val sizeText = "${array_incorrect.size}개"
            Result_progress.text = progressText
            Result_sizeIncorrect.text = sizeText
            if(array_incorrect.size == 0)
                Result_btn.text = "다시 학습하기"
            Result_btn.setOnClickListener {
                val intent_ = Intent(this, StudyActivity::class.java)
                intent_.putStringArrayListExtra("array_word", intent.getStringArrayListExtra("array_word"))
                intent_.putStringArrayListExtra("array_meaning", intent.getStringArrayListExtra("array_meaning"))
                intent_.putExtra("title", intent.getStringExtra("title"))
                intent_.putIntegerArrayListExtra("array_incorrect", array_incorrect)
                intent_.putExtra("lastIndex", intent.getIntExtra("lastIndex", 0))
                intent_.putExtra("progress", progress)
                startActivity(intent_)
                finish()
            }

        }
    }
}
