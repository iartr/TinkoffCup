import org.gradle.api.JavaVersion

object Releases {
    val versionCode = 1
    val versionName = "1.0"
}

object CompilerOptions {
    val javaVersion = JavaVersion.VERSION_17
}

object DefaultConfig {
    val minSdk = 24
    val targetSdk = 33
    val compileSdk = 33

    fun getAppId(postfix: String = ""): String {
        return "com.artr.tinkoffcup$postfix"
    }
}

object Libs {
    val androidToolsPlugin = "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN_VERSION}"

    // D8
    val desugar = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR_VERSION}"

    //region Kotlin
    // Kotlin
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}"
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_VERSION}"
    //endregion

    //region Android
    // https://developer.android.com/kotlin/ktx
    val androidKtx = "androidx.core:core-ktx:${Versions.ANDROID_KTX_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/appcompat
    val androidAppCompat = "androidx.appcompat:appcompat:${Versions.ANDROID_APPCOMPAT_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/activity
    val androidActivity = "androidx.activity:activity-ktx:${Versions.ANDROID_ACTIVITY_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/fragment
    val androidFragment = "androidx.fragment:fragment-ktx:${Versions.ANDROID_FRAGMENT_VERSION}"
    // https://developer.android.com/jetpack/androidx/releases/annotation
    val androidAnnotation = "androidx.annotation:annotation::${Versions.ANDROID_ANNOTATION_VERSION}"
    // // https://developer.android.com/jetpack/androidx/releases/preference
    val androidPreference = "androidx.preference:preference-ktx:${Versions.ANDROID_PREFERENCE_VERSION}"
    val androidLegacySupport = "androidx.legacy:legacy-support-v4:${Versions.ANDROID_LEGACY_SUPPORT_VERSION}"
    val androidMaterial = "com.google.android.material:material:${Versions.ANDROID_MATERIAL_VERSION}"
    // https://github.com/sockeqwe/AdapterDelegates
    val adapterDelegate = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:${Versions.ADAPTER_DELEGATE_VERSION}"
    //endregion

    //region Lifecycle
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_VERSION}"
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE_VERSION}"
    val lifecycleViewModelSaveState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.LIFECYCLE_VERSION}"
    val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE_VERSION}"
    val lifecycleRx = "androidx.lifecycle:lifecycle-reactivestreams-ktx:${Versions.LIFECYCLE_VERSION}"
    //endregion

    //region Imaging (Glide)
    // https://github.com/bumptech/glide
    val glide = "com.github.bumptech.glide:glide:${Versions.GLIDE_VERSION}"
    val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.GLIDE_VERSION}"
    //endregion
}

object LibsTest {
    val junit4 = "junit:junit:${Versions.JUNIT4_VERSION}"
    val androidJunit = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT_VERSION}"
    val androidEspressoCore = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE_VERSION}"

    val okhttpMockWebServer = "com.squareup.okhttp3:mockwebserver"

    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

object Versions {
    const val ANDROID_GRADLE_PLUGIN_VERSION = "8.0.0"
    const val DESUGAR_VERSION = "1.1.5"

    const val KOTLIN_VERSION = "1.8.20"

    const val ANDROID_KTX_VERSION = "1.10.0"
    const val ANDROID_APPCOMPAT_VERSION = "1.6.1"
    const val ANDROID_ACTIVITY_VERSION = "1.4.0"
    const val ANDROID_FRAGMENT_VERSION = "1.4.1"
    const val ANDROID_ANNOTATION_VERSION = "1.4.0"
    const val ANDROID_PREFERENCE_VERSION = "1.2.0"
    const val ANDROID_LEGACY_SUPPORT_VERSION = "1.0.0"
    const val ANDROID_MATERIAL_VERSION = "1.8.0"
    const val ADAPTER_DELEGATE_VERSION = "4.3.2"
    const val LIFECYCLE_VERSION = "2.4.1"
    const val GLIDE_VERSION = "4.13.0"

    const val JUNIT4_VERSION = "4.13.2"
    const val ANDROID_JUNIT_VERSION = "1.1.3"
    const val ESPRESSO_CORE_VERSION = "3.4.0"
}