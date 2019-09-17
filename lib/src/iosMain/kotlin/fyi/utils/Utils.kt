package fyi.utils

import platform.Foundation.*
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

/**
 * iOS implementation of Utils.
 */
actual object Utils {
    /**
     * Converts an [image] to Base64 format.
     * @return The image as Base64.
     */
    fun convertImageToBase64(image: UIImage): String {
        val imageData = UIImagePNGRepresentation(image);
        return "data:image/png;base64,${imageData?.base64EncodedStringWithOptions(NSDataBase64Encoding64CharacterLineLength)!!}"
    }

    /**
     * Function to get the current Date.
     * @return Current Date as String in the format "yyyy-MM-dd HH:mm:ss Timezone"
     */
    actual fun getDate(): String {
        val date = NSDate()
        val formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return "${formatter.stringFromDate(date)} +0100"
    }
}