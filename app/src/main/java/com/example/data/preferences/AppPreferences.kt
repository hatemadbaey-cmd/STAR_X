package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "broadcast_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_USER_ROLE = stringPreferencesKey("user_role")
        val KEY_SESSION_TOKEN = stringPreferencesKey("session_token")
        val KEY_LAST_ACTIVE_TIME = longPreferencesKey("last_active_time")

        // Notification toggles
        val KEY_NOTIF_DEVICE_ADDED = booleanPreferencesKey("notif_device_added")
        val KEY_NOTIF_DEVICE_DELETED = booleanPreferencesKey("notif_device_deleted")
        val KEY_NOTIF_DEVICE_UPDATED = booleanPreferencesKey("notif_device_updated")
        val KEY_NOTIF_STATUS_CHANGED = booleanPreferencesKey("notif_status_changed")
        val KEY_NOTIF_PING_FAILED = booleanPreferencesKey("notif_ping_failed")
        val KEY_NOTIF_USER_CHANGES = booleanPreferencesKey("notif_user_changes")
        val KEY_NOTIF_SOUND = booleanPreferencesKey("notif_sound")
        val KEY_NOTIF_VIBRATE = booleanPreferencesKey("notif_vibrate")

        val KEY_NOTIFY_DEVICE_ADDED = KEY_NOTIF_DEVICE_ADDED
        val KEY_NOTIFY_DEVICE_DELETED = KEY_NOTIF_DEVICE_DELETED
        val KEY_NOTIFY_DEVICE_UPDATED = KEY_NOTIF_DEVICE_UPDATED
        val KEY_NOTIFY_STATUS_CHANGED = KEY_NOTIF_STATUS_CHANGED
        val KEY_NOTIFY_PING_FAILED = KEY_NOTIF_PING_FAILED
        val KEY_NOTIFY_USER_CHANGES = KEY_NOTIF_USER_CHANGES
        val KEY_NOTIFY_SOUND = KEY_NOTIF_SOUND
        val KEY_NOTIFY_VIBRATE = KEY_NOTIF_VIBRATE

        // Theme
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "SYSTEM", "LIGHT", "DARK"
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }
    val currentUsername: Flow<String> = context.dataStore.data.map { it[KEY_USERNAME] ?: "hatem" }
    val currentUserRole: Flow<String> = context.dataStore.data.map { it[KEY_USER_ROLE] ?: "ADMIN" }
    val sessionToken: Flow<String> = context.dataStore.data.map { it[KEY_SESSION_TOKEN] ?: "" }
    val lastActiveTime: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_ACTIVE_TIME] ?: 0L }

    val notifyDeviceAdded: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_DEVICE_ADDED] ?: true }
    val notifyDeviceDeleted: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_DEVICE_DELETED] ?: true }
    val notifyDeviceUpdated: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_DEVICE_UPDATED] ?: true }
    val notifyStatusChanged: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_STATUS_CHANGED] ?: true }
    val notifyPingFailed: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_PING_FAILED] ?: true }
    val notifyUserChanges: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_USER_CHANGES] ?: true }
    val notifySound: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_SOUND] ?: true }
    val notifyVibrate: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_VIBRATE] ?: true }

    val themeMode: Flow<String> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: "SYSTEM" }

    suspend fun saveSession(username: String, role: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USERNAME] = username
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_SESSION_TOKEN] = token
            prefs[KEY_LAST_ACTIVE_TIME] = System.currentTimeMillis()
        }
    }

    suspend fun updateLastActiveTime() {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_ACTIVE_TIME] = System.currentTimeMillis()
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs[KEY_SESSION_TOKEN] = ""
        }
    }

    suspend fun setNotificationToggle(key: Preferences.Key<Boolean>, enabled: Boolean) {
        context.dataStore.edit { it[key] = enabled }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }
}
