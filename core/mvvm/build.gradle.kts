plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        namespace = DefaultConfig.getAppId(".mvvm")
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk

        testInstrumentationRunner = LibsTest.testInstrumentationRunner
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
    implementation(Libs.rxJava)
    implementation(Libs.rxAndroid)
    implementation(Libs.rxKotlin)

    implementation(Libs.cicerone)

    implementation(project(":core:utils"))
    implementation(project(":core:design"))

    testImplementation(LibsTest.junit4)
}