plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "app.openincomaps"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.ii.android.comapsgooglemaps"
        minSdk = 23
        targetSdk = 35
        versionCode = 4
        versionName = "1.1.0"

        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        allWarningsAsErrors = true
    }

    testOptions {
        unitTests.all {
            it.useJUnit()
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
