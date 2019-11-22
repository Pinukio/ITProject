package com.example.itproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MakeSetAdapter(val list : MutableList<Model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var array_checkbox : ArrayList<CheckBox>? = null
    var array_selected : ArrayList<Int>? = null
    var array_isChecked : ArrayList<Boolean>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?
        array_checkbox = ArrayList()
        array_selected = ArrayList()
        array_isChecked = ArrayList()
        for(i in 0 until list.size - 1)
            array_isChecked!!.add(false)

        return when(viewType) {

            Model.TITLE_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_make_set, parent, false)
                TitleTypeViewHolder(view)
            }

            Model.CARD_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_make_set, parent, false)
                Log.i("Hello", "Hello")
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
                holder.checkbox.tag = position

                holder.checkbox.setOnClickListener {
                    val tmp = it.tag as Int
                    if(!array_isChecked!![tmp - 1]) {
                        array_selected!!.add(tmp)
                        array_isChecked!![tmp - 1] = true
                        Log.i("Hello", tmp.toString())
                    }

                    else {
                        array_selected!!.remove(tmp)
                        array_isChecked!![tmp - 1] = false
                    }

                }
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
        val et_title : EditText = itemView.findViewById(R.id.MakeSet_title)
        val et_subtitle : EditText = itemView.findViewById(R.id.MakeSet_subtitle)
    }

    inner class CardTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val et_word : EditText = itemView.findViewById(R.id.MakeSet_word)
        val et_meaning : EditText = itemView.findViewById(R.id.MakeSet_meaning)
        val checkbox : CheckBox = itemView.findViewById(R.id.MakeSet_checkbox)

    }

    private fun removeItem(position : Int) {
        list.removeAt(position)
        //array_selected!!.remove(position)
        //array_checkbox!!.removeAt(position - 1)
        notifyItemRemoved(position)
    }

    fun deleteItems() {
        Collections.sort(array_selected!!, AscendingInteger())
        val iter : MutableIterator<Int> = array_selected!!.iterator()
        while(iter.hasNext()) {
            removeItem(iter.next())
            iter.remove()
            Log.i("궁금", array_selected!!.size.toString())
        }
    }

    internal inner class AscendingInteger : Comparator<Int> {
        override fun compare(a: Int, b: Int): Int {
            return b.compareTo(a)
        }
    }

}
