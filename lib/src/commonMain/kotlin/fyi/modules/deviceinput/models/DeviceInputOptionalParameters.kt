package fyi.modules.deviceinput.models

import fyi.utils.Args
import fyi.utils.OptionalParameters

/**
 * Class that contains information about all the [OptionalParameters] the DeviceInput requests can have.
 * It also knows how this information should be constructed in the body of the request.
 *
 * @property mBuilder Builder responsible for setting the optional parameters.
 */
data class DeviceInputOptionalParameters private constructor(
    val mBuilder: Builder
): OptionalParameters {

    /**
     * Transform this [OptionalParameters] to a map
     */
    override fun toMap(): MutableMap<String, String> {
        return Args.createMap(
            Pair("value", mBuilder.mValue),
            Pair("data", mBuilder.mData),
            Pair("action", mBuilder.mAction),
            Pair("name", mBuilder.mName)
        )
    }

    /**
     * Builder class to facilitate the insertion of optional data.
     *
     * @property mValue DeviceInput value.
     * @property mData DeviceInput data.
     * @property mAction DeviceInput action.
     * @property mName DeviceInput name.
     */
    class Builder() {
        var mValue = ""
        var mData = ""
        var mAction = ""
        var mName = ""

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

        fun build(): DeviceInputOptionalParameters {
            return DeviceInputOptionalParameters(this)
        }
    }
}