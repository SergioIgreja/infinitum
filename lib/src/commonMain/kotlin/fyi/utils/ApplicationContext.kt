package fyi.utils

/**
 * Expects the class to be implemented on iOS and Android.
 * Used by the Android side to pass the Context of the application to the SDK via Injection in the getInstance method of
 * the SDK.
 */
expect class ApplicationContext {
    /**
     *@return The device id of the mobile phone.
     */
    fun getDeviceId(): String
}