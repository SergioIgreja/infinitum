package fyi.modules.devices

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.devices.models.DeviceOptionalParameters
import fyi.modules.devices.models.DeviceResponse
import fyi.modules.devices.models.DeviceUser
import fyi.modules.devices.models.UpdateDeviceOptionalParameters
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Responsible for handling all Device related requests.
 *
 * @property mBaseUrl Base url of the Device module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class Devices(private var mBaseUrl: String, private val mNetworkService: NetworkService, val mRepository: Repository) {

    /**
     * Creates a new DeviceUser using the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newDeviceUser(
        builder: DeviceUser.Builder,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_TOKEN.error)
            return
        }

        val url = mBaseUrl.plus("/user")

        when (val body = builder.build().toMap()) {
            is ErrorResponse -> onFailure(body)
            is MutableMap<*, *> -> {
                val header = Args.createAuthorizationHeader(accessToken)

                RequestLauncher.launch(
                    url = url,
                    headerParameters = header,
                    bodyParameters = body as MutableMap<String, String>,
                    method = HttpMethod.Post,
                    networkService = mNetworkService,
                    onSuccess = {
                        onSuccess()
                    },
                    onFailure = onFailure
                )
            }
        }
    }

    /**
     * Creates a new Device with the [deviceName], [appId], [identity] and the [optionalParameters].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newDevice(
        deviceName: String,
        appId: Int,
        identity: String,
        optionalParameters: DeviceOptionalParameters,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceName, appId, identity)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", deviceName),
            Pair("app_id", appId.toString()),
            Pair("identity", identity)
        )

        body.putAll(optionalParameters.toMap())

        RequestLauncher.launch(
            url = mBaseUrl,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    /**
     * Deletes a Device by its [deviceId]
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteDevice(
        deviceId: Int,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/$deviceId")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Delete,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a Device by its [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [DeviceResponse] object.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceById(
        deviceId: Int,
        onSuccess: (DeviceResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$deviceId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val device = Json.nonstrict.parse(DeviceResponse.serializer(), response)
                onSuccess(device)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a Device by its [deviceIdentity].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [DeviceResponse] object.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceByIdentity(
        deviceIdentity: String,
        onSuccess: (DeviceResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceIdentity)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/identity/$deviceIdentity")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val device = Json.nonstrict.parse(DeviceResponse.serializer(), response)
                onSuccess(device)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a list of Devices by [macAddress].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [DeviceResponse] list.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceByMacAddress(
        macAddress: String,
        onSuccess: (List<DeviceResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, macAddress)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/mac_address/$macAddress")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val device = Json.nonstrict.parse(DeviceResponse.serializer().list, response)
                onSuccess(device)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets all Devices of the Infinitum instance.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [DeviceResponse] list.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllDevices(
        onSuccess: (List<DeviceResponse>) -> Unit,
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
                val device = Json.nonstrict.parse(DeviceResponse.serializer().list, response)
                onSuccess(device)
            },
            onFailure = onFailure
        )
    }

    /**
     * Updates a Device by its [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun updateDevice(
        deviceId: Int,
        optionalParameters: UpdateDeviceOptionalParameters.Builder,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = optionalParameters.build().toMap()

        val url = mBaseUrl.plus("/$deviceId")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Put,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    /**
     * Validates if the Device with the identity [deviceIdentity] is Licensed.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the [DeviceResponse] associated with the [deviceIdentity].
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun validateDeviceLicensed(
        deviceIdentity: String,
        onSuccess: (DeviceResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceIdentity)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("identity", deviceIdentity)
        )

        val url = mBaseUrl.plus("/validate")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                val device = Json.nonstrict.parse(DeviceResponse.serializer(), it)
                onSuccess(device)
            },
            onFailure = onFailure
        )
    }

    /**
     * Validates if the [userId] is able to use the Device [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns a [ErrorResponse] that contains information about what went wrong.
     */
    fun validateUserAllowed(
        deviceId: Int,
        userId: Int,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId, userId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_id", deviceId.toString()),
            Pair("user_id", userId.toString())
        )

        val url = mBaseUrl.plus("/validate_user")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
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
