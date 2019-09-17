package fyi.modules.auth

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.auth.models.BiometricAuthOptionalParameters
import fyi.modules.auth.models.AuthResponse
import fyi.modules.auth.models.AuthResponseDTO
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.repository.auth_requests.AuthRequestManager
import fyi.utils.Args
import infinitum.Auth_request
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json

/**
 * Responsible for handling all Authentication related requests.
 *
 * @property mBaseUrl Base url of the Auth module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
class Auth(
    private var mBaseUrl: String,
    private val mNetworkService: NetworkService,
    private val mRepository: Repository){

    /**
     * Authenticates the user by his [email] with the [password].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns an [AuthResponse] that contains data about the User and a token to be used by the SDK.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun authenticate(
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, email, password)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("email", email),
            Pair("password", password)
        )

        val url = mBaseUrl.plus("/login")

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                val authResponse = Json.nonstrict.parse(AuthResponseDTO.serializer(), response)
                mRepository.setUserToken(authResponse.token)
                onSuccess((AuthResponse(authResponse.name, authResponse.email)))
            },
            onFailure = onFailure
        )
    }


    /**
     * Authenticates the user using biometric technology. Uses the [photoB64] which is the user photo in Base64 format
     * and [optionalParametersBuilder] if the User wants to add more information to the request.
     * Note that each platform has an implementation of a method to transform an image to Base64 format in the Utils class.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns an [AuthResponse] that contains data about the User and a token to be used by the SDK.
     */
    fun biometricAuthentication(
        photoB64: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit,
        optionalParametersBuilder: BiometricAuthOptionalParameters.Builder
    ) {

        val authToken = mRepository.getAccessToken()
        val appToken = mRepository.getAppToken()
        val photoOptionalParameters = optionalParametersBuilder.build()

        if (!Args.checkForContent(authToken, appToken, photoB64, photoOptionalParameters)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        if (mRepository.isConnected()) {
            val deviceIdentity = mRepository.getDeviceId()

            val url = mBaseUrl.plus("/biometrid")

            val body = Args.createMap(
                Pair("photo64", photoB64),
                Pair("device_identity", deviceIdentity)
            )

            body.putAll(photoOptionalParameters.toMap())

            val header = Args.createMap(
                Pair("authorization", "Bearer $authToken"),
                Pair("AppToken", appToken)
            )

            RequestLauncher.launch(
                url = url,
                headerParameters = header,
                bodyParameters = body,
                method = HttpMethod.Post,
                networkService = mNetworkService,
                onSuccess = {response ->
                    val authResponse = Json.nonstrict.parse(AuthResponseDTO.serializer(), response)
                    mRepository.setUserToken(authResponse.token)
                    onSuccess((AuthResponse(authResponse.name, authResponse.email)))
                },
                onFailure = onFailure
            )
        }else {
            if (mRepository.isOfflineModeEnabled()) {
                AuthRequestManager.storeNewAuthenticationRequest(photoB64, optionalParametersBuilder, mRepository)
                onFailure(Errors.REQUEST_SAVED.error)
            }else {
                onFailure(Errors.NETWORK_ERROR.error)
            }
        }
    }

    /**
     * Used by the SDK to send stored Authentication requests.
     * Sends a stored [authRequest] to the [baseUrl] with the [authToken] and [appToken].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    internal fun biometricAuthentication(
        baseUrl: String,
        authRequest: Auth_request,
        authToken: String,
        appToken: String,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit) {
        if (mRepository.isConnected()) {
            val deviceIdentity = mRepository.getDeviceId()

            //Url is needed to make sure it uses the right domain of the saved photo request
            val url = baseUrl.plus("auth/biometrid")

            val body = Args.createMap(
                Pair("photo64", authRequest.image),
                Pair("device_identity", deviceIdentity)
            )

            val optionalParametersBuilder = BiometricAuthOptionalParameters.Builder()

            optionalParametersBuilder.
                    setPosition(authRequest.position!!).
                    setAction(authRequest.action!!).
                    setData(authRequest.data!!).
                    setProximity(authRequest.proximity!!).
                    setDate(authRequest.date)

            body.putAll(optionalParametersBuilder.build().toMap())

            val header = Args.createMap(
                Pair("authorization", "Bearer $authToken"),
                Pair("AppToken", appToken)
            )

            RequestLauncher.launch(
                url = url,
                headerParameters = header,
                bodyParameters = body,
                method = HttpMethod.Post,
                networkService = mNetworkService,
                onSuccess = {
                    onSuccess
                },
                onFailure = onFailure
            )
        }
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