package fyi.modules.deviceinput

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.deviceinput.models.DeviceInputOptionalParameters
import fyi.modules.deviceinput.models.DeviceInputResponse
import fyi.modules.deviceinput.models.UpdateDeviceInputOptionalParameters
import fyi.modules.deviceposition.models.DevicePositionResponse
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpMethod
import kotlinx.serialization.list

/**
 * Responsible for handling all DeviceInput related requests.
 *
 * @property mBaseUrl Base url of the DeviceInput module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class DeviceInput(private var mBaseUrl: String, private val mNetworkService: NetworkService, val mRepository: Repository) {

    /**
     * Creates a new DeviceInput related to the [deviceId] with the type [deviceTypeId]. Uses the [builder] to add
     * [DeviceInputOptionalParameters] to the request.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newDeviceInput(
        deviceId: Int,
        deviceTypeId: Int,
        builder: DeviceInputOptionalParameters.Builder,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId, deviceTypeId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("device_id", deviceId.toString()),
            Pair("device_type_id", deviceTypeId.toString())
        )

        body.putAll(builder.build().toMap())

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
     * Deletes a DeviceInput by it's [deviceInputId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteDeviceInput(
        deviceInputId: Int,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceInputId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/$deviceInputId")

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
     * Gets a DeviceInput by it's [deviceInputId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns an [DeviceInput] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceInputById(
        deviceInputId: Int,
        onSuccess: (DeviceInputResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceInputId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$deviceInputId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val deviceInput = kotlinx.serialization.json.Json.nonstrict.parse(DeviceInputResponse.serializer(), response)
                onSuccess(deviceInput)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a list of DeviceInputs related to a Device by it's [deviceId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a List of [DeviceInputResponse] that represent all the inputs of that device.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceInputsByDeviceId(
        deviceId: Int,
        onSuccess: (List<DeviceInputResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.replace("inputs", "$deviceId/inputs")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val deviceInputList = kotlinx.serialization.json.Json.nonstrict.parse(DeviceInputResponse.serializer().list, response)
                onSuccess(deviceInputList)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets a list of DeviceInputs related to a DeviceType by [deviceTypeId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a List of [DeviceInputResponse] that represent all the inputs of that device type.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getDeviceInputsByDeviceTypeId(
        deviceTypeId: Int,
        onSuccess: (List<DeviceInputResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceTypeId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/type/$deviceTypeId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val deviceInputList = kotlinx.serialization.json.Json.nonstrict.parse(DeviceInputResponse.serializer().list, response)
                onSuccess(deviceInputList)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the list of all DeviceInputs.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a List of [DeviceInputResponse] that contains all the DeviceInputs.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllDeviceInputs(
        onSuccess: (List<DeviceInputResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_TOKEN.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = mBaseUrl,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val deviceInputList = kotlinx.serialization.json.Json.nonstrict.parse(DeviceInputResponse.serializer().list, response)
                onSuccess(deviceInputList)
            },
            onFailure = onFailure
        )
    }

    /**
     * Updates a DeviceInput by it's [deviceInputId] with the information from the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the updated [DeviceInputResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateDeviceInput(
        deviceInputId: Int,
        builder: UpdateDeviceInputOptionalParameters.Builder,
        onSuccess: (DeviceInputResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, deviceInputId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$deviceInputId")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = builder.build().toMap()

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Put,
            networkService = mNetworkService,
            onSuccess = {
                val deviceInput = kotlinx.serialization.json.Json.nonstrict.parse(DeviceInputResponse.serializer(), it)
                onSuccess(deviceInput)
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