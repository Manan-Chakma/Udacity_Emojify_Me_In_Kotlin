package com.example.udacityemojifyme.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.udacityemojifyme.R
import com.example.udacityemojifyme.contants.Constants.MyConstants.FILE_PROVIDER_AUTHORITY
import com.example.udacityemojifyme.contants.Constants.MyConstants.MY_DATE_FORMAT
import com.example.udacityemojifyme.contants.Constants.MyConstants.MY_TAG
import com.google.android.gms.auth.api.signin.internal.Storage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class BitMapUtils {
    companion object BUtils {
        lateinit var currentPhotoPath: String

        @Throws(IOException::class)
        fun createImageFile(context: Context): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }

        fun deleteImgFile(context: Context, imgPath: String): Boolean {
            val imgFile = File(imgPath)
            return imgFile.delete()
        }

        private fun galleryAddPic(context: Context, imgPath: String) {
            //ACTION_MEDIA_SCANNER_SCAN_FILE got deprecated
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    val f = File(currentPhotoPath)
                    mediaScanIntent.data = Uri.fromFile(f)
                    context.sendBroadcast(mediaScanIntent)
                    Log.d(MY_TAG, "gallery add pic done")
                }
            }
        }

        fun resamplePic(context: Context, imgPath: String): Bitmap {

            val displayMetrics = DisplayMetrics();
            val manager: WindowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
            manager.defaultDisplay.getMetrics(displayMetrics);
            val targetW = displayMetrics.heightPixels * .6;
            val targetH = displayMetrics.widthPixels * .8;
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight
            val scaleFactor = (photoW / targetW).coerceAtMost(photoH / targetH);
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor.toInt()
            val exif = ExifInterface(File(imgPath).absolutePath)
            val mBitMap = BitmapFactory.decodeFile(imgPath)

            var rotationAngle: Int = 0
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    rotationAngle = 270
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    rotationAngle = 180
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    rotationAngle = 90
                }
            }

            return mBitMap.rotate(rotationAngle)
        }

        fun saveImage(context: Context, mResultsBitmap: Bitmap) {
            val timeStamp = SimpleDateFormat(
                MY_DATE_FORMAT,
                Locale.getDefault()
            ).format(Date())
            var imgFileName = "JPEG_$timeStamp.jpg"
           // val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+ "/Emojify")-won't work
            val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                    + "/Emojify")
            var success = true
            if (!storageDir.exists()) {
                success = storageDir.mkdir()
            }
            Log.d(MY_TAG, success.toString())
            if (success) {
                var imgFile = File(storageDir, imgFileName)
                val savedImgPath = imgFile.absolutePath
                try {
                    var fOut = FileOutputStream(imgFile)
                    mResultsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                galleryAddPic(context, savedImgPath)
                Toast.makeText(context, R.string.img_saved_msg, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }

        fun shareImage(context: Context, mTempPhotoPath: String) {
            var imgFile = File(mTempPhotoPath)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            var photoUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY,imgFile)
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri)
            context.startActivity(shareIntent)
        }
    }
}

private fun Bitmap.rotate(degree: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degree.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
