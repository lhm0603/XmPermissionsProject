package com.lhm.easypermissionsactivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.Size;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhm.easypermissionsactivity.utils.AppUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.GET_PERMISSIONS;


@TargetApi(Build.VERSION_CODES.M)
public abstract class EasyPermissionActivity extends AppCompatActivity {

    /**
     * This is {@link Manifest.permission_group#CAMERA} Group,
     * Group permissions contains {@link Manifest.permission#CAMERA}
     */
    public static final String CAMERA = Manifest.permission_group.CAMERA;
    /**
     * This is {@link Manifest.permission_group#CALENDAR} Group,
     * Group permissions contains
     * {@link Manifest.permission#READ_CALENDAR},
     * {@link Manifest.permission#WRITE_CALENDAR}
     */
    public static final String CALENDAR = Manifest.permission_group.CALENDAR;
    /**
     * This is {@link Manifest.permission_group#CONTACTS} Group,
     * Group permissions contains
     * {@link Manifest.permission#READ_CONTACTS},
     * {@link Manifest.permission#WRITE_CONTACTS},
     * {@link Manifest.permission#GET_ACCOUNTS}
     */
    public static final String CONTACTS = Manifest.permission_group.CONTACTS;
    /**
     * This is {@link Manifest.permission_group#STORAGE} Group,
     * Group permissions contains
     * {@link Manifest.permission#WRITE_EXTERNAL_STORAGE},
     * {@link Manifest.permission#READ_EXTERNAL_STORAGE}
     */
    public static final String STORAGE = Manifest.permission_group.STORAGE;
    /**
     * This is {@link Manifest.permission_group#LOCATION} Group,
     * Group permissions contains
     * {@link Manifest.permission#ACCESS_FINE_LOCATION},
     * {@link Manifest.permission#ACCESS_COARSE_LOCATION}
     */
    public static final String LOCATION = Manifest.permission_group.LOCATION;
    /**
     * This is {@link Manifest.permission_group#SMS} Group,
     * Group permissions contains
     * {@link Manifest.permission#SEND_SMS},
     * {@link Manifest.permission#SEND_SMS},
     * {@link Manifest.permission#RECEIVE_SMS},
     * {@link Manifest.permission#RECEIVE_MMS},
     * {@link Manifest.permission#RECEIVE_WAP_PUSH}
     */
    public static final String SMS = Manifest.permission_group.SMS;
    /**
     * This is {@link Manifest.permission_group#PHONE} Group,
     * Group permissions contains
     * {@link Manifest.permission#READ_PHONE_STATE},
     * {@link Manifest.permission#CALL_PHONE},
     * {@link Manifest.permission#READ_CALL_LOG},
     * {@link Manifest.permission#WRITE_CALL_LOG},
     * {@link Manifest.permission#ADD_VOICEMAIL},
     * {@link Manifest.permission#USE_SIP},
     * {@link Manifest.permission#PROCESS_OUTGOING_CALLS}
     */
    public static final String PHONE = Manifest.permission_group.PHONE;

    /**
     * This is {@link Manifest.permission_group#SENSORS} Group,
     * Group permissions contains
     * {@link Manifest.permission#BODY_SENSORS}
     */
    public static final String SENSORS = Manifest.permission_group.SENSORS;
    private String[] allDefinitionPermissions;


