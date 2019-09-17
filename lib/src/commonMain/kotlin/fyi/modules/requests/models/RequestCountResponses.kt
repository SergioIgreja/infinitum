package fyi.modules.requests.models

import kotlinx.serialization.Serializable

/**
 * Class that represents a Request Count by api.
 *
 * @property api Api name.
 * @property count Total number of requests made to that [api]
 */
@Serializable
data class RequestCountApi(
    val api: String,
    val count: Int
)

/**
 * Class that represents a Request Count by module.
 *
 * @property module Module name.
 * @property count Total number of requests made to that [module]
 */
@Serializable
data class RequestCountModule(
    val module: String,
    val count: Int
)

/**
 * Class that represents a Request Count by code.
 *
 * @property code Request code.
 * @property count Total number of requests where the response code was [code]
 */
@Serializable
data class RequestCountCode(
    val code: Int,
    val count: Int
)

/**
 * Class that represents a Request Count by Success or Failure
 *
 * @property success Total number of successful requests.
 * @property failure Total number of failed requests.
 */
@Serializable
data class RequestCountErrors(
    val success: Int,
    val failure: Int
)
