package com.example.itproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SetAdapter(private val list : MutableList<Model>, private val name : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val array_star : ArrayList<Boolean> = ArrayList()
    private lateinit var tmp : CollectionReference
    private var title = ""
    private var titleCreated : Boolean = false
    private var cardCreated : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View?
        val size = list.size - 1
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        tmp = FirebaseFirestore.getInstance().collection("users").document(email).collection("sets")
        if(array_star.size != size) {
            for(i in 0 until size) {
                array_star.add(false)
            }
        }
        return when(viewType) {

            Model.TITLE_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_set, parent, false)
                TitleTypeViewHolder(view)
            }

            Model.CARD_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_set, parent, false)
                CardTypeViewHolder(view)
            }

            else -> throw RuntimeException("viewType 에러")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        when(item.type) {
            Model.TITLE_TYPE -> {
                if(!titleCreated) {
                    title = item.text
                    (holder as TitleTypeViewHolder).title.text = title
                    if(item.text2 != "")
                        holder.subtitle.text = item.text2
                    else
                        holder.subtitle.visibility = View.GONE
                    holder.countText.text = "단어 ${list.size - 1}개"
                    holder.nameText.text = name
                    titleCreated = true
                }
            }

            Model.CARD_TYPE -> {
                if(!cardCreated) {
                    (holder as CardTypeViewHolder).word.text = item.text
                    holder.meaning.text = item.text2
                    holder.star.setOnClickListener {
                        val index = position - 1
                        if(!array_star[index]) {
                            holder.star.setImageResource(R.drawable.star_yellow)
                            array_star[index] = true
                            tmp.document(title).collection("_").document(index.toString()).update("star", true)
                        }
                        else {
                            holder.star.setImageResource(R.drawable.star_white)
                            array_star[index] = false
                            tmp.document(title).collection("_").document(index.toString()).update("star", false)
                        }
                    }
                    if(position == list.size - 1)
                        cardCreated = true
                }

            }
        }
    }

    inner class TitleTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.SetActivity_title)
        val subtitle : TextView = itemView.findViewById(R.id.SetActivity_subtitle)
        val countText : TextView = itemView.findViewById(R.id.SetActivity_count)
        val nameText : TextView = itemView.findViewById(R.id.SetActivity_name)
    }

    inner class CardTypeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val word : TextView = itemView.findViewById(R.id.Set_word)
        val meaning : TextView = itemView.findViewById(R.id.Set_meaning)
        val star : ImageView = itemView.findViewById(R.id.Set_star)
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }
}