package fyi.utils

/**
 * Singleton class that contains helpful methods that create arguments used constantly by the SDK.
 */
object Args {
    var identity : String? = null

    /**
     * All the [arguments] received from the user are validated in this function.
     *
     * The [arguments] are validated by type.
     * @return True if the arguments are valid, false otherwise.
     */
    internal fun checkForContent(vararg arguments: Any): Boolean {
        for (argument in arguments) {
            when (argument) {
                is String -> {
                    if (argument.isBlank()) return false
                }

                is Int -> {
                    if (argument < 0) return false
                }

                is Pair<*,*> -> {
                    if ((argument.second as String).isBlank()) return false
                }

                is MutableList<*> -> {
                    argument.forEach {
                        if ((it as String).isBlank()) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Creates a map that represents the body of a http request.
     *
     * Uses the [pairs] given to create the map. Will only add the pairs if the value is not null
     * to not clutter the request with useless data.
     * @return A map of the given pairs.
     */
    internal fun createMap(vararg pairs: Pair<String, String>): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()

        for(pair in pairs) {
            if (pair.second.isNotBlank()) result[pair.first] = pair.second
        }

        return result
    }

    //Will add to the map the array as key[0], key[1], key[2]
    /**
     * Creates a map that represents the body of a http request.
     *
     * For requests that require an array, use this function to create an array for the [key] with the [list]
     * of values.
     * @return A map that contains an array. { key[0] = list[0], key[1] = list[1], .... }
     */
    internal fun createMap(key: String, list: MutableList<String>): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()

        if (list.isNotEmpty()) {
            var i = 0
            list.forEach {
                if (it.isNotBlank()) {
                    result["$key[$i]"] = it
                    i += 1
                }
            }
        }

        return result
    }

    /**
     * Returns a map that represents the header of a request.
     *
     * Uses "Bearer [accessToken]" as a value of the Authorization field in a header.
     * @return A map representing an Authorization header.
     */
    internal fun createAuthorizationHeader(accessToken: String): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()

        result["Authorization"] = "Bearer $accessToken"

        if (identity != null) {
            result["identity"] = identity!!
        }

        return result
    }

    /**
     * Creates a Json string where the lat field has the value [latitude] and the lng
     * field has the [longitude] value.
     * @return A string in Json format.
     */
    fun createPositionJson(latitude: Float, longitude: Float): String {
        return """{"lat":$latitude,"lng":$longitude}"""
    }

}