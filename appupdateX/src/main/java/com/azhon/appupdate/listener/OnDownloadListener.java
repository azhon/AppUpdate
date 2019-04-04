package com.azhon.appupdate.listener;

import java.io.File;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.listener
 * 文件名:    OnDownloadListener
 * 创建时间:  2018/1/27 on 18:59
 * 描述:     TODO 正在下载的回调
 *
 * @author 阿钟
 */


public interface OnDownloadListener {

    /**
     * 开始下载
     */
    void start();

    /**
     * 下载中
     *
     * @param max      总进度
     * @param progress 当前进度
     */
    void downloading(int max, int progress);

    /**
     * 下载完成
     *
     * @param apk 下载好的apk
     */
    void done(File apk);

    /**
     * 取消下载
     */
    void cancel();

    /**
     * 下载出错
     *
     * @param e 错误信息
     */
    void error(Exception e);
}
