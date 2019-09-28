package com.example.itproject

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_image.*
import android.util.Log

class ImageActivity : AppCompatActivity() {

    private lateinit var dataPath: String
    private lateinit var tess: TessBaseAPI
    private val RESULT_OCR: Int = 100
    private val messageHandler: MessageHandler = MessageHandler()
    private lateinit var progress: ProgressDialog
    private var assetsCopied = false
    private lateinit var bitMap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)


        val sf: SharedPreferences =
            applicationContext!!.getSharedPreferences("assetsCopied", Context.MODE_PRIVATE)
        assetsCopied = sf.getBoolean("assetsCopied", false)

        if (!assetsCopied) copyTask().execute()
        else OCRTask().execute()
    }

    inner class OCRThread(bm: Bitmap) : Thread() {

        private var bitMap: Bitmap = bm

        override fun run() {
            super.run()

            tess.setImage(bitMap)
            var OCRresult = tess.utF8Text

            val message: Message = Message.obtain()
            message.what = RESULT_OCR
            message.obj = OCRresult
            messageHandler.sendMessage(message)

        }
    }

    @SuppressLint("HandlerLeak")
    inner class MessageHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                RESULT_OCR -> {
                    textView.text = msg.obj.toString() //텍스트 변경

                    Toast.makeText(
                        applicationContext,
                        "인식 완료",
                        Toast.LENGTH_SHORT
                    ).show()
                    progress.dismiss()

                }
            }
        }
    }

    private fun copyAssets() {
        val assetManager = assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("tessdata/")
        } catch (e: IOException) {
            Log.e("tag", "Failed to get asset file list.", e)
        }

        var myDir = File("${Environment.getExternalStorageDirectory()}/tesseract")
        if (myDir.exists()) {
            Log.i("heee", "제거")
            myDir.delete()
        }

        myDir = File("${Environment.getExternalStorageDirectory()}/tessdata")
        if (!myDir.exists()) {
            myDir.mkdir()
        }

        if (files != null)
            for (filename in files) {
                var ins: InputStream? = null
                var ous: OutputStream? = null
                try {
                    ins = assetManager.open("tessdata/${filename}")
                    val outFile =
                        File("${Environment.getExternalStorageDirectory()}/tessdata/", filename)
                    ous = FileOutputStream(outFile)
                    copyFile(ins, ous)
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
                    if (ous != null) {
                        try {
                            ous.close()
                        } catch (e: IOException) {
                            // NOOP
                        }

                    }
                }
            }
    }


    @Throws(IOException::class)
    private fun copyFile(ins: InputStream, ous: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = ins.read(buffer)
        while (read != -1) {
            ous.write(buffer, 0, read)
            read = ins.read(buffer)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class copyTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()

            progress = ProgressDialog(this@ImageActivity)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.setMessage("잠시만 기다려주세요.")
            progress.setCancelable(false)
            progress.show()

        }

        override fun doInBackground(vararg p0: Void?): Void? {
            copyAssets()
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            OCRTask().execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class OCRTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()

            if (assetsCopied) {

                progress = ProgressDialog(this@ImageActivity)
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progress.setMessage("잠시만 기다려주세요.")
                progress.setCancelable(false)
                progress.show()

            }

        }

        override fun doInBackground(vararg params: Void?): Void? {

            val intent_: Intent? = intent
            val imageUri: Uri = Uri.parse(intent_!!.getStringExtra("uri"))
            val lang = "eng"
            bitMap =
                MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, imageUri)
            dataPath = "${Environment.getExternalStorageDirectory()}/"
            tess = TessBaseAPI()
            tess.init(dataPath, lang)
            val ocrThread = OCRThread(bitMap)
            ocrThread.isDaemon = true
            ocrThread.start()

            return null

        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            imageView.setImageBitmap(bitMap)

            val sf: SharedPreferences =
                applicationContext!!.getSharedPreferences("assetsCopied", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sf.edit()

            editor.putBoolean("assetsCopied", true)
            editor.apply()
        }
    }

}