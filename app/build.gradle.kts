plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.devtools.ksp")

    id("com.google.dagger.hilt.android")

    id ("kotlin-parcelize")

//    id("com.google.gms.google-services")

}

android {
    namespace = "com.my.ganeshseats"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.my.ganeshseats"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            buildConfigField(type = "String", name = "BASE_URL", value = "\"https://ganesh.dosbharat.com/api/\"" )

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }

        debug {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField(type = "String", name = "BASE_URL", value = "\"https://ganesh.dosbharat.com/api/\"" )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

//    kotlinOptions {
//        jvmTarget = "11"
//    }
    // New recommended way:
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // design dynamic
    implementation (libs.ssp.android)
    implementation (libs.sdp.android)

    //    Room Database
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    ksp (libs.androidx.room.compiler)

//    Img
    implementation (libs.glide)
    ksp (libs.compiler)

    // lottie animation
    implementation (libs.lottie)


    //    Hilt-Dagger
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)

    //    Lifecycles
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

    //    Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    // Image Loading Library
    implementation (libs.squareup.okhttp)

// Navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

//    ViewPager2
    implementation (libs.androidx.viewpager2)

//    Permission
    implementation(libs.kotlin.permission)

    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

//    implementation ("androidx.sqlite:sqlite-framework:2.6.2")
//
//    implementation("net.zetetic:sqlcipher-android:4.12.0")
//    implementation("androidx.sqlite:sqlite:2.6.2")
}