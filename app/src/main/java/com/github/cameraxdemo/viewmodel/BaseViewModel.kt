package com.github.cameraxdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-26 14:59
 * desc     ：基类
 * revise   :
 * =====================================
 */
abstract class BaseViewModel : ViewModel() {

//    protected val scope = CoroutineScope(
//        Job() + Dispatchers.Main
//    )
//
//    override fun onCleared() {
//        scope.cancel()
//    }

    /** */
    val mException: MutableLiveData<Exception> = MutableLiveData()

    /** */
    val mMediaFilePath: MutableLiveData<String> = MutableLiveData()


    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        launchOnUI {

        }
    }
}