package com.example.itproject

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MakeSetAdapter(val list : MutableList<Model>, val onItemClick: OnItemCheckListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var array_word : ArrayList<String>? = ArrayList()
    var array_meaning : ArrayList<String>? = ArrayList()
    var title : String = ""
    var subtitle : String = ""
    var textWatcher1 : MyTextWatcher = MyTextWatcher(0)
    var textWatcher2 : MyTextWatcher = MyTextWatcher(1)
    var textWatcher3 : MyTextWatcher = MyTextWatcher(2)
    var textWatcher4 : MyTextWatcher = MyTextWatcher(3)

    interface OnItemCheckListener {
        fun onItemCheck(index : Int)
        fun onItemUncheck(index : Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?

        for(i in 0..1) {
            array_word!!.add("")
            array_meaning!!.add("")
        }

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
                textWatcher3.updatePosition(holder.adapterPosition)
                textWatcher4.updatePosition(holder.adapterPosition)

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
        array_word!!.removeAt(position - 1)
        array_meaning!!.removeAt(position -1)
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
        array_word!!.add("")
        array_meaning!!.add("")
        notifyItemInserted(list.size - 1)
        return getLastIndex()
    }

    fun getLastIndex() : Int {
        return list.size - 1
    }

    fun getWords() : ArrayList<String> {
        return array_word!!
    }

    fun getMeanings() : ArrayList<String> {
        return array_meaning!!
    }

    fun getTitleText() : String {
        return title
    }

    fun getSubtitleText() : String {
        return subtitle
    }

    inner class MyTextWatcher(val spec : Int) : TextWatcher {
        var position : Int? = null
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
                when(spec) {
                    0 -> if(s.toString().isNotEmpty())Log.i("sex", s.toString()) else Log.i("sex", "헤헤헤호ㅔ헤헤ㅔㅎㅎ") //title = s.toString()
                    1 -> subtitle = s.toString()
                    2 -> array_word!![position!! - 1] = s.toString()
                    3 -> array_meaning!![position!! - 1] = s.toString()
                }
        }
        fun updatePosition(pos : Int) {
            position = pos
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if(holder.adapterPosition == 0) {
            title = (holder as TitleTypeViewHolder).et_title.text.toString()
            subtitle = holder.et_subtitle.text.toString()
        }
        else {
            array_word!![holder.adapterPosition - 1] = (holder as CardTypeViewHolder).et_word.text.toString()
            array_meaning!![holder.adapterPosition - 1] = holder.et_meaning.text.toString()
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(holder.adapterPosition == 0) {
            (holder as TitleTypeViewHolder).et_title.setText(title)
            holder.et_subtitle.setText(subtitle)
            holder.et_title.addTextChangedListener(textWatcher1)
            holder.et_subtitle.addTextChangedListener(textWatcher2)

        }
        else {
            val word = array_word!![holder.adapterPosition - 1]
            (holder as CardTypeViewHolder).et_word.setText(word)
            val meaning = array_meaning!![holder.adapterPosition - 1]
            holder.et_meaning.setText(meaning)
            holder.et_word.addTextChangedListener(textWatcher3)
            holder.et_meaning.addTextChangedListener(textWatcher4)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        if(holder.adapterPosition == 0) {
            (holder as TitleTypeViewHolder).et_title.removeTextChangedListener(textWatcher1)
            holder.et_subtitle.removeTextChangedListener(textWatcher2)

        }
        else {
            array_word!![holder.adapterPosition - 1] = (holder as CardTypeViewHolder).et_word.text.toString()
            array_meaning!![holder.adapterPosition - 1] = holder.et_meaning.text.toString()
            holder.et_word.removeTextChangedListener(textWatcher3)
            holder.et_meaning.removeTextChangedListener(textWatcher4)
        }
    }
}
