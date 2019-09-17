package fyi.utils

/**
 * Interface implemented by the OptionalParameters of some requests.
 */
interface OptionalParameters {
    /**
     * @return the information in the OptionalParameters as a map to be used during the request.
     */
    fun toMap(): MutableMap<String, String>
}