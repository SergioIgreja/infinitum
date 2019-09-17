package fyi.modules.apis.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class that represents an Api.
 *
 * @property id Id of the Api.
 * @property name Name of the Api.
 * @property alias Alias of the Api.
 * @property data Data of the Api.
 * @property api_type_id Type id of the Api.
 * @property state_id State of the Api.
 */
@Serializable
data class ApiResponse(
    val id: Int = -1,
    val name: String = "",
    val alias: String = "",
    val data: JsonElement?,
    val api_type_id: Int,
    val state_id: Int
)