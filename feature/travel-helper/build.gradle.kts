plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.helper"
}

dependencies {
    implementation(project(":data:travel"))
}
