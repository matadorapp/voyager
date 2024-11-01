object BuildPlugins {
    val android = "com.android.tools.build:gradle:${Versions.gradle_version}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}"
    val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt_version}"
    val ksp = "com.google.devtools.ksp:symbol-processing-gradle-plugin:${Versions.ksp_version}"
}

object Dependencies {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin_version}"
    val ktx = "androidx.core:core-ktx:${Versions.ktx_version}"
    val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_version}"
    val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose_version}"

    val composeUi = "androidx.compose.ui:ui:${Versions.compose_version}"
    val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"
    val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose_version}"
    val composeMaterial = "androidx.compose.material3:material3:${Versions.compose_material_version}"
    val composeActivity = "androidx.activity:activity-compose:${Versions.compose_activity_version}"
    val composeLifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.compose_lifecycle_runtime_version}"

    val lifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_version}"
    val lifecycleCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle_version}"

    val hiltKsp = "com.google.dagger:hilt-compiler:${Versions.hilt_version}"
    val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt_version}"
}
