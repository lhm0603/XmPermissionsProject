/*
 * *************************************************************************
 * Copyright (c) 2020
 * Author：HuaMing Lin
 * Email：linhuaming0603@outlook.com
 * Blog：https://blog.csdn.net/h461415832
 * *************************************************************************
 */

package com.xm.permissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_PERMISSIONS
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import java.util.*

/**
 * Android6.0权限申请框架
 * 支持请求所有权限
 * 支持按需请求权限
 * 支持处理被用户永久拒绝的权限，提供跳转设置界面去打开权限并接收结果（需使用者自行引导用户跳转到设置界面）
 *
 * @author LinHuaMing
 * @see 2020-11-18
 */
class XmPermissions private constructor(act: Activity) {
    companion object {
        fun newInstance(activity: Activity): XmPermissions {
            Objects.requireNonNull(activity)
            return XmPermissions(activity)
        }

        /**
         * 申请权限的请求码
         */
        private const val REQUEST_CODE_PERMISSIONS = 10109

        /**
         * 跳转到设置的请求码
         */
        private const val REQUEST_CODE_TO_SETTING = 10107
    }

    private var onRequestPermissionsCallback: OnRequestPermissionsCallback? = null

    private var activityWeakReference: WeakReference<Activity>? = null

    private var currentRequestPermissions: Array<out String>? = null

    init {
        activityWeakReference = WeakReference(act)
    }

    private val activity: Activity?
        get() = activityWeakReference?.get()

    /**
     * 设置申请权限结果的回调
     */
    fun setOnRequestPermissionsCallback(callback: OnRequestPermissionsCallback) {
        this.onRequestPermissionsCallback = callback
    }

    /**
     * 请求指定权限，
     * 需要提前调用 [XmPermissions.setOnRequestPermissionsCallback] 设置请求权限的回调函数来接受请求结果
     */
    fun requestPermissions(vararg permissions: String) {
        val activityTemp = activity ?: return
        val permissionTemp = filterValidPermission(permissions)
        if (permissionTemp.isEmpty()) {
            // 没有需要请求的权限，直接通过
            onRequestPermissionsCallback?.onGranted()
        }
        currentRequestPermissions = permissionTemp
        ActivityCompat.requestPermissions(activityTemp, permissionTemp, REQUEST_CODE_PERMISSIONS)
    }

    /**
     * 筛选出合法权限，过滤出在配置文件中定义过的权限。PS:要申请某项权限，请务必在配置文件中申明该权限
     */
    private fun filterValidPermission(permissions: Array<out String>): Array<out String> {
        val allManifestPermission = getAllManifestPermission()
        return permissions.filter { allManifestPermission.contains(it) }.toTypedArray()
    }

    /**
     * 请求所有在配置文件中定义的权限，
     * 需要提前调用 [XmPermissions.setOnRequestPermissionsCallback] 设置请求权限的回调函数来接受请求结果
     */
    fun requestAllPermissions() {
        val activityTemp = activity ?: return
        val allManifestPermission = getAllManifestPermission()
        if (allManifestPermission.isEmpty()) {
            // 没有配置权限，直接通过
            onRequestPermissionsCallback?.onGranted()
            return
        }
        currentRequestPermissions = allManifestPermission
        ActivityCompat.requestPermissions(activityTemp, allManifestPermission, REQUEST_CODE_PERMISSIONS)
    }

    /**
     * 处理权限申请的回调
     * 请务必在[androidx.fragment.app.FragmentActivity.onRequestPermissionsResult]方法中调用该方法并传递相应参数
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != REQUEST_CODE_PERMISSIONS) {
            return
        }
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onRequestPermissionsCallback?.onGranted()
            return
        }
        val deniedPermissions = filterDeniedPermission(permissions)
        if (somePermissionPermanentlyDenied(deniedPermissions)) {
            onRequestPermissionsCallback?.onPermanentlyDenied(deniedPermissions)
            return
        }
        onRequestPermissionsCallback?.onDenied(deniedPermissions)
    }

    /**
     * 检测被拒绝的权限是否已经被用户永久拒绝不在提示，此时建议引导用户去设置界面打开权限。
     * 可以调用[XmPermissions.jumpToSettingPermissionPage]该方法跳转，会跳转到应用对应的设置界面
     * 用户从设置界面返回应用，是否允许了相关权限，会通过 [OnRequestPermissionsCallback.onSettingBackDenied] 回调
     */
    private fun somePermissionPermanentlyDenied(deniedPermissions: Array<out String>): Boolean {
        val activityTemp = activity ?: return false
        return deniedPermissions.none {
            ActivityCompat.shouldShowRequestPermissionRationale(activityTemp, it)
        }
    }

    /**
     * 筛选出被拒绝的权限
     */
    private fun filterDeniedPermission(permissions: Array<out String>) = permissions.filter { !checkPermissionGranted(it) }.toTypedArray()

    /**
     * 获取配置文件中定义的所有权限
     */
    private fun getAllManifestPermission(): Array<out String> {
        val activityTemp = activity ?: return emptyArray()
        return try {
            activityTemp.packageManager?.getPackageInfo(activityTemp.packageName, GET_PERMISSIONS)?.requestedPermissions
                    ?: emptyArray()
        } catch (e: Exception) {
            emptyArray()
        }
    }

    /**
     * 跳转到应用设置界面
     */
    fun jumpToSettingPermissionPage() {
        val activityTemp = activity ?: return
        activityTemp.startActivityForResult(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.fromParts(
                                "package", activityTemp.packageName, null)), REQUEST_CODE_TO_SETTING)
    }

    /**
     * 处理跳转设置返回的结果
     * 请务必在[androidx.fragment.app.FragmentActivity.onActivityResult]方法中调用该方法并传递相应参数
     */
    fun onActivityResult(requestCode: Int) {
        if (requestCode != REQUEST_CODE_TO_SETTING) {
            return
        }
        val requestPermissions = currentRequestPermissions ?: return
        val filterDeniedPermission = filterDeniedPermission(requestPermissions)
        if (filterDeniedPermission.isEmpty()) {
            onRequestPermissionsCallback?.onGranted()
            return
        }
        onRequestPermissionsCallback?.onSettingBackDenied(filterDeniedPermission)
    }

    /**
     * 检测权限是否已经被允许
     */
    private fun checkPermissionGranted(permission: String): Boolean {
        val activityTemp = activity ?: return false
        return ContextCompat.checkSelfPermission(activityTemp, permission) == PackageManager.PERMISSION_GRANTED
    }
}