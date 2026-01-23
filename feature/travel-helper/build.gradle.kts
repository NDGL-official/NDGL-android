plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.ndgl.feature.travelhelper"
}

dependencies {
    implementation(project(":data:travel"))
}
