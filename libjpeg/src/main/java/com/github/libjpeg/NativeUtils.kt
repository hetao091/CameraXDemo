package com.github.libjpeg


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019/1/15 15:24
 * desc     ：
 * revise   :
 * =====================================
 */
object NativeUtils {

    init {
        System.loadLibrary("native-lib")
    }


    external fun stringFromJNI(): String

    /**
     * [w] 图片宽度
     * [h] 图片宽度
     * [quality] 图片压缩质量
     * [fileByteArray] 图片源文件
     * [optimize] 是否启用霍夫曼编码
     */
    external fun compressBitmap(w: Int, h: Int, quality: Int, fileByteArray: ByteArray, optimize: Boolean): String

    /**
     * [w] 图片宽度
     * [h] 图片宽度
     * [quality] 图片压缩质量
     * [filePath] 图片源文件
     * [optimize] 是否启用霍夫曼编码
     */
    external fun compressImageFile(quality: Int, filePath: String, optimize: Boolean): String
}