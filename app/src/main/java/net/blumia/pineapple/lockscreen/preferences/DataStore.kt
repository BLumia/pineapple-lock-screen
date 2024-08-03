package net.blumia.pineapple.lockscreen.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.blumia.pineapple.lockscreen.BuildConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val PROMINENT_DISCLOSURE_ACCEPTED = booleanPreferencesKey("prominent_disclosure_accepted")
    val DEPRECATED_SHORTCUT_METHOD = booleanPreferencesKey("deprecated_shortcut_method")
    val USE_LAUNCHER_ICON_TO_LOCK = booleanPreferencesKey("use_launcher_icon_to_lock")
    val EXCLUDE_FROM_RECENTS = booleanPreferencesKey("exclude_from_recents")
}

fun Context.stringPreference(key: Preferences.Key<String>) : Flow<String> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: ""
        }
}

private fun booleanDefaultValue(key: Preferences.Key<Boolean>): Boolean {
    return when (key) {
        PreferencesKeys.PROMINENT_DISCLOSURE_ACCEPTED -> !BuildConfig.PROMINENT_DISCLOSURE_REQUIRED
        PreferencesKeys.DEPRECATED_SHORTCUT_METHOD -> BuildConfig.USE_DEPRECATED_SHORTCUT_METHOD
        PreferencesKeys.USE_LAUNCHER_ICON_TO_LOCK -> false
        PreferencesKeys.EXCLUDE_FROM_RECENTS -> false
        else -> false
    }
}

fun Context.booleanPreference(key: Preferences.Key<Boolean>) : Flow<Boolean> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: booleanDefaultValue(key)
        }
}

suspend fun Context.setStringPreference(key: Preferences.Key<String>, value: String) {
    dataStore.edit { settings ->
        settings[key] = value
    }
}

suspend fun Context.setBooleanPreference(key: Preferences.Key<Boolean>, value: Boolean) {
    dataStore.edit { settings ->
        settings[key] = value
    }
}