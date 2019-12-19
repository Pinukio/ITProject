package com.example.itproject.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itproject.R
import com.example.itproject.SearchItem
import com.example.itproject.fragment.SearchFragment

class SearchAdapter(private val list : ArrayList<SearchItem>, private val fragment : SearchFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.card_search, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        (holder as MyViewHolder).title.text = item.title
        if(item.subtitle.isBlank()) {
            holder.subtitle.visibility = View.GONE
            holder.subtitle.text = ""
        }
        else
            holder.subtitle.text = item.subtitle
        holder.name.text = item.name
        holder.email.text = item.email
        if(item.profileUri.isEmpty())
            holder.profile.setImageResource(R.drawable.profile_user)
        else
            holder.profile.setImageURI(Uri.parse(item.profileUri))
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.Search_title)
        val subtitle : TextView = itemView.findViewById(R.id.Search_subtitle)
        val name : TextView = itemView.findViewById(R.id.Search_name)
        val email : TextView = itemView.findViewById(R.id.Search_email)
        val profile : ImageView = itemView.findViewById(R.id.Search_profile)
        init {
            itemView.setOnClickListener {
                val titleText = title.text.toString()
                val subtitleText = subtitle.text.toString()
                val emailText = email.text.toString()
                fragment.moveToSet(titleText, subtitleText, emailText)
            }
        }
    }

    fun changeList(list_ : ArrayList<SearchItem>) {
        list.clear()
        list.addAll(list_)
        notifyDataSetChanged()
    }
}