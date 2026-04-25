import java.util.Properties

plugins {
    // --- Android ---
    alias(libs.plugins.android.library)

    // --- DI ---
    alias(libs.plugins.google.dagger.hilt.android)

    // --- Codegen ---
    alias(libs.plugins.google.devtools.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

android {
    namespace = "t.me.octopusapps.cinemapulse.data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${localProperties["apikey"] ?: ""}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // --- Java / Kotlin ---
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }

        // --- API discipline ---
        explicitApi()
    }

    // --- Features ---
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // --- Modules ---
    implementation(project(":domain"))

    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // --- DI (Hilt) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)

    // --- Local storage (Room) ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // --- Android testing ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}