package fyi.modules.apps.models

import kotlinx.serialization.Serializable

/**
 * Class that represents an App.
 *
 * @property id App id.
 * @property name Name of the App.
 * @property token Token of the App.
 * @property type [Type] of the App.
 * @property client [Client] of the App.
 */
@Serializable
data class App(
    val id: Int,
    val name: String,
    val token: String,
    val type: Type,
    val client: Client
)

/**
 * Class that represents an App Type.
 *
 * @property alias Alias of the Type.
 */
@Serializable
data class Type(
    val alias: String
)

/**
 * Class that represents a App Client.
 *
 * @property id App Client id.
 * @property secret App Client secret.
 */
@Serializable
data class Client(
    val id: String,
    val secret: String
)