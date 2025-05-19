plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
}

android {
    namespace = "com.truspirit.idcard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.truspirit.idcard"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation ("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.0")
    implementation ("androidx.recyclerview:recyclerview:1.4.0")

    // Room
    implementation ("androidx.room:room-runtime:2.6.0")
    kapt ("androidx.room:room-compiler:2.6.0")
    implementation ("androidx.room:room-ktx:2.6.0")

    // Image cropper (uCrop)
    implementation ("com.github.yalantis:ucrop:2.2.10")

    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation ("com.github.yalantis:ucrop:2.2.10")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("com.google.android.material:material:1.11.0")


    // FileProvider
    // No extra lib needed; configured below

}