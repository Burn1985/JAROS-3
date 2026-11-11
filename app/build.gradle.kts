
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jaros.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jaros.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "3.0"
    }
}
