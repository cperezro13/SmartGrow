plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ejemplo.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ejemplo.myapplication"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Importa Firebase BoM (Bill of Materials) para manejar versiones automáticamente
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    implementation("org.tensorflow:tensorflow-lite:2.9.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
