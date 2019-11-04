package com.github.cameraxdemo.utiils

import android.content.Context
import android.graphics.Matrix
import android.hardware.display.DisplayManager
import android.util.Size
import android.view.*
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-05-23 19:42
 * desc     ：相机预览相关设置
 * revise   :
 * =====================================
 */
class CameraXPreviewBuilder private constructor(
    config: PreviewConfig,
    viewFinderRef: WeakReference<TextureView>
) {
    companion object {
        // 获取当前设备的方向
        fun getDisplaySurfaceRotation(display: Display?) =
            when (display?.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> null
            }

        /**
         * 返回预览实例
         */
        fun build(config: PreviewConfig, viewFinder: TextureView) =
            CameraXPreviewBuilder(config, WeakReference(viewFinder)).useCase
    }


    val useCase: Preview
    // 视图旋转
    private var viewFinderRotation: Int? = null
    private var viewFinderDisplay: Int = -1
    //
    private var bufferRotation: Int = 0
    // 初始化Preview 的 size
    private var bufferDimens: Size = Size(0, 0)
    // 初始化
    private var viewFinderDimens: Size = Size(0, 0)
    private lateinit var displayManager: DisplayManager
    /** 试图变化监听 */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) {
            val viewFinder = viewFinderRef.get() ?: return
            if (displayId != viewFinderDisplay) {
                val display = displayManager.getDisplay(displayId)
                val rotation = getDisplaySurfaceRotation(display)
                updateTransform(viewFinder, rotation, bufferDimens, viewFinderDimens)
            }
        }
    }

    init {
        // 获取引用实例  如果为空抛出异常
        val viewFinder = viewFinderRef.get() ?: throw  IllegalAccessException("没有找到TextureView具体引用")
        viewFinderRotation = getDisplaySurfaceRotation(viewFinder.display) ?: 0
        viewFinderDisplay = viewFinder.display.displayId
        useCase = Preview(config)
        //  更新布局
        useCase.setOnPreviewOutputUpdateListener {
            val viewFinder =
                viewFinderRef.get() ?: return@setOnPreviewOutputUpdateListener
            // 始终位于底层
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            // 这块拿到SurfaceTexture可以通过attachToGLContext 实现离屏渲染
            viewFinder.surfaceTexture = it.surfaceTexture
            //  拿到预览方向
            bufferRotation = it.rotationDegrees
            val rotation = getDisplaySurfaceRotation(viewFinder.display)
            // 设置具体预览属性
            updateTransform(viewFinder, rotation, it.textureSize, viewFinderDimens)
        }

        // 监听布局
        viewFinder.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val viewFinder = v as TextureView
            // 获取新尺寸
            val newViewFinderDimens = Size(right - left, bottom - top)
            // 获取角度
            val rotation = getDisplaySurfaceRotation(viewFinder.display)
            // 更新预览
            updateTransform(viewFinder, rotation, bufferDimens, newViewFinderDimens)
        }
        displayManager = viewFinder.context
            .getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        // 注册监听
        displayManager.registerDisplayListener(displayListener, null)
        // 结束监听
        viewFinder.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View?) = Unit
            override fun onViewDetachedFromWindow(view: View?) {
                displayManager.unregisterDisplayListener(displayListener)
            }

        })

    }

    private fun updateTransform(
        textureView: TextureView?,
        rotation: Int?,
        newBufferDimens: Size,
        newViewFinderDimens: Size
    ) {
        // 为空直接返回
        val textureView = textureView ?: return
        // 没有属性改变不作处理
        if (rotation == viewFinderRotation &&
            Objects.equals(newBufferDimens, bufferDimens) &&
            Objects.equals(newViewFinderDimens, viewFinderDimens)
        ) {
            return
        }
        if (rotation == null) {
            return
        } else {
            viewFinderRotation = rotation
        }

        if (newBufferDimens.width == 0 || newBufferDimens.height == 0) {
            return
        } else {
            bufferDimens = newBufferDimens
        }

        if (newViewFinderDimens.width == 0 || newViewFinderDimens.height == 0) {
            return
        } else {
            viewFinderDimens = newViewFinderDimens
        }
        //
        val matrix = Matrix()
        //  取景器中心坐标
        val centerX = viewFinderDimens.width / 2f
        val centerY = viewFinderDimens.height / 2f
        // 旋转
        matrix.postRotate(-viewFinderRotation!!.toFloat(), centerX, centerY)
        // 获取宽高比
        val bufferRatio = bufferDimens.height / bufferDimens.width.toFloat()
        val scaledWidth: Int
        val scaledHeight: Int
        // 转化
        if (viewFinderDimens.width > viewFinderDimens.height) {
            scaledHeight = viewFinderDimens.width
            scaledWidth = (viewFinderDimens.width * bufferRatio).roundToInt()
        } else {
            scaledHeight = viewFinderDimens.height
            scaledWidth = (viewFinderDimens.height * bufferRatio).roundToInt()
        }
        //
        val xScale = scaledWidth / viewFinderDimens.width.toFloat()
        val yScale = scaledHeight / viewFinderDimens.height.toFloat()
        //
        matrix.preScale(xScale, yScale, centerX, centerY)
        //
        textureView.setTransform(matrix)
    }
}

