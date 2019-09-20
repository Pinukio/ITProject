package com.example.itproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import java.io.IOException

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val intent_: Intent? = intent
        val imageUri: Uri = Uri.parse(intent_!!.getStringExtra("uri"))
        val image: FirebaseVisionImage
        val detector : FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        val textView : TextView = findViewById(R.id.textView)


        try {

            image = FirebaseVisionImage.fromFilePath(applicationContext, imageUri)

            val result : Task<FirebaseVisionText> = detector.processImage(image)
                .addOnSuccessListener {

                    val resultString : String = it.text

                    for(block in it.textBlocks) {

                        val blockText : String = block.text

                        for(line : FirebaseVisionText.Line in block.lines) {

                            val lineText : String = line.text

                            for(element : FirebaseVisionText.Element in line.elements) {

                                val elementText : String = element.text
                                textView.setText(elementText)

                            }

                        }

                    }

                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                }


        }

        catch (e: IOException) {
            e.printStackTrace()
        }




    }

}