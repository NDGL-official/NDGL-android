import convention.configureCoroutineAndroid
import convention.configureHiltAndroid
import convention.configureKotlinAndroid
import convention.configureTimber
import org.gradle.api.Plugin
import org.gradle.api.Project

class NDGLDataPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply("com.android.library")
        }

        configureKotlinAndroid()
        configureHiltAndroid()
        configureCoroutineAndroid()
        configureTimber()

        if (path != ":data:core") {
            dependencies.add("implementation", project(":data:core"))
        }
    }
}
