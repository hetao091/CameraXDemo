package com.github.cameraxdemo.state

import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.cameraxdemo.singleton.TakePictureCallBack


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-19 14:39
 * desc     ：定义公共接口抽取相机操作行为
 * revise   :
 * =====================================
 */
interface CameraXState {

    // 开始
    fun start(lifecycleOwner: LifecycleOwner, viewFinder: TextureView)

    //  释放
    fun switch()

    // 释放
    fun release()

    // 对焦
    fun focus(x: Float, y: Float,action: () -> Unit)

    // 拍照
    fun capture(callBack: () -> TakePictureCallBack)

    // 录制视频
    fun record()

    // 取消录制
    fun stopRecord()

    // 缩放
    fun zoom()

    //  操作取消
    fun cancel()

    // 操作确认
    fun confirm()

    // 闪光灯控制
    fun flash()
}