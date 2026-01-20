plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.data.auth"
}

dependencies {
    implementation(project(":data:core"))
}
