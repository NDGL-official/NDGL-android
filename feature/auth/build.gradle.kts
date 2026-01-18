plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.auth"
}

dependencies {
    implementation(project(":data:auth"))
}
