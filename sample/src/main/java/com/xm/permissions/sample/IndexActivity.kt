/*
 * *************************************************************************
 * Copyright (c) 2020
 * Author：HuaMing Lin
 * Email：linhuaming0603@outlook.com
 * Blog：https://blog.csdn.net/h461415832
 * *************************************************************************
 */

package com.xm.permissions.sample

import android.content.Intent
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
    private val xmPermissions = XmPermissions.newInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        xmPermissions.setOnRequestPermissionsCallback(this)
//        xmPermissions.requestPermissions(getString(R.string.InvalidPermissionText), Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)
        xmPermissions.requestAllPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 必须重些该方法，并方法给XmPermissions
        xmPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 必须重些该方法，并方法给XmPermissions
        xmPermissions.onActivityResult(requestCode)
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
                    xmPermissions.jumpToSettingPermissionPage()
                }.setNegativeButton(R.string.cancel) { _, _ ->
                    onDenied(deniedPermissions)
                }.setCancelable(false).show()
    }

    override fun onSettingBackDenied(deniedPermissions: Array<String>) {
        Toast.makeText(this, R.string.backSettingsDeniedPermissions, Toast.LENGTH_LONG).show()
    }
}