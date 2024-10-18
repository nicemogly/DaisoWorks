// Top-level build file where you can add configuration options common to all sub-projects/modules.


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
}



buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")


    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.3")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

