import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
}

subprojects {
    if (name != "baselineprofile") {
        apply {
            plugin(rootProject.libs.plugins.ktlint.get().pluginId)
            plugin(rootProject.libs.plugins.detekt.get().pluginId)
        }

        configure<KtlintExtension> {
            version.set(rootProject.libs.versions.ktlint.source.get())
            android.set(true)
            verbose.set(true)
        }

        configure<DetektExtension> {
            parallel = true
            buildUponDefaultConfig = true
            toolVersion = rootProject.libs.versions.detekt.get()
            config.setFrom(files("$rootDir/detekt-config.yml"))
        }
    }

    afterEvaluate {
        // Use Kotlin BOM with enforcedPlatform to strictly enforce Kotlin stdlib versions
        configurations.findByName("implementation")?.let {
            dependencies {
                "implementation"(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:${rootProject.libs.versions.kotlin.get()}"))
            }
        }
    }
}
