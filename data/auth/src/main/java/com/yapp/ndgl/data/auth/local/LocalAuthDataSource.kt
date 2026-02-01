package com.yapp.ndgl.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yapp.ndgl.data.auth.local.util.handleException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAuthDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val accessToken: Flow<String> = dataStore.data
        .handleException()
        .map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }

    private val uuid: Flow<String> = dataStore.data
        .handleException()
        .map { preferences ->
            preferences[UUID_KEY] ?: ""
        }

    suspend fun getAccessToken(): String = accessToken.first()

    suspend fun getUuid(): String = uuid.first()

    suspend fun setAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun setUuid(uuid: String) {
        dataStore.edit { preferences ->
            preferences[UUID_KEY] = uuid
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(UUID_KEY)
        }
    }

    private companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val UUID_KEY = stringPreferencesKey("uuid")
    }
}
