package fyi.modules.users.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class that represents the User response from the server.
 *
 * @property id User id.
 * @property name User name.
 * @property email User email.
 * @property phone User phone.
 * @property avatar User avatar.
 * @property info User [Info].
 */
@Serializable
data class UserResponse(
    val id: Int,
    val name: String? = "",
    val email: String = "",
    val phone: String? = "",
    val avatar: String? = "",
    val info: Info? = null
)

/**
 * Class that represents User info.
 *
 * @property birthdate User birthdate.
 * @property language User language.
 * @property photo User photo.
 * @property data User data.
 */
@Serializable
data class Info(
    val birthdate: String? = "",
    val language: String? = "",
    val photo: String? = "",
    val data: JsonElement?
)