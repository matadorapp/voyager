buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev" )
    }

    dependencies {
        classpath(BuildPlugins.android)
        classpath(BuildPlugins.kotlin)
        classpath(BuildPlugins.hilt)
        classpath(BuildPlugins.ksp)
    }

    allprojects {
        repositories {
            google()
            mavenCentral()
            maven("https://jitpack.io")
        }
    }
}
