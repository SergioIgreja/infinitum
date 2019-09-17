package fyi.modules.deviceposition.models

import kotlinx.serialization.Serializable

/**
 * Class that represents a DevicePosition response from the server.
 *
 * @property id DevicePosition id.
 * @property device_id Device id of the DevicePosition.
 * @property lat Latitude value of the position.
 * @property lng Longitude value of the position.
 */
@Serializable
data class DevicePositionResponse(
    val id: Int,
    val device_id: Int,
    val lat: String,
    val lng: String
)