package com.example.udacityemojifyme.emojifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import com.example.udacityemojifyme.R
import com.example.udacityemojifyme.contants.Constants.MyConstants.EMOJI_SCALE_FACTOR
import com.example.udacityemojifyme.contants.Constants.MyConstants.EYE_OPEN_PROB_THRESHOLD
import com.example.udacityemojifyme.contants.Constants.MyConstants.MY_TAG
import com.example.udacityemojifyme.contants.Constants.MyConstants.SMILING_PROB_THRESHOLD
import com.example.udacityemojifyme.emojicat.EmojiFactory
import com.example.udacityemojifyme.emojicat.EmojiType
import com.example.udacityemojifyme.emojicat.Emojies
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector

class Emojifier {

    companion object Emoji {
        lateinit var mEmoji: Emojies
        private lateinit var emojiBitmap: Bitmap

        private val mSmile = EmojiFactory.create(EmojiType.SMILE)
        private val mFrown = EmojiFactory.create(EmojiType.FROWN)
        private val mRWink = EmojiFactory.create(EmojiType.RIGHT_WINK)
        private val mLWink = EmojiFactory.create(EmojiType.LEFT_WINK)
        private val mLWFrown = EmojiFactory.create(EmojiType.LEFT_WINK_FROWN)
        private val mRWFrown = EmojiFactory.create(EmojiType.RIGHT_WINK_FROWN)
        private val mCESmile = EmojiFactory.create(EmojiType.CLOSED_EYE_SMILE)
        private val mCEFrown = EmojiFactory.create(EmojiType.CLOSED_EYE_FROWN)

        fun detectFaceAndOverlay(context: Context, bitmap: Bitmap): Bitmap {
            val detector = FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build()

            val frame = Frame.Builder().setBitmap(bitmap).build()
            val faces = detector.detect(frame)
            Log.d(MY_TAG, "Number of faces: $faces")
            var resultBitmap = bitmap
            if (faces.size() == 0) {
                Toast.makeText(context, R.string.no_face_detected, Toast.LENGTH_SHORT).show()
            } else {

                for (i in 0 until faces.size()) {
                    var isPresent = false
                    var face = faces.valueAt(i)
                    when (whichEmoji(face)) {
                        mSmile -> {
                            isPresent = true
                            emojiBitmap =
                                BitmapFactory.decodeResource(context.resources, R.drawable.smile)
                        }
                        mFrown -> {
                            isPresent = true
                            emojiBitmap =
                                BitmapFactory.decodeResource(context.resources, R.drawable.frown)
                        }
                        mLWink -> {
                            isPresent = true
                            emojiBitmap =
                                BitmapFactory.decodeResource(context.resources, R.drawable.leftwink)
                        }
                        mRWink -> {
                            isPresent = true
                            emojiBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.rightwink
                            )
                        }
                        mLWFrown -> {
                            isPresent = true
                            emojiBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.leftwinkfrown
                            )
                        }
                        mRWFrown -> {
                            isPresent = true
                            emojiBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.rightwinkfrown
                            )
                        }
                        mCESmile -> {
                            isPresent = true
                            emojiBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.closed_smile
                            )
                        }
                        mCEFrown -> {
                            isPresent = true
                            emojiBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.closed_frown
                            )
                        }
                        else -> {
                            Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (isPresent) {
                        resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face)
                    }
                }
            }
            detector.release()
            return resultBitmap
        }


        private fun addBitmapToFace(
            backgroundbitmap: Bitmap,
            mEmojiBitmap: Bitmap,
            face: Face
        ): Bitmap {
            val resultBitmap = Bitmap.createBitmap(
                backgroundbitmap.width,
                backgroundbitmap.height,
                backgroundbitmap.config
            )
            val scaleFactor = EMOJI_SCALE_FACTOR
            val newEmoWidth = (face.width * scaleFactor).toInt()
            val newEmoHeight = (face.height * scaleFactor).toInt()

            Log.d(MY_TAG, "Main Image Width: ${resultBitmap.width}")
            Log.d(MY_TAG, "Main Image Height: ${resultBitmap.height}")
            Log.d(MY_TAG, "New Emo Width: $newEmoWidth")
            Log.d(MY_TAG, "New Emo HEight: $newEmoHeight")

            val mEmoBitmap =
                Bitmap.createScaledBitmap(mEmojiBitmap, newEmoWidth, newEmoHeight, false)

            val emojiPositionX = face.position.x
            val emojiPositionY = face.position.y

            Log.d(MY_TAG, "Face Position X: ${face.position.x}")
            Log.d(MY_TAG, "Face Position Y: ${face.position.y}")
            Log.d(MY_TAG, "Emoji Position X: $emojiPositionX")
            Log.d(MY_TAG, "Emoji Position Y: $emojiPositionY")

            val canvas = Canvas(resultBitmap)
            canvas.drawBitmap(backgroundbitmap, Matrix(), null)
            canvas.drawBitmap(mEmoBitmap, emojiPositionX, emojiPositionY, null)
            Log.d(MY_TAG, "returning resultBitmap")
            return resultBitmap
        }

        private fun whichEmoji(face: Face): Emojies {
            Log.d(MY_TAG, "whichEmoji: smilingProb = ${face.isSmilingProbability}")
            Log.d(MY_TAG, "whichEmoji: leftEyeOpenProb = ${face.isLeftEyeOpenProbability}")
            Log.d(MY_TAG, "whichEmoji: rightEyeOpenProb = ${face.isRightEyeOpenProbability}");
            var smiling = face.isSmilingProbability > SMILING_PROB_THRESHOLD
            var leftEyeClosed = face.isLeftEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD
            var rightEyeClosed = face.isRightEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD

            if (smiling) {
                mEmoji = if (leftEyeClosed && !rightEyeClosed) {
                    mLWink
                } else if (rightEyeClosed && !leftEyeClosed) {
                    mRWink
                } else if (leftEyeClosed) {
                    mCESmile
                } else {
                    mSmile
                }
            } else {
                mEmoji = if (leftEyeClosed && !rightEyeClosed) {
                    mLWFrown
                } else if (rightEyeClosed && !leftEyeClosed) {
                    mRWFrown
                } else if (leftEyeClosed) {
                    mCEFrown
                } else {
                    mFrown
                }
            }
            return mEmoji

        }

    }
}