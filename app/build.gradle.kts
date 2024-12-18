plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt") // Mantén solo una vez cada línea necesaria
}

android {
    namespace = "com.example.inicio"
    compileSdk = 33  // Cambié a 33 para estar alineado con los requisitos de Google Play

    defaultConfig {
        applicationId = "com.example.inicio"
        minSdk = 24
        targetSdk = 33  // Cambié a 33 para cumplir con los requisitos de Google Play
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dependencias de Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // login interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    implementation("androidx.recyclerview:recyclerview:1.2.1")
    // Dependencia de RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Dependencia de Picasso
    implementation("com.squareup.picasso:picasso:2.8") // Agrega la dependencia de Picasso
}
