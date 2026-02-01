plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.ndgl.data.auth"
}

dependencies {
    implementation(libs.androidx.datastore)
}
