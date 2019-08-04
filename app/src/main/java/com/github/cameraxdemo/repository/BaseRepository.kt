package com.github.cameraxdemo.repository

import com.github.cameraxdemo.model.BaseResponse


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-07-01 10:37
 * desc     ：
 * revise   :
 * =====================================
 */
open class BaseRepository {

    /**
     *  网络请求回调
     */
    suspend fun <T : Any> apiCall(call: suspend () -> BaseResponse<T>): BaseResponse<T> {
        return call.invoke()
    }

}