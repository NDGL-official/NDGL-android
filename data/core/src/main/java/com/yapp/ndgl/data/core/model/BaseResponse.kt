package com.yapp.ndgl.data.core.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val code: String,
    val message: String,
    val data: T?,
)

fun <T> BaseResponse<T>.getData(): T {
    return data ?: error("Response data is null. code=$code, message=$message")
}
