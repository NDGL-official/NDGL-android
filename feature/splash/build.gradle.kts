plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.ndgl.feature.splash"
}

dependencies {
    implementation(projects.data.auth)
}
