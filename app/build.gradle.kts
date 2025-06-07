plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.jewelryworkshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jewelryworkshop"
        minSdk = 34
        //noinspection OldTargetApi
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true;
        buildConfig = true
    }


//noinspection WrongGradleMethod
    dependencies {

        implementation("androidx.navigation:navigation-compose:2.7.5")
        implementation(libs.androidx.room.runtime.android)

        val roomVersion = "2.7.1"

        implementation("androidx.room:room-runtime:$roomVersion")
        ksp("androidx.room:room-compiler:$roomVersion")


        // optional - Kotlin Extensions and Coroutines support for Room
        implementation("androidx.room:room-ktx:$roomVersion")

        // optional - Guava support for Room, including Optional and ListenableFuture
        implementation("androidx.room:room-guava:$roomVersion")

        // optional - Test helpers
        testImplementation("androidx.room:room-testing:$roomVersion")

        // optional - Paging 3 Integration
        implementation("androidx.room:room-paging:$roomVersion")

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

//@Suppress("DSL_SCOPE_VIOLATION")