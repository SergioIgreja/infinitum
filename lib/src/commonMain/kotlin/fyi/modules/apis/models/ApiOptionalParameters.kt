package fyi.modules.apis.models

import fyi.utils.Args
import fyi.utils.OptionalParameters

/**
 * Class that represents the OptionalParameters of the Apis module.
 *
 * Implements OptionalParameters to override the function toMap that transforms the parameters to a map that represents
 * the body of a request.
 * @property mBuilder The builder that has the data to instantiate this class.
 */
data class ApiOptionalParameters(
    val mBuilder: Builder
): OptionalParameters {

    /**
     * Function that will transform the OptionalParameters to a map to later be used during the request.
     * @return Map representing the body of the request.
     */
    override fun toMap(): MutableMap<String, String> {
        return Args.createMap(
            Pair("name", mBuilder.mName),
            Pair("api_type_id", if (mBuilder.mApiTypeId == -1) "" else mBuilder.mApiTypeId.toString()),
            Pair("data", mBuilder.mData)
        )
    }


    /**
     * Builder to easily add OptionalParameters.
     *
     * @property mName Api name.
     * @property mApiTypeId Api type id.
     * @property mData Data.
     */
    class Builder() {
        var mName = ""
        var mApiTypeId = -1
        var mData = ""

        fun setName(name: String): Builder {
            mName = name
            return this
        }

        fun setApiType(apiTypeId: Int): Builder {
            mApiTypeId = apiTypeId
            return this
        }

        fun setData(data: String): Builder {
            mData = data
            return this
        }

        internal fun build(): ApiOptionalParameters {
            return ApiOptionalParameters(this)
        }
    }
}