plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.data.travel"
}

dependencies {
    implementation(project(":data:core"))
}
