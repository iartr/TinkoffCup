plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        namespace = DefaultConfig.getAppId(".utils")
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk
    }

    compileOptions {
        sourceCompatibility = CompilerOptions.javaVersion
        targetCompatibility = CompilerOptions.javaVersion
    }

    kotlinOptions {
        jvmTarget = CompilerOptions.javaVersion.toString()
    }
}

dependencies {
    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    testImplementation(LibsTest.junit4)
}