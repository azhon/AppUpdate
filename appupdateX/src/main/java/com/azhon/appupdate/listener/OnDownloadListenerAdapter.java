package com.azhon.appupdate.listener;

import java.io.File;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.listener
 * 文件名:    OnDownloadListenerAdapter
 * 创建时间:  2020/9/30 on 09:09
 * 描述:     TODO
 *
 * @author 阿钟
 */

public abstract class OnDownloadListenerAdapter implements OnDownloadListener {
    /**
     * 开始下载
     */
    @Override
    public void start() {

    }

    /**
     * 下载中
     *
     * @param max      总进度
     * @param progress 当前进度
     */
    @Override
    public void downloading(int max, int progress) {

    }

    /**
     * 下载完成
     *
     * @param apk 下载好的apk
     */
    @Override
    public void done(File apk) {

    }

    /**
     * 取消下载
     */
    @Override
    public void cancel() {

    }

    /**
     * 下载出错
     *
     * @param e 错误信息
     */
    @Override
    public void error(Exception e) {

    }
}
