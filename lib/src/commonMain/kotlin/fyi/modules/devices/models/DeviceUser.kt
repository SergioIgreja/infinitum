package fyi.modules.devices.models

import fyi.exceptions.Errors
import fyi.utils.OptionalParameters

/**
 * Class responsible to handle the creation of a new Device User. It has a lot of conditions like it needs to have
 * a deviceId or a deviceIdentity and a userId or a userEmail.
 *
 * @property mBuilder Builder.
 * @property result The map that will be used to create the Request body.
 */
data class DeviceUser private constructor(
    val mBuilder: Builder){
    val result = mutableMapOf<String, String>()

    /**
     * Transform this [OptionalParameters] to a map
     */
    fun toMap(): Any {
        val error = Errors.INVALID_PARAMETER.error
        if (mBuilder.mDeviceId == -1 && mBuilder.mDeviceIdentity == "") {
            error.message += " Required deviceId or deviceIdentity."
            return error
        }else {
            //Prioritizes device id over device identity if both are set
            if (mBuilder.mDeviceId >= 0) {
                result["device_id"] = mBuilder.mDeviceId.toString()
            }else {
                result["device_identity"] = mBuilder.mDeviceIdentity
            }
        }

        if (mBuilder.mUserId == -1 && mBuilder.mUserEmail == "") {
            error.message += " Required userId or userEmail."
            return error
        } else {
            //Prioritizes userId over userEmail if both are set
            if (mBuilder.mUserId >= 0) {
                result["user_id"] = mBuilder.mUserId.toString()
            }else {
                result["user_email"] = mBuilder.mUserEmail
            }
        }
        return result
    }

    /**
     * Builder class to facilitate the insertion of optional data.
     *
     * @property mDeviceId Device id.
     * @property mUserId User id.
     * @property mDeviceIdentity Device Identity.
     * @property mUserEmail User email.
     */
    class Builder {
        var mDeviceId = -1
        var mUserId = -1
        var mDeviceIdentity = ""
        var mUserEmail = ""

        fun setDeviceId(deviceId: Int): Builder {
            this.mDeviceId = deviceId
            return this
        }

        fun setUserId(userId: Int): Builder {
            this.mUserId = userId
            return this
        }

        fun setDeviceIdentity(deviceIdentity: String): Builder {
            mDeviceIdentity = deviceIdentity
            return this
        }

        fun setUserEmail(userEmail: String): Builder {
            mUserEmail = userEmail
            return this
        }

        internal fun build(): DeviceUser {
            return DeviceUser(this)
        }
    }

}