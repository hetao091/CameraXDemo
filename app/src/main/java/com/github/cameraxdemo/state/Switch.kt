package com.github.cameraxdemo.state

import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.cameraxdemo.singleton.TakePictureCallBack


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-19 18:32
 * desc     ：负责相机操作各种状态切换
 * revise   :
 * =====================================
 */
class Switch {


    private lateinit var mCameraXState: CameraXState

    init {
        // 初始化为预览状态
        getPreviewState()
    }

    private fun setCameraXState(mCameraXState: CameraXState) {
        this.mCameraXState = mCameraXState
    }

    /**
     * 获取预览状态
     */
    fun getPreviewState() {
        setCameraXState(PreviewState())
    }

    /**
     * 获取图片状态
     */
//    fun getPictureState() {
//        setCameraXState(PictureState())
//    }
//
//    /**
//     * 获取视频状态
//     */
//    fun getVideoState() {
//        setCameraXState(VideoState())
//    }

    fun startCamera(lifecycleOwner: LifecycleOwner, viewFinder: TextureView) {
        mCameraXState.start(lifecycleOwner, viewFinder)
    }

    fun capture(callBack: () -> TakePictureCallBack) {
        mCameraXState.capture(callBack)
    }

    fun startRecord() {
        mCameraXState.record()
    }

    fun stopRecord() {
        mCameraXState.stopRecord()
    }

    fun releaseCamera() {
        mCameraXState.release()
    }

    // 对焦
    fun focus(x: Float, y: Float, action: () -> Unit) {
        mCameraXState.focus(x, y, action)
    }
}