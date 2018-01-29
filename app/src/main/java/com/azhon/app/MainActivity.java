package com.azhon.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnDownloadListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Update(View view) {
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setTitle("有新版本")
                .setMessage("----QQ·乐在沟通-----\n" +
                        "√服务超过90%的移动互联网用户\n" +
                        "√多人视频、文件多端互传，不断创新满足沟通所需\n" +
                        "√致力于打造欢乐无限的沟通、娱乐...")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startUpdate();
                    }
                })
                .setNegativeButton("不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();


    }

    private void startUpdate() {
        DownloadManager manager = DownloadManager.getInstance(this);
        /**
         * 整个库允许配置的内容
         * 非必选
         */
        UpdateConfiguration configuration = new UpdateConfiguration()
                //输出错误日志
                .setEnableLog(true)
                //设置自定义的下载
//                .setHttpManager()
                //下载完成自动跳动安装页面
                .setJumpInstallPage(true)
                //支持断点下载
                .setBreakpointDownload(false)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置下载过程的监听
                .setOnDownloadListener(this);

        manager.setApkName("QQ.apk")
                .setApkUrl("http://122.228.255.161/appdl.hicloud.com/dl/appdl/application/apk/90/9012af6dab704164a7792bc185015cf4/com.lovebizhi.wallpaper.1801111518.apk?mkey=5a6ca5f233a0cd8f&f=2664&c=0&sign=portal@portal1517060867705&source=portalsite&p=.apk")
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setConfiguration(configuration)
                .download();
    }

    @Override
    public void start() {

    }

    @Override
    public void downloading(int max, int progress) {
    }

    @Override
    public void done(File apk) {

    }

    @Override
    public void error(Exception e) {

    }
}
