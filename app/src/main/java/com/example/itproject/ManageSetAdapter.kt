package com.example.itproject

import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ManageSetAdapter(private var list : ArrayList<MSModel>, private val onItemCheck : OnItemCheckListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemList : ArrayList<MSModel> = ArrayList()
    private var text : String = ""
    private var topCreated : Boolean = false
    private var cardCreated : Boolean = false
    private lateinit var topHolder : TopViewHolder
    //private var filteredArray : ArrayList<MSModel> = list
    //private val unFilteredArray : ArrayList<MSModel> = list
    private var array_selected : ArrayList<Int> = ArrayList()

    interface OnItemCheckListener {
        fun onItemCheck(index : Int)
        fun onItemUncheck(index : Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {
            MSModel.TOP -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.top_manage_set, parent, false)
                TopViewHolder(view)
            }
            MSModel.CARD_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_manage_set, parent, false)
                CardTypeViewHolder(view)
            }
            else -> throw RuntimeException("viewType 에러")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //val item = list[position]
        val item = list[position]
        when(item.type) {
            MSModel.TOP -> {
                if(!topCreated) {
                    for(i in 0 until list.size) {
                        itemList.add(list[i])
                    }
                    //(holder as TopViewHolder).edit.addTextChangedListener(MyTextWatcher())
                    (holder as TopViewHolder).search.setOnClickListener {
                        val text = holder.edit.text.toString()
                        if(text.isNotEmpty()) {
                            for(i in 1 until list.size) {
                                val index = list.size - i
                                list.removeAt(index)
                            }
                            for(i in 1 until itemList.size) {

                                if(itemList[i].text.contains(text)) {
                                    list.add(itemList[i])
                                }
                            }
                        }
                        else {
                            list = ArrayList()
                            for(i in 0 until itemList.size) {
                                list.add(itemList[i])
                            }

                        }
                        val r = Runnable {
                            notifyDataSetChanged()
                            holder.edit.setText(text)
                        }
                        Handler().post(r)

                    }
                    holder.trashBtn.setOnClickListener {
                        deleteItems()
                    }
                    topCreated = true
                    topHolder = holder
                }
            }
            MSModel.CARD_TYPE -> {
                if(!cardCreated) {
                    (holder as CardTypeViewHolder).title.text = item.text
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
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //return list[position].type
        return list[position].type
    }

    inner class TopViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val search : ImageView = itemView.findViewById(R.id.ManageSet_search)
        val edit : EditText= itemView.findViewById(R.id.ManageSet_edit)
        val checkAll : CheckBox = itemView.findViewById(R.id.ManageSet_checkAll)
        val trashBtn : ImageView = itemView.findViewById(R.id.ManageSet_trash)
    }

    inner class CardTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.ManageSet_title)
        val subtitle : TextView = itemView.findViewById(R.id.ManageSet_subtitle)
        val progressBar : RoundCornerProgressBar = itemView.findViewById(R.id.ManageSet_progress)
        val checkbox : CheckBox = itemView.findViewById(R.id.ManageSet_checkbox)
    }

    /*inner class MyTextWatcher : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            text = s.toString()
            //if(text.isNotEmpty())
                //filter(text)
            //filter.filter(text)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }*/

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        topHolder.edit.setText(text)
    }

    fun setSelectedArray(a : Int, isAdd : Boolean) {
        if(isAdd)
            array_selected.add(a)
        else
            array_selected.remove(a)
    }

    fun deleteItems(){
        //dialog.show()
        if(array_selected.size > 1)
            Collections.sort(array_selected, AscendingInteger())
        array_selected.forEach {
            removeItem(it)
        }
        array_selected = ArrayList()
    }

    internal inner class AscendingInteger : Comparator<Int> { //정렬
        override fun compare(a: Int, b: Int): Int {
            return b.compareTo(a)
        }
    }

    private fun removeItem(position : Int) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        db.collection("users").document(email).collection("sets").document(list[position].text).delete()
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}