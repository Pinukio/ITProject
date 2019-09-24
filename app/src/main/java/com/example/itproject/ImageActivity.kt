package com.example.itproject

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*
import android.widget.Toast
import android.os.Handler
import kotlinx.android.synthetic.main.activity_image.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log


class ImageActivity : AppCompatActivity() {

    private lateinit var dataPath: String
    private val languageList: Array<String> = arrayOf("eng", "kor")
    private lateinit var tess: TessBaseAPI
    private val RESULT_OCR: Int = 100
    private val messageHandler: MessageHandler = MessageHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        //copyAssets()

        val intent_: Intent? = intent
        val imageUri: Uri = Uri.parse(intent_!!.getStringExtra("uri"))
        val textView: TextView = findViewById(R.id.textView)
        val imageView: ImageView = findViewById(R.id.imageView)
        //imageView.setImageURI(imageUri)

        val bitMap: Bitmap =
            MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, imageUri)
        imageView.setImageBitmap(bitMap)
        dataPath = "${filesDir}/tesseract/"

        var lang = "eng+kor"

        tess = TessBaseAPI()
        tess.init(dataPath, lang)
        val ocrThread: OCRThread = OCRThread(bitMap)
        ocrThread.isDaemon = true
        ocrThread.start()

    }

    fun checkFile(dir: File, lang: String) {

        if (!dir.exists() && dir.mkdirs()) copyFiles(lang)

        if (dir.exists()) {

            val filePath = "${dataPath}tessdata/${lang}.traineddata"
            val dataFile = File(filePath)

            if (!dataFile.exists()) copyFiles(lang)

        }

    }

    fun copyFiles(lang: String) {

        try {

            val filePath = "${dataPath}/tessdata/${lang}.traineddata"
            val assetManager: AssetManager = assets
            val ins: InputStream = assetManager.open("tessdata/${lang}.traineddata")
            val ous: OutputStream = FileOutputStream(filePath)
            val buffer = ByteArray(1024)
            var read: Int? = 0

            while (read != -1) {
                ous.write(buffer, 0, read!!)
                read = ins.read(buffer)
            }

            ous.flush()
            ous.close()
            ins.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    inner class OCRThread(bm: Bitmap) : Thread() {

        private var bitMap: Bitmap = bm

        override fun run() {

            super.run()
            var OCRresult: String? = null

            tess.setImage(bitMap)
            OCRresult = tess.utF8Text

            val message: Message = Message.obtain()
            message.what = RESULT_OCR
            message.obj = OCRresult
            messageHandler.sendMessage(message)

        }
    }

    inner class MessageHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                RESULT_OCR -> {
                    //val OCRTextView = findViewById(R.id.textView)
                    textView.text = msg.obj.toString() //텍스트 변경

                    Toast.makeText(
                        applicationContext,
                        "안녕하세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun copyAssets() {
        val assetManager = assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.i("hello", "Failed to get asset file list.", e)
        }

        //if (files != null)
        //for (filename in files!!) Log.i("Hello2", filename)
        val filename: String = "tessdata/"
        var ins: InputStream? = null
        var out: OutputStream? = null

        try {

            for (lang in languageList) {

                ins = assetManager.open("${filename}${lang}.traineddata")
                val outFile = File(getExternalFilesDir(null), filename)
                out = FileOutputStream(outFile)
                copyFile(ins, out)

            }

        } catch (e: IOException) {
            Log.e("tag", "Failed to copy asset file: $filename", e)
        } finally {
            if (ins != null) {
                try {
                    ins.close()
                } catch (e: IOException) {
                    // NOOP
                }

            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    // NOOP
                }

            }
        }
        //}
    }

    @Throws(IOException::class)
    private fun copyFile(ins: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = ins.read(buffer)
        while (read != -1) {
            out.write(buffer, 0, read)
            read = ins.read(buffer)
        }
    }

}