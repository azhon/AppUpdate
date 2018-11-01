package com.azhon.appupdate.config;

import android.app.NotificationChannel;
import android.support.annotation.ColorInt;

import com.azhon.appupdate.base.BaseHttpDownloadManager;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListener;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.config
 * 文件名:    UpdateConfiguration
 * 创建时间:  2018/1/27 on 10:42
 * 描述:     TODO 版本更新的一些配置信息,配置一些必须要的默认信息
 *
 * @author 阿钟
 */


public class UpdateConfiguration {
    /**
     * 通知栏id
     */
    private int notifyId = 1011;
    /**
     * 适配Android O的渠道通知
     */
    private NotificationChannel notificationChannel;
    /**
     * 用户自定义的下载管理
     */
    private BaseHttpDownloadManager httpManager;
    /**
     * 是否需要支持断点下载 (默认是支持的)
     */
    private boolean breakpointDownload = true;
    /**
     * 是否需要日志输出（错误日志）
     */
    private boolean enableLog = true;
    /**
     * 是否需要显示通知栏进度
     */
    private boolean showNotification = true;
    /**
     * 下载过程回调
     */
    private OnDownloadListener onDownloadListener;
    /**
     * 按钮点击事件回调
     */
    private OnButtonClickListener onButtonClickListener;
    /**
     * 下载完成是否自动弹出安装页面 (默认为true)
     */
    private boolean jumpInstallPage = true;
    /**
     * 是否强制升级(默认为false)
     */
    private boolean forcedUpgrade = false;
    /**
     * 内置对话框背景图片资源id （图片规范参照demo中的示例图大小）
     */
    private int dialogImage = -1;
    /**
     * 内置对话框按钮的颜色
     */
    private int dialogButtonColor = -1;
    /**
     * 内置对话框按钮的文字颜色
     */
    private int dialogButtonTextColor = -1;

    public int getNotifyId() {
        return notifyId;
    }

    public UpdateConfiguration setNotifyId(int notifyId) {
        this.notifyId = notifyId;
        return this;
    }

    public UpdateConfiguration setHttpManager(BaseHttpDownloadManager httpManager) {
        this.httpManager = httpManager;
        return this;
    }

    public BaseHttpDownloadManager getHttpManager() {
        return httpManager;
    }

    public boolean isBreakpointDownload() {
        return breakpointDownload;
    }

    public UpdateConfiguration setBreakpointDownload(boolean breakpointDownload) {
        this.breakpointDownload = breakpointDownload;
        return this;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public UpdateConfiguration setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
        return this;
    }

    public OnDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }

    public UpdateConfiguration setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
        return this;
    }

    public boolean isJumpInstallPage() {
        return jumpInstallPage;
    }

    public UpdateConfiguration setJumpInstallPage(boolean jumpInstallPage) {
        this.jumpInstallPage = jumpInstallPage;
        return this;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public boolean isShowNotification() {
        return showNotification;
    }

    public UpdateConfiguration setShowNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public UpdateConfiguration setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
        return this;
    }

    public boolean isForcedUpgrade() {
        return forcedUpgrade;
    }

    public UpdateConfiguration setForcedUpgrade(boolean forcedUpgrade) {
        this.forcedUpgrade = forcedUpgrade;
        return this;
    }

    public UpdateConfiguration setDialogImage(int dialogImage) {
        this.dialogImage = dialogImage;
        return this;
    }

    public int getDialogImage() {
        return dialogImage;
    }

    public UpdateConfiguration setDialogButtonColor(@ColorInt int dialogButtonColor) {
        this.dialogButtonColor = dialogButtonColor;
        return this;
    }

    public UpdateConfiguration setDialogButtonTextColor(int dialogButtonTextColor) {
        this.dialogButtonTextColor = dialogButtonTextColor;
        return this;
    }

    public int getDialogButtonTextColor() {
        return dialogButtonTextColor;
    }

    public int getDialogButtonColor() {
        return dialogButtonColor;
    }

    public UpdateConfiguration setButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
        return this;
    }

    public OnButtonClickListener getOnButtonClickListener() {
        return onButtonClickListener;
    }
}
