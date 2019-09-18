package fyi.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Class that will handle the persistence of relevant data from the SDK using SharedPreferences.
 *
 * @constructor applicationContext is not relevant on the iOS side.
 * @property SHARED_PREFERENCES_NAME name of the SharedPreferences.
 * @property mSharedPreferences SharedPreferences instance.
 * @property mApplicationContext is not relevant on the iOS side.
 * @property mEditor Editor of the SharedPreferences.
 */
actual class PreferenceEditor actual constructor(applicationContext: ApplicationContext) {

    private val SHARED_PREFERENCES_NAME = "InfinitumPreferences"

    private val mApplicationContext: ApplicationContext
    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    init {
        mApplicationContext = applicationContext
        mSharedPreferences = mApplicationContext.context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
    }

    actual fun getString(key: Keys): String {
        return mSharedPreferences.getString(key.name, "")
    }

    actual fun setString(key: Keys, string: String) {
        mEditor.putString(key.name, string)
        mEditor.apply()
    }

    actual fun getBoolean(key: Keys): Boolean {
        return mSharedPreferences.getBoolean(key.name, false)
    }

    actual fun setBoolean(key: Keys, boolean: Boolean) {
        mEditor.putBoolean(key.name, boolean)
        mEditor.apply()
    }

    internal actual fun cleanAll(vararg keys: Keys) {
        keys.forEach {
            mEditor.remove(it.name)
        }
        mEditor.apply()
    }
}