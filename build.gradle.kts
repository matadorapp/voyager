buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev" )
    }

    dependencies {
        classpath(libs.plugin.hilt)
        classpath(libs.plugin.maven)
        classpath(libs.plugin.android)
        classpath(libs.plugin.kotlin)
    }
}
