package fyi.repository

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.utils.Dispatcher
import io.ktor.http.HttpMethod
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Singleton class that handles the creation of coroutines to make the requests asynchronous.
 */
object RequestLauncher {

    /**
     * Launches a request of type [method] to the [url] with the [headerParameters] and [bodyParameters].
     * [networkService] injected to facilitate testing.
     * Executes [onSuccess] if the request is successful, [onFailure] otherwise.
     */
     fun launch(
        url: String,
        headerParameters: MutableMap<String, String>? = null,
        bodyParameters: MutableMap<String, String>? = null,
        networkService: NetworkService,
        method: HttpMethod,
        onSuccess: (String) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {

        GlobalScope.launch(Dispatcher.ApplicationDispatcher) {
            val response = networkService.request(url,headerParameters,bodyParameters,method)

            when (response) {
                is String -> onSuccess(response)

                is ErrorResponse -> {
                    println(response)
                    onFailure(response)
                }

                else -> onFailure(Errors.UNKNOWN_EXCEPTION.error)
            }
        }

    }
}