package com.github.cameraxdemo.application

import android.app.Application
import android.content.Context


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-21 18:19
 * desc     ：
 * revise   :
 * =====================================
 */
class BaseApplication : Application() {
    init {
        getInstance = this
    }

    companion object {
        private var getInstance: BaseApplication? = null
        fun getContext(): Context {
            return getInstance!!.applicationContext
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }




}