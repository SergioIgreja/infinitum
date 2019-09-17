package fyi.modules.deviceinput.models

import fyi.utils.Args
import fyi.utils.OptionalParameters

/**
 * Class that contains information about all the [OptionalParameters] the DeviceInput UPDATE request can have.
 * It also knows how this information should be constructed in the body of the request.

 * @property mBuilder Builder responsible for setting the optional parameters.
 */
data class UpdateDeviceInputOptionalParameters private constructor(
    val mBuilder: Builder
): OptionalParameters {

    /**
     * Transform this [OptionalParameters] to a map
     */
    override fun toMap(): MutableMap<String, String> {
        return Args.createMap(
            Pair("device_id", if (mBuilder.mDeviceId == -1) "" else mBuilder.mDeviceId.toString()),
            Pair("device_type_id", if (mBuilder.mDeviceTypeId == -1) "" else mBuilder.mDeviceTypeId.toString()),
            Pair("value", mBuilder.mValue),
            Pair("data", mBuilder.mData),
            Pair("action", mBuilder.mAction),
            Pair("name", mBuilder.mName)
        )
    }

    /**
     * Builder class to facilitate the insertion of optional data.
     *
     * @property mDeviceId Device id.
     * @property mDeviceTypeId Id of the Device type.
     * @property mValue DeviceInput value.
     * @property mData DeviceInput data.
     * @property mAction DeviceInput action.
     * @property mName DeviceInput name.
     */
    class Builder() {
        var mDeviceId = -1
        var mDeviceTypeId = -1
        var mValue = ""
        var mData = ""
        var mAction = ""
        var mName = ""

        fun setDeviceId(deviceId: Int): Builder {
            mDeviceId = deviceId
            return this
        }

        fun setDeviceTypeId(deviceTypeId: Int): Builder {
            mDeviceTypeId = deviceTypeId
            return this
        }

        fun setValue(value: String): Builder {
            mValue = value
            return this
        }

        fun setData(data: String): Builder {
            mData = data
            return this
        }

        fun setAction(action: String): Builder {
            mAction = action
            return this
        }

        fun setName(name: String): Builder {
            mName = name
            return this
        }

        internal fun build(): UpdateDeviceInputOptionalParameters {
            return UpdateDeviceInputOptionalParameters(this)
        }
    }
}