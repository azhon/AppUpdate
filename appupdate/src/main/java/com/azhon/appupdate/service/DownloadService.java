package com.azhon.appupdate.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.azhon.appupdate.base.BaseHttpDownloadManager;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.manager.HttpDownloadManager;
import com.azhon.appupdate.utils.ApkUtil;
import com.azhon.appupdate.utils.FileUtil;
import com.azhon.appupdate.utils.LogUtil;
import com.azhon.appupdate.utils.NotificationUtil;

import java.io.File;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.service
 * 文件名:    DownloadService
 * 创建时间:  2018/1/27 on 13:22
 * 描述:     TODO apk 下载服务
 *
 * @author 阿钟
 */


public final class DownloadService extends Service implements OnDownloadListener {

    private static final String TAG = "DownloadService";
    private int smallIcon;
    private String apkUrl;
    private String apkName;
    private String downloadPath;
    private OnDownloadListener listener;
    private boolean showNotification;
    private boolean jumpInstallPage;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return START_STICKY;
        }
        init();
        return super.onStartCommand(intent, flags, startId);
    }


    private void init() {
        apkUrl = DownloadManager.getInstance().getApkUrl();
        apkName = DownloadManager.getInstance().getApkName();
        downloadPath = DownloadManager.getInstance().getDownloadPath();
        smallIcon = DownloadManager.getInstance().getSmallIcon();
        //创建apk文件存储文件夹
        FileUtil.createDirDirectory(downloadPath);

        listener = DownloadManager.getInstance().getConfiguration().getOnDownloadListener();
        showNotification = DownloadManager.getInstance().getConfiguration().isShowNotification();
        jumpInstallPage = DownloadManager.getInstance().getConfiguration().isJumpInstallPage();
        download();
    }

    /**
     * 获取下载管理者
     */
    private void download() {
        BaseHttpDownloadManager manager = DownloadManager.getInstance().getConfiguration().getHttpManager();
        //如果用户自己定义了下载过程
        if (manager != null) {
            manager.download(apkUrl, apkName, this);
        } else {
            //使用自己的下载
            new HttpDownloadManager(this, downloadPath).download(apkUrl, apkName, this);
        }
    }


    @Override
    public void start() {
        if (showNotification) {
            handler.sendEmptyMessage(0);
            NotificationUtil.showNotification(this, smallIcon, "开始下载", "可稍后查看下载进度");
        }
        if (listener != null) {
            listener.start();
        }
    }

    @Override
    public void downloading(int max, int progress) {
        if (showNotification) {
            NotificationUtil.showProgressNotification(this, smallIcon, "正在下载新版本", "", max, progress);
        }
        if (listener != null) {
            listener.downloading(max, progress);
        }
    }

    @Override
    public void done(File apk) {
        if (showNotification) {
            NotificationUtil.showDoneNotification(this, smallIcon, "下载完成", "点击进行安装", apk);
        }
        //如果用户设置了回调 则先处理用户的事件 在执行自己的
        if (listener != null) {
            listener.done(apk);
        }
        if (jumpInstallPage) {
            ApkUtil.installApk(this, apk);
        }
        releaseResources();
    }

    @Override
    public void error(Exception e) {
        LogUtil.e(TAG, "error: " + e);
        if (showNotification) {
            NotificationUtil.showErrorNotification(this, smallIcon, "下载出错", "点击继续下载");
        }
        if (listener != null) {
            listener.error(e);
        }
    }

    /**
     * 下载完成释放资源
     */

    private void releaseResources() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        stopSelf();
        DownloadManager.getInstance().release();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(DownloadService.this, "正在后台下载新版本...", Toast.LENGTH_SHORT).show();

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
