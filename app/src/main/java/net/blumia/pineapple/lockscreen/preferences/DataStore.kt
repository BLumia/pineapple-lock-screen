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

val P_PROMINENT_DISCLOSURE_ACCEPTED = booleanPreferencesKey("prominent_disclosure_accepted")
val P_DEPRECATED_SHORTCUT_METHOD = booleanPreferencesKey("deprecated_shortcut_method")

fun Context.stringPreference(key: Preferences.Key<String>) : Flow<String> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: ""
        }
}

fun Context.booleanPreference(key: Preferences.Key<Boolean>, defaultValue: Boolean) : Flow<Boolean> {
    return dataStore.data
        .map { preferences ->
            preferences[key] ?: defaultValue
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