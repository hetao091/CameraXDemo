package com.github.cameraxdemo.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.github.cameraxdemo.utiils.ScreenSizeUtils


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-08 15:07
 * desc     ：拍照录制等控件集成
 * revise   :
 * =====================================
 */
class CameraCaptureLayout : FrameLayout {


    private lateinit var mCameraReturnButton: CameraReturnButton
    private lateinit var mCameraCaptureButton: CameraCaptureButton
    private var mCaptureBtSize: Int = 0
    private var mCaptureLayoutWidth: Int = 0
    private var mCaptureLayoutHeight: Int = 0
    private var mContext: Context
    private lateinit var mBtCancel: MediaPreviewButton
    private lateinit var mBtEditor: MediaPreviewButton
    private lateinit var mBtConfirm: MediaPreviewButton

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        mCaptureLayoutWidth = ScreenSizeUtils.getScreenWidth(context)
        // 以1080 为例  按钮宽高216   放大后324  缩小112  差108
        mCaptureBtSize = (mCaptureLayoutWidth / 5)

        //  考虑到按住录制按钮放大
        mCaptureLayoutHeight = (mCaptureBtSize + (mCaptureBtSize / 4) * 2 + 216)
        initView()
    }

    private fun initView() {
        setWillNotDraw(false)
        // 返回按钮
        mCameraReturnButton = CameraReturnButton(mContext, mCaptureBtSize / 2)
        val mCameraReturnButtonLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mCameraReturnButtonLayoutParams.gravity = Gravity.CENTER_VERTICAL
        mCameraReturnButtonLayoutParams.setMargins(mCaptureLayoutWidth / 6, 0, 0, 0)
        mCameraReturnButton.layoutParams = mCameraReturnButtonLayoutParams
        this.addView(mCameraReturnButton)
        // 拍照
        mCameraCaptureButton = CameraCaptureButton(mContext, mCaptureBtSize)
        val mmCameraCaptureButtonLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mmCameraCaptureButtonLayoutParams.gravity = Gravity.CENTER
        mCameraCaptureButton.layoutParams = mmCameraCaptureButtonLayoutParams
        this.addView(mCameraCaptureButton)

        /******************* *************************/
        //
        mBtCancel = MediaPreviewButton(mContext, MediaPreviewButton.CANCEL, mCaptureBtSize)
        val mBtCancelParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mBtCancelParam.gravity = Gravity.CENTER_VERTICAL
        mBtCancelParam.setMargins((mCaptureLayoutWidth / 6 - mCaptureBtSize / 2), 0, 0, 0)
        mBtCancel.layoutParams = mBtCancelParam
        this.addView(mBtCancel)

        mBtEditor = MediaPreviewButton(mContext, MediaPreviewButton.EDITOR, mCaptureBtSize)
        val mBtEditorParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mBtEditorParam.gravity = Gravity.CENTER
        mBtEditorParam.setMargins(0, 0, 0, 0)
        mBtEditor.layoutParams = mBtEditorParam
        this.addView(mBtEditor)

        mBtConfirm = MediaPreviewButton(mContext, MediaPreviewButton.CONFIRM, mCaptureBtSize)
        val mBtConfirmParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mBtConfirmParam.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        mBtConfirmParam.setMargins(0, 0, (mCaptureLayoutWidth / 6 - mCaptureBtSize / 2), 0)
        mBtConfirm.layoutParams = mBtConfirmParam
        this.addView(mBtConfirm)
        // 默认隐藏
        setMediaButtonGone()
    }

    /**
     *
     */
    private fun setMediaButtonGone() {
        mBtCancel.visibility = View.GONE
        mBtEditor.visibility = View.GONE
        mBtConfirm.visibility = View.GONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mCaptureLayoutWidth, mCaptureLayoutHeight)
    }


    /*************** 对外提供方法 ************************/

    /**
     * 返回按钮点击事件回调
     */
    fun returnButtonOnClickListenerCallBack(callback: () -> Unit) {

        mCameraReturnButton.setOnClickListener {
            Log.v("mCameraReturnButton ", "setOnClickListener")
            callback()
        }
    }

    /**
     *  拍照按钮状态回调
     */
    fun captureButtonStateCallBack(callback: (State) -> Unit) {
        Log.v("captureButtonState", "returnButtonCallback")
        mCameraCaptureButton.transferTheCaptureButtonState {
            callback(it)
        }
    }

    fun setMediaVisible() {
        mCameraReturnButton.visibility = View.GONE
        mCameraCaptureButton.visibility = View.GONE
        mBtCancel.visibility = View.VISIBLE
        mBtEditor.visibility = View.VISIBLE
        mBtConfirm.visibility = View.VISIBLE
        mBtCancel.isEnabled = false
        mBtEditor.isEnabled = false
        mBtConfirm.isEnabled = false
        // 添加X轴平移动画
        val animatorCancel =
            ObjectAnimator.ofFloat(mBtCancel, "translationX", (mCaptureLayoutWidth /6 ).toFloat(), 0f)
        val animatorConfirm =
            ObjectAnimator.ofFloat(mBtConfirm, "translationX", -(mCaptureLayoutWidth / 6).toFloat(), 0f)

        val set = AnimatorSet()
        set.playTogether(animatorCancel, animatorConfirm)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mBtCancel.isEnabled = true
                mBtEditor.isEnabled = true
                mBtConfirm.isEnabled = true
            }
        })
        set.duration = 200
        set.start()
    }


}