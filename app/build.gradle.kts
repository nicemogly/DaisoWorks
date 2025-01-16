
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.gms.google-services")
    id("kotlin-parcelize")

    id("androidx.navigation.safeargs.kotlin")
}

val properties = Properties().apply { load(project.rootProject.file("local.properties").inputStream()) }

android {
    namespace = "com.example.daisoworks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.daisoworks"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", properties.getProperty("api.key.herp"))
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
        viewBinding = true
        buildConfig = true

        dataBinding = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.runtime.saved.instance.state)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.media3.common)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.google.android.material:material:1.4.0")



    /* Retro API SETTING */
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")


    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-dynamic-module-support:16.0.0-beta03")

    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-messaging-directboot:20.2.0")

    //Bio
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha04")

    //image
    implementation("com.github.bumptech.glide:glide:4.12.0")
   // implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")


    implementation("commons-net:commons-net:3.9.0")
    //implementation("com.github.bumptech.glide:compiler:4.14.2")

    implementation("androidx.cardview:cardview:1.0.0") // 레이아웃으로 사용할 CardView

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")

    implementation("com.google.zxing:core:3.4.0")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    //차트
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}
