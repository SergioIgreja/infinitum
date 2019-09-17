package fyi.utils

import android.content.Context
import android.provider.Settings

/**
 * The implementation of ApplicationContext on Android.
 * @property context The Context of the Android application.
 */
actual data class ApplicationContext(val context: Context) {
    /**
     * @return Android device id.
     */
    actual fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

}