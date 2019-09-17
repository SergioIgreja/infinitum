package fyi.modules.auth.models

import kotlinx.serialization.Serializable

/**
 * Since the [token] is not relevant to the user it was necessary to create a DTO to parse the servers response.
 *
 * @property token User access token.
 * @property name User name.
 * @property email User email.
 */
@Serializable
internal data class AuthResponseDTO(
    val token: String,
    val name: String,
    val email: String
)

/**
 * An instance of this class will be returned to the User when an authentication request is successful.
 *
 * @property name User name.
 * @property email User email.
 */
@Serializable
data class AuthResponse(
    val name: String,
    val email: String
)
