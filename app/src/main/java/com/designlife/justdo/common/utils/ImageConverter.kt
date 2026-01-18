package com.designlife.justdo.common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageConverter {

    private fun getConvertedByteArray(bitmap: Bitmap) : ByteArray{
        val halfMb = 500*1024
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        var byteArray = stream.toByteArray()

        if (byteArray.size > halfMb){
            val resizedBitMap = resizeBitmap(bitmap,halfMb)
            resizedBitMap.compress(Bitmap.CompressFormat.PNG,100,stream)
            byteArray = stream.toByteArray()
        }
        return byteArray
    }

    private fun getImageSizeInKb(bitmap: Bitmap): Int {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.size() / 1024
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSizeKb: Int): Bitmap {
        // Calculate the original size in kilobytes
        val originalSizeKb = getImageSizeInKb(bitmap)

        // Calculate the sample size to reduce image dimensions
        val options = BitmapFactory.Options()
        options.inSampleSize = Math.ceil((originalSizeKb / maxSizeKb).toDouble()).toInt()

        // Decode the bitmap with the calculated sample size
        return BitmapFactory.decodeByteArray(getByteArrayFromBitmap(bitmap, options), 0, getByteArrayFromBitmap(bitmap, options).size, options)
    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap, options: BitmapFactory.Options): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun getConvertedBitMap(imageData: ByteArray) : Bitmap?{
        try {
//            val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getBitMapFromByteArray(imageData : ByteArray?) : Bitmap?{
        if (imageData == null)
            return null
       return getConvertedBitMap(imageData)
    }

    fun getByteArrayFromBitMap(bitmap : Bitmap?) : ByteArray?{
        if (bitmap == null)
            return null
        return getConvertedByteArray(bitmap)
    }
}