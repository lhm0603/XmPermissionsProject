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
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
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
class XmPermissions private constructor(act: FragmentActivity) {
    companion object {
        @JvmStatic
        fun newInstance(activity: FragmentActivity): XmPermissions {
            Objects.requireNonNull(activity)
            return XmPermissions(activity)
        }

        /**
         * 跳转到应用设置界面
         */
        fun jumpToSettingPermissionPage(activity: FragmentActivity) {
            activity.startActivityForResult(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                            Uri.fromParts(
                                    "package", activity.packageName, null)), PermissionFragment.REQUEST_CODE_TO_SETTING)
        }

        private const val PERMISSION_FRAGMENT_TAG = "XmPermissions"
    }

    private val permissionsFragment: PermissionFragment by lazy {
        getPermissionsFragment(act.supportFragmentManager)
    }

    private fun getPermissionsFragment(fragmentManager: FragmentManager): PermissionFragment {
        var permissionsFragment: PermissionFragment? = findRxPermissionsFragment(fragmentManager)
        if (permissionsFragment == null) {
            permissionsFragment = PermissionFragment()
            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, PERMISSION_FRAGMENT_TAG)
                    .commitNow()
        }
        return permissionsFragment
    }

    private fun findRxPermissionsFragment(fragmentManager: FragmentManager): PermissionFragment? {
        return fragmentManager.findFragmentByTag(PERMISSION_FRAGMENT_TAG) as PermissionFragment?
    }

    private var activityWeakReference: WeakReference<Activity>? = null

    init {
        activityWeakReference = WeakReference(act)
    }

    private val activity: Activity?
        get() = activityWeakReference?.get()

    /**
     * 设置申请权限结果的回调
     */
    fun setOnRequestPermissionsCallback(callback: OnRequestPermissionsCallback): XmPermissions {
        permissionsFragment.setOnRequestPermissionsCallback(callback)
        return this
    }

    /**
     * 请求指定权限，
     * 需要提前调用 [XmPermissions.setOnRequestPermissionsCallback] 设置请求权限的回调函数来接受请求结果
     */
    fun requestPermissions(vararg permissions: String) {
        permissionsFragment.requestPermissions(*permissions)
    }

    /**
     * 请求所有在配置文件中定义的权限，
     * 需要提前调用 [XmPermissions.setOnRequestPermissionsCallback] 设置请求权限的回调函数来接受请求结果
     */
    fun requestAllPermissions() {
        permissionsFragment.requestAllPermissions()
    }
}