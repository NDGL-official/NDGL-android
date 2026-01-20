plugins {
    id("ndgl.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.yapp.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
