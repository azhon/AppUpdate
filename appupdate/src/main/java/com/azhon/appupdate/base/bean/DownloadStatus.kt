package com.azhon.appupdate.base.bean

import java.io.File


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.base.bean
 * FileName:    DownloadStatus
 * CreateDate:  2022/4/14 on 11:18
 * Desc:
 *
 * @author   azhon
 */


sealed class DownloadStatus {

    object Start : DownloadStatus()

    data class Downloading(val max: Int, val progress: Int) : DownloadStatus()

    class Done(val apk: File) : DownloadStatus()

    object Cancel : DownloadStatus()

    data class Error(val e: Throwable) : DownloadStatus()
}
