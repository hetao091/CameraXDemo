package com.github.cameraxdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.cameraxdemo.R


/**
 * =====================================
 * <ul>
 *     这里多媒体处理单独抽出来便于复用
 * </ul>
 *
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-06-28 17:13
 * desc     ：图片视频编辑处理
 * revise   :
 * =====================================
 */
class MediaEditorFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media_editor, container, false)
    }

}