object BuildPlugins {
    val android by lazy { "com.android.tools.build:gradle:${Versions.gradle_version}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}" }
    val hilt by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt_version}" }
}

object Dependencies {
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin_version}" }
    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.compose_version}" }
    val composeTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.compose_version}" }
    val composeFoundation by lazy { "androidx.compose.foundation:foundation:${Versions.compose_version}" }
    val composeMaterial by lazy { "androidx.compose.material:material:${Versions.compose_version}" }
    val composeActivity by lazy { "androidx.activity:activity-compose:${Versions.compose_activity}" }
    val composeRuntime by lazy { "androidx.lifecycle:lifecycle-runtime-compose:${Versions.compose_runtime}" }

    val hiltKapt by lazy { "com.google.dagger:hilt-compiler:${Versions.hilt_version}" }
    val hiltAndroid by lazy { "com.google.dagger:hilt-android:${Versions.hilt_version}" }
}
