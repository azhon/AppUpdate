package com.azhon.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnDownloadListener {

    private NumberProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.number_progress_bar);
    }

    public void Update(View view) {
//        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
//                .setTitle("有新版本")
//                .setMessage("----QQ·乐在沟通-----\n" +
//                        "√服务超过90%的移动互联网用户\n" +
//                        "√多人视频、文件多端互传，不断创新满足沟通所需\n" +
//                        "√致力于打造欢乐无限的沟通、娱乐...")
//                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        startUpdate();
//                    }
//                })
//                .setNegativeButton("不更新", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).create().show();

        startUpdate();

    }

    private void startUpdate() {
        /*
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

        DownloadManager manager = DownloadManager.getInstance(this);
        manager.setApkName("QQ.apk")
                .setApkUrl("http://gdown.baidu.com/data/wisegame/9d24e3f43ca2de66/mojitianqi_7030202.apk")
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setApkVersionCode(2)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setConfiguration(configuration)
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
}
