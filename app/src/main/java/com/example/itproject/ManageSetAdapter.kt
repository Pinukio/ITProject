package com.example.itproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ManageSetAdapter(
    private val list : ArrayList<MSItem>,
    private val onItemCheck : OnItemCheckListener,
    private val fragment : ManageSetFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cardCreated : Boolean = false
    private var array_selected : ArrayList<Int> = ArrayList()
    private val array_cardHolder : ArrayList<CardViewHolder> = ArrayList()
    private var allItemsChecked : Boolean = false
    private val itemList : ArrayList<MSItem> = ArrayList()
    //private val tmp_cardHolder : ArrayList<CardViewHolder> = ArrayList()
    private val tmp_selected : ArrayList<Int> = ArrayList()
    private val array_index : ArrayList<Int> = ArrayList()

    init{
        itemList.addAll(list)
        for(i in 0 until list.size) {
            array_index.add(i)
        }
    }

    interface OnItemCheckListener {
        fun onItemCheck(index : Int)
        fun onItemUncheck(index : Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = LayoutInflater.from(parent.context!!).inflate(R.layout.card_manage_set, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        (holder as CardViewHolder).title.text = item.text
        if(item.text2.isNotEmpty())
            holder.subtitle.text = item.text2
        else
            holder.subtitle.visibility = View.GONE
        val progressBar = holder.progressBar
        progressBar.max = 100f
        progressBar.progress = item.progress.toFloat()
        progressBar.progressColor = Color.parseColor("#2196f3")
        progressBar.progressBackgroundColor = Color.LTGRAY

        holder.checkbox.setOnClickListener {
            if(holder.checkbox.isChecked)
                onItemCheck.onItemCheck(holder.adapterPosition)
            else
                onItemCheck.onItemUncheck(holder.adapterPosition)

        }
        if(!cardCreated) {
            array_cardHolder.add(holder)
            //tmp_cardHolder.add(holder)
            if(array_cardHolder.size == list.size)
                cardCreated = true
        }
    }

    inner class CardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.ManageSet_title)
        val subtitle : TextView = itemView.findViewById(R.id.ManageSet_subtitle)
        val progressBar : RoundCornerProgressBar = itemView.findViewById(R.id.ManageSet_progress)
        val checkbox : CheckBox = itemView.findViewById(R.id.ManageSet_checkbox)
    }

    fun setSelectedArray(a : Int, isAdd : Boolean) {
        if(isAdd) {
            array_selected.add(a)
            tmp_selected.add(a)
            val tmp = areAllItemsChecked()
            if(tmp) {
                allItemsChecked = true
                //topHolder.checkAll.isChecked = true
                fragment.changeCbState(true)
            }
        }
        else {
            array_selected.remove(a)
            tmp_selected.remove(a)
            if(allItemsChecked) {
                allItemsChecked = false
                //topHolder.checkAll.isChecked = false
                fragment.changeCbState(false)
            }
        }
    }


    fun deleteItems(){
        //dialog.show()
        if(array_selected.size > 1)
            Collections.sort(array_selected, AscendingInteger())
        array_selected.forEach {
            removeItem(it)
        }
        val sf : SharedPreferences = fragment.context!!.getSharedPreferences("count_sets", Context.MODE_PRIVATE)
        val et: SharedPreferences.Editor = sf.edit()
        val count = sf.getInt("sets", 0)
        et.putInt("sets", count - array_selected.size).apply()
        array_selected.clear()
        tmp_selected.clear()
    }

    internal inner class AscendingInteger : Comparator<Int> { //정렬
        override fun compare(a: Int, b: Int): Int {
            return b.compareTo(a)
        }
    }

    private fun removeItem(position : Int) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        //db.collection("users").document(email).collection("sets").document(list[position].text).delete()
        db.collection("users").document(email).collection("sets").document(itemList[position].text).delete()
        //list.removeAt(position)
        itemList.removeAt(position)
        list.clear()
        list.addAll(itemList)
        notifyItemRemoved(position)
    }

    fun checkAllCb() {
        /*for(i in 0 until array_cardHolder.size) {
            if(array_selected.contains(i))
                continue
            else {
                array_selected.add(i)
                tmp_selected.add(i)
            }
            array_cardHolder[i].checkbox.isChecked = true
            allItemsChecked = true
        }*/
        array_index.forEachIndexed { index, i ->
            if(!array_selected.contains(i)) {
                array_selected.add(i)
                tmp_selected.add(i)
            }
            array_cardHolder[index].checkbox.isChecked = true
            allItemsChecked = true
        }
    }

    fun unCheckAllCb() {
        /*for(i in 0 until array_cardHolder.size) {
            if(!array_selected.contains(i))
                continue
            else {
                array_selected.remove(i)
                tmp_selected.remove(i)
            }
            array_cardHolder[i].checkbox.isChecked = false
            allItemsChecked = false
        }*/

        array_index.forEachIndexed { index, i ->
            if(array_selected.contains(i)) {
                array_selected.remove(i)
                tmp_selected.remove(i)
            }
            array_cardHolder[index].checkbox.isChecked = false
            allItemsChecked = false
        }
    }

    fun filter(s : String) {
        val text = s.toLowerCase(Locale.getDefault())
        list.clear()
        array_selected.clear()
        array_index.clear()

        for(i in 0 until array_cardHolder.size) {
            array_cardHolder[i].checkbox.isChecked = false
        }
        if(text.isEmpty()) {
            list.addAll(itemList)
            array_selected.addAll(tmp_selected) // 체크박스 전체 선택하는 거 문제 있음.....
        }

        else {
            for(item in itemList) {
                val title = item.text
                if(title.toLowerCase(Locale.getDefault()).contains(text)) {
                    list.add(item)
                    val index = itemList.indexOf(item)
                    array_index.add(index)
                    if(tmp_selected.contains(index)) {
                        array_cardHolder[index].checkbox.isChecked = true
                        array_selected.add(index)
                    }

                        //array_selected.add(list.size - 1)
                }
            }
        }
        val tmp = areAllItemsChecked()
        if(tmp) {
            allItemsChecked = true
            fragment.changeCbState(true)
        }
        else {
            allItemsChecked = false
            fragment.changeCbState(false)
        }
        notifyDataSetChanged()
    }

    private fun areAllItemsChecked() : Boolean {
        var tmp = true
        /*for(i in 0 until array_cardHolder.size) {
            if(!array_cardHolder[i].checkbox.isChecked) {
                tmp = false
                break
            }
        }*/
        array_index.forEach {
            if(!array_cardHolder[it].checkbox.isChecked)
                tmp = false
        }
        return tmp
    }

}