package com.example.itproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

class MakeSetAdapter(val list : MutableList<Model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /*private var array_word : ArrayList<String>? = null
    private var array_meaning : ArrayList<String>? = null
    private lateinit var title : String
    private lateinit var subtitle : String*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?
        /*array_word = ArrayList()
        array_meaning = ArrayList()*/
        return when(viewType) {

            Model.TITLE_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_make_set, parent, false)
                TitleTypeViewHolder(view)
            }

            Model.CARD_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_make_set, parent, false)
                CardTypeViewHolder(view)
            }

            else -> throw RuntimeException("viewType 에러")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = list[position]
        when(item.type) {

            Model.TITLE_TYPE -> {
                (holder as TitleTypeViewHolder).et_title.setText(item.text)
                holder.et_subtitle.setText(item.text2)
            }

            Model.CARD_TYPE -> {
                (holder as CardTypeViewHolder).et_word.setText(item.text)
                holder.et_meaning.setText(item.text2)
                /*array_word!!.add(item.text)
                array_meaning!!.add(item.text2)*/
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    inner class TitleTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var et_title : EditText = itemView.findViewById(R.id.MakeSet_title)
        val et_subtitle : EditText = itemView.findViewById(R.id.MakeSet_subtitle)
    }

    inner class CardTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val et_word : EditText = itemView.findViewById(R.id.MakeSet_word)
        val et_meaning : EditText = itemView.findViewById(R.id.MakeSet_meaning)
    }

    /*fun getWords() : ArrayList<String> {
        return array_word!!
    }

    fun getMeanings() : ArrayList<String> {
        return array_meaning!!
    }*/
}