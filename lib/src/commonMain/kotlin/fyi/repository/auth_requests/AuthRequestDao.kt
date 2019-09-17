package fyi.repository.auth_requests

import fyi.modules.auth.models.BiometricAuthOptionalParameters
import infinitum.AuthRequestsQueries
import infinitum.Auth_request

/**
 * Interface that contains all functions that need to be implemented by the DAO.
 */
internal interface AuthRequestDao {
    fun getAllAuthRequests(): List<Auth_request>

    fun addRequest(date: String, image: String, domain: String, appToken: String, optionalParameters: BiometricAuthOptionalParameters.Builder)

    fun deleteRequest(id: Long)
}

/**
 * DAO that will handle the database requests to retrieve Auth_Requests.
 *
 * Implements the AuthRequestDao interface.
 * @property query AuthRequestsQueries injected from Repository.
 */
internal class AuthRequestDaoImpl(val query: AuthRequestsQueries): AuthRequestDao {
    /**
     * Deletes the Auth_Request related to the given [id]
     */
    override fun deleteRequest(id: Long) {
        query.deleteById(id)
    }

    /**
     * Inserts a new Auth_Request with the given information to the Database.
     */
    override fun addRequest(date: String, image: String, domain: String, appToken: String,  optionalParameters: BiometricAuthOptionalParameters.Builder) {
        query.insert(date, image, domain, appToken, optionalParameters.position, optionalParameters.proximity,
            optionalParameters.action, optionalParameters.data)
    }

    /**
     * @return A list containing all the stored Auth_Requests.
     */
    override fun getAllAuthRequests(): List<Auth_request>{
        return query.selectAll().executeAsList()
    }
}