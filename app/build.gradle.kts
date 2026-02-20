import java.util.Properties

plugins {
    id("ndgl.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = Configuration.APPLICATION_ID

    val localProperties = Properties().apply {
        load(rootProject.file("local.properties").bufferedReader())
    }

    defaultConfig {
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY", "")
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("KEYSTORE_PATH"))
            storePassword = localProperties.getProperty("KEYSTORE_STORE_PASSWORD")
            keyAlias = localProperties.getProperty("KEYSTORE_ALIAS")
            keyPassword = localProperties.getProperty("KEYSTORE_KEY_PASSWORD")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "NDGL_BASE_URL", "\"${localProperties.getProperty("NDGL_BASE_URL_DEBUG")}\"")
            buildConfigField("String", "NDGL_API_KEY", "\"${localProperties.getProperty("NDGL_API_KEY", "")}\"")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "NDGL_BASE_URL", "\"${localProperties.getProperty("NDGL_BASE_URL_RELEASE")}\"")
            buildConfigField("String", "NDGL_API_KEY", "\"${localProperties.getProperty("NDGL_API_KEY", "")}\"")
        }
    }
}

dependencies {
    implementation(project(":navigation"))

    implementation(project(":feature:home"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:travel"))
    implementation(project(":feature:travel-helper"))

    implementation(project(":data:core"))

    implementation(project(":core:ui"))

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
