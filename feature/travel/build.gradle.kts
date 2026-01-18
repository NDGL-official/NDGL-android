plugins {
    id("ndgl.feature")
}

android {
    namespace = "com.yapp.travel"
}

dependencies {
    implementation(project(":data:travel"))
}
