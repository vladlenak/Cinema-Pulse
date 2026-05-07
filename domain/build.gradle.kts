plugins {
    // --- Java ---
    id("java-library")

    // --- Kotlin ---
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin {
    // --- API discipline ---
    explicitApi()
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
