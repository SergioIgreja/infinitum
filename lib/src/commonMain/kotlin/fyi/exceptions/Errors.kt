package fyi.exceptions

/**
 * Enum class with recurring errors that can occur in the execution of Infinitum.
 *
 * @property message error message.
 */
enum class Errors(var error: ErrorResponse) {
    INVALID_PARAMETER(ErrorResponse("Parameter is invalid.", ErrorTypes.InfinitumSDK.name,0)),
    NETWORK_ERROR(ErrorResponse("Something is wrong with the connection.",ErrorTypes.Network.name,0)),
    UNKNOWN_EXCEPTION(ErrorResponse("An unknown exception was caught.", ErrorTypes.InfinitumSDK.name, 0)),
    INVALID_DOMAIN(ErrorResponse("Attempt to ping this domain failed.", ErrorTypes.InfinitumSDK.name, 11)),
    INVALID_TOKEN(ErrorResponse("Invalid token, initialize the SDK again", ErrorTypes.InfinitumSDK.name, 10)),
    REQUEST_SAVED(ErrorResponse("The authentication request was saved since the device is not connected to the internet.", ErrorTypes.InfinitumSDK.name, 1))
}