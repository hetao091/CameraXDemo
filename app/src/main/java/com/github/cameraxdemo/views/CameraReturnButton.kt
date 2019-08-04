package com.github.cameraxdemo.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-08 15:09
 * desc     ：camera返回按钮
 * revise   :
 * =====================================
 */
class CameraReturnButton(mContext: Context, private val btSize: Int) : View(mContext) {


    // 中心坐标
    private var centerX = (btSize / 2).toFloat()
    private var centerY = (btSize / 2).toFloat()
    //
    private var strokeWidth = btSize / 15

    private var mPaint = Paint()
    private var mPath = Path()

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = strokeWidth.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(btSize, (btSize / 2))
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //
        mPath.moveTo(strokeWidth.toFloat(), (strokeWidth / 2).toFloat())
        mPath.lineTo(centerX, centerY - (strokeWidth / 2))
        mPath.lineTo((btSize - strokeWidth).toFloat(), (strokeWidth / 2).toFloat())
        canvas.drawPath(mPath, mPaint)

    }
}