package com.example.itproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MakeSetAdapter(val list : MutableList<Model>, val onItemClick: OnItemCheckListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemCheckListener {
        fun onItemCheck(index : Int)
        fun onItemUncheck(index : Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?

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
                holder.checkbox.isChecked = false

                holder.checkbox.setOnClickListener {
                    if(holder.checkbox.isChecked)
                        onItemClick.onItemCheck(holder.adapterPosition)
                    else
                        onItemClick.onItemUncheck(holder.adapterPosition)


                }
                holder.et_word.requestFocus()
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
        notifyItemRemoved(position)
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
    }

    fun addItem() : Int {
        list.add(Model(Model.CARD_TYPE, "", ""))
        notifyItemInserted(list.size - 1)
        return getLastIndex()
    }

    fun getLastIndex() : Int {
        return list.size - 1
    }
}
