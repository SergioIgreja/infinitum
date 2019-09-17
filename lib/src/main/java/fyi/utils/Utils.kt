package fyi.utils

import android.graphics.Bitmap
import kotlinx.io.ByteArrayOutputStream
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.*

/**
 * Android implementation of Utils.
 */
actual object Utils {
    /**
     * Converts an [image] to Base64 format.
     * @return The image as Base64.
     * @throws Exception if the Bitmap exceeds 1mb after compression.
     */
    fun convertImageToBase64(image: Bitmap): String {
        val MAX_SIZE = 1000000
        val MIN_QUALITY = 50

        val baos = ByteArrayOutputStream()
        var currSize: Int
        var currQuality = 80;

        do {
            image.compress(Bitmap.CompressFormat.JPEG, currQuality, baos)
            currSize = baos.toByteArray().size
            currQuality -= 5
        }while (currSize >= MAX_SIZE && currQuality >= MIN_QUALITY)

        if (currQuality < MIN_QUALITY) {
            throw Exception("InfinitumSDK: Invalid image size - Make sure your image size does not exceed 1mb.")
        }

        return "data:image/jpeg;base64,${Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)}"
    }

    /**
     * Function to get the current Date.
     * @return Current Date as String in the format "yyyy-MM-dd HH:mm:ss Timezone"
     */
    actual fun getDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
        return format.format(Date())
    }
}