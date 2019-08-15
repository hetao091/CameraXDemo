plugins{
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion (Android.compileSdkVersion)
    buildToolsVersion  (Android.buildToolsVersion)

    defaultConfig {
        minSdkVersion (Android.minSdkVersion)
        targetSdkVersion (Android.targetSdkVersion)
        versionCode = Android.versionCode
        versionName = Android.versionName

        testInstrumentationRunner = Android.testInstrumentationRunner

        externalNativeBuild {
            cmake {
                cppFlags.add("")
                ndk {
                    abiFilters  ("armeabi-v7a")
                }

            }
        }
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled = false
        }
    }
    externalNativeBuild {
        cmake {
          setPath("CMakeLists.txt")
        }
    }

    sourceSets {
        getByName("main").jniLibs.srcDirs ("libs")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation (Libs.appcompat)

    testImplementation (Libs.junit)
    androidTestImplementation (Libs.androidx_test_runner)
    androidTestImplementation (Libs.espresso_core)

    implementation (Libs.core_ktx)
    implementation (Libs.kotlin_stdlib_jdk8)
}
