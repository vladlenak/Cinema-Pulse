import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.extensions.FailOnSeverity

plugins {
    // --- Android ---
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // --- Kotlin ---
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization) apply false

    // --- DI ---
    alias(libs.plugins.google.dagger.hilt.android) apply false

    // --- Codegen ---
    alias(libs.plugins.google.devtools.ksp) apply false

    // --- Static analysis / Formatting ---
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless)
}

spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    val editorConfigOverride = mapOf(
        "ktlint_code_style" to "android_studio",
        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        "ij_kotlin_allow_trailing_comma" to "true",
        "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
    )

    kotlin {
        target(
            "app/src/**/*.kt",
            "data/src/**/*.kt",
            "domain/src/**/*.kt",
        )
        ktlint(ktlintVersion).editorConfigOverride(editorConfigOverride)
    }

    kotlinGradle {
        target(
            "*.gradle.kts",
            "app/*.gradle.kts",
            "data/*.gradle.kts",
            "domain/*.gradle.kts",
        )
        ktlint(ktlintVersion).editorConfigOverride(editorConfigOverride)
    }

    format("xml") {
        target(
            "app/src/main/**/*.xml",
            "data/src/main/**/*.xml",
        )
        trimTrailingWhitespace()
        endWithNewline()
    }

    format("misc") {
        target(
            "README.md",
            ".github/workflows/*.yml",
            "gradle.properties",
            "gradle/libs.versions.toml",
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
}

val detektVersion = libs.versions.detekt.get()

subprojects {
    pluginManager.apply("dev.detekt")

    extensions.configure<DetektExtension>("detekt") {
        toolVersion = detektVersion
        source.setFrom(
            files(
                "src/main/java",
                "src/main/kotlin",
                "src/test/java",
                "src/test/kotlin",
                "src/androidTest/java",
                "src/androidTest/kotlin",
            ),
        )
        parallel = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        ignoreFailures = false
        failOnSeverity = FailOnSeverity.Error
        basePath.set(rootDir)
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget.set("21")
        exclude("**/build/**", "**/generated/**")
        reports {
            checkstyle.required.set(true)
            html.required.set(true)
            sarif.required.set(true)
            markdown.required.set(false)
        }
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget.set("21")
        exclude("**/build/**", "**/generated/**")
    }
}

tasks.register("format") {
    group = "formatting"
    description = "Formats Kotlin, Gradle Kotlin DSL, XML, and project metadata files."
    dependsOn("spotlessApply")
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs Kotlin formatting checks and Detekt static analysis."
    dependsOn("spotlessCheck")
    dependsOn(subprojects.map { "${it.path}:detekt" })
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn("qualityCheck")
}
