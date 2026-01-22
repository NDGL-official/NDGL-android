plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.home"
}

dependencies {
    implementation(project(":data:travel"))
}
