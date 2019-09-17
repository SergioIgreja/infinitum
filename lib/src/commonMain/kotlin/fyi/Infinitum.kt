package fyi

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.models.ConfigResponse
import fyi.models.InitResponse
import fyi.models.InitResponseDTO
import fyi.modules.apis.Apis
import fyi.modules.apps.Apps
import fyi.modules.auth.Auth
import fyi.modules.deviceinput.DeviceInput
import fyi.modules.devices.Devices
import fyi.modules.deviceposition.DevicePosition
import fyi.modules.requests.Requests
import fyi.modules.roles.Roles
import fyi.modules.users.Users
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import fyi.repository.node.WebSocket
import fyi.repository.node.NodeEvent.NodeEventBuilder
import fyi.utils.ApplicationContext
import fyi.utils.Args
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmStatic
import kotlin.native.concurrent.ThreadLocal

/**
 * The main class of the SDK.
 * Contains access to all modules implemented and is responsible to initialize all the relevant classes.
 * @constructor Private constructor to make sure there's only one instance of this class.
 * @property mAuth Auth module.
 * @property mApps Apps module.
 * @property mDevicePosition DevicePosition module.
 * @property mUsers Users module.
 * @property mDevices Devices module.
 * @property mDeviceInput DeviceInput module.
 * @property mRequests Requests module.
 * @property mRoles Roles module.
 * @property mApis Apis module.
 * @property mDomain The current domain that is being used. If it's not initialized and someone tries to get a module,
 * it will return null.
 * @property mApplicationContext ApplicationContext that is used to get the Context from android.
 * In iOS this class won't require any values in the constructor.
 * @property mRepository Repository of the SDK. Handles all the Preference Editor and SQL requests.
 * @property mWebSocket WebSocket that will connect to the node server.
 */
@ThreadLocal
class Infinitum {
    private val mNetworkService = NetworkService()
    private lateinit var mAuth: Auth
    private lateinit var mApps: Apps
    private lateinit var mDevicePosition: DevicePosition
    private lateinit var mUsers: Users
    private lateinit var mDevices: Devices
    private lateinit var mDeviceInput: DeviceInput
    private lateinit var mRequests: Requests
    private lateinit var mRoles: Roles
    private lateinit var mApis: Apis
    private lateinit var mDomain: String
    private val mApplicationContext: ApplicationContext
    private val mRepository: Repository
    private lateinit var mWebSocket: WebSocket

    /**
     * Receives a [applicationContext] that will be used to get the android Context. On iOS this class doesn't require
     * any parameters in it's constructor.
     */
    private constructor(applicationContext: ApplicationContext) {
        mApplicationContext = applicationContext
        try {
            mRepository = Repository(mApplicationContext)

            if (mRepository.getDomain().isNotBlank()) {
                mDomain = mRepository.getDomain()
            }
        }catch (e: Exception) {
            throw Exception("Error instantiating Infinitum. Make sure your context is not null.")
        }
    }

    //INITIALIZATION METHODS

    /**
     * Verifies if the SDK has been initialized. We consider the SDK initialized when it contains the domain and an access token.
     * Returns true if initialized, false otherwise.
     */
    internal fun isInitialized(): Boolean {
        return mRepository.getAccessToken().isNotBlank() &&
                mRepository.getDomain().isNotBlank()
    }

    /**
     * Invokes [onSuccess] if the request to the given [domain] for applications with the [appType] was successful.
     * Invokes [onFailure] otherwise.
     * Returns a [ConfigResponse] object that contains a list of applications when successful or an [ErrorResponse] if the request failed.
     */
    fun config(domain: String,
               appType: String,
               onSuccess: (ConfigResponse) -> Unit,
               onFailure: (ErrorResponse) -> Unit) {

        if (!isDomainInitialized(domain)) {
            ping(
                domain = domain,
                onSuccess = {
                    mDomain = domain
                    config(mDomain, appType, onSuccess, onFailure)
                },
                onFailure = onFailure
            )
            return
        }

        if (!Args.checkForContent(appType)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = BASE_URL.replace("DOMAIN",domain).plus("config")

        val body = Args.createMap(Pair("app_type", appType))

        RequestLauncher.launch(
            url = url,
            headerParameters = null,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                val configResponse = Json.nonstrict.parse(ConfigResponse.serializer(), response)
                onSuccess(configResponse)
            },
            onFailure = onFailure
        )
    }

