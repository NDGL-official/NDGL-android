package com.yapp.ndgl.data.auth.util

import android.os.Build

object DeviceInfoUtil {
    const val DEVICE_OS: String = "Android"
    val deviceModel: String = Build.MODEL
    val deviceOsVersion: String = Build.VERSION.RELEASE
}
