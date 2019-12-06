package com.example.itproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SetAdapter(val array_word : ArrayList<String>, val array_meaning : ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val array_star : ArrayList<Boolean> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_set, parent, false)
        if(array_star.size != array_word.size) {
            for(i in 0 until array_word.size) {
                array_star.add(false)
            }
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array_word.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val index = holder.adapterPosition
        (holder as MyViewHolder).word.text = array_word[index]
        holder.meaning.text = array_meaning[index]
        holder.star.setOnClickListener {
            if(!array_star[index]) {
                //it.setBackgroundResource(R.drawable.star_yellow)
                holder.star.setImageResource(R.drawable.star_yellow)
                array_star[index] = true
            }
            else {
                //it.setBackgroundResource(R.drawable.star_white)
                holder.star.setImageResource(R.drawable.star_white)
                array_star[index] = false
            }
        }
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val word : TextView = itemView.findViewById(R.id.Set_word)
        val meaning : TextView = itemView.findViewById(R.id.Set_meaning)
        val star : ImageView = itemView.findViewById(R.id.Set_star)
    }

}