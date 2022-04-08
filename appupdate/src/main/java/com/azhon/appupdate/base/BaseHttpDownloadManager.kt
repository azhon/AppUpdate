package com.azhon.appupdate.base

import com.azhon.appupdate.listener.OnDownloadListener

/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.base
 * FileName:    BaseHttpDownloadManager
 * CreateDate:  2022/4/7 on 10:24
 * Desc:
 *
 * @author azhon
 */

abstract class BaseHttpDownloadManager {
    /**
     * download apk from apkUrl
     *
     * @param apkUrl
     * @param apkName
     * @param listener
     */
    abstract fun download(apkUrl: String, apkName: String, listener: OnDownloadListener?)

    /**
     * cancel download apk
     */
    abstract fun cancel()

    /**
     * release memory
     */
    abstract fun release()
}