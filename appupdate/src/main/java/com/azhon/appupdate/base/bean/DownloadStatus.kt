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

    data class Start(val unit: Unit) : DownloadStatus()

    data class Downloading(val max: Int, val progress: Int) : DownloadStatus()

    data class Done(val apk: File) : DownloadStatus()

    data class Cancel(val unit: Unit) : DownloadStatus()

    data class Error(val e: Throwable) : DownloadStatus()
}
