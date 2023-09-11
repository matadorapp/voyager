plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.publicapp.voyager.navigator.navigator"
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

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose_compiler_version
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(Dependencies.ktx)
    implementation(Dependencies.stdLib)
    implementation(Dependencies.composeRuntime)

    implementation(Dependencies.composeActivity)
    implementation(Dependencies.composeLifecycleRuntime)

    implementation(project(":core"))
}

group = "com.publicapp.voyager"
version = Versions.library_version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.publicapp.voyager"
                artifactId = "navigator"
                version = Versions.library_version
            }
        }
    }
}
