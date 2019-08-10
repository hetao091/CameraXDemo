plugins{
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}


android {
    compileSdkVersion (Android.compileSdkVersion)
    defaultConfig {
        applicationId = Android.applicationId
        minSdkVersion (Android.minSdkVersion)
        targetSdkVersion (Android.targetSdkVersion)
        versionCode = Android.versionCode
        versionName = Android.versionName
        testInstrumentationRunner = Android.testInstrumentationRunner
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation (Libs.kotlin_stdlib_jdk8)
    implementation (Libs.kotlinx_coroutines_android)
    //
    implementation (Libs.appcompat)
    implementation (Libs.constraint_layout)
    implementation (Libs.core_ktx)

    ///
    testImplementation (Libs.junit)
    androidTestImplementation (Libs.androidx_test_runner)
    androidTestImplementation (Libs.espresso_core)


    // Lifecycle and LiveData
    implementation (Libs.androidx_lifecycle_runtime)
    implementation (Libs.androidx_lifecycle_viewmodel_ktx)
    implementation (Libs.androidx_lifecycle_extensions)
    implementation (Libs.androidx_lifecycle_livedata)
    implementation (Libs.androidx_lifecycle_common_java8)

    // Navigation library
    implementation (Libs.androidx_navigation_fragment_ktx)
    implementation (Libs.androidx_navigation_ui_ktx)

    // CameraX
    implementation (Libs.androidx_camera_core)
    implementation (Libs.androidx_camera_camera2)

    // Glide
    implementation (Libs.glide)
    annotationProcessor (Libs.glide_compiler)
}
