plugins {
    id("ndgl.android.library")
}

android {
    namespace = "com.yapp.ndgl.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.util)
}
