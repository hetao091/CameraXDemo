package com.github.cameraxdemo.utiils

import android.content.Context
import android.util.DisplayMetrics


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-06 19:39
 * desc     ：
 * revise   :
 * =====================================
 */
class ScreenSizeUtils {
    companion object {
        /**
         * @param context 上下文
         * @return 屏幕高度值
         */
        fun getScreenHeight(context: Context): Int {
            return getPoint(context).heightPixels
        }

        /**
         * @param context 上下文
         * @return 屏幕宽度值
         */
        fun getScreenWidth(context: Context): Int {
            return getPoint(context).widthPixels
        }

        /**
         * @param context 上下文
         * @return DisplayMetrics
         */
        private fun getPoint(context: Context): DisplayMetrics {

            return context.resources.displayMetrics
        }
    }

}