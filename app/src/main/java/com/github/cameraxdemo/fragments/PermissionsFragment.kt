
package com.github.cameraxdemo.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.github.cameraxdemo.R

/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-05-23 18:51
 * desc     ：权限申请Fragment
 * revise   :
 * =====================================
 */


// 回调值
private const val PERMISSIONS_REQUEST_CODE = 10
//  需要申请的权限列表
private val PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

class PermissionsFragment : Fragment() {

    // 设置导航前弹出的目标
    private val navOptions = NavOptions.Builder().setPopUpTo(R.id.permissionsFragment, true).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            // 申请权限
            requestPermissions(
                PERMISSIONS_REQUIRED,
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            // 已获得权限后是直接跳转至拍照
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(
                        R.id.action_permissions_to_camerax, null,
                            navOptions)
        }

    }

    /**
     * 权限申请
     */
    private fun allPermissionsGranted(): Boolean {
        for (permission in PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "权限申请通过", Toast.LENGTH_LONG).show()
                //
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                        .navigate(
                            R.id.action_permissions_to_camerax, null,
                                navOptions)
            } else {
                Toast.makeText(context, "权限申请失败", Toast.LENGTH_LONG).show()
            }
        }
    }
}
