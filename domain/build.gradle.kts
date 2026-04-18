plugins {
    // --- Java ---
    id("java-library")

    // --- Kotlin ---
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    // --- Compiler ---
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }

    // --- API discipline ---
    explicitApi()
}