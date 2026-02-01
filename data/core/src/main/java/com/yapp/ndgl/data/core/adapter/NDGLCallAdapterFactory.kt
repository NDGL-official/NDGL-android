package com.yapp.ndgl.data.core.adapter

import com.yapp.ndgl.data.core.model.BaseResponse
import com.yapp.ndgl.data.core.model.error.ErrorResponse
import com.yapp.ndgl.data.core.model.error.HttpResponseException
import kotlinx.serialization.json.Json
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NDGLCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {
    override fun get(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(type) != Call::class.java) return null

        val wrapperType = getParameterUpperBound(0, type as ParameterizedType)
        if (getRawType(wrapperType) != BaseResponse::class.java) return null

        return NDGLCallAdapter(wrapperType)
    }
}

private class NDGLCallAdapter(
    private val resultType: Type,
) : CallAdapter<Any, Call<Any>> {
    override fun responseType(): Type = resultType
    override fun adapt(call: Call<Any>): Call<Any> = NDGLCall(call)
}

private class NDGLCall<T : Any>(
    private val delegate: Call<T>,
) : Call<T> {
    private val json = Json { ignoreUnknownKeys = true }

    override fun enqueue(callback: Callback<T>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        callback.onResponse(this@NDGLCall, response)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: ""

                        // errorBody를 ErrorResponse로 디코딩
                        val errorResponse = try {
                            json.decodeFromString<ErrorResponse>(errorBody)
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to parse error response")
                            null
                        }

                        val exception = HttpResponseException(
                            code = errorResponse?.code ?: response.code().toString(),
                            errorMessage = errorResponse?.message ?: errorBody,
                            fieldErrors = errorResponse?.errors,
                        )

                        callback.onFailure(this@NDGLCall, exception)
                    }
                }

                override fun onFailure(call: Call<T>, throwable: Throwable) {
                    callback.onFailure(this@NDGLCall, throwable)
                }
            },
        )
    }

    override fun clone(): Call<T> = NDGLCall(delegate.clone())
    override fun execute(): Response<T> =
        throw NotImplementedError("NDGLCall doesn't support execute()")

    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}
