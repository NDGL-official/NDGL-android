import convention.configureCoroutineAndroid
import convention.configureHiltAndroid
import convention.configureKotlinAndroid
import convention.configureTimber
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import util.libraryExtension
import util.libs

class NDGLDataPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        configureKotlinAndroid()
        configureHiltAndroid()
        configureCoroutineAndroid()
        configureTimber()

        libraryExtension.defaultConfig {
            consumerProguardFiles("consumer-rules.pro")
        }

        if (path != ":data:core") {
            dependencies.add("implementation", project(":data:core"))
        }

        dependencies {
            "implementation"(project(":core:util"))
            "implementation"(libs.findLibrary("retrofit").get())
            "implementation"(libs.findLibrary("retrofit-kotlinx-serialization-json").get())
            "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            "implementation"(libs.findLibrary("okhttp").get())
        }
    }
}
