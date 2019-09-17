package fyi.modules.devices.models

import fyi.utils.Args
import fyi.utils.OptionalParameters

/**
 * Class that contains information about all the [OptionalParameters] the DeviceInput UPDATE request can have.
 * It also knows how this information should be constructed in the body of the request.
 *
 * @property mBuilder Builder responsible for setting the optional parameters.
 */
data class UpdateDeviceOptionalParameters(
    val mBuilder: Builder
): OptionalParameters {

    /**
     * Transform this [OptionalParameters] to a map
     */
    override fun toMap(): MutableMap<String, String> {
        return Args.createMap(
            Pair("name", mBuilder.mName),
            Pair("ip", mBuilder.mIp),
            Pair("mac_address", mBuilder.mMacAddress),
            Pair("device_type", mBuilder.mDeviceType),
            Pair("app_id", if (mBuilder.mAppId == -1) "" else mBuilder.mAppId.toString()), //Since it's initialized to -1..
            Pair("app_version", mBuilder.mAppVersion),
            Pair("licensed", if (mBuilder.mLicensed) "1" else "0"),
            Pair("data", mBuilder.mData),
            Pair("users", mBuilder.mUsers),
            Pair("locations", mBuilder.mLocations)
        )
    }

    /**
     * Builder class to facilitate the insertion of optional data.
     *
     * @property mName Device name.
     * @property mIp Device ip.
     * @property mMacAddress Device mac address.
     * @property mDeviceType Device type.
     * @property mAppVersion Device app version.
     * @property mLicensed Device licensed state.
     * @property mData Device data.
     * @property mUsers Device Users.
     * @property mLocations Device locations.
     */
    class Builder() {
        var mName = ""
        var mIp = ""
        var mMacAddress = ""
        var mDeviceType = ""
        var mAppId = -1
        var mAppVersion = ""
        var mLicensed = false
        var mData = ""
        var mUsers = ""
        var mLocations = ""

        fun setIp(ip: String): Builder {
            mIp = ip
            return this
        }

        fun setMacAddress(macAddress: String): Builder {
            mMacAddress = macAddress
            return this
        }

        fun setDeviceType(deviceType: String): Builder {
            mDeviceType = deviceType
            return this
        }

        fun setAppVersion(appVersion: String): Builder {
            mAppVersion = appVersion
            return this
        }

        fun setLicensed(licensed: Boolean): Builder {
            mLicensed = licensed
            return this
        }

        fun setData(data: String): Builder {
            mData = data
            return this
        }

        fun setUsers(users: String): Builder {
            mUsers = users
            return this
        }

        fun setLocations(locations: String): Builder {
            mLocations = locations
            return this
        }

        internal fun build(): UpdateDeviceOptionalParameters {
            return UpdateDeviceOptionalParameters(this)
        }
    }
}