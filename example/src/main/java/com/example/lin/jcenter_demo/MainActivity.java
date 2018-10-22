package com.example.lin.jcenter_demo;

import android.os.Bundle;
import android.widget.Toast;

import com.lhm.easypermissionsactivity.EasyPermissionsActivity;

public class MainActivity extends EasyPermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加存储权限使用用途描述
        super.addPermissionGroupDescription(EasyPermissionsActivity.STORAGE, "下载书籍，节省流量。");
        super.addPermissionGroupDescription(EasyPermissionsActivity.LOCATION, "获取位置信息，智能推荐。");
        super.addPermissionGroupDescription(EasyPermissionsActivity.PHONE, "检验IMEI码，保证账号安全，防止账号被盗。");
        super.updatePermissionGroupName(EasyPermissionsActivity.STORAGE, "文件存储");
        super.updatePermissionGroupName(EasyPermissionsActivity.LOCATION, "位置信息");
        //开启无限请求权限模式
        isRequestAgain(true);
        //开始请求权限
        super.requestPermissions();
    }

    @Override
    protected void onRequestPermissionsBefore() {
        //权限请求之前,该方法被调用
    }

    /**
     * 权限请求之后
     *
     * @param success true 成功/ false失败
     */
    @Override
    protected void onRequestPermissionsAfter(boolean success) {
        if (success) {
            setContentView(R.layout.activity_main);
            //do something...
        } else {
            Toast.makeText(this, "还有权限没有请求到!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
