plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.ndgl.data.auth"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${Configuration.VERSION_NAME}\"")
    }
}

dependencies {
    implementation(project(":data:core"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.datastore)
}
