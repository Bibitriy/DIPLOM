plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.witte"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.witte"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testIns trumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation ("org.jsoup:jsoup:1.13.1")
    implementation ("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation ("io.ktor:ktor-client-core:2.3.8")
    implementation ("io.ktor:ktor-client-cio:2.3.8")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
    implementation ("io.ktor:ktor-client-serialization-jvm:2.3.8")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.0") // Call requires API level 26 (current min is 24): java.time.LocalDate#
}