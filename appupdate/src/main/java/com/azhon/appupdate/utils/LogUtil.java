package com.azhon.appupdate.utils;

import android.util.Log;

import com.azhon.appupdate.manager.DownloadManager;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.utils
 * 文件名:    LogUtil
 * 创建时间:  2018/1/28 on 15:55
 * 描述:     TODO 日志工具类
 *
 * @author 阿钟
 */


public final class LogUtil {

    private static boolean b = true;

    static {
        b = DownloadManager.getInstance().getConfiguration().isEnableLog();
    }

    public static void e(String tag, String msg) {
        if (b) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, int msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    public static void e(String tag, float msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    public static void e(String tag, Long msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    public static void e(String tag, double msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    public static void e(String tag, boolean msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (b) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (b) {
            Log.i(tag, msg);
        }
    }
}
