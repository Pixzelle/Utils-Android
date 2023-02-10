package mx.pixzelle.utils.common

import android.content.Context
import android.content.SharedPreferences
import mx.pixzelle.utils.extensions.safeContext
import mx.pixzelle.utils.lazyFast
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate for binding Shared Preferences.
 * You can use it like this:  var userId: String by bindSharedPreference(LasecApp.appContext, "userId", "")
 */
private class SharedPreferenceDelegate<T>(
        private val context: Context,
        private val defaultValue: T,
        private val getter: SharedPreferences.(String, T) -> T,
        private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
        private val key: String
) : ReadWriteProperty<Any, T> {

    private val safeContext: Context by lazyFast { context.safeContext() }

    private val sharedPreferences: SharedPreferences by lazyFast {
        safeContext.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) =
            sharedPreferences
                    .getter(key, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
            sharedPreferences
                    .edit()
                    .setter(key, value)
                    .apply()
}

/**
 * Method for binding directly a variable to a Shared Preference value, has to be used with delegate.
 * @param context Context of the application
 * @param key Key of the value store
 * @param defaultValue Value to store
 * @return Value stored in Shared
 */
@Suppress("UNCHECKED_CAST")
fun <T> bindSharedPreference(context: Context, key: String, defaultValue: T): ReadWriteProperty<Any, T> =
        when (defaultValue) {
            is Boolean ->
                SharedPreferenceDelegate(context, defaultValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean, key)
            is Int ->
                SharedPreferenceDelegate(context, defaultValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt, key)
            is Long ->
                SharedPreferenceDelegate(context, defaultValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong, key)
            is Float ->
                SharedPreferenceDelegate(context, defaultValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat, key)
            is String ->
                SharedPreferenceDelegate(context, defaultValue, SharedPreferences::getString, SharedPreferences.Editor::putString, key)

            else -> throw IllegalArgumentException()
        } as ReadWriteProperty<Any, T>