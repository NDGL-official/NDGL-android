import java.util.Properties
import kotlin.apply

plugins {
    id("ndgl.data")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yapp.ndgl.data.core"

    defaultConfig {
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField(
            "String",
            "NDGL_BASE_URL",
            localProperties["NDGL_BASE_URL"] as String,
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}
