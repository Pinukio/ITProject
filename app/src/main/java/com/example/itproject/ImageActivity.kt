package com.example.itproject

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class ImageActivity : AppCompatActivity() {

    private lateinit var dataPath : String
    private val languageList : Array<String> = arrayOf("eng", "kor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val intent_: Intent? = intent
        val imageUri: Uri = Uri.parse(intent_!!.getStringExtra("uri"))
        val image: FirebaseVisionImage
        val detector : FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        val textView : TextView = findViewById(R.id.textView)
        val imageView : ImageView = findViewById(R.id.imageView)
        imageView.setImageURI(imageUri)

        val bitMap : Bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, imageUri)
        dataPath = "${filesDir}/tesseract/"

        var lang = ""
        var count = 0
        for(language : String in languageList) {
            checkFile(File(dataPath + "tessdata/"), language)
            if(count < languageList.size - 1)
                lang += language + "+"
            else
                lang += language
            count++
        }
        val tess = TessBaseAPI()
        Log.i("Hello", lang)
        //tess.init(dataPath, lang)

    }

    fun checkFile(dir : File, lang : String) {

        if(!dir.exists() && dir.mkdirs()) {
            copyFiles(lang)
        }

        if(dir.exists()) {

            val filePath = dataPath + "tessdata/" + lang + ".traineddata"
            val dataFile : File = File(filePath)

            if(!dataFile.exists()) {
                 copyFiles(lang)
            }

        }

    }

    fun copyFiles(lang : String) {

        try {

            val temp = "tessdata/" + lang + ".traineddata"
            val filePath = dataPath + temp
            val assetManager : AssetManager = assets
            val ins : InputStream = assetManager.open(temp)
            val ous : OutputStream = FileOutputStream(filePath)
            val buffer = ByteArray(1024)
            val read : Int = ins.read(buffer)

            while(read != -1) {
                ous.write(buffer, 0, read)
            }

            ous.flush()
            ous.close()
            ins.close()

        }
        catch(e : FileNotFoundException) {
            e.printStackTrace()
        }
        catch(e : IOException) {
            e.printStackTrace()
        }

    }

}