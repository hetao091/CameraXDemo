package com.github.cameraxdemo.singleton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaScannerConnection
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.TextureView
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.ImageCapture.Metadata
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import com.github.cameraxdemo.application.BaseApplication
import com.github.cameraxdemo.utiils.CameraXPreviewBuilder
import com.github.cameraxdemo.utiils.FileUtils
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-19 15:10
 * desc     ：相机操作单例
 * revise   :
 * =====================================
 */


class CameraHolder private constructor() {

    // 默认开启后置摄像头
    private var mCameraLensFacing = LensFacing.BACK
    private lateinit var mLifecycleOwner: LifecycleOwner
    private lateinit var mCameraView: TextureView
    // 图片采集器
    private var imageCapture: ImageCapture? = null
    // 视频采集器
    private var videoCapture: VideoCapture? = null

    private lateinit var mPreview: Preview

    //
    private val mFocusingRect = Rect()
    private val mMeteringRect = Rect()
    private val mCropRegion: Rect? = null

    private var mCameraManager: CameraManager? = null

    companion object {
        const val TAG = "CameraHolder"
        const val MAX_VIEW_DIMENSION = 2000
        const val UNITY_ZOOM_SCALE = 1f

        val getInstance: CameraHolder by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CameraHolder()
        }
    }

    /**
     * 开启相机
     */
    fun startCamera(lifecycleOwner: LifecycleOwner, viewFinder: TextureView) {
        mLifecycleOwner = lifecycleOwner
        mCameraView = viewFinder
        // 确保没有绑定到CameraX的其他用例
        mCameraManager = (mCameraView.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
        startCameraPreview(lifecycleOwner, viewFinder)
    }

    /**
     * 释放相机
     */
    fun releaseCamera() {
        CameraX.unbindAll()
    }

    /**
     * 拍照
     */
    fun takePicture(callBack: () -> TakePictureCallBack): TakePictureCallBack {
        Log.v("takePicture", "takePicture")
        // 元数据
        val metadata = Metadata().apply {
            //  是否镜像显示
            isReversedHorizontal = mCameraLensFacing == LensFacing.FRONT
        }
        imageCapture?.takePicture(FileUtils.getOutputMediaFile(), object : ImageCapture.OnImageSavedListener {
            override fun onImageSaved(file: File) {
                // 成功后回调-> 照片处理模块
                Log.v("imageSavedListener", file.absolutePath)
                //扫描创建的目录加入到系统media菜单
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(file.extension)
                MediaScannerConnection.scanFile(
                    BaseApplication.getContext(), arrayOf(file.absolutePath), arrayOf(mimeType), null
                )
                callBack().success(file.absolutePath)
            }

            override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                cause!!.printStackTrace()
                callBack().failure("message")
            }

        }, metadata)
        //
        return callBack()
    }


    /**
     * 切换摄像头
     */
    @SuppressLint("RestrictedApi")
    fun switchCamera() {
        //
        mCameraLensFacing = if (LensFacing.FRONT == mCameraLensFacing) {
            LensFacing.BACK
        } else {
            LensFacing.FRONT
        }
        try {
            CameraX.getCameraWithLensFacing(mCameraLensFacing)
        } catch (e: Exception) {
            //  相机无法访问

        }
        // 重新预览
        startCameraPreview(mLifecycleOwner, mCameraView)
    }

    @SuppressLint("RestrictedApi")
    fun startRecording() {


//        videoCapture!!.startRecording(videoFile, object : VideoCapture.OnVideoSavedListener {
//            override fun onVideoSaved(file: File?) {
//
//            }
//
//            override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
//
//            }
//
//        })
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        videoCapture!!.stopRecording()
    }

    /************************ ***************************/


    /**
     * 创建分析器
     */
    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
            // 时间戳
            image!!.timestamp
            // 图片字节流 取YUV格式
            image.planes[0].buffer
        }

    }

    private fun getDisplayMetrics() =
        DisplayMetrics().also { mCameraView.display.getRealMetrics(it) }


    private fun getScreenSize() =
        Size(getDisplayMetrics().widthPixels, getDisplayMetrics().heightPixels)


    private fun getScreenAspectRatio() =
        Rational(getDisplayMetrics().widthPixels, getDisplayMetrics().heightPixels)


    /**
     * 开启预览
     */
    private fun setCameraPreview(viewFinder: TextureView): Preview {
        // 确保没有绑定到CameraX的其他用例
        CameraX.unbindAll()
        //预览
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(mCameraLensFacing)
            // 设置图像比例
            setTargetAspectRatio(getScreenAspectRatio())
            // 设置取景的目标分辨率
            setTargetResolution(getScreenSize())
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        mPreview = CameraXPreviewBuilder.build(previewConfig, viewFinder)
        return mPreview
    }

    @SuppressLint("RestrictedApi")
    private fun startCameraPreview(lifecycleOwner: LifecycleOwner, viewFinder: TextureView) {
        setCameraPreview(viewFinder)
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(mCameraLensFacing)
            // 或者使用MAX_QUALITY 获取更高质量
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            setTargetAspectRatio(getScreenAspectRatio())
            setTargetRotation(viewFinder.display.rotation)
            // 闪光灯关闭
            setFlashMode(FlashMode.OFF)
        }.build()
        //  预览设置
        imageCapture = ImageCapture(imageCaptureConfig)

        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setLensFacing(mCameraLensFacing)
            setTargetAspectRatio(getScreenAspectRatio())
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        videoCapture = VideoCapture(videoCaptureConfig)

        //
        CameraX.bindToLifecycle(lifecycleOwner, mPreview, imageCapture,videoCapture)
    }

    /**
     * 缩放
     */
    fun handlerZoom() {
        //
        mPreview.zoom(null)

    }

    /**
     * 对焦
     */
    fun handlerFocus(x: Float, y: Float, action: () -> Unit) {
        calculateTapArea(mFocusingRect, x, y, 1f)
        calculateTapArea(mMeteringRect, x, y, 1.5f)

        if (area(mFocusingRect) > 0 && area(mMeteringRect) > 0) {
            handleFocus(mFocusingRect, mMeteringRect,action)
        }
    }

    private fun handleFocus(focus: Rect, metering: Rect,action: () -> Unit) {
        val rescaledFocus: Rect
        val rescaledMetering: Rect
        try {
            val sensorRegion: Rect
            if (mCropRegion != null) {
                sensorRegion = mCropRegion
            } else {
                sensorRegion = getActiveCamera()?.let { getSensorSize(it) }!!
            }
            rescaledFocus = rescaleViewRectToSensorRect(focus, sensorRegion)
            rescaledMetering = rescaleViewRectToSensorRect(metering, sensorRegion)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rescale the focus and metering rectangles.", e)
            action.invoke()
            return
        }

        mPreview.focus(rescaledFocus, rescaledMetering, object : OnFocusListener {
            override fun onFocusUnableToLock(afRect: Rect?) {
                //无法获得焦点时回调
                Log.v(TAG, "onFocusUnableToLock")
                action.invoke()
            }

            override fun onFocusTimedOut(afRect: Rect?) {
                // 对焦超时达到且af状态尚未确定时回调。
                Log.v(TAG, "onFocusTimedOut")
                action.invoke()
            }

            override fun onFocusLocked(afRect: Rect?) {
                action.invoke()
                // 锁定焦点后回调
                Log.v(TAG, "onFocusLocked")
            }

        })
    }

    private fun rescaleViewRectToSensorRect(view: Rect, sensor: Rect): Rect {
        // Scale width and height.
        val newWidth = (view.width() * sensor.width() / MAX_VIEW_DIMENSION.toFloat()).roundToInt()
        val newHeight = (view.height() * sensor.height() / MAX_VIEW_DIMENSION.toFloat()).roundToInt()
        // Scale top/left corner.
        val halfViewDimension = MAX_VIEW_DIMENSION / 2
        val leftOffset =
            ((view.left + halfViewDimension) * sensor.width() / MAX_VIEW_DIMENSION.toFloat()).roundToInt() + sensor.left
        val topOffset =
            ((view.top + halfViewDimension) * sensor.height() / MAX_VIEW_DIMENSION.toFloat()).roundToInt() + sensor.top
        // Now, produce the scaled rect.
        val scaled = Rect()
        scaled.left = leftOffset
        scaled.top = topOffset
        scaled.right = scaled.left + newWidth
        scaled.bottom = scaled.top + newHeight
        return scaled
    }

    @Throws(CameraAccessException::class)
    private fun getSensorSize(cameraId: String): Rect? {
        val characteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
        return characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
    }

    @SuppressLint("RestrictedApi")
    @Throws(CameraInfoUnavailableException::class)
    fun getActiveCamera(): String? {
        return CameraX.getCameraWithLensFacing(mCameraLensFacing)
    }


    /** The area must be between -1000,-1000 and 1000,1000  */
    private fun calculateTapArea(rect: Rect, x: Float, y: Float, coefficient: Float) {
        var x = x
        var y = y
        val max = 1000
        val min = -1000
        // 默认为300(总面积的1/6)，并按系数缩放
        val areaSize = (300 * coefficient).toInt()
        // 如果相机方向不同，则旋转坐标
        var width = mCameraView.width
        var height = mCameraView.height
        // 补偿方向，因为它是镜像在预览前向相机
        val compensateForMirroring = mCameraLensFacing == LensFacing.FRONT
        val relativeCameraOrientation = getRelativeCameraOrientation(compensateForMirroring)
        val temp: Int
        val tempF: Float
        when (relativeCameraOrientation) {
            90,
                // Fall-through
            270 -> {
                // We're horizontal. Swap width/height. Swap x/y.
                temp = width
                width = height
                height = temp
                tempF = x

                x = y
                y = tempF
            }
            else -> {
            }
        }
        when (relativeCameraOrientation) {
            // 映射到正确的坐标根据相对ameraorientation
            90 -> y = height - y
            180 -> {
                x = width - x
                y = height - y
            }
            270 -> x = width - x
            else -> {
            }
        }
        // 如果是镜像预览，交换x
        if (compensateForMirroring) {
            x = width - x
        }
        // 从视图中获取x, y的位置，并将其标准化为-1000到1000
        x = (min + distance(max, min) * (x / width))
        y = (min + distance(max, min) * (y / height))
        // 将矩形修改为边界区域
        rect.top = y.toInt() - areaSize / 2
        rect.left = x.toInt() - areaSize / 2
        rect.bottom = rect.top + areaSize
        rect.right = rect.left + areaSize
        // 上限在-1000到1000之间
        rect.top = rangeLimit(rect.top, max, min)
        rect.left = rangeLimit(rect.left, max, min)
        rect.bottom = rangeLimit(rect.bottom, max, min)
        rect.right = rangeLimit(rect.right, max, min)
    }

    // 计算矩形面积
    private fun area(rect: Rect): Int {
        return rect.width() * rect.height()
    }

    // 计算距离
    private fun distance(a: Int, b: Int): Int {
        return abs(a - b)
    }

    private fun rangeLimit(`val`: Int, max: Int, min: Int): Int {
        return min(max(`val`, min), max)
    }

    @SuppressLint("RestrictedApi")
    fun getRelativeCameraOrientation(compensateForMirroring: Boolean): Int {
        var rotationDegrees: Int
        try {
            val cameraId = CameraX.getCameraWithLensFacing(mCameraLensFacing)
            val cameraInfo = CameraX.getCameraInfo(cameraId)
            rotationDegrees = cameraInfo!!.getSensorRotationDegrees(getDisplaySurfaceRotation())
            if (compensateForMirroring) {
                rotationDegrees = (360 - rotationDegrees) % 360
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query camera", e)
            rotationDegrees = 0
        }

        return rotationDegrees
    }

    private fun getDisplaySurfaceRotation(): Int {
        val display = ViewCompat.getDisplay(mCameraView) ?: return 0
        return display.rotation
    }

}

/***** 返回结果 *****/
interface TakePictureCallBack {
    fun success(mediaFilePath: String)
    fun failure(errorString: String)
}
