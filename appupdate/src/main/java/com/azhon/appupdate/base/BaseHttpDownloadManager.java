package com.azhon.appupdate.base;

import com.azhon.appupdate.listener.OnDownloadListener;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.base
 * 文件名:    BaseHttpDownloadManager
 * 创建时间:  2018/1/27 on 19:25
 * 描述:     TODO 下载管理者
 *
 * @author 阿钟
 */


public abstract class BaseHttpDownloadManager {

    /**
     * 下载apk
     *
     * @param apkUrl   apk下载地址
     * @param apkName  apk名字
     * @param listener 回调
     */
    public abstract void download(String apkUrl, String apkName, OnDownloadListener listener);

    /**
     * 取消下载apk
     */
    public abstract void cancel();

    /**
     * 释放资源
     */
    public abstract void release();
}
