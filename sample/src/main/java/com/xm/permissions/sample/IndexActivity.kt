/*
 * *************************************************************************
 * Copyright (c) 2020
 * Author：HuaMing Lin
 * Email：linhuaming0603@outlook.com
 * Blog：https://blog.csdn.net/h461415832
 * *************************************************************************
 */

package com.xm.permissions.sample

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.xm.permissions.OnRequestPermissionsCallback
import com.xm.permissions.XmPermissions
import java.util.*

/**
 * 示例代码
 */
class IndexActivity : AppCompatActivity(), OnRequestPermissionsCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        XmPermissions.newInstance(this).setOnRequestPermissionsCallback(this)
                .requestPermissions(getString(R.string.InvalidPermissionText), Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)
//                .requestAllPermissions()
    }

    override fun onGranted() {
        Toast.makeText(this, R.string.userGrantedAllPermission, Toast.LENGTH_LONG).show()
    }

    override fun onDenied(deniedPermissions: Array<String>) {
        Toast.makeText(this, R.string.userDeniedSomePermission, Toast.LENGTH_LONG).show()
    }

    override fun onPermanentlyDenied(deniedPermissions: Array<String>) {
        AlertDialog.Builder(this).setTitle(R.string.somePermissionsPromptAgain)
                .setMessage(String.format(Locale.CHINA, getString(R.string.deniedPermissions), deniedPermissions.contentToString()))
                .setPositiveButton(R.string.to_open) { _, _ ->
                    XmPermissions.jumpToSettingPermissionPage(this)
                }.setNegativeButton(R.string.cancel) { _, _ ->
                    onDenied(deniedPermissions)
                }.setCancelable(false).show()
    }

    override fun onSettingBackDenied(deniedPermissions: Array<String>) {
        Toast.makeText(this, R.string.backSettingsDeniedPermissions, Toast.LENGTH_LONG).show()
    }
}