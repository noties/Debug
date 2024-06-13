plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "io.noties.debug.sample.android"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24
    }
    
    dependencies {
        implementation(project(":debug"))
    }
}
