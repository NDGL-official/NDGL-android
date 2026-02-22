package com.yapp.ndgl.data.core.interceptor

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest
import javax.inject.Inject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AndroidCredentialInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
) : Interceptor {
    private val packageName: String = context.packageName
    private val sha1Cert: String = computeSha1(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("X-Android-Package", packageName)
            .addHeader("X-Android-Cert", sha1Cert)
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        private fun computeSha1(context: Context): String = try {
            val signatures = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                .signingInfo?.apkContentsSigners
            MessageDigest.getInstance("SHA-1")
                .digest(signatures?.getOrNull(0)?.toByteArray())
                .joinToString("") { "%02x".format(it) }
        } catch (e: Exception) { "" }
    }
}
