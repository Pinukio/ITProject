package com.example.itproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.InputStream

class ImageActivity : AppCompatActivity() {

    private val REQ_GALLERY : Int = 100
    private val REQ_CAMERA : Int = 102
    private var imageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageView : ImageView = findViewById(R.id.imageView)

        var intent_ : Intent? = null
        intent_ = intent

        if(intent_ != null) {

            val imageUrl : Uri = Uri.parse(intent_.getStringExtra("uri")!!)
            imageView.setImageURI(imageUrl)
        }

    }

}