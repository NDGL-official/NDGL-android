
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class NDGLKotlinLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.jvm")
        }
        configureKotlinJvm()
    }
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = Configuration.JAVA_VERSION
        targetCompatibility = Configuration.JAVA_VERSION
    }

    extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(Configuration.JVM_TARGET))
        }
    }
}
