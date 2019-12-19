package com.example.itproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itproject.R
import com.example.itproject.adapter.CardAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_card.*
import link.fls.swipestack.SwipeStack

class CardActivity : AppCompatActivity() {

    private var cardTouched : Boolean = false
    private lateinit var array_word : ArrayList<String>
    private lateinit var array_meaning : ArrayList<String>
    private lateinit var array_star : BooleanArray
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        array_word = intent.getStringArrayListExtra("array_word")!!
        array_meaning = intent.getStringArrayListExtra("array_meaning")!!
        array_star = intent.getBooleanArrayExtra("array_star")!!
        val lastIndex : Int = intent.getIntExtra("lastIndex_card", 0)
        title = intent.getStringExtra("title")!!
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
        val stackListener : SwipeStack.SwipeStackListener = object : SwipeStack.SwipeStackListener {

            override fun onViewSwipedToLeft(position: Int) {
                addToDB(position, lastIndex)
                adapter.setCnt(0)
            }

            override fun onViewSwipedToRight(position: Int) {
                addToDB(position, lastIndex)
                adapter.setCnt(0)
            }

            override fun onStackEmpty() {
                moveToResult()
            }

        }
        Card_card.setSwipeProgressListener(listener)
        Card_card.setListener(stackListener)
        Card_card.adapter = adapter

        Card_back.setOnClickListener {
            finish()
        }
    }

    private fun addToDB(position : Int, lastIndex : Int) {
        val index = position + 1 + lastIndex
        val map : HashMap<String, Any> = hashMapOf("lastIndex_card" to index)
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.email!!).collection("sets").document(title).update(map)
        val ac = SetActivity.ac
        ac.setLastIndexCard(index)
    }

    private fun moveToResult() {
        val intent = Intent(this, CardResultActivity::class.java)
        val ac = SetActivity.ac
        intent.putStringArrayListExtra("array_word", ac.getWords())
        intent.putStringArrayListExtra("array_meaning", ac.getMeanings())
        intent.putExtra("array_star", ac.getStars())
        intent.putExtra("title", title)
        startActivity(intent)
        finish()
    }
}
