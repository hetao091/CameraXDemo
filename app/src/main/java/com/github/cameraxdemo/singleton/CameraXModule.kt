package com.github.cameraxdemo.singleton

import android.view.TextureView
import androidx.camera.core.ImageCapture.CaptureMode
import android.os.Looper
import android.graphics.SurfaceTexture
import androidx.camera.core.CameraOrientationUtil
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.camera.core.FlashMode
import androidx.camera.core.CameraX.LensFacing
import java.util.Arrays.asList
import android.Manifest.permission
import android.util.Rational
import androidx.camera.core.CameraX
import androidx.camera.core.CameraInfoUnavailableException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraAccessException
import android.annotation.SuppressLint
import androidx.camera.core.VideoCapture

import androidx.camera.core.VideoCapture.OnVideoSavedListener
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedListener
import androidx.camera.core.ImageCapture.OnImageCapturedListener
import androidx.camera.core.Preview
import android.R.attr.left
import android.R.attr.right
import android.content.Context
import androidx.camera.core.VideoCaptureConfig
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.PreviewConfig
import android.content.Context.CAMERA_SERVICE
import android.graphics.Matrix
import android.graphics.Rect
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.camera2.CameraManager
import android.util.Log
import android.util.Size
import androidx.annotation.Nullable
import androidx.annotation.RequiresPermission
import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.LinkedHashSet


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-07-04 15:39
 * desc     ：
 * revise   :
 * =====================================
 */
