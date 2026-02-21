package com.yapp.ndgl.data.auth.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.yapp.ndgl.core.util.suspendRunCatching
import com.yapp.ndgl.data.auth.BuildConfig
import com.yapp.ndgl.data.auth.api.AuthApi
import com.yapp.ndgl.data.auth.local.LocalAuthDataSource
import com.yapp.ndgl.data.auth.model.AuthResponse
import com.yapp.ndgl.data.auth.model.CreateUserRequest
import com.yapp.ndgl.data.auth.model.LoginRequest
import com.yapp.ndgl.data.auth.util.DeviceInfoUtil
import com.yapp.ndgl.data.core.model.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val localAuthDataSource: LocalAuthDataSource,
) {
    suspend fun initSession(): Boolean {
        val uuid = localAuthDataSource.getUuid()
        var isFirstUser = false
        val response = if (uuid.isNotEmpty()) {
            suspendRunCatching {
                login(uuid)
            }.getOrElse {
                localAuthDataSource.clearSession()
                isFirstUser = true
                createUser()
            }
        } else {
            isFirstUser = true
            createUser()
        }

        localAuthDataSource.setAccessToken(response.accessToken)
        localAuthDataSource.setUuid(response.uuid)
        return isFirstUser
    }

    private suspend fun createUser(): AuthResponse {
        return api.createUser(
            CreateUserRequest(
                fcmToken = getFCMToken(),
                deviceModel = DeviceInfoUtil.deviceModel,
                deviceOs = DeviceInfoUtil.DEVICE_OS,
                deviceOsVersion = DeviceInfoUtil.deviceOsVersion,
                appVersion = BuildConfig.VERSION_NAME,
            ),
        ).getData()
    }

    private suspend fun login(uuid: String): AuthResponse {
        return api.login(LoginRequest(uuid)).getData()
    }

    private suspend fun getFCMToken(): String = withContext(Dispatchers.IO) {
        try {
            Tasks.await(FirebaseMessaging.getInstance().token)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to get FCM token", e)
        }
    }
}
