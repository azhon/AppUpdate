package com.azhon.appupdate.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.azhon.appupdate.R;
import com.azhon.appupdate.base.BaseHttpDownloadManager;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.manager.HttpDownloadManager;
import com.azhon.appupdate.utils.ApkUtil;
import com.azhon.appupdate.utils.Constant;
import com.azhon.appupdate.utils.FileUtil;
import com.azhon.appupdate.utils.LogUtil;
import com.azhon.appupdate.utils.NotificationUtil;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

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

    private static final String TAG = Constant.TAG + "DownloadService";
    private int smallIcon;
    private String apkUrl;
    private String apkName;
    private String downloadPath;
    private List<OnDownloadListener> listeners;
    private boolean showNotification;
    private boolean showBgdToast;
    private boolean jumpInstallPage;
    private int lastProgress;
    private DownloadManager downloadManager;
    private BaseHttpDownloadManager httpManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return START_STICKY;
        }
        init();
        return super.onStartCommand(intent, flags, startId);
    }


    private void init() {
        downloadManager = DownloadManager.getInstance();
        if (downloadManager == null) {
            LogUtil.d(TAG, "init DownloadManager.getInstance() = null ,请先调用 getInstance(Context context) !");
            return;
        }
        apkUrl = downloadManager.getApkUrl();
        apkName = downloadManager.getApkName();
        downloadPath = downloadManager.getDownloadPath();
        smallIcon = downloadManager.getSmallIcon();
        //创建apk文件存储文件夹
        FileUtil.createDirDirectory(downloadPath);

        UpdateConfiguration configuration = downloadManager.getConfiguration();
        listeners = configuration.getOnDownloadListener();
        showNotification = configuration.isShowNotification();
        showBgdToast = configuration.isShowBgdToast();
        jumpInstallPage = configuration.isJumpInstallPage();
        //获取app通知开关是否打开
        boolean enable = NotificationUtil.notificationEnable(this);
        LogUtil.d(TAG, enable ? "应用的通知栏开关状态：已打开" : "应用的通知栏开关状态：已关闭");
        if (checkApkMD5()) {
            LogUtil.d(TAG, "文件已经存在直接进行安装");
            //直接调用完成监听即可
            done(FileUtil.createFile(downloadPath, apkName));
        } else {
            LogUtil.d(TAG, "文件不存在开始下载");
            download(configuration);
        }
    }

    /**
     * 校验Apk是否已经下载好了，不重复下载
     *
     * @return 是否下载完成
     */
    private boolean checkApkMD5() {
        if (FileUtil.fileExists(downloadPath, apkName)) {
            String fileMD5 = FileUtil.getFileMD5(FileUtil.createFile(downloadPath, apkName));
            return fileMD5.equalsIgnoreCase(downloadManager.getApkMD5());
        }
        return false;
    }

    /**
     * 获取下载管理者
     */
    private synchronized void download(UpdateConfiguration configuration) {
        if (downloadManager.isDownloading()) {
            LogUtil.e(TAG, "download: 当前正在下载，请务重复下载！");
            return;
        }
        httpManager = configuration.getHttpManager();
        //使用自己的下载
        if (httpManager == null) {
            httpManager = new HttpDownloadManager(downloadPath);
            configuration.setHttpManager(httpManager);
        }
        //如果用户自己定义了下载过程
        httpManager.download(apkUrl, apkName, this);
        downloadManager.setState(true);
    }


    @Override
    public void start() {
        if (showNotification) {
            if (showBgdToast) {
                handler.sendEmptyMessage(0);
            }
            String startDownload = getResources().getString(R.string.start_download);
            String startDownloadHint = getResources().getString(R.string.start_download_hint);
            NotificationUtil.showNotification(this, smallIcon, startDownload, startDownloadHint);
        }
        handler.sendEmptyMessage(1);
    }

    @Override
    public void downloading(int max, int progress) {
        LogUtil.i(TAG, "max: " + max + " --- progress: " + progress);
        if (showNotification) {
            //优化通知栏更新，减少通知栏更新次数
            int curr = (int) (progress / (double) max * 100.0);
            if (curr != lastProgress) {
                lastProgress = curr;
                String downloading = getResources().getString(R.string.start_downloading);
                String content = curr < 0 ? "" : curr + "%";
                NotificationUtil.showProgressNotification(this, smallIcon, downloading,
                        content, max == -1 ? -1 : 100, curr);
            }
        }
        handler.obtainMessage(2, max, progress).sendToTarget();
    }

    @Override
    public void done(File apk) {
        LogUtil.d(TAG, "done: 文件已下载至" + apk.toString());
        downloadManager.setState(false);
        //如果是android Q（api=29）及其以上版本showNotification=false也会发送一个下载完成通知
        if (showNotification || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String downloadCompleted = getResources().getString(R.string.download_completed);
            String clickHint = getResources().getString(R.string.click_hint);
            NotificationUtil.showDoneNotification(this, smallIcon, downloadCompleted,
                    clickHint, Constant.AUTHORITIES, apk);
        }
        if (jumpInstallPage) {
            ApkUtil.installApk(this, Constant.AUTHORITIES, apk);
        }
        //如果用户设置了回调 则先处理用户的事件 在执行自己的
        handler.obtainMessage(3, apk).sendToTarget();
    }

    @Override
    public void cancel() {
        downloadManager.setState(false);
        if (showNotification) {
            NotificationUtil.cancelNotification(this);
        }
        handler.sendEmptyMessage(4);
    }

    @Override
    public void error(Exception e) {
        LogUtil.e(TAG, "error: " + e);
        downloadManager.setState(false);
        if (showNotification) {
            String downloadError = getResources().getString(R.string.download_error);
            String conDownloading = getResources().getString(R.string.continue_downloading);
            NotificationUtil.showErrorNotification(this, smallIcon, downloadError, conDownloading);
        }
        handler.obtainMessage(5, e).sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(DownloadService.this, R.string.background_downloading, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    for (OnDownloadListener listener : listeners) {
                        listener.start();
                    }
                    break;
                case 2:
                    for (OnDownloadListener listener : listeners) {
                        listener.downloading(msg.arg1, msg.arg2);
                    }
                    break;
                case 3:
                    for (OnDownloadListener listener : listeners) {
                        listener.done((File) msg.obj);
                    }
                    //执行了完成开始释放资源
                    releaseResources();
                    break;
                case 4:
                    for (OnDownloadListener listener : listeners) {
                        listener.cancel();
                    }
                    break;
                case 5:
                    for (OnDownloadListener listener : listeners) {
                        listener.error((Exception) msg.obj);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 下载完成释放资源
     */
    private void releaseResources() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (httpManager != null) {
            httpManager.release();
        }
        stopSelf();
        downloadManager.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
