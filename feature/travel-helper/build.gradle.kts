plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.travel.helper"
}

dependencies {
    implementation(project(":data:travel"))
}
