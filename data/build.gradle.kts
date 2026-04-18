plugins {
    // --- Android ---
    alias(libs.plugins.android.library)

    // --- DI ---
    alias(libs.plugins.google.dagger.hilt.android)

    // --- Codegen ---
    alias(libs.plugins.google.devtools.ksp)
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
}

dependencies {
    // --- Modules ---
    implementation(project(":domain"))

    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- DI (Hilt) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // --- Testing ---
    testImplementation(libs.junit)

    // --- Android testing ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}