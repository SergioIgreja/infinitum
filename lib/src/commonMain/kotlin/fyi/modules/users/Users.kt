package fyi.modules.users

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.users.models.UserOptionalParameters
import fyi.modules.users.models.UserResponse
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Responsible for handling all User related requests.
 *
 * @property mBaseUrl Base url of the Device module.
 * @property mNetworkService Injected NetworkService.
 * @property mRepository Injected Repository.
 */
data class Users(private var mBaseUrl: String, private val mNetworkService: NetworkService, private val mRepository: Repository) {


    /**
     * Gets the count of all the Users in this domain.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the User count.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllUsersCount(
        onSuccess: (Int) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/count")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                onSuccess(response.toString().toInt())
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets all the Users in this domain.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] a list of [UserResponse] objects.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getAllUsers(
        onSuccess: (List<UserResponse>) -> Unit,
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
            onSuccess = { response ->
                val users = Json.nonstrict.parse(UserResponse.serializer().list, response)
                onSuccess(users)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the User by his [userId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [UserResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getUserById(
        userId: Int,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, userId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$userId")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = null,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = { response ->
                val user = Json.nonstrict.parse(UserResponse.serializer(), response)
                onSuccess(user)
            },
            onFailure = onFailure
        )
    }

    /**
     * Gets the User by his [photo] in Base64 format. Use Utils class to parse an image to Base64.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [UserResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getUserByFace(
        photo: String,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, photo)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/face")

        val body = Args.createMap(
            Pair("photo64", photo)
        )

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = { response ->
                val user = Json.nonstrict.parse(UserResponse.serializer(), response)
                onSuccess(user)
            },
            onFailure = onFailure
        )
    }

    /**
     * TODO This function is not working because the servers isn't receiving photo64. Cmon SEBASSSSSSSSSS
     * Gets a list of users present in the [photo] in Base64 format. Use Utils class to parse an image to Base64.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a list of [UserResponse] objects.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun getUsersByMultipleFaces(
        photo: String,
        onSuccess: (List<UserResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, photo)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/faces")

        val body = Args.createMap(
            Pair("photo64", photo)
        )

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = { response ->
                val users = Json.nonstrict.parse(UserResponse.serializer().list, response)
                onSuccess(users)
            },
            onFailure = onFailure
        )
    }

    /**
     * Creates a new User with the [name] and [optionalParameters].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun createUser(
        name: String,
        optionalParameters: UserOptionalParameters.Builder,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, optionalParameters!!)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", name)
        )

        body.putAll(optionalParameters.build().toMap())

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
     * Deletes an User by his [userId].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns a [UserResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun deleteUser(
        userId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, userId)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$userId")

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
     * Updates an User by his [userId] to have a new [name] and other relevant information contained in [otherParameters].
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns an [UserResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun updateUser(
        userId: Int,
        name: String,
        otherParameters: UserOptionalParameters.Builder,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, userId, name, otherParameters)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/$userId")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("name", name)
        )

        body.putAll(otherParameters.build().toMap())

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = { response ->
                val user = Json.nonstrict.parse(UserResponse.serializer(), response)
                onSuccess(user)
            },
            onFailure = onFailure
        )
    }

    /**
     * Verifies if the User can use Liveness request.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun userLivenessRequest(
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/liveness")

        val header = Args.createAuthorizationHeader(accessToken)

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    /**
     * TODO request not done in the Server side.
     * Verifies User documents [front] and [back]. Use utils class to convert images to base64.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the body of the Http response.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun verifyDocuments(
        front: String,
        back: String,
        onSuccess: (String) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, front, back)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.plus("/document/verify")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("front64", front),
            Pair("back64", back)
        )

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess(it)
            },
            onFailure = onFailure
        )
    }

    /**
     * Verifies an User by his [photo]. Use Utils class to convert an image to base64.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] an [UserResponse] object.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun verifyUserByPhoto(
        photo: String,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ){
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, photo)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.replace("/users","/user").plus("/biometric")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("photo64", photo)
        )

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                val user = Json.nonstrict.parse(UserResponse.serializer(), response)
                onSuccess(user)
            },
            onFailure = onFailure
        )

    }

    /**
     * TODO Request not done in the Server side.
     * Verifies User face properties from the [photo]. Use Utils class to convert an image to Base64.
     * Invokes [onSuccess] if the request was successful, [onFailure] otherwise.
     * The [onSuccess] returns the body of the Http response.
     * The [onFailure] returns an [ErrorResponse] that contains information about what went wrong.
     */
    fun verifyUserFaceProperties(
        photo: String,
        onSuccess: (String) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ){
        val accessToken = mRepository.getAccessToken()

        if (!Args.checkForContent(accessToken, photo)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = mBaseUrl.replace("/users","/user").plus("/face_properties")

        val header = Args.createAuthorizationHeader(accessToken)

        val body = Args.createMap(
            Pair("photo64", photo)
        )

        RequestLauncher.launch(
            url = url,
            headerParameters = header,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                onSuccess(response)
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