    /**
     * Invokes [onSuccess] if the request to initialize the Application with the given [appToken] was successful.
     * Invokes [onFailure] otherwise.
     * Use [eventBuilder] to tell the SDK which Socket events need to be listened to.
     */
    fun init(domain: String,
             appToken: String,
             onSuccess: (InitResponse) -> Unit,
             onFailure: (ErrorResponse) -> Unit,
             eventBuilder: NodeEventBuilder
             ) {

        if (!isDomainInitialized(domain)) {
            ping(
                domain = domain,
                onSuccess = {
                    mDomain = domain
                    init(mDomain, appToken, onSuccess, onFailure, eventBuilder)
                },
                onFailure = onFailure
            )
            return
        }

        val identity = mRepository.getDeviceId()

        if (!Args.checkForContent(domain, appToken)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        mRepository.setAppToken(appToken)

        val url = BASE_URL.replace("DOMAIN",domain).plus("init")

        val body = Args.createMap(
            Pair("app_token", appToken),
            Pair("identity", identity)
        )

        RequestLauncher.launch(
            url = url,
            headerParameters = null,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                val initResponse = Json.nonstrict.parse(InitResponseDTO.serializer(), response)
                exit()
                mRepository.setDomain(mDomain)
                mRepository.setClientToken(initResponse.access_token)
                mRepository.setNode(initResponse.node)
                mRepository.setAppToken(appToken)
                //To know if the requests should be saved in case it's offline
                mRepository.setOfflineMode(initResponse.config.offline == 1)
                println(initResponse.access_token)
                webSocket(eventBuilder)

                onSuccess(InitResponse(initResponse.config))
            },
            onFailure = onFailure
        )
    }

    /**
     * Function that is used by the SDK to initialize an Application with the [appToken] of the given [domain]. This is
     * used when the SDK has Biometric Authentication attempts that were stored offline and needs to send them to the server.
     * Because this requests can have a different domain than the one currently in use, the SDK will initialize the app
     * but won't store any of its tokens.
     * More information about the [onSuccess] and [onFailure] lambdas will be in the AuthRequestManager class.
     */
    internal fun init(domain: String, appToken: String, onSuccess: (String) -> Unit, onFailure: (ErrorResponse) -> Unit) {
        val identity = mRepository.getDeviceId()

        val url = BASE_URL.replace("DOMAIN",domain).plus("init")

        val body = Args.createMap(
            Pair("app_token", appToken),
            Pair("identity", identity)
        )

        println(body)

        RequestLauncher.launch(
            url = url,
            headerParameters = null,
            bodyParameters = body,
            method = HttpMethod.Post,
            networkService = mNetworkService,
            onSuccess = {response ->
                println("onsuccessinit")
                val initResponse = Json.nonstrict.parse(InitResponseDTO.serializer(), response)
                onSuccess(initResponse.access_token)
            },
            onFailure = {error ->
                println("init error internal")
                onFailure(error)
            }
        )
    }

    /**
     * Starts the websocket with the events stored in the [nodeEventBuilder].
     */
    private fun webSocket(nodeEventBuilder: NodeEventBuilder) {
        mWebSocket = WebSocket(nodeEventBuilder, mRepository)
    }

    /**
     * Exits the Websocket and clears relevant data.
     */
    fun exit() {
        if (::mWebSocket.isInitialized) mWebSocket.disconnect()
        mRepository.cleanPreferenceEditor()
    }

    /**
     * Function used by the SDK before the config and init functions to make sure the [domain] is valid.
     * Calls [onSuccess] if the domain received the ping or [onFailure] if the ping wasn't received.
     */
    private fun ping(
        domain: String,
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit) {

        if (!Args.checkForContent(domain)) {
            onFailure(Errors.INVALID_PARAMETER.error)
            return
        }

        val url = BASE_URL.replace("DOMAIN", domain).plus("ping")

        RequestLauncher.launch(
            url = url,
            method = HttpMethod.Get,
            networkService = mNetworkService,
            onSuccess = {
                onSuccess()
            },
            onFailure = {
                onFailure(Errors.INVALID_DOMAIN.error)
            }
        )
    }

    /**
     * Verifies if the [domain] has been initialized.
     * @return True if initialized, false otherwise.
     */
    private fun isDomainInitialized(domain: String): Boolean {
        if (!::mDomain.isInitialized || !isInitialized()) {
            return false
        }else return mDomain == domain
    }

    //MODULES

    /**
     * Function to retrieve the Apps module.
     * @return Apps module if the SDK has been initialized, null otherwise.
     */
    fun apps(): Apps? {
        if (!isDomainInitialized(mDomain)) return null

        val appsUrl = BASE_URL.replace("DOMAIN", mDomain).plus("apps")

        if (!::mApps.isInitialized) {
            mApps = Apps(appsUrl, mNetworkService, mRepository)
        }else {
            mApps.setUrl(appsUrl)
        }

        return mApps
    }

    /**
     * Function to retrieve the Auth module.
     * @return Auth module if the SDK has been initialized, null otherwise.
     */
    fun auth(): Auth? {
        if (!isDomainInitialized(mDomain)) return null

        val authUrl = BASE_URL.replace("DOMAIN", mDomain).plus("auth")

        println(authUrl)

        if (!::mAuth.isInitialized) {
            mAuth = Auth(authUrl, mNetworkService, mRepository)
        }else {
            mAuth.setUrl(authUrl)
        }

        return mAuth
    }

