package fyi.modules.devices.models

import fyi.models.Config
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class that represents a Device response from the server.
 *
 * @property id Device id.
 * @property name Device name.
 * @property ip Device ip.
 * @property mac_address Device mac address.
 * @property identity Device identity.
 * @property value Device value.
 * @property app_version Device app version.
 * @property licensed Device licensed state.
 * @property action Device action.
 * @property app Device [App].
 * @property type Device [Type]
 */
@Serializable
data class DeviceResponse(
    val id: Int? = -1,
    val name: String? = "",
    val ip: String? = "",
    val mac_address: String? = "",
    val identity: String? = "",
    val value: String? = "",
    val app_version: String? = "",
    val licensed: Int? = 0,
    val action: String? = "",
    val app: App?,
    val type: Type?
)

/**
 * Class that represents a Device App.
 *
 * @property id App id.
 * @property name App name.
 * @property token App token.
 * @property data App data.
 * @property biometric_clock App [Config]
 */
@Serializable
data class App(
    val id: Int? = -1,
    val name: String? = "",
    val token: String? = "",
    val data: JsonElement?,
    val biometric_clock: Config?
)

/**
 * Class that represents a Device Type.
 *
 * @property id Device Type id.
 * @property name Device Type name.
 * @property alias Device Type alias.
 */
@Serializable
data class Type(
    val id: Int? = -1,
    val name: String? = "",
    val alias: String? = ""
)
