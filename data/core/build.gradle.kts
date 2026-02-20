plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.ndgl.data.core"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.okhttp.logging.interceptor)
}
