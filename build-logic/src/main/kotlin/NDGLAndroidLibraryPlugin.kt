import convention.configureComposeAndroid
import convention.configureFirebase
import convention.configureKotlinAndroid
import convention.configureTimber
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import util.libs

class NDGLAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }

        configureKotlinAndroid()
        configureFirebase()
        configureComposeAndroid()
        configureTimber()

        dependencies {
            "implementation"(libs.findLibrary("kotlinx-immutable").get())
        }
    }
}
