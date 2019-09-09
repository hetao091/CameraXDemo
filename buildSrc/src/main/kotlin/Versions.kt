/**
 * 依赖版本号管理……。
 *
 * <p>demo演示用途，其他第三方依赖自行添加。</p>
 * <ul><li></li></ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;2019-07-19 10:45<br>
 * <strong>copyright</strong>&nbsp;&nbsp;&nbsp;&nbsp;2019, RongCloud<br>
 *
 * @version  : 1.0.0
 * @author   : hetao
 */
object Versions {
    const val appcompat = "1.0.2"

    const val core_ktx = "1.0.2"

    const val espresso_core = "3.2.0"

    const val androidx_test_runner = "1.2.0"

    const val constraint_layout = "1.1.3"

    const val com_android_tools_build_gradle = "3.5.0"

    const val lint_gradle = "26.4.2"

    const val junit = "4.12"

    const val org_jetbrains_kotlin = "1.3.50"

    const val coroutines_version = "1.1.1"

    const val lifecycle_version = "2.2.0-alpha02"

    const val nav_version = "2.0.0"
    //
    const val camerax_version = "1.0.0-alpha04"
    //

    const val glide_version = "4.9.0"

    object Gradle {
        const val runningVersion = "5.1.1"

        const val currentVersion = "5.5.1"

        const val nightlyVersion = "5.6-20190718055002+0000"

        const val releaseCandidate = ""
    }
}

object Android {
    const val minSdkVersion = 21
    const val targetSdkVersion = 28
    const val compileSdkVersion = 28
    const val buildToolsVersion = "28.0.3"
    const val applicationId = "com.github.cameraxdemo"
    const val versionCode = 1
    const val versionName = "0.1"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
