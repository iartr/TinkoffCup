import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = DefaultConfig.compileSdk

    defaultConfig {
        applicationId = DefaultConfig.getAppId()
        namespace = DefaultConfig.getAppId()
        minSdk = DefaultConfig.minSdk
        targetSdk = DefaultConfig.targetSdk
        versionCode = Releases.versionCode
        versionName = Releases.versionName
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("DebugKeystore.keystore")
            storePassword = "debugkey"
            keyAlias = "debug"
            keyPassword = "debugkey"
        }
        create("release") {
            val props = gradleLocalProperties(rootDir)
            val envAlias = props.getProperty("signing.alias")
            val envPass = props.getProperty("signing.pas")

            storeFile = file("release.keystore")
            storePassword = envPass
            keyAlias = envAlias
            keyPassword = envPass
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isShrinkResources = true
            isMinifyEnabled = true
            setProguardFiles(
                listOf(
                    File("proguard-rules.pro"),
                    getDefaultProguardFile("proguard-android-optimize.txt")
                )
            )
        }
    }

    compileOptions {
        sourceCompatibility = CompilerOptions.javaVersion
        targetCompatibility = CompilerOptions.javaVersion
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = CompilerOptions.javaVersion.toString()
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    coreLibraryDesugaring(Libs.desugar)

    implementation(Libs.androidKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.androidActivity)
    implementation(Libs.androidFragment)
    implementation(Libs.androidPreference)
    implementation(Libs.androidLegacySupport)
    implementation(Libs.androidMaterial)

    implementation(Libs.lifecycleRuntime)
    implementation(Libs.lifecycleViewModel)
    implementation(Libs.lifecycleViewModelSaveState)
    implementation(Libs.lifecycleLiveData)
    implementation(Libs.lifecycleRx)

    implementation(Libs.cicerone)
    implementation(Libs.koin)

    implementation(Libs.rxJava)
    implementation(Libs.rxKotlin)
    implementation(Libs.rxAndroid)

    implementation(platform(Libs.okHttp))
    implementation(Libs.okHttpLogger)
    implementation(Libs.okHttpMockWebServer)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitRxSupport)
    implementation(Libs.retrofitJsonReader)
    implementation(Libs.retrofitScalars)
    implementation(Libs.gson)

    implementation(Libs.glide)
    kapt(Libs.glideCompiler)

    implementation(project(":core:utils"))
    implementation(project(":core:ext"))
    implementation(project(":core:design"))
    implementation(project(":core:mvvm"))
    implementation(project(":core:network"))

    testImplementation(LibsTest.junit4)
    testImplementation(LibsTest.okhttpMockWebServer)
    testImplementation(LibsTest.koinTest)
    androidTestImplementation(LibsTest.androidJunit)
    androidTestImplementation(LibsTest.androidEspressoCore)
}