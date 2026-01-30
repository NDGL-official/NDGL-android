plugins {
    id("ndgl.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yapp.ndgl.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
