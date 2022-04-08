package com.azhon.appupdate.listener

import android.app.Activity
import android.app.Application
import android.os.Bundle


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.listener
 * FileName:    LifecycleCallbacksAdapter
 * CreateDate:  2022/4/8 on 11:26
 * Desc:
 *
 * @author   azhon
 */

abstract class LifecycleCallbacksAdapter : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}