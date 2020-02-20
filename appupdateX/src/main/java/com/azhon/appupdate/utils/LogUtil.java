package com.azhon.appupdate.utils;

import android.util.Log;

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

    /**
     * 日志开关
     */
    private static boolean b = true;

    public static void enable(boolean enable) {
        b = enable;
    }


    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg String
     */
    public static void e(String tag, String msg) {
        if (b) {
            Log.e(tag, msg);
        }
    }

    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg int
     */
    public static void e(String tag, int msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg float
     */
    public static void e(String tag, float msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg Long
     */
    public static void e(String tag, Long msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg double
     */
    public static void e(String tag, double msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    /**
     * 输出Error信息
     *
     * @param tag tag
     * @param msg boolean
     */
    public static void e(String tag, boolean msg) {
        if (b) {
            Log.e(tag, String.valueOf(msg));
        }
    }

    /**
     * 输出Debug信息
     *
     * @param tag tag
     * @param msg String
     */
    public static void d(String tag, String msg) {
        if (b) {
            Log.d(tag, msg);
        }
    }

    /**
     * 输出Info信息
     *
     * @param tag tag
     * @param msg 字符串
     */
    public static void i(String tag, String msg) {
        if (b) {
            Log.i(tag, msg);
        }
    }
}
