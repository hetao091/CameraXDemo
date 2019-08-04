package com.github.cameraxdemo.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.github.cameraxdemo.utiils.ScreenSizeUtils


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-06 17:37
 * desc     ：相机对焦框
 * revise   :
 * =====================================
 */
class FocusView : View {

    // 定义对焦框边长
    private var size: Int = 0

    // 画笔
    private var mPaint: Paint = Paint()
    // 画笔宽度
    private val mPaintStrokeWidth: Float = 4.0f
    // 矩形框位移宽度
    private val mHalfOfPaintStrokeWidth = mPaintStrokeWidth / 2

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        size = ScreenSizeUtils.getScreenWidth(context!!) / 3
        // 设置画笔无锯齿
        mPaint.isAntiAlias = true
        // 防抖动
        mPaint.isDither = true
        // 画笔颜色
        mPaint.color = 0xEE16AE16.toInt()
        // 画笔粗细大小 单位为像素
        mPaint.strokeWidth = mPaintStrokeWidth
        // 描边绘制
        mPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 设置对焦框宽高
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制正方形
        canvas.drawRect(
            mHalfOfPaintStrokeWidth,
            mHalfOfPaintStrokeWidth,
            size - mHalfOfPaintStrokeWidth,
            size - mHalfOfPaintStrokeWidth,
            mPaint
        )
        // 绘制左短线
        canvas.drawLine(
            mHalfOfPaintStrokeWidth,
            (height / 2).toFloat(),
            (width / 10).toFloat(),
            (height / 2).toFloat(),
            mPaint
        )
        // 上短线
        canvas.drawLine(
            (width / 2).toFloat(),
            mHalfOfPaintStrokeWidth,
            (width / 2).toFloat(),
            (height / 10).toFloat(),
            mPaint
        )
        //  右短线
        canvas.drawLine(
            (width - width / 10).toFloat(),
            (height / 2).toFloat(),
            width - mHalfOfPaintStrokeWidth,
            (height / 2).toFloat(),
            mPaint
        )
        //  下短线
        canvas.drawLine(
            (width / 2).toFloat(),
            (height - height / 10).toFloat(),
            (width / 2).toFloat(),
            (height - mHalfOfPaintStrokeWidth),
            mPaint
        )


    }
}