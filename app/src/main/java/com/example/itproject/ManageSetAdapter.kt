package com.example.itproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar

class ManageSetAdapter(private val list : ArrayList<MSModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when(item.type) {
            //구현하기
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class TopViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val search : TextView = itemView.findViewById(R.id.ManageSet_search)
        val edit : EditText = itemView.findViewById(R.id.ManageSet_edit)
        val checkAll : CheckBox = itemView.findViewById(R.id.ManageSet_checkAll)
        val trashBtn : ImageView = itemView.findViewById(R.id.ManageSet_trash)
    }

    inner class CardTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.ManageSet_title)
        val subtitle : TextView = itemView.findViewById(R.id.ManageSet_subtitle)
        val progressBar : RoundCornerProgressBar = itemView.findViewById(R.id.ManageSet_progress)
    }
}