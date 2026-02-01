import java.util.Properties
import kotlin.apply

plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.ndgl.data.core"

    defaultConfig {
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }

        buildConfigField("String", "NDGL_BASE_URL", "\"${localProperties["NDGL_BASE_URL"]}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.okhttp.logging.interceptor)
}
