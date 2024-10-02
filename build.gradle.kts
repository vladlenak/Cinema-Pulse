plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    // Serialization
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization) apply false
    // Hilt
    alias(libs.plugins.google.dagger.hilt.android) apply false
    // Kapt
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
}