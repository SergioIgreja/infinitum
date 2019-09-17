package fyi.modules.auth.models

import fyi.utils.Args
import fyi.utils.OptionalParameters
import fyi.utils.Utils


//It's necessary to use the builder because in java it forces us to use nulls, we cant select the parameters we want.
/**
 * Class that contains information about all the [OptionalParameters] the authentication requests can have.
 * It also knows how this information should be constructed in the body of the request.
 *
 * @property mPosition User position when the request was made.
 * @property mProximity Proximity of the photo.
 * @property mAction Action
 * @property mData Authentication data.
 * @property mDate The date the request was made. Can be useful to use [Utils.getDate] method to build this string.
 */
class BiometricAuthOptionalParameters private constructor (
    builder: Builder) : OptionalParameters{

    private val mPosition: String
    private val mProximity: String
    private val mAction: String
    private val mData: String
    private val mDate: String

    init {
        mPosition = builder.getPosition()
        mProximity = builder.getProximity()
        mAction = builder.getAction()
        mData = builder.getData()
        mDate = builder.getDate()
    }

    /**
     * Transforms this [OptionalParameters] to a map to be used has a request body.
     */
    override fun toMap(): MutableMap<String, String> {
        return Args.createMap(
            Pair("position", mPosition),
            Pair("proximity", mProximity),
            Pair("action", mAction),
            Pair("data", mData),
            Pair("date", mDate)
        )
    }


    /**
     * Builder class to facilitate the insertion of optional data.
     */
    class Builder {
        var position: String
        var proximity: String
        var action: String
        var data: String
        var date: String

        init {
            position = ""
            proximity = ""
            action = ""
            data = ""
            date = ""
        }

        fun setPosition(latitude: Float, longitude: Float): Builder {
            this.position = Args.createPositionJson(latitude, longitude)
            return this
        }

        fun setProximity(proximity: String): Builder {
            this.proximity = proximity
            return this
        }

        fun setAction(action: String): Builder {
            this.action = action
            return this
        }

        fun setData(data: String): Builder {
            this.data = data
            return this
        }

        fun setDate(date: String): Builder {
            this.date = date
            return this
        }

        internal fun setPosition(position: String): Builder {
            this.position = position
            return this
        }

        fun build(): BiometricAuthOptionalParameters {
            return BiometricAuthOptionalParameters(
                this
            )
        }

        internal fun getPosition(): String {
            return position
        }

        internal fun getProximity(): String {
            return proximity
        }

        internal fun getAction(): String {
            return action
        }

        internal fun getData(): String {
            return data
        }

        internal fun getDate(): String {
            return date
        }
    }

}