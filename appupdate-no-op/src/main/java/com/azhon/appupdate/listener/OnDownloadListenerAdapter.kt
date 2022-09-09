package com.azhon.appupdate.listener

import java.io.File


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.listener
 * FileName:    OnDownloadListenerAdapter
 * CreateDate:  2022/4/8 on 10:58
 * Desc:
 *
 * @author   azhon
 */

abstract class OnDownloadListenerAdapter : OnDownloadListener {
    override fun start() {
    }

    override fun downloading(max: Int, progress: Int) {
    }

    override fun done(apk: File) {
    }

    override fun cancel() {
    }

    override fun error(e: Throwable) {
    }
}