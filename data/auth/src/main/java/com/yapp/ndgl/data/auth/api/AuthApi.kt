package com.yapp.ndgl.data.auth.api

import com.yapp.ndgl.data.auth.model.AuthResponse
import com.yapp.ndgl.data.auth.model.CreateUserRequest
import com.yapp.ndgl.data.auth.model.LoginRequest
import com.yapp.ndgl.data.core.model.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/auth/users")
    suspend fun createUser(
        @Body request: CreateUserRequest,
    ): BaseResponse<AuthResponse>

    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): BaseResponse<AuthResponse>
}
