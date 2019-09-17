package fyi.models

import kotlinx.serialization.Serializable

/**
 * Represents the response returned by the server when executing the init request.
 * Used a DTO because there is information that is not relevant to the User of the SDK.
 *
 * @property access_token Access token that will be used throughout the SDK  to make requests.
 * @property config Application config.
 * @property node Node server url.
 */
@Serializable
data class InitResponseDTO(
    val access_token: String,
    val config: Config,
    val node: String
)

/**
 * The response returned to the User of the SDK.
 *
 * @property config Application config.
 */
@Serializable
data class InitResponse(
    val config: Config
)

/**
 * Application config.
 *
 * @property country App country.
 * @property background App background.
 * @property logo App logo.
 * @property text_color App text color.
 * @property button_color App button color.
 * @property button_text_color App button text color.
 * @property pincode App pincode.
 * @property offline App offline mode (Enabled or not).
 */
@Serializable
data class Config(
    val country: String = "",
    val background: String = "",
    val logo: String = "",
    val text_color: String = "",
    val button_color: String = "",
    val button_text_color: String = "",
    val pincode: String = "",
    val offline: Int = -1
)