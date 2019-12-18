package com.example.itproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.itproject.R

class CardAdapter(private val array_word : ArrayList<String>, private val array_meaning : ArrayList<String>, private val array_star : BooleanArray) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.card, parent, false)
        val textView = view.findViewById<TextView>(R.id.textViewCard)
        textView.text = array_word[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return array_word[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return array_word.size
    }

}