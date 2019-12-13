package com.example.itproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class StudyAdapter(private val array_meaning : ArrayList<String>, private val activity : StudyActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val array_holder : ArrayList<MyViewHolder> = ArrayList()
    private var size = 0
    private var index = 0

    init{
        size = if(array_meaning.size >= 4) 4
        else array_meaning.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.card_study, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(array_holder.size < size)
            array_holder.add(holder as MyViewHolder)
        if(array_holder.size == size)
            setSelections(index)
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val meaning : TextView = itemView.findViewById(R.id.Study_meaning)
        init {
            itemView.setOnClickListener {
                if(meaning.text.toString() == array_meaning[index]) {
                    //정답
                }
            }
        }
    }

    fun setSelections(index : Int) {
        val randArray : ArrayList<Int> = makeRandomArray(size, size)
        val i = Random().nextInt(size) // 정답이 들어갈 인덱스
        val tmpArray : ArrayList<Int> = makeRandomArray(size - 1, array_meaning.size, index)

        var count = 0
        randArray.forEach {
            if(it == i) {
                array_holder[it].meaning.text = array_meaning[index]
            }
            else {
                array_holder[it].meaning.text = array_meaning[tmpArray[count]]
                count++
            }
        }

    }

    private fun makeRandomArray(size_ : Int, max : Int, exclude : Int = -1) : ArrayList<Int> {
        val randArray : ArrayList<Int> = ArrayList()
        for(it in 0 until size_) {
            var tmp = 0
            if(it == 0) {
                tmp = Random().nextInt(max)

                if(exclude != -1) {
                    while(tmp == exclude)
                        tmp = Random().nextInt(max)
                }

                randArray.add(tmp)
            }
            else {
                var b = true
                while(b) {
                    tmp = Random().nextInt(max)
                    if(exclude != -1) {
                        while(tmp == exclude)
                            tmp = Random().nextInt(max)
                    }
                    b = randArray.contains(tmp)
                }
                randArray.add(tmp)
            }
        }
        return randArray
    }
}