class CameraXModule(view: TextureView) {

//    private val MAX_VIEW_DIMENSION = 2000
//    private val UNITY_ZOOM_SCALE = 1f
//    private val ZOOM_NOT_SUPPORTED = UNITY_ZOOM_SCALE
//    private val ASPECT_RATIO_16_9 = Rational(16, 9)
//    private val ASPECT_RATIO_4_3 = Rational(4, 3)
//    private val ASPECT_RATIO_9_16 = Rational(9, 16)
//    private val ASPECT_RATIO_3_4 = Rational(3, 4)
//    private var mCameraManager: CameraManager? = null
//    private var mPreviewConfigBuilder: PreviewConfig.Builder? = null
//    private var mVideoCaptureConfigBuilder: VideoCaptureConfig.Builder? = null
//    private var mImageCaptureConfigBuilder: ImageCaptureConfig.Builder? = null
//
//    private val mCameraLensFacing = LensFacing.BACK
//    private var mCameraView: TextureView? = null
//
//    var mPreview: Preview? = null
//    private val mFlash = FlashMode.OFF
//    private val mCropRegion: Rect? = null
//    private val mFocusingRect = Rect()
//    private val mMeteringRect = Rect()
//
//    init {
//        this.mCameraView = view
//        mCameraManager = view.context.getSystemService(CAMERA_SERVICE) as CameraManager?
//
//        mPreviewConfigBuilder = PreviewConfig.Builder().setTargetName("Preview")
//        mImageCaptureConfigBuilder = ImageCaptureConfig.Builder().setTargetName("ImageCapture")
//        mVideoCaptureConfigBuilder = VideoCaptureConfig.Builder().setTargetName("VideoCapture")
//    }
//
//
//    fun handlerFocus(x: Float, y: Float, action: () -> Unit) {
//        calculateTapArea(mFocusingRect, x, y, 1f)
//        calculateTapArea(mMeteringRect, x, y, 1.5f)
//
//        if (area(mFocusingRect) > 0 && area(mMeteringRect) > 0) {
//            focus(mFocusingRect, mMeteringRect,action)
//        }
//    }
//
//    fun focus(focus: Rect, metering: Rect,action: () -> Unit) {
//        if (mPreview == null) {
//            // Nothing to focus on since we don't yet have a preview
//            return
//        }
//        val rescaledFocus: Rect
//        val rescaledMetering: Rect
//        try {
//            val sensorRegion: Rect
//            if (mCropRegion != null) {
//                sensorRegion = mCropRegion
//            } else {
//                sensorRegion = getActiveCamera()?.let { getSensorSize(it) }!!
//            }
//            rescaledFocus = rescaleViewRectToSensorRect(focus, sensorRegion)
//            rescaledMetering = rescaleViewRectToSensorRect(metering, sensorRegion)
//        } catch (e: Exception) {
//            Log.e(CameraHolder.TAG, "Failed to rescale the focus and metering rectangles.", e)
//            return
//        }
//        mPreview!!.focus(rescaledFocus, rescaledMetering,object : OnFocusListener {
//            override fun onFocusUnableToLock(afRect: Rect?) {
//                //无法获得焦点时回调
//                Log.v(CameraHolder.TAG, "onFocusUnableToLock")
//                action.invoke()
//            }
//
//            override fun onFocusTimedOut(afRect: Rect?) {
//                // 对焦超时达到且af状态尚未确定时回调。
//                Log.v(CameraHolder.TAG, "onFocusTimedOut")
//                action.invoke()
//            }
//
//            override fun onFocusLocked(afRect: Rect?) {
//                action.invoke()
//                // 锁定焦点后回调
//                Log.v(CameraHolder.TAG, "onFocusLocked")
//            }
//
//        })
//    }
//
//    private fun rescaleViewRectToSensorRect(view: Rect, sensor: Rect): Rect {
//        // Scale width and height.
//        val newWidth = (view.width() * sensor.width() / MAX_VIEW_DIMENSION.toFloat()).roundToInt()
//        val newHeight = (view.height() * sensor.height() / MAX_VIEW_DIMENSION.toFloat()).roundToInt()
//        // Scale top/left corner.
//        val halfViewDimension = MAX_VIEW_DIMENSION / 2
//        val leftOffset =
//            ((view.left + halfViewDimension) * sensor.width() / MAX_VIEW_DIMENSION.toFloat()).roundToInt() + sensor.left
//        val topOffset =
//            ((view.top + halfViewDimension) * sensor.height() / MAX_VIEW_DIMENSION.toFloat()).roundToInt() + sensor.top
//        // Now, produce the scaled rect.
//        val scaled = Rect()
//        scaled.left = leftOffset
//        scaled.top = topOffset
//        scaled.right = scaled.left + newWidth
//        scaled.bottom = scaled.top + newHeight
//        return scaled
//    }
//
//    @Throws(CameraAccessException::class)
//    private fun getSensorSize(cameraId: String): Rect? {
//        val characteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
//        return characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Throws(CameraInfoUnavailableException::class)
//    fun getActiveCamera(): String? {
//        return CameraX.getCameraWithLensFacing(mCameraLensFacing)
//    }
//
//
//    /** The area must be between -1000,-1000 and 1000,1000  */
//    private fun calculateTapArea(rect: Rect, x: Float, y: Float, coefficient: Float) {
//        var x = x
//        var y = y
//        val max = 1000
//        val min = -1000
//        // Default to 300 (1/6th the total area) and scale by the coefficient
//        val areaSize = (300 * coefficient).toInt()
//        // Rotate the coordinates if the camera orientation is different
//        var width = mCameraView!!.width
//        var height = mCameraView!!.height
//        // Compensate orientation as it's mirrored on preview for forward facing cameras
//        val compensateForMirroring = mCameraLensFacing == LensFacing.FRONT
//        val relativeCameraOrientation = getRelativeCameraOrientation(compensateForMirroring)
//        val temp: Int
//        val tempf: Float
//        when (relativeCameraOrientation) {
//            90,
//                // Fall-through
//            270 -> {
//                // We're horizontal. Swap width/height. Swap x/y.
//                temp = width
//
//                width = height
//                height = temp
//                tempf = x
//
//                x = y
//                y = tempf
//            }
//            else -> {
//            }
//        }
//        when (relativeCameraOrientation) {
//            // Map to correct coordinates according to relativeCameraOrientation
//            90 -> y = height - y
//            180 -> {
//                x = width - x
//                y = height - y
//            }
//            270 -> x = width - x
//            else -> {
//            }
//        }
//        // Swap x if it's a mirrored preview
//        if (compensateForMirroring) {
//            x = width - x
//        }
//        // Grab the x, y position from within the View and normalize it to -1000 to 1000
//        x = (min + distance(max, min) * (x / width)).toFloat()
//        y = (min + distance(max, min) * (y / height)).toFloat()
//        // Modify the rect to the bounding area
//        rect.top = y.toInt() - areaSize / 2
//        rect.left = x.toInt() - areaSize / 2
//        rect.bottom = rect.top + areaSize
//        rect.right = rect.left + areaSize
//        // Cap at -1000 to 1000
//        rect.top = rangeLimit(rect.top, max, min)
//        rect.left = rangeLimit(rect.left, max, min)
//        rect.bottom = rangeLimit(rect.bottom, max, min)
//        rect.right = rangeLimit(rect.right, max, min)
//    }
//
//    private fun area(rect: Rect): Int {
//        return rect.width() * rect.height()
//    }
//
//    private fun distance(a: Int, b: Int): Int {
//        return abs(a - b)
//    }
//
//    private fun rangeLimit(`val`: Int, max: Int, min: Int): Int {
//        return min(max(`val`, min), max)
//    }
//
//    @SuppressLint("RestrictedApi")
//    fun getRelativeCameraOrientation(compensateForMirroring: Boolean): Int {
//        var rotationDegrees: Int
//        try {
//            val cameraId = CameraX.getCameraWithLensFacing(mCameraLensFacing)
//            val cameraInfo = CameraX.getCameraInfo(cameraId)
//            rotationDegrees = cameraInfo!!.getSensorRotationDegrees(getDisplaySurfaceRotation())
//            if (compensateForMirroring) {
//                rotationDegrees = (360 - rotationDegrees) % 360
//            }
//        } catch (e: Exception) {
//            Log.e(CameraHolder.TAG, "Failed to query camera", e)
//            rotationDegrees = 0
//        }
//
//        return rotationDegrees
//    }
//
//    private fun getDisplaySurfaceRotation(): Int {
//        val display = ViewCompat.getDisplay(mCameraView!!) ?: return 0
//        return display.rotation
//    }


