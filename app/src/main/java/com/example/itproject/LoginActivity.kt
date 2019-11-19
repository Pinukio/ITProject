package com.example.itproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginActivity_LoginBtn.setOnClickListener {

            val email : String = LoginActivity_email.text.toString()
            val passWord : String = LoginActivity_password.text.toString()

            if(email.isEmpty())
                Toast.makeText(applicationContext, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            else if(email.isEmpty())
                Toast.makeText(applicationContext, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()

            val wanderingCubes: Sprite = WanderingCubes()
            val progressBar : ProgressBar = findViewById(R.id.LoginActivity_progress)

            progressBar.indeterminateDrawable = wanderingCubes
            progressBar.visibility = View.VISIBLE
            LoginActivity_bg.visibility = View.VISIBLE

            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithEmailAndPassword(email, passWord)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        progressBar.visibility = View.INVISIBLE
                        LoginActivity_bg.visibility = View.INVISIBLE
                        finish()
                    }
                    else {
                        Toast.makeText(applicationContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
            
        }

    }
}
