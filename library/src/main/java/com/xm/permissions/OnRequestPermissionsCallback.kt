/*
 * *************************************************************************
 * Copyright (c) 2020
 * Author：HuaMing Lin
 * Email：linhuaming0603@outlook.com
 * Blog：https://blog.csdn.net/h461415832
 * *************************************************************************
 */

package com.xm.permissions

/**
 * 申请权限结果回调
 *
 * @author LinHuaMing
 * @since 2020-11-15
 */
interface OnRequestPermissionsCallback {
    /**
     * 申请的权限都已授权
     */
    fun onGranted()

    /**
     * 部分申请的权限被拒绝
     */
    fun onDenied(deniedPermissions: Array<String>)

    /**
     * 部分申请的权限被永久拒绝
     */
    fun onPermanentlyDenied(deniedPermissions: Array<String>)

    /**
     * 从设置回到应用，希望被授予的权限还没有被授予
     */
    fun onSettingBackDenied(deniedPermissions: Array<String>)
}