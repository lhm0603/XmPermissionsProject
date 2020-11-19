# XmPermissions

## 项目介绍
Android动态权限申请框架

Github地址：[https://github.com/lhm0603/XmPermissionsProject](https://github.com/lhm0603/XmPermissionsProject)

### 使用说明

XmPermissions 支持 Android 5.0及更高版本，在 Android6.0之前的权限申请默认直接允许。

## 开始使用

### 引入 XmPermissions

使用 `Android Studio` 或者其他 `Gradle` 构建项目

```groovy
dependencies {
    //其他依赖
	implementation 'com.xm.permissions:XmPermissions:1.0.0'
}
```

### 使用 XmPermissions

在AndroidMainifest.xml 文件中加入你的应用程序需要使用到的权限

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="com.lin.example.epa"      				
	xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.CALL_PHONE"/>
    <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>

    <application>
    </application>
</manifest>
```

在您需要申请权限的 Activity 中，创建XmPermissions实例，并实现必要的回调：

```kotlin
class IndexActivity : AppCompatActivity() {
    /**
     * 创建XmPermissions实例
     */
    private val xmPermissions = XmPermissions.newInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
		// 按需请求需要的权限
//        xmPermissions.requestPermissions(getString(R.string.InvalidPermissionText), Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)

        // 请求配置文件中申明的所有权限（不推荐）
        xmPermissions.requestAllPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 必须重些该方法，并将方法参数传递给XmPermissions
        xmPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 必须重些该方法，并将方法参数传递给XmPermissions
        xmPermissions.onActivityResult(requestCode)
    }
}
```

> PS：权限的申请建议遵循安卓规范，比如：
>
> 1. 需要某些权限才可以执行下一步动作之前，再去申请相关权限
> 2. 在申请权限之前，可以通过弹框，或者全屏界面提示，明确提示用户接下来要申请的权限是作用于哪些用途
> 3. 只申请应用相关的权限，与本应用不相关的权限不要申请。
> 4. 就算用户未同意权限，也尽可能的能够让用户在权限受限的情况下，可以体验无需权限的部分功能。
>
> 更多规范，请参考：[https://developer.android.google.cn/guide/topics/permissions/overview](https://developer.android.google.cn/guide/topics/permissions/overview)



你一定关心权限申请成功或者失败的回调方法，你可以通过设置XmPermissions的setOnRequestPermissionsCallback 设置权限申请的回调：

```kotlin
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
```



让Activity实现该接口或创建该接口的实现类等，并重些相应方法：

```kotlin
// Activity 实现了 OnRequestPermissionsCallback 接口，并重写了相关函数
class IndexActivity : AppCompatActivity(), OnRequestPermissionsCallback {
    private val xmPermissions = XmPermissions.newInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        // 设置权限请求回调监听
        xmPermissions.setOnRequestPermissionsCallback(this)
        xmPermissions.requestAllPermissions()
    }
	
    // 省略部分代码...
    
    // 申请的权限用户都已授权，可以开心的搞事情了
    override fun onGranted() {
        Toast.makeText(this, R.string.userGrantedAllPermission, Toast.LENGTH_LONG).show()
    }

    // 申请的权限部分被用户拒绝，需要权限的功能受限，可以友好的提示用户
    override fun onDenied(deniedPermissions: Array<String>) {
        Toast.makeText(this, R.string.userDeniedSomePermission, Toast.LENGTH_LONG).show()
    }

    // 申请的权限部分被用户多次拒绝或用户直接拒绝并勾选了不再提示。意味着下次被永久拒绝的权限不会再有系统权限弹框，
    // 而是直接被拒绝，这里建议可以再次告知用户，为什么需要该权限，没有该权限功能受阻等。并提供UI让用户可以跳转到设置界面去手动打开相应的权限。
    // 可以调用 xmPermissions.jumpToSettingPermissionPage() ，内部实现了跳转到应用设置界面的函数。并且对从设置回到应用也做了回调处理
    override fun onPermanentlyDenied(deniedPermissions: Array<String>) {
        AlertDialog.Builder(this).setTitle(R.string.somePermissionsPromptAgain)
                .setMessage(String.format(Locale.CHINA, getString(R.string.deniedPermissions), deniedPermissions.contentToString()))
                .setPositiveButton(R.string.to_open) { _, _ ->
                    xmPermissions.jumpToSettingPermissionPage()
                }.setNegativeButton(R.string.cancel) { _, _ ->
                    onDenied(deniedPermissions)
                }.setCancelable(false).show()
    }

    // 该方法的回调前提是调用过 xmPermissions.jumpToSettingPermissionPage()，并且用户到了设置界面并没有打开所需要的权限，当用户从设置回到应用时，该方法会被执行。同样意味着权限没有申请成功
    override fun onSettingBackDenied(deniedPermissions: Array<String>) {
        Toast.makeText(this, R.string.backSettingsDeniedPermissions, Toast.LENGTH_LONG).show()
    }
}
```

#### 许可证

```
Copyright 2020 Huaming Lin.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```