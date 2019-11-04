package com.github.cameraxdemo.views

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-08 16:28
 * desc     ：拍照录像按钮
 * revise   :
 * =====================================
 */

private const val progressColor = 0xEE16AE16.toInt()      //进度条颜色
private const val outsideColor = 0xEEDCDCDC.toInt()     //外圆背景色
private const val insideColor = 0xFFFFFFFF.toInt()      //内圆背景色
private const val maxViedoDuration = 10 * 1500  //最大时长
private const val minViedoDuration = 1500       //最小时长

/**
 * 按钮状态
 */
enum class State {
    STATE_IDLE,             // 空闲
    STATE_PRESS,            // 点击
    STATE_LONG_PRESS,        // 长按
    STATE_RECORD_OVER
}

class CameraCaptureButton : View {

    private var strokeWidth: Float = 0.0f        //进度条宽度
    private var outsideScaleSize: Int = 0       //长按外圆半径变大的Size
    private var insideScaleSize: Int = 0     //长安内圆缩小的Size

    //中心坐标
    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f

    private var btRadius: Float = 0.0f      //按钮半径
    private var btOutsideRadius = 0.0f    //外圆半径
    private var btInsideRadius = 0.0f     //内圆半径
    private var btRealSize = 0
    // 进度
    private var progress = 0.0f
    private var currentRecordTime = 0     //当前录制的时间
    //
    private lateinit var mRectF: RectF
    private var mPaint = Paint()
    //
    private var state: State = State.STATE_IDLE
    private lateinit var mLongPressRunnable: LongPressRunnable

    // 定义回调
    private lateinit var action: (State) -> Unit

    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Default + job

    constructor(context: Context) : super(context)
    constructor(context: Context, btSize: Int) : super(context) {

        btRadius = (btSize / 2).toFloat()
        btOutsideRadius = btRadius
        btInsideRadius = (btOutsideRadius * 0.75).toFloat()
        strokeWidth = (btSize / 15).toFloat()
        outsideScaleSize = btSize / 4
        insideScaleSize = btSize / 8

        btRealSize = btSize + (outsideScaleSize * 2)
        //
        centerX = (btRealSize / 2).toFloat()
        centerY = (btRealSize / 2).toFloat()
        mPaint.isAntiAlias = true
        //
        mRectF = RectF(
            centerX - (btRadius + outsideScaleSize - strokeWidth / 2),
            centerY - (btRadius + outsideScaleSize - strokeWidth / 2),
            centerX + (btRadius + outsideScaleSize - strokeWidth / 2),
            centerY + (btRadius + outsideScaleSize - strokeWidth / 2)
        )

        mLongPressRunnable = LongPressRunnable()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.v("btRealSize----", "$btRealSize")
        setMeasuredDimension(btRealSize, btRealSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 默认填充
        mPaint.style = Paint.Style.FILL
        mPaint.color = outsideColor
        // 绘制外圆
        canvas.drawCircle(centerX, centerY, btOutsideRadius, mPaint)
        // 绘制内圆
        mPaint.color = insideColor
        canvas.drawCircle(centerX, centerY, btInsideRadius, mPaint)

        // 录制状态下绘制
        if (state == State.STATE_LONG_PRESS) {
            //
            mPaint.color = progressColor
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
            canvas.drawArc(mRectF, -90f, progress, false, mPaint)
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("onTouchEvent", "MotionEvent.ACTION_DOWN ")
                // 点击事件
                state = State.STATE_PRESS
                // 长按事件
                postDelayed(mLongPressRunnable, 500)
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("", "")
            }

            MotionEvent.ACTION_UP -> {
                // 重置状态
                if (state == State.STATE_PRESS && ::action.isInitialized) {
                    action(state)
                }
                resetCaptureState()
            }
        }
        return true
    }

    inner class LongPressRunnable : Runnable {
        override fun run() {
            // 长按状态
            state = State.STATE_LONG_PRESS
            if (::action.isInitialized) {
                action(state)
            }
            //
            setCaptureAnimation(
                btOutsideRadius,
                btOutsideRadius + outsideScaleSize,
                btInsideRadius,
                btInsideRadius - insideScaleSize
            )
        }

    }


    /**
     * 重置按钮状态
     */
    private fun resetCaptureState() {
        removeCallbacks(mLongPressRunnable)
        //
        if (state == State.STATE_LONG_PRESS) {
            state = State.STATE_RECORD_OVER
            if (::action.isInitialized) {
                action(state)
            }
            setCaptureAnimation(
                btOutsideRadius,
                btRadius,
                btInsideRadius,
                (btRadius * 0.75).toFloat()
            )
//            job.cancel()
            updateProgress().cancel()
            progress = 0f
            invalidate()
        } else {
            state = State.STATE_IDLE
        }

    }

    /**
     *  动画管理
     */
    private fun setCaptureAnimation(
        outsideStart: Float,
        outsideEnd: Float,
        insideStart: Float,
        insideEnd: Float
    ) {
        // 外圆半径
        val outValueAnimator = ValueAnimator.ofFloat(outsideStart, outsideEnd)
        val insideValueAnimator = ValueAnimator.ofFloat(insideStart, insideEnd)
        outValueAnimator.addUpdateListener {
            btOutsideRadius = it.animatedValue as Float
            invalidate()
        }

        insideValueAnimator.addUpdateListener {
            btInsideRadius = it.animatedValue as Float
            invalidate()
        }

        val set = AnimatorSet()

        set.run {
            playTogether(outValueAnimator, insideValueAnimator)
            set.duration = 100
            start()

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    //  如果是录制状态更新进度
                    if (state == State.STATE_LONG_PRESS) {
                        updateProgress()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
        }
        // 如果考虑到异常情况动画结束函数不会被回调可以利用Handler
//        Handler().postDelayed({
//            // 动画结束
//        }, set.duration)
    }

    // 更新进度
    private fun updateProgress() =
        GlobalScope.launch(Dispatchers.IO) {
            // 可取消的计算
            repeat(150) {
                progress = (it / 149.0f) * 360f
                withContext(Dispatchers.Main) {
                    // 利用协程挂起
                    delay(100L)
                    Log.v("updateProgress", "$progress")
                }
            }
        }


    /**
     * 对外提供当前按钮 状态
     */
    fun transferTheCaptureButtonState(action: (State) -> Unit) {
        this.action = action
    }
}