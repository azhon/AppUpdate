package com.azhon.appupdate.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.azhon.appupdate.base.BaseHttpDownloadManager;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.utils.Constant;
import com.azhon.appupdate.utils.FileUtil;
import com.azhon.appupdate.utils.LogUtil;
import com.azhon.appupdate.utils.SharePreUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.manager
 * 文件名:    HttpDownloadManager
 * 创建时间:  2018/1/27 on 21:50
 * 描述:     TODO 库中默认的下载管理
 *
 * @author 阿钟
 */


public class HttpDownloadManager extends BaseHttpDownloadManager {

    private static final String TAG = Constant.TAG + "HttpDownloadManager";
    private Context context;
    private String apkUrl, apkName, downloadPath;
    private OnDownloadListener listener;

    public HttpDownloadManager(Context context, String downloadPath) {
        this.context = context;
        this.downloadPath = downloadPath;
    }

    @Override
    public void download(String apkUrl, String apkName, OnDownloadListener listener) {
        this.apkUrl = apkUrl;
        this.apkName = apkName;
        this.listener = listener;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(Constant.THREAD_NAME);
                return thread;
            }
        });
        executor.execute(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //删除之前的安装包
            int length = SharePreUtil.getInt(context, Constant.PROGRESS, 0);
            if (length == 0 && FileUtil.fileExists(downloadPath, apkName)) {
                FileUtil.delete(downloadPath, apkName);
            }
            //检查是否需要断点下载
            boolean breakpoint = DownloadManager.getInstance().getConfiguration().isBreakpointDownload();
            if (breakpoint) {
                breakpointDownload();
            } else {
                fullDownload();
            }
        }
    };

    /**
     * 断点下载
     */
    private void breakpointDownload() {
        listener.start();
        int length = getContentLength();
        if (length <= 0) {
            listener.error(new RuntimeException("获取到的文件大小为0！"));
            return;
        }
        try {
            URL url = new URL(apkUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(Constant.HTTP_TIME_OUT);
            con.setConnectTimeout(Constant.HTTP_TIME_OUT);
            con.setRequestProperty("Accept-Encoding", "identity");
            //判断上一次下载一半的文件是否存在
            if (!FileUtil.fileExists(downloadPath, apkName)) {
                //不存在 则从头开始下载
                SharePreUtil.putInt(context, Constant.PROGRESS, 0);
            }
            //上次下载到的位置
            int start = SharePreUtil.getInt(context, Constant.PROGRESS, 0);
            //设置下载位置(从服务器上取要下载文件的某一段) 下载范围
            con.setRequestProperty("Range", "bytes=" + start + "-" + length);
            if (con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                InputStream is = con.getInputStream();
                RandomAccessFile accessFile = FileUtil.createRAFile(downloadPath, apkName);
                if (accessFile != null) {
                    //从上次结束的位置开始下载
                    accessFile.seek(start);
                    int len;
                    //当前已下载完成的进度 (包括之前下载的进度)
                    int progress = start;
                    byte[] buffer = new byte[1024 * 4];
                    while ((len = is.read(buffer)) != -1) {
                        //写入文件
                        accessFile.write(buffer, 0, len);
                        progress += len;
                        //保存当前的下载进度
                        SharePreUtil.putInt(context, Constant.PROGRESS, progress);
                        listener.downloading(length, progress);
                    }
                    //下载完成,将之前保存的进度清0
                    SharePreUtil.putInt(context, Constant.PROGRESS, 0);
                    //释放资源
                    accessFile.close();
                    listener.done(FileUtil.createFile(downloadPath, apkName));

                } else {
                    listener.error(new RuntimeException("apk存储文件创建失败！"));
                }
                is.close();
            } else {
                //走了这里，当前下载地址可能不支持 断点下载；那就使用全量下载
                LogUtil.e(TAG, "breakpointDownload: 当前下载地址可能不支持断点下载，使用全量下载");
                fullDownload();
            }
            con.disconnect();
        } catch (Exception e) {
            listener.error(e);
            e.printStackTrace();
        }
    }

    /**
     * 全部下载
     */
    private void fullDownload() {
        listener.start();
        try {
            URL url = new URL(apkUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(Constant.HTTP_TIME_OUT);
            con.setConnectTimeout(Constant.HTTP_TIME_OUT);
            con.setRequestProperty("Accept-Encoding", "identity");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = con.getInputStream();
                int length = con.getContentLength();
                int len;
                //当前已下载完成的进度
                int progress = 0;
                byte[] buffer = new byte[1024 * 4];
                File file = FileUtil.createFile(downloadPath, apkName);
                FileOutputStream stream = new FileOutputStream(file);
                while ((len = is.read(buffer)) != -1) {
                    //将获取到的流写入文件中
                    stream.write(buffer, 0, len);
                    progress += len;
                    listener.downloading(length, progress);
                }
                //完成io操作,释放资源
                stream.flush();
                stream.close();
                is.close();
                listener.done(file);
            } else {
                listener.error(new SocketTimeoutException("连接超时！"));
            }
            con.disconnect();
        } catch (Exception e) {
            listener.error(e);
            e.printStackTrace();
        }
    }

    /**
     * 首先获取要下载文件的大小
     */
    private int getContentLength() {
        HttpURLConnection con = null;
        try {
            URL url = new URL(apkUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(Constant.HTTP_TIME_OUT);
            con.setConnectTimeout(Constant.HTTP_TIME_OUT);
            //使用 gzip方式获取 解决部分链接无法获取到文件大小问题
            con.setRequestProperty("Accept-Encoding", "identity");
            int length = 0;
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                length = con.getContentLength();
            }
            con.disconnect();
            //返回文件长度
            return length;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
