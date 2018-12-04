package com.azhon.app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.app
 * 文件名:    MyDownload
 * 创建时间:  2018/1/27 on 19:25
 * 描述:     TODO 一个简单好用的版本更新库
 *
 * @author 阿钟
 */

public class MainActivity extends AppCompatActivity implements OnDownloadListener, View.OnClickListener, OnButtonClickListener {

    private NumberProgressBar progressBar;
    private DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("一个简单好用的版本更新库");
        progressBar = findViewById(R.id.number_progress_bar);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
//        删除旧版本安装包
//        boolean b = ApkUtil.deleteOldApk(this, getExternalCacheDir().getPath() + "/appupdate.apk");
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
                .setTitle("发现新版本")
                .setMessage("1.支持断点下载\n2.支持Android N\n3.支持Android O\n4.支持自定义下载过程\n5.支持 设备>=Android M 动态权限的申请")
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DownloadManager.getInstance().release();
                            progressBar.setProgress(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        manager = DownloadManager.getInstance(MainActivity.this);
                        manager.setApkName("appupdate.apk")
                                .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .download();
                    }
                }).create().show();
    }

    private void startUpdate2() {
        try {
            DownloadManager.getInstance().release();
            progressBar.setProgress(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager = DownloadManager.getInstance(MainActivity.this);
        manager.setApkName("appupdate.apk")
                .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
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
                //设置按钮的文字颜色
                .setDialogButtonTextColor(Color.WHITE)
                //支持断点下载
                .setBreakpointDownload(true)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置强制更新
                .setForcedUpgrade(false)
                //设置对话框按钮的点击监听
                .setButtonClickListener(this)
                //设置下载过程的监听
                .setOnDownloadListener(this);

        manager = DownloadManager.getInstance(this);
        manager.setApkName("appupdate.apk")
                .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowNewerToast(true)
                .setConfiguration(configuration)
//                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setApkVersionCode(2)
                .setApkVersionName("2.1.8")
                .setApkSize("20.4")
                .setAuthorities(getPackageName())
                .setApkDescription("1.支持断点下载\n2.支持Android N\n3.支持Android O\n4.支持自定义下载过程\n5.支持 设备>=Android M 动态权限的申请\n6.支持通知栏进度条展示(或者自定义显示进度)")
                .download();
    }

    @Override
    public void start() {

    }

    @Override
    public void downloading(int max, int progress) {
        Message msg = new Message();
        msg.arg1 = max;
        msg.arg2 = progress;
        handler.sendMessage(msg);
    }

    @Override
    public void done(File apk) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void error(Exception e) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.setMax(msg.arg1);
            progressBar.setProgress(msg.arg2);
        }
    };

    @Override
    public void onButtonClick(int id) {
        Log.e("TAG", String.valueOf(id));
    }
}
