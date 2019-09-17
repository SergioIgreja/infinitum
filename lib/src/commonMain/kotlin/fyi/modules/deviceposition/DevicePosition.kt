package fyi.modules.deviceposition

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.deviceposition.models.DevicePositionResponse
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Module that contains all the DevicePosition related requests.
 *
 * @property mBaseUrl Base url of the DevicePosition module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class DevicePosition (
    private var mBaseUrl: String,
    private val mNetworkService: NetworkService,
    private val mRepository: Repository
) {

    /**
     * Gets the list of DevicePositions in the initialized domain.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [DevicePositionResponse] that contains all the existing DevicePositions.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllDevicePositions(
        onSuccess: (List<DevicePositionResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = mBaseUrl,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val devicePositions = Json.nonstrict.parse(DevicePositionResponse.serializer().list,
                    response)
                onSuccess(devicePositions)
            },
            onFailure = onFailure
        )

    }

    /**
     * Creates a new Device Position at [latitude] and [longitude] and associates to the current Device.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newDevicePosition(
        latitude: String,
        longitude: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        val deviceIdentity = mRepository.getDeviceId()

        if (!Args.checkForContent(accessToken, deviceIdentity, latitude, longitude)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_identity", deviceIdentity),
            Pair("lat", latitude),
            Pair("lng", longitude)
        )

        RequestLauncher.launch(
            url = mBaseUrl,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess(true)
            },
            onFailure = onFailure
        )
    }

    /**
     * Creates a new Device Position at [latitude] and [longitude] and associates to a Device by its [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newDevicePosition(
        deviceId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId, latitude, longitude)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_id", deviceId.toString()),
            Pair("lat", latitude),
            Pair("lng", longitude)
        )

        RequestLauncher.launch(
            url = mBaseUrl,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess(true)
            },
            onFailure = onFailure
        )
    }

    /**
     * Deletes a DevicePosition by its [devicePositionId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteDevicePosition(
        devicePositionId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, devicePositionId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$devicePositionId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Delete,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess(true)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a DevicePosition by its [devicePositionId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns an [DevicePositionResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getDevicePositionById(
        devicePositionId: Int,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, devicePositionId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$devicePositionId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val devicePosition = Json.nonstrict.parse(DevicePositionResponse.serializer(), response)
                onSuccess(devicePosition)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a list of DevicePositions by the associated [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [DevicePositionResponse] associated to that Device.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getDevicePositionsByDeviceId(
        deviceId: Int,
        onSuccess: (List<DevicePositionResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.replace("positions", "$deviceId/positions")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val devicePosition = Json.nonstrict.parse(DevicePositionResponse.serializer().list, response)
                onSuccess(devicePosition)
            },
            onFailure = onFailure
        )
    }

    /**
     * Updates a DevicePosition by its [devicePositionId], and updates the [deviceId], [latitude] and [longitude] values.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the updated [DevicePositionResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateDevicePosition(
        devicePositionId: Int,
        deviceId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(devicePositionId, accessToken, deviceId, latitude, longitude)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_id", deviceId.toString()),
            Pair("lat", latitude),
            Pair("lng", longitude)
        )

        val url = mBaseUrl.plus("/$devicePositionId")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Put,
            networkService = mNetworkService,
            onSuccess = {response ->
                val devicePosition = Json.nonstrict.parse(DevicePositionResponse.serializer(), response)
                onSuccess(devicePosition)
            },
            onFailure = onFailure
        )
    }

    /**
     * Updates a DevicePosition by its [devicePositionId], and updates the [latitude] and [longitude] values. It also
     * exchanges the deviceId to be that of the current device.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the updated [DevicePositionResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateDevicePosition(
        devicePositionId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        val deviceIdentity = mRepository.getDeviceId()

        if (!Args.checkForContent(devicePositionId, accessToken, deviceIdentity, latitude, longitude)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_identity", deviceIdentity),
            Pair("lat", latitude),
            Pair("lng", longitude)
        )

        val url = mBaseUrl.plus("/$devicePositionId")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Put,
            networkService = mNetworkService,
            onSuccess = {response ->
                val devicePosition = Json.nonstrict.parse(DevicePositionResponse.serializer(), response)
                onSuccess(devicePosition)
            },
            onFailure = onFailure
        )
    }


    /**
     * Used by the SDK to set the [url] to make sure the module is using the latest domain.
     */
    internal fun setUrl(url: String) {
        if (mBaseUrl != url) {
            mBaseUrl = url
        }
    }
}