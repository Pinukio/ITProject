package com.example.itproject.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.itproject.R
import com.example.itproject.activity.SetActivity

class CardAdapter(private val array_word: ArrayList<String>, private val array_meaning: ArrayList<String>, private val array_star: BooleanArray) : BaseAdapter(),FragmentManager.OnBackStackChangedListener {

    private lateinit var textView : TextView
    private var cnt = 0
    private val array_view : ArrayList<View> = ArrayList()
    //private val array_star : ArrayList<Boolean> = ArrayList()
    //private lateinit var listener : View.OnClickListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.card, parent, false)
        textView = view.findViewById(R.id.textViewCard)
        textView.text = array_word[position]
        val star = view.findViewById<ImageView>(R.id.Card_star)
        if(array_star[position])
            star.setImageResource(R.drawable.star_yellow)
        else
            star.setImageResource(R.drawable.star_white)
        val ac = SetActivity.ac
        star.setOnClickListener {
            if(array_star[position]) {
                //체크되어 있으면
                star.setImageResource(R.drawable.star_white)
                array_star[position] = false
                ac.changeStar(position, false)
            }
            else {
                star.setImageResource(R.drawable.star_yellow)
                array_star[position] = true
                ac.changeStar(position, true)
            }
        }
        array_view.add(view)
        return view
    }



    override fun getItem(position: Int): Any {
        return array_word[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return array_word.size
    }

    override fun onBackStackChanged() {

    }

    /*fun setOnClick(b : Boolean) {
        when(b) {
            false -> textView.setOnClickListener(null)
            true -> textView.setOnClickListener(listener)
        }
    }*/

    fun click(position : Int) {
        ++cnt
        val v = array_view[position]
        val oa1:ObjectAnimator = ObjectAnimator.ofFloat(v,"scaleX",1f,0f)
        val oa2:ObjectAnimator = ObjectAnimator.ofFloat(v,"scaleX",1f,0f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.duration = 300
        oa2.duration = 300

        oa1.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                v.findViewById<TextView>(R.id.textViewCard).text = array_meaning[position]
                v.requestLayout()
            }
        })
        oa2.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                v.findViewById<TextView>(R.id.textViewCard).text = array_word[position]
                v.requestLayout()
            }
        })
        if (cnt%2==0){
            oa2.start()
        }
        else {
            oa1.start()
        }
    }
}
