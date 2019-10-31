package com.example.itproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*
import android.widget.Toast
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    private lateinit var dataPath: String
    private lateinit var tess: TessBaseAPI
    private val RESULT_OCR: Int = 100
    private val messageHandler: MessageHandler = MessageHandler()
    private lateinit var progressBar: ProgressBar
    private var assetsCopied = false
    private lateinit var bitMap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val sf: SharedPreferences =
            applicationContext!!.getSharedPreferences("assetsCopied", Context.MODE_PRIVATE)
        assetsCopied = sf.getBoolean("assetsCopied", false)

        progressBar = findViewById(R.id.progress)

        if (!assetsCopied) {
            copyTask().execute()
            Log.i("흠", "터")
        }
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
                    val textArray: Array<String> =
                        msg.obj.toString().split("\\W+".toRegex()).toTypedArray()
                    val textSize = 16f
                    val size = Point()
                    windowManager.defaultDisplay.getSize(size)
                    val displayWidth =
                        size.x - (40 * resources.displayMetrics.density + 0.5f).toInt()
                    val lParams_textView: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val lParams_linearLayout: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    var textViewWidth = 0
                    var linearLayout = LinearLayout(applicationContext)
                    linearLayout.layoutParams = lParams_linearLayout
                    linearLayout_scroll.addView(linearLayout)

                    //텍스트 생성
                    for (c: String in textArray) run {

                        var textView = TextView(applicationContext)
                        textView.layoutParams = lParams_textView
                        textView.text = c
                        textView.textSize = textSize
                        textView.setTextColor(Color.BLACK)
                        textView.setOnClickListener {
                            Toast.makeText(applicationContext, c, Toast.LENGTH_SHORT).show()
                        }

                        textView.measure(0, 0)
                        textViewWidth += textView.measuredWidth

                        if(textViewWidth > displayWidth) {

                            textViewWidth = textView.measuredWidth
                            //재선언하여 다음 줄에 사용하기 위함
                            linearLayout = LinearLayout(applicationContext)
                            linearLayout.layoutParams = lParams_linearLayout
                            linearLayout_scroll.addView(linearLayout)
                            linearLayout.addView(textView)

                        }

                        else linearLayout.addView(textView)

                        textView = TextView(applicationContext)
                        textView.textSize = textSize
                        textView.text = " "
                        textView.measure(0, 0)
                        textViewWidth += textView.measuredWidth

                        if(textViewWidth > displayWidth) {

                            textViewWidth = 0
                            //재선언하여 다음 줄에 사용하기 위함
                            linearLayout = LinearLayout(applicationContext)
                            linearLayout.layoutParams = lParams_linearLayout
                            linearLayout_scroll.addView(linearLayout)

                        }

                        else linearLayout.addView(textView)

                    }
                    Toast.makeText(applicationContext, "인식 완료", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
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

        //val myDir = File("${Environment.getExternalStorageDirectory()}/tessdata")
        val myDir = File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/tessdata")
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
                        //File("${Environment.getExternalStorageDirectory()}/tessdata/", filename)
                        File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/tessdata/", filename)
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

            val wanderingCubes: Sprite = WanderingCubes()
            progressBar.indeterminateDrawable = wanderingCubes
            progressBar.visibility = View.VISIBLE

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

                val wanderingCubes: Sprite = WanderingCubes()
                progressBar.indeterminateDrawable = wanderingCubes
                progressBar.visibility = View.VISIBLE

            }

        }

        override fun doInBackground(vararg params: Void?): Void? {

            val intent_: Intent? = intent
            val imageUri: Uri = Uri.parse(intent_!!.getStringExtra("uri"))
            val lang = "eng+kor"
            bitMap =
                MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, imageUri)
            //dataPath = "${Environment.getExternalStorageDirectory()}/"
            dataPath = "${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/"
            tess = TessBaseAPI()
            tess.init(dataPath, lang)
            val ocrThread = OCRThread(bitMap)
            ocrThread.isDaemon = true
            ocrThread.start()

            return null

        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val sf: SharedPreferences =
                applicationContext!!.getSharedPreferences("assetsCopied", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sf.edit()

            editor.putBoolean("assetsCopied", true)
            editor.apply()
        }
    }

}