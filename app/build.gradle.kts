plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.jewelryworkshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jewelryworkshop"
        minSdk = 26  // Уменьшил для совместимости с LocalDateTime
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "DEFAULT_REPOSITORY", "\"ROOM_DATABASE\"")
            buildConfigField("boolean", "ENABLE_REPOSITORY_SELECTOR", "true")
        }

        create("debugMock") {
            initWith(getByName("debug"))
            buildConfigField("String", "DEFAULT_REPOSITORY", "\"MOCK_DATA\"")
            applicationIdSuffix = ".mock"
        }

        create("debugInMemory") {
            initWith(getByName("debug"))
            buildConfigField("String", "DEFAULT_REPOSITORY", "\"IN_MEMORY_TEST\"")
            applicationIdSuffix = ".memory"
        }

        release {
            buildConfigField("String", "DEFAULT_REPOSITORY", "\"ROOM_DATABASE\"")
            buildConfigField("boolean", "ENABLE_REPOSITORY_SELECTOR", "false")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17  // Понизил до 17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Optional Room features
//    implementation(libs.androidx.room.guava)
//    implementation(libs.androidx.room.paging)
//    testImplementation(libs.androidx.room.testing)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}