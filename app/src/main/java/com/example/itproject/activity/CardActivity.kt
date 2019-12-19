package com.example.itproject.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itproject.R
import com.example.itproject.adapter.CardAdapter
import kotlinx.android.synthetic.main.activity_card.*
import link.fls.swipestack.SwipeStack

class CardActivity : AppCompatActivity() {

    private var cardTouched : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        //val adapter = CardAdapter()
        val intent = intent
        val array_word : ArrayList<String> = intent.getStringArrayListExtra("array_word")!!
        val array_meaning : ArrayList<String> = intent.getStringArrayListExtra("array_meaning")!!
        val array_star : BooleanArray = intent.getBooleanArrayExtra("array_star")!!
        val adapter = CardAdapter(array_word, array_meaning, array_star)
        val listener : SwipeStack.SwipeProgressListener = object : SwipeStack.SwipeProgressListener {
            override fun onSwipeEnd(position: Int) {
                if(cardTouched)
                    adapter.click(position)
            }

            override fun onSwipeStart(position: Int) {
                cardTouched = true
            }

            override fun onSwipeProgress(position: Int, progress: Float) {
                cardTouched = false
            }

        }
        Card_card.setSwipeProgressListener(listener)
        Card_card.adapter = adapter

        Card_back.setOnClickListener {
            finish()
        }
    }
}
