package fyi.utils

import platform.UIKit.UIDevice

/**
 * The implementation of ApplicationContext in iOS.
 */
actual class ApplicationContext {
    /**
     * @return the device id of the iPhone.
     */
    actual fun getDeviceId(): String {
        return UIDevice.currentDevice().identifierForVendor()?.UUIDString()!!
    }
}