    val TAG = "CameraXModule"
    private val MAX_VIEW_DIMENSION = 2000
    private val UNITY_ZOOM_SCALE = 1f
    private val ZOOM_NOT_SUPPORTED = UNITY_ZOOM_SCALE
    private val ASPECT_RATIO_16_9 = Rational(16, 9)
    private val ASPECT_RATIO_4_3 = Rational(4, 3)
    private val ASPECT_RATIO_9_16 = Rational(9, 16)
    private val ASPECT_RATIO_3_4 = Rational(3, 4)
    private var mCameraManager: CameraManager
    private var mPreviewConfigBuilder: PreviewConfig.Builder
    private var mVideoCaptureConfigBuilder: VideoCaptureConfig.Builder
    private var mImageCaptureConfigBuilder: ImageCaptureConfig.Builder
    private val mCameraView: TextureView
    val mVideoIsRecording = AtomicBoolean(false)
    private var mCaptureMode = CaptureMode.IMAGE
    private var mMaxVideoDuration = -1L
    private var mMaxVideoSize = -1L
    private var mFlash = FlashMode.OFF
   
    private var mImageCapture: ImageCapture? = null
   
    private var mVideoCapture: VideoCapture? = null
   
    var mPreview: Preview? = null
    
    var mCurrentLifecycle: LifecycleOwner? = null
    private val mCurrentLifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            if (owner === mCurrentLifecycle) {
                clearCurrentLifecycle()
                mPreview!!.removePreviewOutputListener()
            }
        }
    }
 
    private var mNewLifecycle: LifecycleOwner? = null
    private var mZoomLevel = UNITY_ZOOM_SCALE
 
    private var mCropRegion: Rect? = null
    @Nullable
    private var mCameraLensFacing: CameraX.LensFacing? = LensFacing.BACK

   init{
        this.mCameraView = view
        mCameraManager = view.getContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mPreviewConfigBuilder = PreviewConfig.Builder().setTargetName("Preview")
        mImageCaptureConfigBuilder = ImageCaptureConfig.Builder().setTargetName("ImageCapture")
        mVideoCaptureConfigBuilder = VideoCaptureConfig.Builder().setTargetName("VideoCapture")
    }

    /**
     * Rescales view rectangle with dimensions in [-1000, 1000] to a corresponding rectangle in the
     * sensor coordinate frame.
     */
    private fun rescaleViewRectToSensorRect(view: Rect, sensor: Rect): Rect {
        // Scale width and height.
        val newWidth = Math.round(view.width() * sensor.width() / MAX_VIEW_DIMENSION.toFloat())
        val newHeight = Math.round(view.height() * sensor.height() / MAX_VIEW_DIMENSION.toFloat())
        // Scale top/left corner.
        val halfViewDimension = MAX_VIEW_DIMENSION / 2
        val leftOffset = Math.round(
            (view.left + halfViewDimension) * sensor.width() / MAX_VIEW_DIMENSION.toFloat()
        ) + sensor.left
        val topOffset = Math.round(
            (view.top + halfViewDimension) * sensor.height() / MAX_VIEW_DIMENSION.toFloat()
        ) + sensor.top
        // Now, produce the scaled rect.
        val scaled = Rect()
        scaled.left = leftOffset
        scaled.top = topOffset
        scaled.right = scaled.left + newWidth
        scaled.bottom = scaled.top + newHeight
        return scaled
    }

    @RequiresPermission(permission.CAMERA)
    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        mNewLifecycle = lifecycleOwner
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            bindToLifecycleAfterViewMeasured()
        }
    }

    @SuppressLint("RestrictedApi")
    @RequiresPermission(permission.CAMERA)
    fun bindToLifecycleAfterViewMeasured() {
        if (mNewLifecycle == null) {
            return
        }
        clearCurrentLifecycle()
        mCurrentLifecycle = mNewLifecycle
        mNewLifecycle = null
        if (mCurrentLifecycle!!.getLifecycle().getCurrentState() === Lifecycle.State.DESTROYED) {
            mCurrentLifecycle = null
            throw IllegalArgumentException("Cannot bind to lifecycle in a destroyed state.")
        }
        val cameraOrientation: Int
        try {
            val cameraId: String?
            val available = getAvailableCameraLensFacing()
            if (available.isEmpty()) {
                Log.w(TAG, "Unable to bindToLifeCycle since no cameras available")
                mCameraLensFacing = null
            }
            // Ensure the current camera exists, or default to another camera
            if (mCameraLensFacing != null && !available.contains(mCameraLensFacing)) {
                Log.w(TAG, "Camera does not exist with direction " + mCameraLensFacing!!)
                // Default to the first available camera direction
                mCameraLensFacing = available.iterator().next()
                Log.w(TAG, "Defaulting to primary camera with direction " + mCameraLensFacing!!)
            }
            // Do not attempt to create use cases for a null cameraLensFacing. This could occur if
            // the
            // user explicitly sets the LensFacing to null, or if we determined there
            // were no available cameras, which should be logged in the logic above.
            if (mCameraLensFacing == null) {
                return
            }
            cameraId = CameraX.getCameraWithLensFacing(mCameraLensFacing)
            if (cameraId == null) {
                return
            }
            val cameraInfo = CameraX.getCameraInfo(cameraId)
            cameraOrientation = cameraInfo!!.sensorRotationDegrees
        } catch (e: Exception) {
            throw IllegalStateException("Unable to bind to lifecycle.", e)
        }

        // Set the preferred aspect ratio as 4:3 if it is IMAGE only mode. Set the preferred aspect
        // ratio as 16:9 if it is VIDEO or MIXED mode. Then, it will be WYSIWYG when the view finder
        // is
        // in CENTER_INSIDE mode.
        val isDisplayPortrait = getDisplayRotationDegrees() == 0 || getDisplayRotationDegrees() == 180
        if (getCaptureMode() === CaptureMode.IMAGE) {
            mImageCaptureConfigBuilder.setTargetAspectRatio(
                if (isDisplayPortrait) ASPECT_RATIO_3_4 else ASPECT_RATIO_4_3
            )
            mPreviewConfigBuilder.setTargetAspectRatio(
                if (isDisplayPortrait) ASPECT_RATIO_3_4 else ASPECT_RATIO_4_3
            )
        } else {
            mImageCaptureConfigBuilder.setTargetAspectRatio(
                if (isDisplayPortrait) ASPECT_RATIO_9_16 else ASPECT_RATIO_16_9
            )
            mPreviewConfigBuilder.setTargetAspectRatio(
                if (isDisplayPortrait) ASPECT_RATIO_9_16 else ASPECT_RATIO_16_9
            )
        }
        mImageCaptureConfigBuilder.setTargetRotation(getDisplaySurfaceRotation())
        mImageCaptureConfigBuilder.setLensFacing(mCameraLensFacing)
        mImageCapture = ImageCapture(mImageCaptureConfigBuilder.build())
        mVideoCaptureConfigBuilder.setTargetRotation(getDisplaySurfaceRotation())
        mVideoCaptureConfigBuilder.setLensFacing(mCameraLensFacing)
        mVideoCapture = VideoCapture(mVideoCaptureConfigBuilder.build())
        mPreviewConfigBuilder.setLensFacing(mCameraLensFacing)
        val relativeCameraOrientation = getRelativeCameraOrientation(false)
        if (relativeCameraOrientation == 90 || relativeCameraOrientation == 270) {
            mPreviewConfigBuilder.setTargetResolution(
                Size(getMeasuredHeight(), getMeasuredWidth())
            )
        } else {
            mPreviewConfigBuilder.setTargetResolution(
                Size(getMeasuredWidth(), getMeasuredHeight())
            )
        }
        mPreview = Preview(mPreviewConfigBuilder.build())
        mPreview!!.onPreviewOutputUpdateListener = Preview.OnPreviewOutputUpdateListener { output ->
            val needReverse = cameraOrientation != 0 && cameraOrientation != 180
            val textureWidth = if (needReverse)
                output.textureSize.height
            else
                output.textureSize.width
            val textureHeight = if (needReverse)
                output.textureSize.width
            else
                output.textureSize.height
            this@CameraXModule.onPreviewSourceDimensUpdated(
                textureWidth,
                textureHeight
            )
            this@CameraXModule.setSurfaceTexture(output.surfaceTexture)
        }
        if (getCaptureMode() === CaptureMode.IMAGE) {
            CameraX.bindToLifecycle(mCurrentLifecycle, mImageCapture, mPreview)
        } else if (getCaptureMode() === CaptureMode.VIDEO) {
            CameraX.bindToLifecycle(mCurrentLifecycle, mVideoCapture, mPreview)
        } else {
            CameraX.bindToLifecycle(mCurrentLifecycle, mImageCapture, mVideoCapture, mPreview)
        }
        setZoomLevel(mZoomLevel)
        mCurrentLifecycle!!.getLifecycle().addObserver(mCurrentLifecycleObserver)
        // Enable flash setting in ImageCapture after use cases are created and binded.
        setFlash(getFlash())
    }

    fun open() {
        throw UnsupportedOperationException(
            "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead."
        )
    }

    fun close() {
        throw UnsupportedOperationException(
            "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead."
        )
    }

    fun takePicture(listener: OnImageCapturedListener?) {
        if (mImageCapture == null) {
            return
        }
        if (getCaptureMode() === CaptureMode.VIDEO) {
            throw IllegalStateException("Can not take picture under VIDEO capture mode.")
        }
        if (listener == null) {
            throw IllegalArgumentException("OnImageCapturedListener should not be empty")
        }
        mImageCapture!!.takePicture(listener)
    }

    fun takePicture(saveLocation: File, listener: OnImageSavedListener?) {
        if (mImageCapture == null) {
            return
        }
        if (getCaptureMode() === CaptureMode.VIDEO) {
            throw IllegalStateException("Can not take picture under VIDEO capture mode.")
        }
        if (listener == null) {
            throw IllegalArgumentException("OnImageSavedListener should not be empty")
        }
        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = mCameraLensFacing == LensFacing.FRONT
        mImageCapture!!.takePicture(saveLocation, listener, metadata)
    }

    @SuppressLint("RestrictedApi")
    fun startRecording(file: File, listener: OnVideoSavedListener?) {
        if (mVideoCapture == null) {
            return
        }
        if (getCaptureMode() === CaptureMode.IMAGE) {
            throw IllegalStateException("Can not record video under IMAGE capture mode.")
        }
        if (listener == null) {
            throw IllegalArgumentException("OnVideoSavedListener should not be empty")
        }
        mVideoIsRecording.set(true)
        mVideoCapture!!.startRecording(
            file,
            object : OnVideoSavedListener {

                override fun onVideoSaved(savedFile: File) {
                    mVideoIsRecording.set(false)
                    listener.onVideoSaved(savedFile)
                }

                override fun onError(
                    useCaseError: VideoCapture.UseCaseError,
                    message: String,
                    @Nullable cause: Throwable?
                ) {
                    mVideoIsRecording.set(false)
                    Log.e(TAG, message, cause)
                    listener.onError(useCaseError, message, cause)
                }
            })
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        if (mVideoCapture == null) {
            return
        }
        mVideoCapture!!.stopRecording()
    }

    fun isRecording(): Boolean {
        return mVideoIsRecording.get()
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    fun setCameraLensFacing(@Nullable lensFacing: LensFacing) {
        // Setting same lens facing is a no-op, so check for that first
        if (mCameraLensFacing != lensFacing) {
            // If we're not bound to a lifecycle, just update the camera that will be opened when we
            // attach to a lifecycle.
            mCameraLensFacing = lensFacing
            if (mCurrentLifecycle != null) {
                // Re-bind to lifecycle with new camera
                bindToLifecycle(mCurrentLifecycle!!)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @RequiresPermission(permission.CAMERA)
    fun hasCameraWithLensFacing(lensFacing: LensFacing): Boolean {
        val cameraId: String?
        try {
            cameraId = CameraX.getCameraWithLensFacing(lensFacing)
        } catch (e: Exception) {
            throw IllegalStateException("Unable to query lens facing.", e)
        }

        return cameraId != null
    }

    @Nullable
    fun getLensFacing(): LensFacing? {
        return mCameraLensFacing
    }

    fun toggleCamera() {
        // TODO(b/124269166): Rethink how we can handle permissions here.
        @SuppressLint("MissingPermission")
        val availableCameraLensFacing = getAvailableCameraLensFacing()
        if (availableCameraLensFacing.isEmpty()) {
            return
        }
        if (mCameraLensFacing == null) {
            setCameraLensFacing(availableCameraLensFacing.iterator().next())
            return
        }
        if (mCameraLensFacing == LensFacing.BACK && availableCameraLensFacing.contains(LensFacing.FRONT)) {
            setCameraLensFacing(LensFacing.FRONT)
            return
        }
        if (mCameraLensFacing == LensFacing.FRONT && availableCameraLensFacing.contains(LensFacing.BACK)) {
            setCameraLensFacing(LensFacing.BACK)
            return
        }
    }

    fun focus(focus: Rect, metering: Rect) {
        if (mPreview == null) {
            // Nothing to focus on since we don't yet have a preview
            return
        }
        val rescaledFocus: Rect
        val rescaledMetering: Rect
        try {
            val sensorRegion: Rect?
            if (mCropRegion != null) {
                sensorRegion = mCropRegion
            } else {
                sensorRegion = getSensorSize(getActiveCamera())
            }
            rescaledFocus = rescaleViewRectToSensorRect(focus, sensorRegion!!)
            rescaledMetering = rescaleViewRectToSensorRect(metering, sensorRegion!!)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rescale the focus and metering rectangles.", e)
            return
        }

        mPreview!!.focus(rescaledFocus, rescaledMetering)
    }

    fun getZoomLevel(): Float {
        return mZoomLevel
    }

    fun setZoomLevel(zoomLevel: Float) {
        // Set the zoom level in case it is set before binding to a lifecycle
        this.mZoomLevel = zoomLevel
        if (mPreview == null) {
            // Nothing to zoom on yet since we don't have a preview. Defer calculating crop
            // region.
            return
        }
        val sensorSize: Rect?
        try {
            sensorSize = getSensorSize(getActiveCamera())
            if (sensorSize == null) {
                Log.e(TAG, "Failed to get the sensor size.")
                return
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get the sensor size.", e)
            return
        }

        val minZoom = getMinZoomLevel()
        val maxZoom = getMaxZoomLevel()
        if (this.mZoomLevel < minZoom) {
            Log.e(TAG, "Requested zoom level is less than minimum zoom level.")
        }
        if (this.mZoomLevel > maxZoom) {
            Log.e(TAG, "Requested zoom level is greater than maximum zoom level.")
        }
        this.mZoomLevel = Math.max(minZoom, Math.min(maxZoom, this.mZoomLevel))
        val zoomScaleFactor = if (maxZoom == minZoom) minZoom else (this.mZoomLevel - minZoom) / (maxZoom - minZoom)
        val minWidth = Math.round(sensorSize!!.width() / maxZoom)
        val minHeight = Math.round(sensorSize!!.height() / maxZoom)
        val diffWidth = sensorSize!!.width() - minWidth
        val diffHeight = sensorSize!!.height() - minHeight
        val cropWidth = diffWidth * zoomScaleFactor
        val cropHeight = diffHeight * zoomScaleFactor
        val cropRegion = Rect(
            /*left=*/ Math.ceil((cropWidth / 2 - 0.5f).toDouble()).toInt(),
            /*top=*/ Math.ceil((cropHeight / 2 - 0.5f).toDouble()).toInt(),
            /*right=*/ Math.floor((sensorSize!!.width() - cropWidth / 2 + 0.5f).toDouble()).toInt(),
            /*bottom=*/ Math.floor((sensorSize!!.height() - cropHeight / 2 + 0.5f).toDouble()).toInt()
        )
        if (cropRegion.width() < 50 || cropRegion.height() < 50) {
            Log.e(TAG, "Crop region is too small to compute 3A stats, so ignoring further zoom.")
            return
        }
        this.mCropRegion = cropRegion
        mPreview!!.zoom(cropRegion)
    }

    fun getMinZoomLevel(): Float {
        return UNITY_ZOOM_SCALE
    }

    fun getMaxZoomLevel(): Float {
        try {
            val characteristics = mCameraManager.getCameraCharacteristics(getActiveCamera()!!)
            val maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
                ?: return ZOOM_NOT_SUPPORTED
            return if (maxZoom == ZOOM_NOT_SUPPORTED) {
                ZOOM_NOT_SUPPORTED
            } else maxZoom
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get SCALER_AVAILABLE_MAX_DIGITAL_ZOOM.", e)
        }

        return ZOOM_NOT_SUPPORTED
    }

    fun isZoomSupported(): Boolean {
        return getMaxZoomLevel() != ZOOM_NOT_SUPPORTED
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    private fun rebindToLifecycle() {
        mCurrentLifecycle?.let { bindToLifecycle(it) }
    }

    @SuppressLint("RestrictedApi")
    fun getRelativeCameraOrientation(compensateForMirroring: Boolean): Int {
        var rotationDegrees: Int
        try {
            val cameraId = CameraX.getCameraWithLensFacing(getLensFacing())
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

    fun invalidateView() {
        transformPreview()
        updateViewInfo()
    }

    fun clearCurrentLifecycle() {
        if (mCurrentLifecycle != null) {
            // Remove previous use cases
            CameraX.unbind(mImageCapture, mVideoCapture, mPreview)
        }
        mCurrentLifecycle = null
    }

    @Throws(CameraAccessException::class)
    private fun getSensorSize(cameraId: String?): Rect? {
        val characteristics = mCameraManager.getCameraCharacteristics(cameraId!!)
        return characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
    }

    @SuppressLint("RestrictedApi")
    @Throws(CameraInfoUnavailableException::class)
    fun getActiveCamera(): String? {
        return CameraX.getCameraWithLensFacing(mCameraLensFacing)
    }

    @UiThread
    private fun transformPreview() {
        val previewWidth = getPreviewWidth()
        val previewHeight = getPreviewHeight()
        val displayOrientation = getDisplayRotationDegrees()
        val matrix = Matrix()
        // Apply rotation of the display
        val rotation = -displayOrientation
        val px = Math.round(previewWidth / 2.0).toInt()
        val py = Math.round(previewHeight / 2.0).toInt()
        matrix.postRotate(rotation.toFloat(), px.toFloat(), py.toFloat())
        if (displayOrientation == 90 || displayOrientation == 270) {
            // Swap width and height
            val xScale = previewWidth / previewHeight.toFloat()
            val yScale = previewHeight / previewWidth.toFloat()
            matrix.postScale(xScale, yScale, px.toFloat(), py.toFloat())
        }
        setTransform(matrix)
    }

    // Update view related information used in use cases
    @SuppressLint("RestrictedApi")
    private fun updateViewInfo() {
        if (mImageCapture != null) {
            mImageCapture!!.setTargetAspectRatio(Rational(getWidth(), getHeight()))
            mImageCapture!!.setTargetRotation(getDisplaySurfaceRotation())
        }
        if (mVideoCapture != null) {
            mVideoCapture!!.setTargetRotation(getDisplaySurfaceRotation())
        }
    }

    @RequiresPermission(permission.CAMERA)
    private fun getAvailableCameraLensFacing(): LinkedHashSet<LensFacing> {
        // Start with all camera directions
        val available = LinkedHashSet<LensFacing>()
        // If we're bound to a lifecycle, remove unavailable cameras
        if (mCurrentLifecycle != null) {
            if (!hasCameraWithLensFacing(LensFacing.BACK)) {
                available.remove(LensFacing.BACK)
            }
            if (!hasCameraWithLensFacing(LensFacing.FRONT)) {
                available.remove(LensFacing.FRONT)
            }
        }
        return available
    }

    fun getFlash(): FlashMode {
        return mFlash
    }

    fun setFlash(flash: FlashMode) {
        this.mFlash = flash
        if (mImageCapture == null) {
            // Do nothing if there is no imageCapture
            return
        }
        mImageCapture!!.flashMode = flash
    }

    fun enableTorch(torch: Boolean) {
        if (mPreview == null) {
            return
        }
        mPreview!!.enableTorch(torch)
    }

    fun isTorchOn(): Boolean {
        return if (mPreview == null) {
            false
        } else mPreview!!.isTorchOn
    }

    fun getContext(): Context {
        return mCameraView.getContext()
    }

    fun getWidth(): Int {
        return mCameraView.getWidth()
    }

    fun getHeight(): Int {
        return mCameraView.getHeight()
    }

    fun getDisplayRotationDegrees(): Int {
        return 0
    }

    protected fun getDisplaySurfaceRotation(): Int {
       // return mCameraView.getDisplaySurfaceRotation()
        return 0
    }

    /**
     *
     */
    fun setSurfaceTexture(st: SurfaceTexture) {
        mCameraView.setSurfaceTexture(st)
    }

    private fun getPreviewWidth(): Int {
        return mCameraView.width
    }

    private fun getPreviewHeight(): Int {
        return mCameraView.height
    }

    private fun getMeasuredWidth(): Int {
        return mCameraView.measuredWidth
    }

    private fun getMeasuredHeight(): Int {
        return mCameraView.measuredHeight
    }

    fun setTransform(matrix: Matrix) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mCameraView.post { setTransform(matrix) }
        } else {
            mCameraView.setTransform(matrix)
        }
    }

    /**
     * Notify the view that the source dimensions have changed.
     *
     *
     * This will allow the view to layout the preview to display the correct aspect ratio.
     *
     * @param width  width of camera source buffers.
     * @param height height of camera source buffers.
     */
    fun onPreviewSourceDimensUpdated(width: Int, height: Int) {
        mCameraView.onPreviewSourceDimensUpdated(width, height)
    }

    fun getCaptureMode(): CaptureMode {
        return mCaptureMode
    }

    fun setCaptureMode(captureMode: CaptureMode) {
        this.mCaptureMode = captureMode
        rebindToLifecycle()
    }

    fun getMaxVideoDuration(): Long {
        return mMaxVideoDuration
    }

    fun setMaxVideoDuration(duration: Long) {
        mMaxVideoDuration = duration
    }

    fun getMaxVideoSize(): Long {
        return mMaxVideoSize
    }

    fun setMaxVideoSize(size: Long) {
        mMaxVideoSize = size
    }

    fun isPaused(): Boolean {
        return false
    }

    enum class CaptureMode private constructor(internal val id: Int) {
        /** A mode where image capture is enabled.  */
        IMAGE(0),
        /** A mode where video capture is enabled.  */
        VIDEO(1),
        /**
         * A mode where both image capture and video capture are simultaneously enabled. Note that
         * this mode may not be available on every device.
         */
        MIXED(2);


        companion object {
            internal fun fromId(id: Int): CaptureMode {
                for (f in values()) {
                    if (f.id == id) {
                        return f
                    }
                }
                throw IllegalArgumentException()
            }
        }
    }
}