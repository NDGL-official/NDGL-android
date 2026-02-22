package com.yapp.ndgl.feature.home.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppVersionProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun getAppVersion(): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        }.getOrDefault("")
    }
}
