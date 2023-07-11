plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.publicapp.voyager.navigator.core"
    compileSdk = 33

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
        lint {
            abortOnError = false
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
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeMaterial)
    implementation(Dependencies.composeFoundation)
    implementation(Dependencies.composeLifecycleRuntime)
    implementation(Dependencies.composeTooling)

    implementation(Dependencies.lifecycleKtx)
    implementation(Dependencies.lifecycleCompose)
}

group = "com.publicapp.voyager"
version = Versions.library_version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.publicapp.voyager"
                artifactId = "core"
                version = Versions.library_version

            }
        }
    }
}
