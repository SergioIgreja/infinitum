package fyi.utils

/**
 * This file contains all the relevant Classes to the Shared Preferences / User Defaults.
 *
 * Class Keys contains all the Keys used by the Shared Preferences / User Defaults.
 */
enum class Keys {
    INFINITUM_CLIENT_TOKEN,
    INFINITUM_USER_TOKEN,
    INFINITUM_APP_TOKEN,
    INFINITUM_NODE,
    INFINITUM_OFFLINE,
    INFINITUM_CONNECTED,
    INFINITUM_DOMAIN
}

/**
 * Class that will handle the persistence of relevant data from the SDK.
 *
 * Expects implementation on iOS and Android. iOS will use NSUserDefaults while Android will use SharedPreferences.
 * @constructor applicationContext is used to get the Context on the Android side.
 */
expect class PreferenceEditor(applicationContext: ApplicationContext) {

    /**
     * Gets the String value of the [key].
     * @return The value if the key exists, "" otherwise.
     */
    internal fun getString(key: Keys): String

    /**
     * Sets the [string] as a value to the [key].
     */
    internal fun setString(key: Keys, string: String)

    /**
     * Gets the boolean value of the [key].
     * @return The value if the key exists, false otherwise.
     */
    internal fun getBoolean(key: Keys): Boolean

    /**
     * Sets the [boolean] as a value to the [key].
     */
    internal fun setBoolean(key: Keys, boolean: Boolean)

    /**
     * Function that will clear the SharedPreferences of the given [keys].
     */
    internal fun cleanAll(vararg keys: Keys)
}