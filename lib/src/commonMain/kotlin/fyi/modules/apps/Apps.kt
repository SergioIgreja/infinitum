package fyi.modules.apps

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.apps.models.App
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Module that contains all the App related requests.
 *
 * @property mBaseUrl Base url of the Apps module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class Apps(private var mBaseUrl: String, private val mNetworkService: NetworkService, val mRepository: Repository) {


    /**
     * Gets the list of existing Applications in the initialized domain.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [App] that contains all the existing Apps.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getApps(
        onSuccess: (List<App>) -> Unit,
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
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val apps = Json.nonstrict.parse(App.serializer().list, response)
                onSuccess(apps)
            },
            onFailure = onFailure
        )
    }

    /**
     * Creates a new App with the given [appName], type [appTypeId] and [token].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun createApp(
        appName: String,
        appTypeId: Int,
        token: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, appName, appTypeId, token)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", appName),
            Pair("app_type_id", appTypeId.toString()),
            Pair("token", token)
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
     * Returns an App by its [appId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [App] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAppById(
        appId: Int,
        onSuccess: (App) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, appId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$appId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {response ->
                val app = Json.nonstrict.parse(App.serializer(), response)
                onSuccess(app)
            },
            onFailure = onFailure
        )
    }

    /**
     * Deletes an App by its [appId]
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteApp(
        appId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
        ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, appId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$appId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Delete,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess(true)
            },
            onFailure = onFailure
        )
    }

    /**
     * Updates an App by it's [appId] to change the [appName] and [appTypeId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateApp(
        appId: Int,
        appName: String,
        appTypeId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, appId, appName, appTypeId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$appId")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", appName),
            Pair("app_type_id", appTypeId.toString())
        )

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Put,
            networkService = mNetworkService,
            onSuccess = {result ->
                onSuccess((result).startsWith("{id:"))
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