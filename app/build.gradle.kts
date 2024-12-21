plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.savoreel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.savoreel"
        minSdk = 24
        targetSdk = 34
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.compose.ui:ui-text:1.7.5")
    implementation("androidx.compose.animation:animation:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.13-rc")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.storage)

    // Correct dependencies for UI testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.compose.ui:ui:1.7.6" )
    implementation ("androidx.compose.material3:material3:1.3.1")
    implementation ("androidx.navigation:navigation-compose:2.8.5")
    implementation ("androidx.compose.foundation:foundation:1.7.6")
    implementation ("androidx.compose.runtime:runtime:1.7.6")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.6") // This is required for @Preview to work
}