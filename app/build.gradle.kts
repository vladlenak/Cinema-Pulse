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

    defaultConfig {
        applicationId = "t.me.octopusapps.cinemapulse"

        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

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

    // --- Paging ---
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // --- Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Image Loading ---
    implementation(libs.coil.compose)

    // --- DI (Hilt) ---
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

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
