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
}