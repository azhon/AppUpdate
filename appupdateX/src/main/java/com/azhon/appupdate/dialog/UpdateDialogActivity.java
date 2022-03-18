package com.azhon.appupdate.dialog;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.azhon.appupdate.R;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.service.DownloadService;
import com.azhon.appupdate.utils.ApkUtil;
import com.azhon.appupdate.utils.Constant;
import com.azhon.appupdate.utils.DensityUtil;
import com.azhon.appupdate.utils.ScreenUtil;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.dialog
 * 文件名:    UpdateDialogActivity
 * 创建时间:  2022/2/18 on 11:07
 * 描述:     显示升级对话框
 *
 * @author 阿钟
 */


public class UpdateDialogActivity extends AppCompatActivity implements View.OnClickListener, OnDownloadListener {
    private DownloadManager manager;
    private Button update;
    private NumberProgressBar progressBar;
    private OnButtonClickListener buttonClickListener;
    private File apk;
    private int dialogImage, dialogButtonTextColor, dialogButtonColor, dialogProgressBarColor;
    private boolean forcedUpgrade;
    private final int install = 0x45F;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setTitle("");
        setContentView(R.layout.dialog_update);
        init();
    }

    /**
     * 初始化布局
     */
    private void init() {
        manager = DownloadManager.getInstance();
        UpdateConfiguration configuration = manager.getConfiguration();
        configuration.setOnDownloadListener(this);
        forcedUpgrade = configuration.isForcedUpgrade();
        buttonClickListener = configuration.getOnButtonClickListener();
        dialogImage = configuration.getDialogImage();
        dialogButtonTextColor = configuration.getDialogButtonTextColor();
        dialogButtonColor = configuration.getDialogButtonColor();
        dialogProgressBarColor = configuration.getDialogProgressBarColor();
        setWindowSize();
        initView();
    }

    private void initView() {
        View ibClose = findViewById(R.id.ib_close);
        ImageView ivBg = findViewById(R.id.iv_bg);
        TextView title = findViewById(R.id.tv_title);
        TextView size = findViewById(R.id.tv_size);
        TextView description = findViewById(R.id.tv_description);
        progressBar = findViewById(R.id.np_bar);
        progressBar.setVisibility(forcedUpgrade ? View.VISIBLE : View.GONE);
        update = findViewById(R.id.btn_update);
        update.setTag(0);
        View line = findViewById(R.id.line);
        update.setOnClickListener(this);
        ibClose.setOnClickListener(this);
        //自定义
        if (dialogImage != -1) {
            ivBg.setBackgroundResource(dialogImage);
        }
        if (dialogButtonTextColor != -1) {
            update.setTextColor(dialogButtonTextColor);
        }
        if (dialogButtonColor != -1) {
            StateListDrawable drawable = new StateListDrawable();
            GradientDrawable colorDrawable = new GradientDrawable();
            colorDrawable.setColor(dialogButtonColor);
            colorDrawable.setCornerRadius(DensityUtil.dip2px(this, 3));
            drawable.addState(new int[]{android.R.attr.state_pressed}, colorDrawable);
            drawable.addState(new int[]{}, colorDrawable);
            update.setBackgroundDrawable(drawable);
        }
        if (dialogProgressBarColor != -1) {
            progressBar.setReachedBarColor(dialogProgressBarColor);
            progressBar.setProgressTextColor(dialogProgressBarColor);
        }
        //强制升级
        if (forcedUpgrade) {
            line.setVisibility(View.GONE);
            ibClose.setVisibility(View.GONE);
        }
        //设置界面数据
        if (!TextUtils.isEmpty(manager.getApkVersionName())) {
            String newVersion = getResources().getString(R.string.dialog_new);
            title.setText(String.format(newVersion, manager.getApkVersionName()));
        }
        if (!TextUtils.isEmpty(manager.getApkSize())) {
            String newVersionSize = getResources().getString(R.string.dialog_new_size);
            size.setText(String.format(newVersionSize, manager.getApkSize()));
            size.setVisibility(View.VISIBLE);
        }
        description.setText(manager.getApkDescription());
    }

    private void setWindowSize() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenUtil.getWidth(this) * 0.7f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_close) {
            if (!forcedUpgrade) {
                finish();
            }
            //回调点击事件
            if (buttonClickListener != null) {
                buttonClickListener.onButtonClick(OnButtonClickListener.CANCEL);
            }
        } else if (id == R.id.btn_update) {
            if ((int) update.getTag() == install) {
                installApk();
                return;
            }
            if (forcedUpgrade) {
                update.setEnabled(false);
                update.setText(R.string.background_downloading);
            } else {
                finish();
            }
            //回调点击事件
            if (buttonClickListener != null) {
                buttonClickListener.onButtonClick(OnButtonClickListener.UPDATE);
            }
            startService(new Intent(this, DownloadService.class));
        }
    }

    /**
     * 强制更新，点击进行安装
     */
    private void installApk() {
        ApkUtil.installApk(this, Constant.AUTHORITIES, apk);
    }

    @Override
    public void onBackPressed() {
        if (forcedUpgrade) return;
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void start() {

    }

    @Override
    public void downloading(int max, int progress) {
        if (max != -1 && progressBar.getVisibility() == View.VISIBLE) {
            int curr = (int) (progress / (double) max * 100.0);
            progressBar.setProgress(curr);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void done(File apk) {
        this.apk = apk;
        if (forcedUpgrade) {
            update.setTag(install);
            update.setEnabled(true);
            update.setText(R.string.click_hint);
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void error(Exception e) {

    }
}