    /**
     * Function to retrieve the DevicePosition module.
     * @return DevicePosition module if the SDK has been initialized, null otherwise.
     */
    fun devicePosition(): DevicePosition? {
        if (!isDomainInitialized(mDomain)) return null

        val devicePositionUrl = BASE_URL.replace("DOMAIN", mDomain).plus("devices/positions")

        if (!::mDevicePosition.isInitialized) {
            mDevicePosition = DevicePosition(devicePositionUrl, mNetworkService, mRepository)
        }else {
            mDevicePosition.setUrl(devicePositionUrl)
        }

        return mDevicePosition
    }

    /**
     * Function to retrieve the Users module.
     * @return Users module if the SDK has been initialized, null otherwise.
     */
    fun users(): Users? {
        if (!isDomainInitialized(mDomain)) return null

        val usersUrl = BASE_URL.replace("DOMAIN", mDomain).plus("users")

        if (!::mUsers.isInitialized) {
            mUsers = Users(usersUrl, mNetworkService, mRepository)
        }else {
            mUsers.setUrl(usersUrl)
        }

        return mUsers
    }

    /**
     * Function to retrieve the Devices module.
     * @return Devices module if the SDK has been initialized, null otherwise.
     */
    fun devices(): Devices? {
        if (!isDomainInitialized(mDomain)) return null

        val devicesUrl = BASE_URL.replace("DOMAIN", mDomain).plus("devices")

        if (!::mDevices.isInitialized) {
            mDevices = Devices(devicesUrl, mNetworkService, mRepository)
        }else {
            mDevices.setUrl(devicesUrl)
        }

        return mDevices
    }

    /**
     * Function to retrieve the DeviceInput module.
     * @return DeviceInput module if the SDK has been initialized, null otherwise.
     */
    fun deviceInput(): DeviceInput? {
        if (!isDomainInitialized(mDomain)) return null

        val deviceInputUrl = BASE_URL.replace("DOMAIN", mDomain).plus("devices/inputs")

        if (!::mDeviceInput.isInitialized) {
            mDeviceInput = DeviceInput(deviceInputUrl, mNetworkService, mRepository)
        }else {
            mDeviceInput.setUrl(deviceInputUrl)
        }
        return mDeviceInput
    }

    /**
     * Function to retrieve the Requests module.
     * @return Requests module if the SDK has been initialized, null otherwise.
     */
    fun requests(): Requests? {
        if (!isDomainInitialized(mDomain)) return null

        val requestsUrl = BASE_URL.replace("DOMAIN", mDomain).plus("requests")

        if (!::mRequests.isInitialized) {
            mRequests = Requests(requestsUrl, mNetworkService, mRepository)
        }else {
            mRequests.setUrl(requestsUrl)
        }

        return mRequests
    }

    /**
     * Function to retrieve the Roles module.
     * @return Roles module if the SDK has been initialized, null otherwise.
     */
    fun roles(): Roles? {
        if (!isDomainInitialized(mDomain)) return null

        val rolesUrl = BASE_URL.replace("DOMAIN", mDomain).plus("roles")

        if (!::mRoles.isInitialized) {
            mRoles = Roles(rolesUrl, mNetworkService, mRepository)
        }else {
            mRoles.setUrl(rolesUrl)
        }

        return mRoles
    }

    /**
     * Function to retrieve the Apis module.
     * @return Apis module if the SDK has been initialized, null otherwise.
     */
    fun apis(): Apis? {
        if (!isDomainInitialized(mDomain)) return null

        val apisUrl = BASE_URL.replace("DOMAIN", mDomain).plus("apis")

        if (!::mApis.isInitialized) {
            mApis = Apis(apisUrl, mNetworkService, mRepository)
        }else {
            mApis.setUrl(apisUrl)
        }

        return mApis
    }

    /**
     * Function to verify if there's Internet connection. Since the SDK uses SocketIO the onConnect and
     * onDisconnect events manage the state of internet connectivity. So if the SDK is not initialized this will always
     * return false.
     * @return True if the SDK is connected to the internet, false otherwise.
     */
    fun isConnected(): Boolean {
        return mRepository.isConnected()
    }


    /**
     * Mostly used internally by the AuthRequestManager class.
     * @return The domain or empty String.
     */
    internal fun getDomain(): String {
        if (isDomainInitialized(mDomain)) {
            return mDomain
        }
        return ""
    }

    /**
     * Contains information tied to the Infinitum class.
     * @property BASE_URL The standard URL of Infinitum instances.
     * @property INSTANCE Singleton of the Infinitum class.
     */
    @ThreadLocal
    companion object {
        internal const val BASE_URL = "https://DOMAIN/api/"
        private var INSTANCE: Infinitum? = null

        /**
         * Function that will instantiate the SDK if it hasn't been initialized yet.
         * Requires an [applicationContext] that will be used by the Android side of the SDK to get the Application Context.
         * On iOS the constructor of this class doesn't require any parameter.
         * @return Infinitum instance.
         */
        fun getInstance(applicationContext: ApplicationContext): Infinitum {
            if (INSTANCE == null) INSTANCE = Infinitum(applicationContext)

            return INSTANCE!!
        }

        /**
         * @return Infinitum instance if the ApplicationContext was already given, null otherwise.
         */
        fun getInstance(): Infinitum? {
            return INSTANCE
        }
    }
}