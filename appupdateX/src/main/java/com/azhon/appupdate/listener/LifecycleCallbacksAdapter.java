package com.azhon.appupdate.listener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.listener
 * 文件名:    LifecycleCallbacksAdapter
 * 创建时间:  2022/2/22 on 13:52
 * 描述:
 *
 * @author 阿钟
 */


public abstract class LifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
