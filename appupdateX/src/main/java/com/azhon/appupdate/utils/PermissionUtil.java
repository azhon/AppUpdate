package com.azhon.appupdate.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.utils
 * 文件名:    PermissionUtil
 * 创建时间:  2018/1/27 on 16:56
 * 描述:     TODO
 *
 * @author 阿钟
 */


public final class PermissionUtil {

    /**
     * 检查存储空间权限
     *
     * @return
     */
    public static boolean checkStoragePermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 请求这个库需要的权限
     *
     * @return
     */
    public static void requestPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }

    /**
     * 检查Android O 应用安装权限
     *
     * @return false 没有权限，需要申请
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean checkOreoInstallPermission(Context context) {
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 检查通知权限
     *
     * @return false 没有权限，需要申请
     */
    public static boolean checkNotificationPermission(Context context) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        return managerCompat.areNotificationsEnabled();
    }
}
