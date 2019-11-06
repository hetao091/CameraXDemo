package com.github.cameraxdemo.utiils

import androidx.annotation.IntDef
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
        fun getFilePath(type: @FileType.Type Int): File {
            val appContext = BaseApplication.getContext()
            var mediaDir = appContext.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            mediaDir = if (mediaDir != null && mediaDir.exists()) {
                mediaDir
            } else {
                appContext.filesDir
            }

            return when (type) {
                FileType.IMAGE -> File(
                    mediaDir,
                    "temp.jpg"
                )
                FileType.VIDEO -> File(
                    mediaDir,
                    "temp.mp4"
                )
                else-> throw IllegalStateException("Type not supported")

            }

        }
    }


}

class FileType {
    companion object {
        const val IMAGE = 0
        const val VIDEO = 1
    }

    @Target(AnnotationTarget.TYPE)
    @IntDef(IMAGE, VIDEO)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class Type
}

