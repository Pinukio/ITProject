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

class MakeSetAdapter(val list : MutableList<Model>, val onItemClick: OnItemCheckListener, val isEmpty : Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var array_word : ArrayList<String>? = ArrayList()
    var array_meaning : ArrayList<String>? = ArrayList()
    var title : String = ""
    var subtitle : String = ""
    var textWatcher1 : MyTextWatcher = MyTextWatcher(0)
    var textWatcher2 : MyTextWatcher = MyTextWatcher(1)
    var list_tw3 : ArrayList<MyTextWatcher> = ArrayList()
    var list_tw4 : ArrayList<MyTextWatcher> = ArrayList()
    var array_selected : ArrayList<Int> = ArrayList()
    var array_holder : ArrayList<CardTypeViewHolder> = ArrayList()

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
                title = item.text
                holder.et_subtitle.setText(item.text2)
                subtitle = item.text2

            }

            Model.CARD_TYPE -> {

                (holder as CardTypeViewHolder).et_word.setText(item.text)
                if(!isEmpty) {
                    array_word!!.add(item.text)
                    list_tw3.add(MyTextWatcher(2))
                    array_meaning!!.add(item.text2)
                    list_tw4.add(MyTextWatcher(3))
                }

                holder.et_meaning.setText(item.text2)
                holder.checkbox.isChecked = false
                holder.checkbox.setOnClickListener {
                    if(holder.checkbox.isChecked)
                        onItemClick.onItemCheck(holder.adapterPosition)
                    else
                        onItemClick.onItemUncheck(holder.adapterPosition)

                }
                holder.et_word.requestFocus()
                array_holder.add(holder)

                /*array_holder.add(holder as CardTypeViewHolder)
                val index = position - 1
                array_holder[index].et_word.setText(item.text)
                array_holder[index].et_meaning.setText(item.text2)
                array_holder[index].checkbox.isChecked = false

                array_holder[index].checkbox.setOnClickListener {
                    if(holder.checkbox.isChecked)
                        onItemClick.onItemCheck(holder.adapterPosition)
                    else
                        onItemClick.onItemUncheck(holder.adapterPosition)

                }
                array_holder[index].et_word.requestFocus()*/

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
        val index = position - 1
        array_holder[index].et_word.removeTextChangedListener(list_tw3[index])
        array_holder[index].et_meaning.removeTextChangedListener(list_tw4[index])
        list.removeAt(position)
        array_holder.removeAt(index)
        list_tw3.removeAt(index)
        list_tw4.removeAt(index)
        array_word!!.removeAt(index)
        array_meaning!!.removeAt(index)
        notifyItemRemoved(position)
    }

    fun deleteItems(array_selected : ArrayList<Int>){
        if(array_selected.size > 1)
            Collections.sort(array_selected, AscendingInteger())
        //this.array_selected = array_selected
        this.array_selected = ArrayList()
        array_selected.forEach {
            removeItem(it)
            Log.i("1111", "!!!")
        }
        for(i in 0 until list_tw3.size) {
            Log.i("2222", i.toString())
            list_tw3[i].updatePosition(i)
            list_tw4[i].updatePosition(i)
        }
    }

    internal inner class AscendingInteger : Comparator<Int> { //정렬
        override fun compare(a: Int, b: Int): Int {
            return b.compareTo(a)
        }
    }

    fun addItem() : Int{
        array_word!!.add("")
        array_meaning!!.add("")
        list_tw3.add(MyTextWatcher(2))
        list_tw4.add(MyTextWatcher(3))
        //list_tw3[list.size - 1].updatePosition(list.size - 1)
        //list_tw4[list.size - 1].updatePosition(list.size - 1)
        for(i in 0 until list.size) {
            list_tw3[i].updatePosition(i)
            list_tw4[i].updatePosition(i)
        }
        list.add(Model(Model.CARD_TYPE, "", ""))
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
                    0 -> title = s.toString()
                    1 -> subtitle = s.toString()
                    2 -> array_word!![position!!] = s.toString()
                    3 -> array_meaning!![position!!] = s.toString()
                }
        }
        fun updatePosition(pos : Int) {
            position = pos
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
            val index = holder.adapterPosition - 1
            val word = array_word!![index]
            (holder as CardTypeViewHolder).et_word.setText(word)
            //array_holder[index].et_word.setText(index.toString())
            //array_holder[index].et_word.addTextChangedListener(list_tw3[index])

            val meaning = array_meaning!![index]
            //array_holder[index].et_meaning.setText(meaning)
            //array_holder[index].et_meaning.addTextChangedListener(list_tw4[index])

            holder.et_meaning.setText(meaning)
            holder.et_word.addTextChangedListener(list_tw3[index])
            holder.et_meaning.addTextChangedListener(list_tw4[index])
            if(array_selected.contains(holder.adapterPosition)) {
                holder.checkbox.isChecked = true
            }
            //array_holder[index] = holder
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        if(holder.adapterPosition == 0) {
            (holder as TitleTypeViewHolder).et_title.removeTextChangedListener(textWatcher1)
            holder.et_subtitle.removeTextChangedListener(textWatcher2)
            title = holder.et_title.text.toString()
            subtitle = holder.et_subtitle.text.toString()
        }

        else if(holder.adapterPosition != -1){
            val index = holder.adapterPosition - 1
            array_word!![index] = (holder as CardTypeViewHolder).et_word.text.toString()
            array_meaning!![index] = holder.et_meaning.text.toString()
            holder.et_word.removeTextChangedListener(list_tw3[index])
            holder.et_meaning.removeTextChangedListener(list_tw4[index])

            /*array_word!![index] = array_holder[index].et_word.text.toString()
            array_meaning!![index] = array_holder[index].et_meaning.text.toString()
            array_holder[index].et_word.removeTextChangedListener(list_tw3[index])
            array_holder[index].et_meaning.removeTextChangedListener(list_tw4[index])*/
        }
    }

    fun setSelectedArray(a : Int, isAdd : Boolean) {
        if(isAdd)
            array_selected.add(a)
        else
            array_selected.remove(a)
    }

}
