package io.github.sheasmith.discountdetective.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

/**
 * Code from StackOverflow for live data - e.g. the settings screen region data.
 * https://stackoverflow.com/questions/50649014/livedata-with-shared-preferences/53028546#53028546
 *
 * @param sharedPrefs Shared preferences (from the settings screen).
 * @param key  The key (name/identifier) for the live data type.
 * @param defValue The default value for the live data.
 */
abstract class SharedPreferenceLiveData<T>(
    val sharedPrefs: SharedPreferences,
    val key: String,
    val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    /**
     * Retrieves the current value of the preference for which the key refers to.
     *
     * @return The value, T.
     */
    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

/**
 * Shared preferences class for when the preference values are of type int.
 *
 * @return Shared preference live data with values of type int.
 */
class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int =
        sharedPrefs.getInt(key, defValue)
}

/**
 * Shared preferences class for when the preference values are of type String.
 *
 * @return Shared preference live data with values of type String.
 */
class SharedPreferenceStringLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: String
) :
    SharedPreferenceLiveData<String>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String): String =
        sharedPrefs.getString(key, defValue)!!
}

/**
 * Shared preferences class for when the preference values are of type Boolean.
 *
 * @return Shared preference live data with values of type Boolean.
 */
class SharedPreferenceBooleanLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Boolean
) :
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defValue)
}

/**
 * Shared preferences class for when the preference values are of type Float.
 *
 * @return Shared preference live data with values of type Float.
 */
class SharedPreferenceFloatLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Float) :
    SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Float): Float =
        sharedPrefs.getFloat(key, defValue)
}

/**
 * Shared preferences class for when the preference values are of type Long.
 *
 * @return Shared preference live data with values of type Long.
 */
class SharedPreferenceLongLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Long) :
    SharedPreferenceLiveData<Long>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Long): Long =
        sharedPrefs.getLong(key, defValue)
}

/**
 * Shared preferences class for when the preference values that are a set of type String.
 *
 * @return Shared preference live data with values that are a set of type String.
 */
class SharedPreferenceStringSetLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Set<String>
) :
    SharedPreferenceLiveData<Set<String>>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>): Set<String> =
        sharedPrefs.getStringSet(key, defValue)!!
}

/**
 * Function which returns live data for a value of type int.
 *
 * @return Shared preference live data for a value of type int.
 */
fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> {
    return SharedPreferenceIntLiveData(this, key, defValue)
}

/**
 * Function which returns live data for a value of type String.
 *
 * @return Shared preference live data for a value of type String.
 */
fun SharedPreferences.stringLiveData(
    key: String,
    defValue: String
): SharedPreferenceLiveData<String> {
    return SharedPreferenceStringLiveData(this, key, defValue)
}

/**
 * Function which returns live data for a value of type Boolean.
 *
 * @return Shared preference live data for a value of type Boolean.
 */
fun SharedPreferences.booleanLiveData(
    key: String,
    defValue: Boolean
): SharedPreferenceLiveData<Boolean> {
    return SharedPreferenceBooleanLiveData(this, key, defValue)
}

/**
 * Function which returns live data for a value of type Float.
 *
 * @return Shared preference live data for a value of type Float.
 */
fun SharedPreferences.floatLiveData(key: String, defValue: Float): SharedPreferenceLiveData<Float> {
    return SharedPreferenceFloatLiveData(this, key, defValue)
}

/**
 * Function which returns live data for a value of type Long.
 *
 * @return Shared preference live data for a value of type Long.
 */
fun SharedPreferences.longLiveData(key: String, defValue: Long): SharedPreferenceLiveData<Long> {
    return SharedPreferenceLongLiveData(this, key, defValue)
}

/**
 * Function which returns live data for a value that is a set of type String.
 *
 * @return Shared preference live data for a value that is a set of type String.
 */
fun SharedPreferences.stringSetLiveData(
    key: String,
    defValue: Set<String>
): SharedPreferenceLiveData<Set<String>> {
    return SharedPreferenceStringSetLiveData(this, key, defValue)
}