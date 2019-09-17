package fyi.models

import kotlinx.serialization.Serializable

/**
 * Represents the response given by the Server when executing the config Request.
 *
 * @property apps List of applications available in the domain used in the config request.
 */
@Serializable
data class ConfigResponse(val apps: List<App>)

/**
 * Represents an App.
 *
 * @property name App name.
 * @property token App token.
 */
@Serializable
data class App(val name: String="", val token: String="")