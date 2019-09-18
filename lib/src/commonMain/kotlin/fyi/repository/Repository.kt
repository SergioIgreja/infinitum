package fyi.repository

import com.squareup.sqldelight.db.SqlDriver
import fyi.Infinitum
import fyi.repository.auth_requests.AuthRequestDao
import fyi.repository.auth_requests.AuthRequestDaoImpl
import fyi.utils.ApplicationContext
import fyi.utils.Keys
import fyi.utils.PreferenceEditor
import pt.fyi.db.MyDatabase

/**
 * Responsible for the storage of relevant information.
 *
 * @property ApplicationContext Used to get the Context from the Android application.
 * @property mPreferenceEditor Instance of the SharedPreferences/NSUserDefaults handler.
 * @property mSqlDriver SQLDelight driver.
 * @property mDatabase Infinitum's Database.
 * @property mAuthRequestDao AuthRequestDao that will handle all the requests regarding Auth_Requests.
 */
data class Repository(private val applicationContext: ApplicationContext) {
    private val mPreferenceEditor: PreferenceEditor
    private val mSqlDriver: SqlDriver
    private val mDatabase: MyDatabase
    private lateinit var mAuthRequestDao: AuthRequestDao


    init {
        mPreferenceEditor = PreferenceEditor(applicationContext)
        mSqlDriver = getSqlDriver(applicationContext, MyDatabase.Schema, "InfinitumDB.db")
        mDatabase = MyDatabase(mSqlDriver)
    }

    /**
     * @return [AuthRequestDao]
     */
    internal fun getAuthRequestDao(): AuthRequestDao {
        if (!::mAuthRequestDao.isInitialized) mAuthRequestDao = AuthRequestDaoImpl(mDatabase.authRequestsQueries)
        return mAuthRequestDao
    }

    //SYSTEM METHODS

    /**
     * @return The device id.
     */
    internal fun getDeviceId(): String {
        return applicationContext.getDeviceId()
    }

    //ALL PREFERENCE EDITOR METHODS

    /**
     * @return An access token that will be used in most of the requests. Prioritizes a user token over a client token.
     */
    internal fun getAccessToken(): String {
        val token = mPreferenceEditor.getString(Keys.INFINITUM_USER_TOKEN)

        return if (token.isNullOrBlank()) {
            mPreferenceEditor.getString(Keys.INFINITUM_CLIENT_TOKEN)
        } else token
    }

    /**
     * Stores the [userToken] in the PreferenceEditor.
     */
    internal fun setUserToken(userToken: String) {
        mPreferenceEditor.setString(Keys.INFINITUM_USER_TOKEN, userToken)
    }

    /**
     * Stores the [appToken] in the PreferenceEditor.
     */
    internal fun setAppToken(appToken: String) {
        mPreferenceEditor.setString(Keys.INFINITUM_APP_TOKEN, appToken)
    }

    /**
     * Stores the [node] url in the PreferenceEditor.
     */
    internal fun setNode(node: String) {
        mPreferenceEditor.setString(Keys.INFINITUM_NODE, node)
    }

    /**
     * Stores the [clientToken] in the PreferenceEditor.
     */
    internal fun setClientToken(clientToken: String) {
        mPreferenceEditor.setString(Keys.INFINITUM_CLIENT_TOKEN, clientToken)
    }

    /**
     * @return AppToken if it's stored, "" otherwise.
     */
    internal fun getAppToken(): String {
        return mPreferenceEditor.getString(Keys.INFINITUM_APP_TOKEN)
    }

    /**
     * @return Node Url if it's stored, "" otherwise.
     */
    internal fun getNode(): String {
        return mPreferenceEditor.getString(Keys.INFINITUM_NODE)
    }

    /**
     * @return Domain if it's stored, "" otherwise.
     */
    internal fun getDomain(): String {
        return mPreferenceEditor.getString(Keys.INFINITUM_DOMAIN)
    }

    /**
     * Used to decide if the Biometric Authentication request should be saved if the SDK is offline.
     *
     * Verifies if the initialized application has offline mode enabled.
     * @return True if it's enabled, false otherwise.
     */
    internal fun isOfflineModeEnabled(): Boolean {
        return mPreferenceEditor.getBoolean(Keys.INFINITUM_OFFLINE)
    }

    /**
     * Stores the [offline] mode of the Initialized application.
     */
    internal fun setOfflineMode(offline: Boolean) {
        mPreferenceEditor.setBoolean(Keys.INFINITUM_OFFLINE, offline)
    }

    //Connection state of the application
    /**
     * Used to verify if the SDK is connected to the Internet. Note that this is managed by the WebSocket, so the mobile
     * application can be connected to the internet but the SDK can be offline if not connected to the Node Server.
     *
     * @return True if the SDK is connected, false otherwise.
     */
    internal fun isConnected(): Boolean {
        return mPreferenceEditor.getBoolean(Keys.INFINITUM_CONNECTED)
    }

    /**
     * Stores the [connected] stated of the SDK.
     */
    internal fun setConnected(connected: Boolean) {
        mPreferenceEditor.setBoolean(Keys.INFINITUM_CONNECTED, connected)
    }

    /**
     * Stores the [domain] in the Preference Editor.
     */
    internal fun setDomain(domain: String) {
        mPreferenceEditor.setString(Keys.INFINITUM_DOMAIN, domain)
    }

    /**
     * Clears the PreferenceEditor variable used by the SDK.
     */
    internal fun cleanPreferenceEditor() {
        mPreferenceEditor.cleanAll(Keys.INFINITUM_NODE, Keys.INFINITUM_APP_TOKEN, Keys.INFINITUM_OFFLINE, Keys.INFINITUM_CONNECTED, Keys.INFINITUM_USER_TOKEN, Keys.INFINITUM_CLIENT_TOKEN, Keys.INFINITUM_DOMAIN)
    }

}

/**
 * Function to get the Android and iOS SqlDrivers.
 */
expect fun getSqlDriver(applicationContext: ApplicationContext, schema: SqlDriver.Schema, dbName: String): SqlDriver