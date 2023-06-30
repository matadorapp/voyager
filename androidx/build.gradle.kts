plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cafe.adriel.voyager.navigator.androidx"
    compileSdk = 33

    defaultConfig {
        minSdk = 30

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose_compiler_version
    }
}

dependencies {
    implementation(Dependencies.ktx)
    implementation(Dependencies.stdLib)
    implementation(Dependencies.composeRuntime)

    implementation(project(":core"))
}
