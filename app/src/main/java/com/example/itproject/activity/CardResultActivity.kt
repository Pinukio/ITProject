package com.example.itproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itproject.R
import kotlinx.android.synthetic.main.activity_card_result.*

class CardResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_result)

        CardResult_back.setOnClickListener {
            finish()
        }

        CardResult_btn.setOnClickListener {
            val intent_ = Intent(this, CardActivity::class.java)
            intent_.putStringArrayListExtra("array_word", intent.getStringArrayListExtra("array_word"))
            intent_.putStringArrayListExtra("array_meaning", intent.getStringArrayListExtra("array_meaning"))
            intent_.putExtra("array_star", intent.getBooleanArrayExtra("array_star"))
            intent_.putExtra("title", intent.getStringExtra("title"))
            startActivity(intent_)
            finish()
        }
    }
}
