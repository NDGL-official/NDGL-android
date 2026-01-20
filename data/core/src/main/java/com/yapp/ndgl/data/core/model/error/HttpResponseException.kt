package com.yapp.ndgl.data.core.model.error

class HttpResponseException(
    val code: String,
    val errorMessage: String,
    val fieldErrors: List<FieldError>? = null,
) : Exception(errorMessage)
