package com.example.udacityemojifyme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.udacityemojifyme.contants.Constants.MyConstants.FILE_PROVIDER_AUTHORITY
import com.example.udacityemojifyme.contants.Constants.MyConstants.MY_TAG
import com.example.udacityemojifyme.contants.Constants.MyConstants.PERMISSION_CODE
import com.example.udacityemojifyme.contants.Constants.MyConstants.REQUEST_IMAGE_CAPTURE
import com.example.udacityemojifyme.emojifier.Emojifier
import com.example.udacityemojifyme.utils.BitMapUtils
import com.example.udacityemojifyme.utils.BitMapUtils.BUtils.currentPhotoPath
import com.example.udacityemojifyme.viewmodels.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var mFinalBitmap: Bitmap
    private lateinit var mImageView: ImageView
    private lateinit var mCloseButton: FloatingActionButton
    private lateinit var mSaveButton: FloatingActionButton
    private lateinit var mShareButton: FloatingActionButton
    private lateinit var mCamOpenButton: Button
    private var mTempPhotoPath: String = ""
    private val mViewModel: MyViewModel by viewModels()
    private lateinit var mResultsBitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeId()

        mViewModel.setState(0)
        mViewModel.getState().observe(this, Observer {
            when (it) {
                0 -> {
                    setFabVisibility(false)
                }
                1 -> {
                    setFabVisibility(true)
                }
            }
        })


        mCamOpenButton.setOnClickListener {
            askForPermission()
        }
        mCloseButton.setOnClickListener {
            clearImage()
        }
        mSaveButton.setOnClickListener {
            saveImage()
        }
        mShareButton.setOnClickListener {
            shareImage()
        }

    }

    private fun initializeId() {
        mCamOpenButton = findViewById(R.id.open_camera_button)
        mCloseButton = findViewById(R.id.cancel_fab)
        mSaveButton = findViewById(R.id.save_fab)
        mShareButton = findViewById(R.id.share_fab)
        mImageView = findViewById(R.id.display_image)
    }

    private fun setFabVisibility(visibility: Boolean) {
        when (visibility) {
            false -> {
                mCamOpenButton.visibility = View.VISIBLE
                mSaveButton.visibility = View.GONE
                mShareButton.visibility = View.GONE
                mCloseButton.visibility = View.GONE
                mImageView.visibility = View.GONE
            }
            true -> {
                mCamOpenButton.visibility = View.GONE
                mSaveButton.visibility = View.VISIBLE
                mShareButton.visibility = View.VISIBLE
                mCloseButton.visibility = View.VISIBLE
                mImageView.visibility = View.VISIBLE
            }
        }
    }

    private fun shareImage() {
        BitMapUtils.deleteImgFile(this, mTempPhotoPath)
        BitMapUtils.saveImage(this, mResultsBitmap)
        BitMapUtils.shareImage(this, mTempPhotoPath)
    }

    private fun saveImage() {
        askForWritePermission()
    }

    private fun askForWritePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED
        ) {
            Toast.makeText(this, R.string.write_access_denied, Toast.LENGTH_SHORT).show()
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //show popup to request permission
            requestPermissions(permission, PERMISSION_CODE)
        } else {
            permittedForSave()
        }
    }

    private fun permittedForSave() {
        BitMapUtils.deleteImgFile(this, mTempPhotoPath)
        BitMapUtils.saveImage(this, mResultsBitmap)
    }

    private fun clearImage() {
        mImageView.setImageResource(0)
        mViewModel.setState(0)
        BitMapUtils.deleteImgFile(this, mTempPhotoPath)
    }

    private fun askForPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        BitMapUtils.createImageFile(this)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Toast.makeText(
                            this,
                            "Error occurred while creating the File",
                            Toast.LENGTH_SHORT
                        ).show()
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        mTempPhotoPath = photoFile.absolutePath
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.udacityemojifyme.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    //Toast.makeText(this, R.string.camera_access_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processAndSetImage()

        } else {
            mViewModel.setState(0)
            if (mTempPhotoPath.trim().isNotEmpty() && BitMapUtils.deleteImgFile(
                    this,
                    mTempPhotoPath
                )
            ) {
                Toast.makeText(this, R.string.img_file_delete_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.img_file_delete_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processAndSetImage() {
        mResultsBitmap = BitMapUtils.resamplePic(this, mTempPhotoPath)
        mViewModel.setState(1)
        mImageView.setImageBitmap(mResultsBitmap)
        CoroutineScope(IO).launch {
            mFinalBitmap = async { getFinalImage(mResultsBitmap) }.await()
            Log.d(MY_TAG, "STEP Completed")
            mImageView.setImageBitmap(mFinalBitmap)
        }


        // mResultsBitmap = BitMapUtils.resamplePic(this, mTempPhotoPath)
        // mResultsBitmap = Emojifier.detectFaceAndOverlay(this, mResultsBitmap)
        //mViewModel.setState(1)
        //mImageView.setImageBitmap(mResultsBitmap)
    }

    private fun getFinalImage(mBit: Bitmap): Bitmap {
        return Emojifier.detectFaceAndOverlay(this, mBit)
    }

    private fun getResampledBitmap(): Bitmap {
        return BitMapUtils.resamplePic(this, mTempPhotoPath)
    }


}


