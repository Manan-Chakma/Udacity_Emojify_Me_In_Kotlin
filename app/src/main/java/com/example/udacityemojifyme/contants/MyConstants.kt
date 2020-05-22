package com.example.udacityemojifyme.contants

import com.example.udacityemojifyme.emojifier.Emojifier

class Constants {
    companion object MyConstants{
        val REQUEST_IMAGE_CAPTURE = 1
        val PERMISSION_CODE = 1000
        val FILE_PROVIDER_AUTHORITY = "com.example.udacityemojifyme.fileprovider"
        val MY_DATE_FORMAT = "EEE, MMM d, ''yy"
        val MY_TAG = "MY_TAG"
        private val LOG_TAG: String = Emojifier::class.java.name


        val EMOJI_SCALE_FACTOR = .9f
        val SMILING_PROB_THRESHOLD = .15f
        val EYE_OPEN_PROB_THRESHOLD = .5f
    }
}