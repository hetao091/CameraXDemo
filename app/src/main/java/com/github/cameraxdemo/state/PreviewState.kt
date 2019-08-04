package com.github.cameraxdemo.state

import android.view.TextureView
import androidx.lifecycle.LifecycleOwner
import com.github.cameraxdemo.singleton.CameraHolder
import com.github.cameraxdemo.singleton.TakePictureCallBack


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-19 18:30
 * desc     ：相机预览状态
 * revise   :
 * =====================================
 */
class PreviewState : CameraXState {


    override fun start(lifecycleOwner: LifecycleOwner, viewFinder: TextureView) {
        CameraHolder.getInstance.startCamera(lifecycleOwner, viewFinder)
    }


    override fun switch() {
        CameraHolder.getInstance.switchCamera()
    }


    override fun release() {
        CameraHolder.getInstance.releaseCamera()
    }

    override fun focus(x: Float, y: Float,action: () -> Unit) {
        CameraHolder.getInstance.handlerFocus(x, y,action)
    }

    override fun capture(callBack: () -> TakePictureCallBack) {
        CameraHolder.getInstance.takePicture(callBack)
    }

    override fun record() {
        CameraHolder.getInstance.startRecording()
    }

    override fun stopRecord() {
        CameraHolder.getInstance.stopRecording()
    }

    override fun zoom() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun flash() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}