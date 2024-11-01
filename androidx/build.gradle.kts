plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.compose") version Versions.compose_plugin_version
}

android {
    namespace = "com.publicapp.voyager.navigator.androidx"
    compileSdk = Versions.compile_sdk_version

    defaultConfig {
        minSdk = 23

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
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(Dependencies.ktx)
    implementation(Dependencies.stdLib)
    implementation(Dependencies.composeRuntime)

    api(project(":core"))
}

group = "com.publicapp.voyager"
version = Versions.library_version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.publicapp.voyager"
                artifactId = "androidx"
                version = Versions.library_version
            }
        }
    }
}
