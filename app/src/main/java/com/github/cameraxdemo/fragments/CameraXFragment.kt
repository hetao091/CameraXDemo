package com.github.cameraxdemo.fragments

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.github.cameraxdemo.R
import com.github.cameraxdemo.singleton.CameraHolder
import com.github.cameraxdemo.utiils.CameraXPreviewBuilder
import com.github.cameraxdemo.views.CameraUIContainerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-05-27 12:51
 * desc     ：具体实现
 * revise   :
 * =====================================
 */
class CameraXFragment : Fragment(), CoroutineScope {

    // 设置协程用来操作图片视频处理
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    // 默认实现后置摄像头
    private var lensFacing = CameraX.LensFacing.BACK
    // 初始化
    private var imageCapture: ImageCapture? = null
    //
    private lateinit var containerView: ConstraintLayout
    private lateinit var viewFinder: TextureView
    private lateinit var mCameraUIContainerView: CameraUIContainerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 与Activity分离
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camerax, container, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerView = view as ConstraintLayout
        viewFinder = view.findViewById(R.id.textureView)
        mCameraUIContainerView = view.findViewById(R.id.cameraUIContainerView)
        mCameraUIContainerView.getViewParent(containerView)
        mCameraUIContainerView.returnButtonCallback {
            Log.v("mCameraUIContainerView ", "returnButtonCallback")
            // 退出界面
            activity!!.finish()
        }
        viewFinder.post {
            //            updateCameraUi()
//            startCamera()

            mCameraUIContainerView.startPreview(this as LifecycleOwner, viewFinder)
        }

    }

    /**
     *
     */
    private fun updateCameraUi() {

//        containerView.findViewById<ConstraintLayout>(R.id.camera_view)?.let {
//            containerView.removeView(it)
//        }
//        // 追加布局
//        val controls = View.inflate(requireContext(), R.layout.camera_view, containerView)

    }

    private fun startCamera() {

        // 确保没有绑定到CameraX的其他用例
        CameraX.unbindAll()
        // 设置预览分辨率为当前屏幕分辨率
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)

        //预览
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            // 设置图像比例
            setTargetAspectRatio(screenAspectRatio)
            // 设置取景的目标分辨率
            setTargetResolution(screenSize)
            setTargetRotation(viewFinder.display.rotation)

        }.build()

        //  预览设置
        val preview = CameraXPreviewBuilder.build(previewConfig, viewFinder)
        //  图像采集设置
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(lensFacing)
            // 或者使用MAX_QUALITY 获取更高质量
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        imageCapture = ImageCapture(imageCaptureConfig)

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            // HandlerThread 进行控制渲染
            val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            // 分析最后的图片
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        val imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
            analyzer = LuminosityAnalyzer().apply {
                onFrameAnalyzed { luma ->
                    // 拿到处理结果
                }
            }
        }

        // 绑定到视图的生命周期
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageCapture, imageAnalyzer)
    }


    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<(luma: Double) -> Unit>()
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * 亮度计算回调函数
         */
        fun onFrameAnalyzed(listener: (luma: Double) -> Unit) = listeners.add(listener)

        /**
         * 扩展函数 提取缓冲区的字节数组
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            // 重置 buffer
            rewind()
            val data = ByteArray(remaining())
            // 赋值
            get(data)
            return data
        }

        /**
         * 分析图像并返回结果。
         *
         * 调用者负责确保该分析方法能够快速执行
         * 足以防止图像采集管道发生故障。否则,新可用
         * 图像不会被获取和分析.
         *
         * <p>在该方法返回后，传递给该方法的图像无效。调用者
         *不应存储对该映像的外部引用，因为这些引用将成为
         *无效.
         *
         * @param image  自动关闭
         * @return 返回结果
         */
        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            // 监听判断
            if (listeners.isEmpty()) return

            // 跟踪分析过的帧
            frameTimestamps.push(System.currentTimeMillis())

            // 计算FPS
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            framesPerSecond = 1.0 / ((frameTimestamps.peekFirst() -
                    frameTimestamps.peekLast()) / frameTimestamps.size.toDouble()) * 1000.0

            // 处理频率不超过每秒一次
            if (frameTimestamps.first - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                // 这里返回的是YUV格式
                val buffer = image.planes[0].buffer

                // 拿到回调中的图像buffer
                val data = buffer.toByteArray()

                // 将数据转换为像素值数组
                val pixels = data.map { it.toInt() and 0xFF }

                // 计算图像的平均亮度
                val luma = pixels.average()

                listeners.forEach {
                    it(luma)
                }

                lastAnalyzedTimestamp = frameTimestamps.first
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 解除绑定
        CameraHolder.getInstance.releaseCamera()
    }

}