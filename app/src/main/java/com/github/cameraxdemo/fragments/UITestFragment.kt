package com.github.cameraxdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.cameraxdemo.R


/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-06 19:33
 * desc     ：
 * revise   :
 * =====================================
 */
class UITestFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.camera_view, container, false)
    }
}