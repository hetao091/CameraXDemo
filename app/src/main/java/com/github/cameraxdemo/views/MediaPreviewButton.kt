package com.github.cameraxdemo.views

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.annotation.StringDef


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-07-02 13:48
 * desc     ：
 * revise   :
 * =====================================
 */


class MediaPreviewButton(
    context: Context?, type: @MediaPreviewButton.Type String, size: Int
) : View(context) {

    companion object {
         const val CANCEL = "cancel"
         const val EDITOR = "editor"
         const val CONFIRM = "confirm"
    }

    @Target(AnnotationTarget.TYPE)
    @StringDef(CANCEL, EDITOR, CONFIRM)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class Type

    /** */
    private var btSize = 0
    private var btRadius = 0f
    /** */
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    private var strokeWidth: Float = 0f

    // 绘制半圆时用到的正方形半径
    private var innerSquareRadius: Float = 0f

    private var rectF: RectF
    private var mPaint = Paint()
    private var mPath = Path()

    private var mType = type

    init {
        btSize = size
        btRadius = size / 2.0f
        centerX = size / 2.0f
        centerY = size / 2.0f
        strokeWidth = size / 50f

        innerSquareRadius = size / 12f
        rectF =
            RectF(centerX, centerY - innerSquareRadius, centerX + innerSquareRadius * 2, centerY + innerSquareRadius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(btSize, btSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 取消
        if (mType == CANCEL) {
            // 绘制背景圆
            mPaint.isAntiAlias = true
            mPaint.color = 0xEEDCDCDC.toInt()
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(centerX, centerY, btRadius, mPaint)
            // 绘制箭头
            mPaint.color = Color.BLACK
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
            // 绘制下横线  Y终点点为正方形下边长一半的位置
            mPath.moveTo(centerX - innerSquareRadius / 7, centerY + innerSquareRadius)
            mPath.lineTo(centerX + innerSquareRadius, centerY + innerSquareRadius)
            // 绘制半圆 默认连接path最后一个点与圆弧起点
            mPath.arcTo(rectF, 90f, -180f)
            // 连接到上横线即以圆弧终点为起点
            mPath.lineTo(centerX - innerSquareRadius, centerY - innerSquareRadius)
            canvas.drawPath(mPath, mPaint)
            // 绘制实心等边三角形
            mPaint.style = Paint.Style.FILL
            mPath.reset()
            //
            mPath.moveTo(centerX - innerSquareRadius, (centerY - innerSquareRadius * 1.5).toFloat())
            mPath.lineTo(centerX - innerSquareRadius, (centerY - innerSquareRadius / 2.3).toFloat())
            mPath.lineTo((centerX - innerSquareRadius * 1.6).toFloat(), centerY - innerSquareRadius)
            mPath.close()
            canvas.drawPath(mPath, mPaint)
        }
        // 编辑
        if (mType == EDITOR) {
            // 绘制背景圆
            mPaint.isAntiAlias = true
            mPaint.color = 0xEEDCDCDC.toInt()
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(centerX, centerY, btRadius, mPaint)
            //
            mPaint.color = Color.BLACK
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
            // 绘制三横线
            mPath.moveTo(centerX - innerSquareRadius, centerY)
            mPath.lineTo(centerX + innerSquareRadius, centerY)

            canvas.drawPath(mPath, mPaint)
        }
        // 确认
        if (mType == CONFIRM) {
            // 绘制白色背景
            mPaint.isAntiAlias = true
            mPaint.color = 0xFFFFFFFF.toInt()
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(centerX, centerY, btRadius, mPaint)
            // 绘制对号
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.STROKE
            mPaint.color = 0xFF00CC00.toInt()
            mPaint.strokeWidth = strokeWidth

            mPath.moveTo(centerX - btSize / 6f, centerY)
            mPath.lineTo(centerX - btSize / 21.2f, centerY + btSize / 7.7f)
            mPath.lineTo(centerX + btSize / 4.0f, centerY - btSize / 8.5f)
            mPath.lineTo(centerX - btSize / 21.2f, centerY + btSize / 9.4f)
            mPath.close()
            canvas.drawPath(mPath, mPaint)
        }
    }
}