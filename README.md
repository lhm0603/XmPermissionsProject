# EasyPermissionsActivity

## 项目介绍
超级简单的 Android6.0动态权限申请程序

### 使用说明

EasyPermissionsActivity 支持 Android 4.0及更高版本，但只有在 Android6.0动态权限 Api 才有效。

## 开始使用

### 引入 EasyPermissionsActivity

这里告诉你如何在项目中引入 Fresco.

#### 使用 Android Studio 或者其他 Gradle 构建的项目

如果您使用Gradle构建，只需将以下行添加到文件的`dependencies`部分`build.gradle`：

```groovy
dependencies {
    //其他依赖
	implementation 'com.github.lhm:easypermissionsactivity:1.0.4'
}
```

### 使用 EasyPermissionsActivity

如果你仅仅是想把所有需要动态申请的权限开启，那么您只需要简单使用 EasyPermissionsActivity 即可。

在您需要动态权限申请的 AppCompatActivity 中，让该 AppCompatActivity 继承 EasyPermissionActivity 。然后在需要动态权限申请的时刻，调用 super.

#### 许可证

```
Copyright 2018 Huaming Lin.

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