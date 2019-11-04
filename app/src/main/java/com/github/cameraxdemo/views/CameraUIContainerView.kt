package com.github.cameraxdemo.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.github.cameraxdemo.singleton.TakePictureCallBack
import com.github.cameraxdemo.state.Switch
import com.github.cameraxdemo.utiils.ScreenSizeUtils


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-06 20:23
 * desc     ：相机上层所有UI
 * revise   :
 * =====================================
 */


class CameraUIContainerView : FrameLayout {
    companion object {
        private const val ANIMATION_FAST_MILLIS = 50L
        private const val ANIMATION_SLOW_MILLIS = 100L
    }

    private var mContext: Context
    private lateinit var mFocusView: FocusView
    private lateinit var mCameraCaptureLayout: CameraCaptureLayout

    private lateinit var mSwitch: Switch
    private var layoutWidth = 0
    private lateinit var parentView: View
    private lateinit var imageView: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        layoutWidth = ScreenSizeUtils.getScreenWidth(mContext)
        initView()
    }

    /**
     *
     */
    private fun initView() {
        setWillNotDraw(false)
        val rootView = LayoutInflater.from(mContext)
            .inflate(com.github.cameraxdemo.R.layout.camera_ui_view, this)
        mFocusView = rootView.findViewById(com.github.cameraxdemo.R.id.focusView)
        mCameraCaptureLayout =
            rootView.findViewById(com.github.cameraxdemo.R.id.cameraCaptureLayout)
        mSwitch = Switch()

        // 照片预览
        imageView = ImageView(mContext)
        val mImageViewLayoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        imageView.layoutParams = mImageViewLayoutParams
        this.addView(imageView, 0)


        // 回调
        mCameraCaptureLayout.captureButtonStateCallBack {

            if (it == State.STATE_PRESS) {
                if (::parentView.isInitialized) {
                    // 闪屏
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        parentView.postDelayed({
                            parentView.foreground = ColorDrawable(Color.WHITE)
                            parentView.postDelayed(
                                { parentView.foreground = null },
                                ANIMATION_FAST_MILLIS
                            )
                        }, ANIMATION_SLOW_MILLIS)

                    }
                }
                //  拍照
                mSwitch.capture {
                    object : TakePictureCallBack {
                        override fun success(mediaFilePath: String) {
                            // 显示预览照片
                            Glide.with(mContext).load(mediaFilePath).into(imageView)
                            mCameraCaptureLayout.setMediaVisible()
                        }

                        override fun failure(errorString: String) {
                            // 采集照片失败

                        }

                    }
                }
            }
            if (it == State.STATE_LONG_PRESS) {
                // 录制视频
                mSwitch.startRecord()
            }
            if (it == State.STATE_RECORD_OVER) {
                // 结束录制
                mSwitch.stopRecord()
                // 预览
            }
        }

        // 预览相关响应
        mCameraCaptureLayout.mediaPreviewCancelAction {
            //  隐藏图片
            imageView.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("ACTION_DOWN ", "$event.x-$event.y")
                // 单指触控 显示对焦框
                if (event.pointerCount == 1) {
                    handleFocusViewState(event.x, event.y)
                }
                // 双指缩放
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // 双指缩放
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("", "")
            }

            MotionEvent.ACTION_UP -> {
                //
                mSwitch.focus(event.x, event.y, action = {
                    // 获取对焦结果  隐藏对焦框
                    mFocusView.visibility = View.GONE
                })
            }
        }
        return true

    }


    /**
     *  处理对焦框边界以及显示
     */
    private fun handleFocusViewState(x: Float, y: Float): Boolean {
        Log.d("handleState", "$x-$y")
        //  触控边界处理
        //  触摸事件在底部栏下方
        //  不处理
        var mTouchX = x
        var mTouchY = y
        // 可见
        mFocusView.visibility = View.VISIBLE
        // 优先响应触摸到底部栏的处理 直接返回不响应触摸操作
        if (mTouchY > mCameraCaptureLayout.top) {
            return false
        }

        if (mTouchX < mFocusView.width / 2) {
            mTouchX = (mFocusView.width / 2).toFloat()
        }

        if (mTouchX > layoutWidth - (mFocusView.width / 2)) {
            mTouchX = (layoutWidth - (mFocusView.width / 2)).toFloat()
        }
        if (mTouchY < mFocusView.height / 2) {
            mTouchY = (mFocusView.height / 2).toFloat()
        }
        if (mTouchY > mCameraCaptureLayout.top - mFocusView.height / 2) {
            mTouchY = (mCameraCaptureLayout.top - mFocusView.height / 2).toFloat()
        }

        // 设置坐标
        mFocusView.x = mTouchX - mFocusView.width / 2
        mFocusView.y = mTouchY - mFocusView.height / 2
        Log.d("handleFocusViewState ", "$mTouchX-$mTouchY")
        // 缩放动画
        val scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1f, 0.6f)
        val scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1f, 0.6f)
        // 改变透明度
        val alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleX).with(scaleY).before(alpha)
        animatorSet.duration = 300
        animatorSet.start()
        //
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.v("onDetachedFromWindow", "onDetachedFromWindow")
        mSwitch.releaseCamera()
    }

    /********** 对外提供方法 ***************/
    fun startPreview(lifecycleOwner: LifecycleOwner, viewFinder: TextureView) {
        mSwitch.startCamera(lifecycleOwner, viewFinder)
    }

    fun returnButtonCallback(UICallBack: () -> Unit) {
        mCameraCaptureLayout.returnButtonOnClickListenerCallBack {
            UICallBack()
        }
    }

    fun getViewParent(view: View) {
        parentView = view
    }
}