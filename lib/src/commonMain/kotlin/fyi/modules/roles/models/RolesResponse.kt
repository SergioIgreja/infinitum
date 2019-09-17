package fyi.modules.roles.models

import kotlinx.serialization.Serializable

/**
 * Class that represents the Roles response from the server.
 *
 * @property id Role id.
 * @property name Role name.
 * @property alias Role alias.
 * @property permissions Role list of permissions.
 * @property backoffice Role backoffice state
 */
@Serializable
data class RolesResponse(
    val id: Int,
    val name: String,
    val alias: String,
    val permissions: List<String?>,
    val backoffice: Int
)