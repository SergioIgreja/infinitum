package fyi.modules.roles

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.roles.models.RolesOptionalParametes
import fyi.modules.roles.models.RolesResponse
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Responsible for handling all Roles related requests.
 *
 * @property mBaseUrl Base url of the Requests module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class Roles(private var mBaseUrl: String, private val mNetworkService: NetworkService, val mRepository: Repository) {


    /**
     * Creates a new Role with the [name], [alias] and [permissions].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun newRole(
        name: String,
        alias: String,
        permissions: MutableList<String>,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, name, alias, permissions)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", name),
            Pair("alias", alias)
        )

        body.putAll(Args.createMap("permissions", permissions))

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
     * Deletes a Role by its [roleId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteRole(
        roleId: Int,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, roleId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val url = mBaseUrl.plus("/$roleId")

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
     * Gets all the Roles that exist in this domain.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [RolesResponse].
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllRoles(
        onSuccess: (List<RolesResponse>) -> Unit,
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
            onSuccess = {
                val rolesList = Json.nonstrict.parse(RolesResponse.serializer().list, it)
                onSuccess(rolesList)
            },
            onFailure = onFailure
        )
    }

    /**
     * TODO the response is in Array format when it should be just the Json object. This parsing won't work. BACKOFFICE SEBASSSSSSS!!!!!!
     * Gets a Role by its [roleId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [RolesResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getRoleById(
        roleId: Int,
        onSuccess: (RolesResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_TOKEN.error)
            return
        }

        val url = mBaseUrl.plus("/$roleId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                val role = Json.nonstrict.parse(RolesResponse.serializer(), it)
                onSuccess(role)
            },
            onFailure = onFailure
        )
    }


    /**
     * Updates a Role by it's [roleId] with information from the [builder].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateRole(
        roleId: Int,
        builder: RolesOptionalParametes.Builder,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_TOKEN.error)
            return
        }

        val url = mBaseUrl.plus("/$roleId")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = builder.build().toMap()

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
     * Used by the SDK to set the [url] to make sure the module is using the latest domain.
     */
    internal fun setUrl(url: String) {
        if (mBaseUrl != url) {
            mBaseUrl = url
        }
    }
}