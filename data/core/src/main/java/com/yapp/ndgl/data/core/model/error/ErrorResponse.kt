package com.yapp.ndgl.data.core.model.error

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val errors: List<FieldError>? = null,
)

@Serializable
data class FieldError(
    val field: String,
    val message: String,
)
