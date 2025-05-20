buildscript {
    extra.apply {
        set("room_version", "2.7.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
