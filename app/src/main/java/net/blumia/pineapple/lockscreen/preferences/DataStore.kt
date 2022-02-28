package net.blumia.pineapple.lockscreen.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val PROMINENT_DISCLOSURE_ACCEPTED = booleanPreferencesKey("prominent_disclosure_accepted")
    val DEPRECATED_SHORTCUT_METHOD = booleanPreferencesKey("deprecated_shortcut_method")
}

fun Context.stringPreference(key: Preferences.Key<String>) : Flow<String> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: ""
        }
}

private fun booleanDefaultValue(key: Preferences.Key<Boolean>): Boolean {
    return when (key) {
        PreferencesKeys.PROMINENT_DISCLOSURE_ACCEPTED -> false
        PreferencesKeys.DEPRECATED_SHORTCUT_METHOD -> false
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