    @StringDef({CAMERA, CALENDAR, CONTACTS, STORAGE, LOCATION, SMS, PHONE, SENSORS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionGroup {

    }

    protected void addPermissionGroupDescription(@PermissionGroup String permissionGroup, String decription) {
        PermissionInfo permissionInfo = infoMap.get(permissionGroup);
        if (permissionInfo != null)
            permissionInfo.permissionDescription = decription;
        showPermissionDescription = true;
    }

    protected void addPermissionGroupDescription(@PermissionGroup String permissionGroup, @StringRes int descriptionRedId) {
        addPermissionGroupDescription(permissionGroup, getResources().getString(descriptionRedId));
    }

    protected void updatePermissionGroupName(@PermissionGroup String permissionGroup, String name) {
        PermissionInfo permissionInfo = infoMap.get(permissionGroup);
        if (permissionInfo != null)
            permissionInfo.permissionName = name;
    }

    protected void updatePermissionGroupName(@PermissionGroup String permissionGroup, @StringRes int nameResId) {
        updatePermissionGroupName(permissionGroup, getResources().getString(nameResId));
    }


    private FragmentActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activity = this;
        initRequestPermissions();
        super.onCreate(savedInstanceState);

    }

    private boolean showPermissionDescription;


    /**
     * 是否显示 权限描述
     */
    protected void showPermissionDescription(boolean showPermissionDescription) {
        this.showPermissionDescription = showPermissionDescription;
    }

    private class PermissionInfo {

        String permissionName;
        String permissionDescription;

        public PermissionInfo(String permissionName) {
            this.permissionName = permissionName;
        }

    }

    private static Map<String, PermissionInfo> infoMap = null;

    /**
     * 初始化
     */
    private void initRequestPermissions() {
        if (infoMap == null) {
            infoMap = new HashMap<>(8);
            infoMap.put(EasyPermissionActivity.CALENDAR, new PermissionInfo(getResources().getString(R.string.calendarPermission)));
            infoMap.put(EasyPermissionActivity.CAMERA, new PermissionInfo(getResources().getString(R.string.cameraPermission)));
            infoMap.put(EasyPermissionActivity.CONTACTS, new PermissionInfo(getResources().getString(R.string.contactsPermission)));
            infoMap.put(EasyPermissionActivity.PHONE, new PermissionInfo(getResources().getString(R.string.phonePermission)));
            infoMap.put(EasyPermissionActivity.LOCATION, new PermissionInfo(getResources().getString(R.string.locationPermission)));
            infoMap.put(EasyPermissionActivity.SENSORS, new PermissionInfo(getResources().getString(R.string.sensorsPermission)));
            infoMap.put(EasyPermissionActivity.SMS, new PermissionInfo(getResources().getString(R.string.smsPermission)));
            infoMap.put(EasyPermissionActivity.STORAGE, new PermissionInfo(getResources().getString(R.string.storagePermission)));
        }
        if (allDefinitionPermissions == null)
            allDefinitionPermissions = allDefinitionPermissions();
    }

    /**
     * 需要动态申请的权限
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String[] allDefinitionPermissions() {
        String[] allPermissions = getAllDefinitionPermissions();
        if (allPermissions == null)
            return null;
        HashMap<String, String> map = new HashMap<>();
        for (String s : allPermissions) {
            String permissionsGroup = whichGroup(s);
            map.put(permissionsGroup, s);
        }
        return map.values().toArray(new String[0]);
    }

    /**
     * 获取所有在 Manifest 配置文件中申请的权限
     *
     * @return
     */
    private String[] getAllDefinitionPermissions() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 安全权限
     */
    private static final String SECURITY_PERMISSIONS = "SECURITY";

    /**
     * 判断 权限属于哪个 分组
     *
     * @param permission 权限
     *
     * @return 分组名称
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String whichGroup(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return Manifest.permission_group.CAMERA;
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                return Manifest.permission_group.CALENDAR;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                return Manifest.permission_group.CONTACTS;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return Manifest.permission_group.LOCATION;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.ADD_VOICEMAIL:
            case Manifest.permission.USE_SIP:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                return Manifest.permission_group.PHONE;
            case Manifest.permission.BODY_SENSORS:
                return Manifest.permission_group.SENSORS;
            case Manifest.permission.READ_SMS:
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.RECEIVE_MMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
                return Manifest.permission_group.SMS;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return Manifest.permission_group.STORAGE;
        }
        //若以上都不是,则属于安全权限,不需要动态申请
        return SECURITY_PERMISSIONS;
    }


    protected static final int REQUEST_PERMISSION_REQUEST_CODE = 0x008;

    /**
     * 权限申请成功后.继续 onCreate 的生命周期流程
     */
    protected void requiresPermissionsBefore() {

    }

    /**
     * 权限申请成功后.继续 onCreate 的生命周期流程
     */
    protected void requiresPermissionsAfter(boolean success) {

    }

    protected void requestPermissions() {
        requiresPermissionsBefore();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (allDefinitionPermissions == null || allDefinitionPermissions.length == 0) {
                //不需要申请权限
                requiresPermissionsAfter(true);
                return;
            }
            final String[] permissions = allDefinitionPermissions;

            final String[] needRequestPermissions = hasPermissions(this, permissions);


            if (needRequestPermissions != null && needRequestPermissions.length > 0) {
                // 请求还有未拥有的权限
                new AlertDialog.Builder(this)
                        .setTitle(R.string.hint)
                        .setMessage(AppUtils.getAppName(this) + " " + getString(R.string.need_permission_info))
                        .setView(getPermissionInfoView(needRequestPermissions))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, needRequestPermissions, REQUEST_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setCancelable(false).show();
            } else {
                // 已经拥有权限,可以搞事情了
                requiresPermissionsAfter(true);
            }
        } else {
            // 已经拥有权限,可以搞事情了
            requiresPermissionsAfter(true);
        }
    }


