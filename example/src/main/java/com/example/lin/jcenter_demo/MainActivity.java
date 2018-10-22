package com.example.lin.jcenter_demo;

import android.os.Bundle;
import android.widget.Toast;

import com.lhm.easypermissionsactivity.EasyPermissionActivity;

public class MainActivity extends EasyPermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加存储权限使用用途描述
        addPermissionGroupDescription(STORAGE, "下载书籍,节省流量!");
        addPermissionGroupDescription(LOCATION, "下载书籍,节省流量!");
        addPermissionGroupDescription(PHONE, "下载书籍,节省流量!");
        //开启无限请求权限模式
        isRequestAgain(true);
        //开始请求权限
        requestPermissions();
    }

    @Override
    protected void requiresPermissionsBefore() {
        //权限请求之前,该方法被调用
    }

    /**
     * 权限请求之后
     *
     * @param success true 成功/ false失败
     */
    @Override
    protected void requiresPermissionsAfter(boolean success) {
        if (success) {
            setContentView(R.layout.activity_main);
            //do something...
        } else {
            Toast.makeText(this, "还有权限没有请求到!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
