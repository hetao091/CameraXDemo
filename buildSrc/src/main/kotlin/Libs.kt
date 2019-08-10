/**
 * 构建所需依赖项管理……。
 *
 * <p>demo演示用途，其他第三方依赖自行添加。……</p>
 * <ul><li></li></ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;2019-07-19 10:45<br>
 * <strong>copyright</strong>&nbsp;&nbsp;&nbsp;&nbsp;2019, RongCloud<br>
 *
 * @version  : 1.0.0
 * @author   : hetao
 */
object Libs {

    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"

    const val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"

    const val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"

    const val androidx_test_runner = "androidx.test:runner:${Versions.androidx_test_runner}"

    const val constraint_layout = "com.android.support.constraint:constraint-layout:${Versions.constraint_layout}"

    const val com_android_tools_build_gradle =
        "com.android.tools.build:gradle:${Versions.com_android_tools_build_gradle}"

    const val lint_gradle = "com.android.tools.lint:lint-gradle:${Versions.lint_gradle}"

    const val junit = "junit:junit:${Versions.junit}"

    // kotlin
    const val kotlin_android_extensions_runtime =
        "org.jetbrains.kotlin:kotlin-android-extensions-runtime:${Versions.org_jetbrains_kotlin}"

    const val kotlin_android_extensions =
        "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.org_jetbrains_kotlin}"

    const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.org_jetbrains_kotlin}"

    const val kotlin_stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.org_jetbrains_kotlin}"

    const val kotlinx_coroutines_android="org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines_version}"





    // Lifecycle and LiveData
    const val androidx_lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle_version}"

    const val androidx_lifecycle_viewmodel_ktx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_version}"

    const val androidx_lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_version}"

    const val androidx_lifecycle_livedata = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle_version}"

    const val androidx_lifecycle_common_java8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle_version}"

    // Navigation library
    const val androidx_navigation_fragment_ktx = "androidx.navigation:navigation-fragment-ktx:${Versions.nav_version}"

    const val androidx_navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Versions.nav_version}"

    // CameraX
    const val androidx_camera_core = "androidx.camera:camera-core:${Versions.camerax_version}"
    const val androidx_camera_camera2 = "androidx.camera:camera-camera2:${Versions.camerax_version}"

    //Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
    const val glide_compiler = "com.github.bumptech.glide:glide:${Versions.glide_version}"
}