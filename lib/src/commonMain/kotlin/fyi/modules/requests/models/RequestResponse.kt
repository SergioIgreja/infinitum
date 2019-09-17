package fyi.modules.requests.models

import kotlinx.serialization.Serializable

/**
 * Represents a request.
 *
 * @property id Request id.
 * @property app Request app name.
 * @property billing_id Request billing_id.
 * @property user User id that made the request.
 * @property ip Request ip.
 * @property type Request type.
 * @property api Request api.
 * @property module Request module.
 * @property location Request location.
 * @property url Request url.
 * @property code Request response code.
 * @property time Request time.
 * @property created_at Created at date.
 * @property updated_at Updated at date.
 */
@Serializable
data class RequestResponse(
    val id: Int = -1,
    val app: String? = "",
    val billing_id: Int? = -1,
    val user: Int = -1,
    val ip: String = "",
    val type: String = "",
    val api: String = "",
    val module: String = "",
    val location: String = "",
    val url: String = "",
    val code: Int = -1,
    val time: Float = -1.0f,
    val created_at: String = "",
    val updated_at: String = ""
)