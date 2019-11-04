package com.github.cameraxdemo.utiils

import com.github.cameraxdemo.R
import com.github.cameraxdemo.application.BaseApplication
import java.io.File


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-21 17:52
 * desc     ：
 * revise   :
 * =====================================
 */

private const val FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"

class FileUtils private constructor() {

    companion object {
        /**
         * 获取外部多媒体路径
         */
        fun getOutputMediaFile(): File {
            val appContext = BaseApplication.getContext()
            var mediaDir = appContext.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            mediaDir = if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                appContext.filesDir
            }
//            return File(
//                mediaDir,
//                SimpleDateFormat(FORMAT, Locale.CHINA).format(System.currentTimeMillis()) + PHOTO_EXTENSION
//            )
            return File(
                mediaDir,
                "tmp.jpg"
            )


        }

        fun getOutputMediaVideoFile(): File {
            val appContext = BaseApplication.getContext()
            var mediaDir = appContext.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            mediaDir = if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                appContext.filesDir
            }
//            return File(
//                mediaDir,
//                SimpleDateFormat(FORMAT, Locale.CHINA).format(System.currentTimeMillis()) + PHOTO_EXTENSION
//            )
            return File(
                mediaDir,
                "tmp.mp4"
            )


        }
    }
}