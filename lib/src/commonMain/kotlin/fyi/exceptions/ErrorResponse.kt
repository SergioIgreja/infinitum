package fyi.exceptions

import kotlinx.serialization.Serializable

/**
 * Returned by the SDK when an error has occurred.
 *
 * @property message Error message.
 * @property type Error type.
 * @property status Error status.
 */
@Serializable
data class ErrorResponse(var message: String?="", var type: String?="", var status: Int?=0)

