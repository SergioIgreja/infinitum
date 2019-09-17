package fyi.repository.auth_requests

import fyi.Infinitum
import fyi.modules.auth.models.BiometricAuthOptionalParameters
import fyi.repository.Repository
import fyi.utils.Utils
import infinitum.Auth_request

/**
 * Singleton class responsible to manage the process of sending stored biometric authentication requests.
 */
internal object AuthRequestManager {

    /**
     * Function called by the WebSocket class every time the SDK is connected.
     * Verifies if there are any Auth_Requests stored and sends them.
     * Receives the [mRepository] to have access to the AuthRequestDao.
     */
    fun sendStoredAuthenticationRequests(mRepository: Repository) {
        val storedRequests = mRepository.getAuthRequestDao().getAllAuthRequests()
        val map = mutableMapOf<Pair<String, String>, MutableList<Auth_request>>()

        if (storedRequests.isNotEmpty()) {
            storedRequests.forEach {
                val key = Pair(it.domain, it.apptoken)
                if (map.containsKey(key)) map[key]?.add(it)
                else map[key] = mutableListOf(it)
            }

            val currentDomainApp = Pair(Infinitum.getInstance()!!.getDomain(), mRepository.getAppToken())

            if (map.containsKey(currentDomainApp)) {
                //Not necessary to send init
                println(currentDomainApp)
                sendRequestsTo(currentDomainApp.first, mRepository.getAppToken(), mRepository.getAccessToken(), map[currentDomainApp]!!, mRepository)
                map.remove(currentDomainApp)
            }

            map.forEach {
                sendInitRequest(it.key.first, it.key.second, it.value, mRepository)
            }
        }
    }

    /**
     * If there are any stored [requests] where the domain is different from the one being used by the SDK then it
     * becomes necessary to send the init request to that [domain] with the associated [appToken] in order
     * to get the necessary tokens to send the Biometric Authentication request.
     * The [mRepository] will then be used by the function sendRequestsTo in order to delete the stored request.
     */
    fun sendInitRequest(domain: String, appToken: String, requests: MutableList<Auth_request>, mRepository: Repository) {
        Infinitum.getInstance()?.init(
            domain = domain,
            appToken = appToken,
            onSuccess = { accessToken ->
                sendRequestsTo(domain, appToken, accessToken, requests, mRepository)
            },
            onFailure = { println("SEND INIT REQUEST $it")}
        )
    }

    /**
     * Once we have the appropriate [accessToken] to the [domain] and [appToken] we can now send the related [requests].
     * Uses the [mRepository] to then delete each Auth_Request. To note that this function will always delete the Auth_Request,
     * even if the Biometric Authentication fails.
     */
    fun sendRequestsTo(domain: String, appToken: String, accessToken: String, requests: MutableList<Auth_request>, mRepository: Repository) {
        val baseUrl = Infinitum.BASE_URL.replace("DOMAIN", domain)
        requests.forEach {
            Infinitum.getInstance()!!.auth()?.biometricAuthentication(
                baseUrl,
                it,
                accessToken,
                appToken,
                { println("saved request sent") },
                { println(it) })
            mRepository.getAuthRequestDao().deleteRequest(it.id)
        }

    }

    /**
     * Function that stores a new Auth_Request with the [image] and [optionalParameters] sent by the user in biometricAuthentication.
     * [mRepository] used to get the AuthRequestDao to store a new Auth_Request.
     */
    fun storeNewAuthenticationRequest(image: String, optionalParameters: BiometricAuthOptionalParameters.Builder, mRepository: Repository) {
        mRepository.getAuthRequestDao().addRequest(
            date = Utils.getDate(),
            image = image,
            domain = Infinitum.getInstance()!!.getDomain(),
            appToken = mRepository.getAppToken(),
            optionalParameters = optionalParameters
        )
    }
}