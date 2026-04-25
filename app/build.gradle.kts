plugins {
    // --- Android ---
    alias(libs.plugins.android.application)

    // --- Kotlin ---
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)

    // --- DI ---
    alias(libs.plugins.google.dagger.hilt.android)

    // --- Codegen ---
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "t.me.octopusapps.cinemapulse"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "t.me.octopusapps.cinemapulse"
        minSdk = 25
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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

    // --- Features ---
    buildFeatures {
        compose = true
    }

    // --- Packaging ---
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Modules ---
    implementation(project(":domain"))
    implementation(project(":data"))

    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Compose ---
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Navigation ---
    implementation(libs.navigation.compose)

    // --- Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Image Loading ---
    implementation(libs.coil.compose)

    // --- DI (Hilt) ---
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // --- Unit tests ---
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // --- Android tests ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- Debug ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}