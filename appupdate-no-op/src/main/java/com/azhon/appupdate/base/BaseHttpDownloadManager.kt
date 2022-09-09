package com.azhon.appupdate.base

import com.azhon.appupdate.base.bean.DownloadStatus
import kotlinx.coroutines.flow.Flow

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
     */
    abstract fun download(apkUrl: String, apkName: String): Flow<DownloadStatus>

    /**
     * cancel download apk
     */
    abstract fun cancel()

    /**
     * release memory
     */
    abstract fun release()
}