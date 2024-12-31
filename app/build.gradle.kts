plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
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
    // AndroidX Libraries
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

    implementation("androidx.compose.ui:ui-text:1.7.5") //
    implementation("androidx.compose.animation:animation:1.7.5")
    implementation ("androidx.compose.ui:ui:1.7.6" )
    implementation ("androidx.compose.foundation:foundation:1.7.6")
    implementation ("androidx.compose.runtime:runtime:1.7.6")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.6")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.compiler:compiler:1.5.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation(libs.androidx.navigation.compose)

    // Accompanist Libraries
    implementation("com.google.accompanist:accompanist-flowlayout:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.13-rc")

    // Firebase and Google Services
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:20.3.0")

    // Google Maps and Places
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:4.4.2")
    implementation("com.google.maps.android:maps-compose-widgets:4.3.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose-utils:4.4.2")
    implementation("com.google.android.libraries.places:places:3.3.0")

    // AndroidX Camera
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // Media and Image Loading
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.1")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Emoji Libraries
    implementation("androidx.emoji2:emoji2:1.3.0")
    implementation("androidx.emoji2:emoji2-views:1.3.0")
    implementation("androidx.emoji2:emoji2-views-helper:1.3.0")

    // Networking and Parsing
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.jsoup:jsoup:1.14.3")

    // Facebook SDK
    implementation("com.facebook.android:facebook-login:12.3.0")

    // AndroidX Storage
    implementation(libs.androidx.storage)

    // LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.emoji2.emojipicker)
    implementation(libs.firebase.auth)
    implementation (libs.accompanist.pager)

    // imgur
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Cloudinary
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.cloudinary:cloudinary-core:1.33.0")
    implementation(libs.firebase.messaging.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
