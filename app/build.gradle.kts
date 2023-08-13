plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ir.amirroid.amirchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "ir.amirroid.amirchat"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.0-alpha04")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
//    implementation("androidx.compose.material3:material3-window-size-class:1.1.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // navigation
    implementation("com.google.accompanist:accompanist-navigation-animation:0.31.3-beta")

    // coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    // constraint layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // card view
    implementation("androidx.cardview:cardview:1.0.0")


    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // map
    implementation("com.google.maps.android:maps-compose:2.12.0")

    // google play service
    implementation("com.google.android.gms:play-services-maps:18.1.0")


    // exo player
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    implementation("androidx.media3:media3-ui:1.1.0")


    // media
    implementation("androidx.media:media:1.6.0")

    // animation
    implementation("androidx.compose.animation:animation-graphics:1.4.3")


    // gson
    implementation("com.google.code.gson:gson:2.10.1")

    // emoji keyboard
    implementation(project(":emojikeyboard2"))

    // zxing
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    // backendless
    implementation("com.backendless:backendless:6.3.6")

    // melli payamak
    implementation("com.google.code.ksoap2-android:ksoap2-android:3.6.1")

    // data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}