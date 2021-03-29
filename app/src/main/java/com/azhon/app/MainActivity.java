package com.azhon.app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.NumberProgressBar;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListenerAdapter;
import com.azhon.appupdate.manager.DownloadManager;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.app
 * 文件名:    MyDownload
 * 创建时间:  2018/1/27 on 19:25
 * 描述:     TODO 一个简单好用的版本更新库
 * <p/>
 * <div>
 * 由于Android Q版本限制后台应用启动Activity，所以下载完成会发送一个通知至通知栏（忽略showNotification的值，需要允许发送通知）
 * <a href="https://developer.android.google.cn/guide/components/activities/background-starts"/>
 * </div>
 * <div>
 * 由于Android Q版本限制应用访问外部存储目录（访问需要同时满足两个条件详情见文档）所以Q版本以上不要设置下载目录
 * <a href="https://developer.android.google.cn/training/data-storage/files/external-scoped"/>
 * </div>
 * https://developer.android.google.cn/training/data-storage/files/external-scoped
 *
 * @author 阿钟
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnButtonClickListener {

    private NumberProgressBar progressBar;
    private DownloadManager manager;
    private String url = "http://s.duapps.com/apks/own/ESFileExplorer-cn.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);
        progressBar = findViewById(R.id.number_progress_bar);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        //删除旧安装包
//        boolean b = ApkUtil.deleteOldApk(this, getExternalCacheDir().getPath() + "/ESFileExplorer.apk");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                startUpdate1();
                break;
            case R.id.btn_2:
                startUpdate2();
                break;
            case R.id.btn_3:
                startUpdate3();
                break;
            case R.id.btn_4:
                if (manager != null) {
                    manager.cancel();
                }
                break;
            default:
                break;
        }
    }

    private void startUpdate1() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_msg)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setProgress(0);
                        manager = DownloadManager.getInstance(MainActivity.this);
                        manager.setApkName("ESFileExplorer.apk")
                                .setApkUrl(url)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .download();
                    }
                }).create().show();
    }

    private void startUpdate2() {
        progressBar.setProgress(0);
        manager = DownloadManager.getInstance(MainActivity.this);
        manager.setApkName("ESFileExplorer.apk")
                .setApkUrl(url)
                .setSmallIcon(R.mipmap.ic_launcher)
                .download();
    }

    private void startUpdate3() {
        /*
         * 整个库允许配置的内容
         * 非必选
         */
        UpdateConfiguration configuration = new UpdateConfiguration()
                //输出错误日志
                .setEnableLog(true)
                //设置自定义的下载
                //.setHttpManager()
                //下载完成自动跳动安装页面
                .setJumpInstallPage(true)
                //设置对话框背景图片 (图片规范参照demo中的示例图)
                //.setDialogImage(R.drawable.ic_dialog)
                //设置按钮的颜色
                //.setDialogButtonColor(Color.parseColor("#E743DA"))
                //设置对话框强制更新时进度条和文字的颜色
                //.setDialogProgressBarColor(Color.parseColor("#E743DA"))
                //设置按钮的文字颜色
                .setDialogButtonTextColor(Color.WHITE)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置是否提示后台下载toast
                .setShowBgdToast(false)
                //设置是否上报数据
                .setUsePlatform(true)
                //设置强制更新
                .setForcedUpgrade(false)
                //设置对话框按钮的点击监听
                .setButtonClickListener(this)
                //设置下载过程的监听
                .setOnDownloadListener(listenerAdapter);

        manager = DownloadManager.getInstance(this);
        manager.setApkName("ESFileExplorer.apk")
                .setApkUrl(url)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowNewerToast(true)
                .setConfiguration(configuration)
                .setApkVersionCode(2)
                .setApkVersionName("2.1.8")
                .setApkSize("20.4")
                .setApkDescription(getString(R.string.dialog_msg))
//                .setApkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
                .download();
    }

    private OnDownloadListenerAdapter listenerAdapter = new OnDownloadListenerAdapter() {
        /**
         * 下载中
         *
         * @param max      总进度
         * @param progress 当前进度
         */
        @Override
        public void downloading(int max, int progress) {
            int curr = (int) (progress / (double) max * 100.0);
            progressBar.setMax(100);
            progressBar.setProgress(curr);
        }
    };

    @Override
    public void onButtonClick(int id) {
        Log.e("TAG", String.valueOf(id));
    }
}
