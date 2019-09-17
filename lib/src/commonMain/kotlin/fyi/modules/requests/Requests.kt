package fyi.modules.requests

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.deviceinput.models.DeviceInputResponse
import fyi.modules.requests.models.*
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.internal.HashMapSerializer
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.MapEntrySerializer
import kotlinx.serialization.internal.MapLikeSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.json
import kotlinx.serialization.list
import kotlinx.serialization.serializer

/**
 * Responsible for handling all Requests related requests.
 *
 * @property mBaseUrl Base url of the Requests module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class Requests(private var mBaseUrl: String, private val mNetworkService: NetworkService, val mRepository: Repository) {

    /**
     * Gets all existing requests by api between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a map where the Key is the api name and the value is a list of [RequestResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsByApiBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val url = mBaseUrl.plus("/list/apis${builder.build().getQuery()}")

        sendRequest(
            url = url,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * TODO the request does not work --- SERVER SIDE ---
     * Gets all existing requests by location between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a map where the Key is the location and the value is a list of [RequestResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsByLocationBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val url = mBaseUrl.plus("/list/locations${builder.build().getQuery()}")

        sendRequest(
            url = url,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Gets all existing requests by module between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a map where the Key is the module name and the value is a list of [RequestResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsByModuleBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val url = mBaseUrl.plus("/list/modules${builder.build().getQuery()}")

        sendRequest(
            url = url,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Gets all existing requests by code between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a map where the Key is the code number and the value is a list of [RequestResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsByCodeBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val url = mBaseUrl.plus("/list/codes${builder.build().getQuery()}")

        sendRequest(
            url = url,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Gets all existing requests by errors between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a map where the Key is the Error and the value is a list of [RequestResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsErrorsBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val url = mBaseUrl.plus("/list/errors${builder.build().getQuery()}")

        sendRequest(
            url = url,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Gets the request count between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the number of requests.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsCountBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (Int) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/count${builder.build().getQuery()}")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val x = Json.parse(JsonObject.serializer(), it).getValue("requests_count")
                onSuccess(x.int)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the request count by Api between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [RequestCountApi].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsCountByApiBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (List<RequestCountApi>) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/count/apis${builder.build().getQuery()}")

        println("---------INFINITUM $url------------")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val response = Json.nonstrict.parse(RequestCountApi.serializer().list, it)
                onSuccess(response)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the request count by module between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [RequestCountModule].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsCountByModuleBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (List<RequestCountModule>) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/count/modules${builder.build().getQuery()}")

        println("---------INFINITUM $url------------")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val response = Json.nonstrict.parse(RequestCountModule.serializer().list, it)
                onSuccess(response)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the request count by code between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [RequestCountCode].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsCountByCodeBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (List<RequestCountCode>) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/count/codes${builder.build().getQuery()}")

        println("---------INFINITUM $url------------")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val response = Json.nonstrict.parse(RequestCountCode.serializer().list, it)
                onSuccess(response)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the request count by errors between the dates in the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [RequestCountErrors].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRequestsErrorsCountBetweenTwoDates(
        builder: RequestsOptionalParameters.Builder,
        onSuccess: (RequestCountErrors) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/count/errors${builder.build().getQuery()}")

        println("---------INFINITUM $url------------")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val response = Json.nonstrict.parse(RequestCountErrors.serializer(), it)
                onSuccess(response)
            },
            onFailure = onFailure
        )
    }


    /**
     * Helper function to avoid duplication of Code.
     */
    private fun sendRequest(
        url: String,
        onSuccess: (Map<String, List<RequestResponse>>) -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        println("---------INFINITUM $url------------")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val requests = Json.nonstrict.parse(HashMapSerializer(String.serializer(), RequestResponse.serializer().list), it)
                onSuccess(requests)
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