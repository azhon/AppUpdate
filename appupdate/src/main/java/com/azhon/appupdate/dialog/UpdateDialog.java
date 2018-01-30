package com.azhon.appupdate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.azhon.appupdate.R;
import com.azhon.appupdate.utils.ScreenUtil;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.dialog
 * 文件名:    UpdateDialog
 * 创建时间:  2018/1/30 on 15:13
 * 描述:     TODO 显示升级对话框
 *
 * @author 阿钟
 */


public class UpdateDialog extends Dialog implements View.OnClickListener {

    public UpdateDialog(@NonNull Context context) {
        super(context, R.style.UpdateDialog);
        init(context);
    }

    /**
     * 初始化布局
     */
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        setContentView(view);
        setWindowSize(context);
        initView(view);
    }

    private void initView(View view) {
        view.findViewById(R.id.ib_close).setOnClickListener(this);
    }

    private void setWindowSize(Context context) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenUtil.getWith(context) * 0.7f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ib_close) {
            dismiss();
        } else {
        }
    }
}
