package com.yapp.ndgl.data.core.api

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.core.model.auth.AuthResponse
import com.yapp.ndgl.data.core.model.auth.CreateUserRequest
import com.yapp.ndgl.data.core.model.auth.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface NDGLApi {
    // Auth
    @POST("/api/v1/auth/users")
    suspend fun createUser(
        @Body request: CreateUserRequest,
    ): BaseResponse<AuthResponse>

    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): BaseResponse<AuthResponse>
}
