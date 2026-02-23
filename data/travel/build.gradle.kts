import java.util.Properties

plugins {
    id("ndgl.data")
}

android {
    namespace = "com.yapp.ndgl.data.travel"

    defaultConfig {
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").bufferedReader())
        }
        buildConfigField("String", "PLACE_API_KEY", "\"${localProperties.getProperty("PLACE_API_KEY", "")}\"")
        buildConfigField("String", "ROUTE_API_KEY", "\"${localProperties.getProperty("ROUTE_API_KEY", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":data:core"))
    implementation(libs.places)
    implementation(libs.kotlinx.coroutines.play.services)
}
