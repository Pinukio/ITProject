package com.example.itproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.insertImage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.frame_picture.*
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PictureFragment : Fragment() {

    //private lateinit var mainButton : CircleImageView
    private lateinit var cameraButton : ImageView
    private lateinit var galleryButton : ImageView
    //private lateinit var background_view : View
    //private lateinit var disappearance_left : Animation
    //private lateinit var disappearance_right : Animation
    //private lateinit var anim_reduction : Animation
    //private lateinit var anim_appearance : Animation
    //private lateinit var anim_disappearance : Animation
    private val REQ_GALLERY : Int = 100
    private val REQ_CAMERA : Int = 101
    private var photoUri : Uri? = null
    private var cameraFileName : String? = null
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.frame_picture, container, false)

        cameraButton = view.findViewById(R.id.imageview_camera_picture)
        galleryButton = view.findViewById(R.id.imageview_gallery_picture)
        val mainButton : CircleImageView = view.findViewById(R.id.main_button_picture)
        val background_view : View = view.findViewById(R.id.view_frame_picture)
        val disappearance_left = AnimationUtils.loadAnimation(activity, R.anim.disappearance_left)
        val disappearance_right = AnimationUtils.loadAnimation(activity, R.anim.disappearance_right)
        val anim_reduction = AnimationUtils.loadAnimation(activity, R.anim.reduction)
        val anim_appearance = AnimationUtils.loadAnimation(activity, R.anim.appearance)
        val anim_moving_left = AnimationUtils.loadAnimation(activity, R.anim.moving_left) //왼쪽으로 움직이며 생성

        cameraButton.animation = anim_appearance
        galleryButton.animation = anim_moving_left

        mainButton.setOnClickListener {

            val fragmentManager : FragmentManager = activity!!.supportFragmentManager

            mainButton.clearAnimation()
            cameraButton.clearAnimation()
            galleryButton.clearAnimation()

            background_view.alpha = 0f

            val params_mainButton : ViewGroup.LayoutParams = mainButton.layoutParams

            val marginParams_mainButton : ViewGroup.MarginLayoutParams = mainButton.layoutParams as ViewGroup.MarginLayoutParams
            val marginParams_cameraButton : ViewGroup.MarginLayoutParams = cameraButton.layoutParams as ViewGroup.MarginLayoutParams
            val marginParams_pencilButton : ViewGroup.MarginLayoutParams = galleryButton.layoutParams as ViewGroup.MarginLayoutParams

            val scale : Float = context!!.resources.displayMetrics.density

            //원래 크기로 돌리는 코드

            params_mainButton.width = (80 * scale + 0.5f).toInt()
            params_mainButton.height = (80 * scale + 0.5f).toInt()

            marginParams_mainButton.topMargin = (420 * scale + 0.5f).toInt()
            marginParams_cameraButton.leftMargin = (60 * scale + 0.5f).toInt()
            marginParams_pencilButton.rightMargin = (60 * scale + 0.5f).toInt()

            mainButton.layoutParams = params_mainButton

            mainButton.layoutParams = marginParams_mainButton
            cameraButton.layoutParams = marginParams_cameraButton
            galleryButton.layoutParams = marginParams_pencilButton

            mainButton.animation = anim_reduction
            cameraButton.animation = disappearance_left
            galleryButton.animation = disappearance_right

            val sf : SharedPreferences = activity!!.getSharedPreferences("count_fragment", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sf.edit()
            editor.putInt("count", 0)
            editor.apply()

            Handler().postDelayed({
                fragmentManager.beginTransaction().remove(this).commit()
            }, anim_reduction.duration)

        }

        background_view.setOnClickListener {

            val fragmentManager : FragmentManager= activity!!.supportFragmentManager

            cameraButton.alpha = 0f
            galleryButton.alpha = 0f

            val sf : SharedPreferences = activity!!.getSharedPreferences("count_fragment", Context.MODE_PRIVATE)
            val sf1 : SharedPreferences = activity!!.getSharedPreferences("count_mainFragment", Context.MODE_PRIVATE)

            val editor : SharedPreferences.Editor = sf.edit()
            val editor1 : SharedPreferences.Editor = sf1.edit()

            editor.putInt("count", 1)
            editor1.putInt("count", 1)

            editor.apply()
            editor1.apply()

            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.beginTransaction().add(R.id.framelayout_main, MainFragment()).commit()

        }

        galleryButton.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQ_GALLERY)

        }

        cameraButton.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            /*val photo : File? = try {
                createImageFile()
            } catch(e : IOException) {
                null
            }
            photoUri = FileProvider.getUriForFile(context!!, "com.example.itproject.fileprovider", photo!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)*/
            dispatchTakePictureIntent()
            //startActivityForResult(intent, REQ_CAMERA)
            //takePictureIntent()

        }

        return view
    }

    //메인에서 호출되는, 뒤로가기 눌렀을 때 애니메이션을 보여주는 함수

    fun back() {

        val fragmentManager : FragmentManager= activity!!.supportFragmentManager

        cameraButton.alpha = 0f
        galleryButton.alpha = 0f

        val sf : SharedPreferences = activity!!.getSharedPreferences("count_fragment", Context.MODE_PRIVATE)
        val sf1 : SharedPreferences = activity!!.getSharedPreferences("count_mainFragment", Context.MODE_PRIVATE)

        val editor : SharedPreferences.Editor = sf.edit()
        val editor1 : SharedPreferences.Editor = sf1.edit()

        editor.putInt("count", 1)
        editor1.putInt("count", 1)


        editor.apply()
        editor1.apply()

        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.beginTransaction().add(R.id.framelayout_main, MainFragment()).commit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == REQ_GALLERY) {

                try {
                    val intent = Intent(activity, ImageActivity::class.java)
                    val imageUri : Uri = data!!.data!!
                    intent.putExtra("uri", imageUri.toString())
                    startActivity(intent)
                }

                catch (e : IOException) {
                    Toast.makeText(context, "사진 불러오기 실패", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }

            }

            else if(requestCode == REQ_CAMERA) {

               /* try {
                    val imageBitmap : Bitmap = data!!.extras!!.get("data") as Bitmap
                    imageview.setImageURI(photoUri)
                    val intent = Intent(activity, ImageActivity::class.java)
                    val stream = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray : ByteArray = stream.toByteArray()
                    val path : String = insertImage(context!!.contentResolver, imageBitmap, "", "")!!
                    intent.putExtra("uri", path)
                    startActivity(intent)
                }

                catch (e : IOException) {

                }*/

                val file = File(currentPhotoPath)
                val bitMap : Bitmap? = MediaStore.Images.Media.getBitmap(context!!.contentResolver, Uri.fromFile(file))
                var rotateBitmap : Bitmap? = null

                if(bitMap != null) {

                    val exifInterface = ExifInterface(currentPhotoPath)
                    val orientation : Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)
                    when(orientation) {

                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap = rotateImage(bitMap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap = rotateImage(bitMap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap = rotateImage(bitMap, 270f)
                        else -> rotateBitmap = bitMap

                    }

                    val intent = Intent(activity, ImageActivity::class.java)
                    val stream = ByteArrayOutputStream()
                    rotateBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray : ByteArray = stream.toByteArray()
                    val path : String = insertImage(context!!.contentResolver, rotateBitmap, "", "")!!
                    intent.putExtra("uri", path)
                    startActivity(intent)

                }

            }

        }

    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.itproject.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQ_CAMERA)
                }
            }
        }
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

}