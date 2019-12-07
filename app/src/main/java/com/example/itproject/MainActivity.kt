package com.example.itproject

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.callback.DragStateListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainButton : CircleImageView
    private lateinit var fragmentTransaction : FragmentTransaction
    private lateinit var nav : SlidingRootNav
    private lateinit var actionBar : ActionBar
    private lateinit var builder : SlidingRootNavBuilder
    private lateinit var dialog : AlertDialog
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        else {
            setContentView(R.layout.activity_main)
            Main_toolbar.setTitleTextColor(Color.WHITE)
            setSupportActionBar(Main_toolbar)

            val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
            alertBuilder.setCancelable(false)
            dialog = alertBuilder.create()
            dialog.show()

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(firebaseAuth.currentUser!!.email.toString()).collection("sets")
                .get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        for(document in it.result!!) {
                            count++
                        }
                        val sf_countBefore : SharedPreferences = getSharedPreferences("count_sets_before", Context.MODE_PRIVATE)
                        val et: SharedPreferences.Editor = sf_countBefore.edit()
                        et.putInt("sets_before", count)
                        et.apply()
                        val tmp = "보유 중인 학습 세트 : ${count}개"
                        textview_allSet_main.text = tmp
                        dialog.dismiss()
                    }
                }
            builder = SlidingRootNavBuilder(this)
            setDragStateListener()
            nav = makeNav()
            actionBar = supportActionBar!!
            actionBar.setDisplayShowCustomEnabled(true)

            actionBar.setDisplayShowTitleEnabled(false)
            val stateListAnimator = StateListAnimator()
            stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(Main_appbarlayout, "elevation", 0f))
            Main_appbarlayout.stateListAnimator = stateListAnimator

            val permissionListener = object : PermissionListener
            {
                override fun onPermissionGranted() {}
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    finish()
                }
            }

            TedPermission.with(applicationContext)
                .setPermissionListener(permissionListener)
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()

            mainButton = findViewById(R.id.circleImgview_main)

            mainButton.setOnClickListener {

                if(nav.isMenuClosed) {
                    fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.add(R.id.Main_frame, MainFragment()).commit()

                    val sf : SharedPreferences = getSharedPreferences("count_fragment", Context.MODE_PRIVATE)
                    val sf1 : SharedPreferences = getSharedPreferences("count_mainFragment", Context.MODE_PRIVATE)

                    val editor : SharedPreferences.Editor = sf.edit()
                    val editor1 : SharedPreferences.Editor = sf1.edit()

                    editor.putInt("count", 1)
                    editor1.putInt("count", 0)

                    editor.apply()
                    editor1.apply()

                }

            }

            Main_background.setOnClickListener{
                nav.closeMenu()
            }
            Menu_home.setOnClickListener {
                nav.closeMenu()

            }
        }
    }

    //뒤로가기를 눌렀을 때 어떤 프래그먼트인지 체크해서 애니메이션을 보여줌

    override fun onBackPressed() {
        val fragment : Fragment? = supportFragmentManager.findFragmentById(R.id.Main_frame)

        if(fragment != null) {

            val sf : SharedPreferences = getSharedPreferences("count_fragment", Context.MODE_PRIVATE)
            val count : Int = sf.getInt("count", 0)

            if(count == 1) {
                val mainFragment : MainFragment = supportFragmentManager.findFragmentById(R.id.Main_frame) as MainFragment
                mainFragment.back()
            }

            if(count == 2) {
                val pictureFragment : PictureFragment = supportFragmentManager.findFragmentById(R.id.Main_frame) as PictureFragment
                pictureFragment.back()
            }

        }

        else {
            if(nav.isMenuOpened) nav.closeMenu()
            else super.onBackPressed()
        }
    }

    private fun makeNav() : SlidingRootNav{

        val dm : DisplayMetrics = applicationContext.resources.displayMetrics
        val width = (dm.widthPixels * 0.35).toInt()
        return builder.withMenuLayout(R.layout.menu)
            .withDragDistancePx(width)
            .withRootViewScale(0.6f)
            .withRootViewElevation(10)
            .withRootViewYTranslation(0)
            .withToolbarMenuToggle(Main_toolbar)
            .inject()
    }

    fun setDragStateListener() {

        val listener : DragStateListener = object : DragStateListener {

            override fun onDragEnd(isMenuOpened: Boolean) {
                if(isMenuOpened)
                    Main_background.visibility = View.VISIBLE
                else
                    Main_background.visibility = View.INVISIBLE

            }

            override fun onDragStart() {
            }

        }
        builder.addDragStateListener(listener)
    }

    override fun onResume() {
        super.onResume()
        val sf : SharedPreferences = getSharedPreferences("count_sets", Context.MODE_PRIVATE)
        val count_ : Int = sf.getInt("sets", count)
        textview_allSet_main.text = "보유 중인 학습 세트 : ${count_}개"
    }
}
