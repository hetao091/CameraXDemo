package com.github.cameraxdemo.model


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-07-01 10:39
 * desc     ：
 * revise   :
 * =====================================
 */
data class BaseResponse<out T>(val code: Int, val msg: String, val data: T)