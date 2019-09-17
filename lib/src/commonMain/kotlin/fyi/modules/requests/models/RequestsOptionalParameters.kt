package fyi.modules.requests.models

/**
 * Class that contains information about all the optional parameters the Request module requests can have.
 * Every request of this module is a GET request and can have a query to get requests between dates.
 * It also knows how to properly construct the url.
 *
 * @property mBuilder The builder created by the user if he wants to get requests between dates.
 */
data class RequestsOptionalParameters private constructor(
    val mBuilder: Builder
) {

    /**
     * Constructs the query to parse between dates.
     * @return The url with the query.
     */
    fun getQuery(): String {
        var result = ""

        if (mBuilder.mStart.isBlank() && mBuilder.mEnd.isBlank()) return result
        else {
            if (mBuilder.mStart.isNotBlank()) {
                result = result.plus("?start=${mBuilder.mStart}")
                if (mBuilder.mEnd.isNotBlank()) {
                    result = result.plus("&end=${mBuilder.mEnd}")
                }
            }else {
                result = result.plus("?end=${mBuilder.mEnd}")
            }
        }

        return result
    }

    /**
     * Builder class to facilitate the insertion of the start and end dates.
     */
    class Builder() {
        var mStart = ""
        var mEnd = ""

        fun setStart(start: String): Builder {
            mStart = start
            return this
        }

        fun setEnd(end: String): Builder {
            mEnd = end
            return this
        }

        fun build(): RequestsOptionalParameters {
            return RequestsOptionalParameters(this)
        }
    }
}