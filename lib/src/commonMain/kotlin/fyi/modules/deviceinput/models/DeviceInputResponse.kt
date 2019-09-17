package fyi.modules.deviceinput.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class that represents a DeviceInput response from the server.
 *
 * @property id DeviceInput id.
 * @property device_id Device id.
 * @property device_type_id Type id of the Device.
 * @property name DeviceInput name.
 * @property value DeviceInput value.
 * @property action DeviceInput action.
 * @property data DeviceInput data.
 */
@Serializable
data class DeviceInputResponse(
    val id: Int = -1,
    val device_id: Int = -1,
    val device_type_id: Int = -1,
    val name: String? = "",
    val value: String? = "",
    val action: String? = "",
    val data: JsonElement?
)