    private View getPermissionInfoView(String[] needRequestPermissions) {
        RecyclerView recyclerView = (RecyclerView) getLayoutInflater().inflate(R.layout.view_dialog_custom, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        PermissionInfoAdapter permissionInfoAdapter = new PermissionInfoAdapter();
        recyclerView.setAdapter(permissionInfoAdapter);
        permissionInfoAdapter.setNeedRequestPermission(needRequestPermissions);
        return recyclerView;
    }


    private class PermissionInfoAdapter extends RecyclerView.Adapter<PermissionInfoAdapter.ItemViewHolder> {

        private String[] needRequestPermission;

        public void setNeedRequestPermission(String[] needRequestPermission) {
            this.needRequestPermission = needRequestPermission;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ItemViewHolder(getLayoutInflater().inflate(R.layout.view_dialog_custom_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            PermissionInfo permissionInfo = infoMap.get(whichGroup(needRequestPermission[i]));
            if (permissionInfo == null)
                return;
            itemViewHolder.tv_name.setText(permissionInfo.permissionName);
            if (showPermissionDescription && !TextUtils.isEmpty(permissionInfo.permissionDescription)) {
                itemViewHolder.tv_description.setVisibility(View.VISIBLE);
                itemViewHolder.tv_description.setText(permissionInfo.permissionDescription);
            } else {
                itemViewHolder.tv_description.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return needRequestPermission == null ? 0 : needRequestPermission.length;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView tv_name;
            private final TextView tv_description;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_description = itemView.findViewById(R.id.tv_description);
            }
        }


    }


    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms one ore more permissions, such as {@link Manifest.permission#CAMERA}.
     *
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     *
     * @see Manifest.permission
     */
    public String[] hasPermissions(@NonNull Context context,
                                   @Size(min = 1) @NonNull String... perms) {
        ArrayList<String> list = new ArrayList<>();
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                list.add(perm);
            }
        }
        return list.toArray(new String[0]);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                denied.add(perm);
            }
        }
        if (!denied.isEmpty()) {
            onPermissionsDenied(requestCode, denied);
        } else {
            onPermissionsGranted();
        }

    }


    public final void onPermissionsGranted() {
        requiresPermissionsAfter(true);
    }

    private boolean somePermissionPermanentlyDenied(@Size(min = 1) List<String> list) {
        for (String s : list) {
            boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this, s);
            if (b) {
                return true;
            }
        }
        return false;
    }

    public final void onPermissionsDenied(int requestCode, @NonNull List<String> list) {
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE) {
            // 还有被拒绝的权限...
            if (somePermissionPermanentlyDenied(list)) {
                //提示用户去设置界面开启

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.hint)
                        .setMessage(AppUtils.getAppName(this) + getString(R.string.need_permission_info))
                        .setView(getPermissionInfoView(list.toArray(new String[0])))
                        .setPositiveButton(R.string.to_open, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts("package", getPackageName(), null));
                                startActivityForResult(intent, APP_SETTINGS_RC);
                            }
                        })
                        .setCancelable(false);
                if (!isRequestAgain) {
                    builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requiresPermissionsAfter(false);
                        }
                    });
                }
                builder.show();
            } else {
                requiresPermissionsAfter(true);
            }
        }
    }

    private static final int APP_SETTINGS_RC = 7534;

    private boolean isRequestAgain = false;

    /**
     * 是否无限请求,直到所有权限都通过
     *
     * @param requestAgain
     */
    protected void isRequestAgain(boolean requestAgain) {
        isRequestAgain = requestAgain;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_SETTINGS_RC:
                if (!isRequestAgain) {
                    // 申请失败!
                    requiresPermissionsAfter(false);
                    return;
                }
                requestPermissions();
                break;
        }
    }
}
