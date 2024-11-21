package com.azhon.appupdate.listener

import java.io.File


/**
 * createDate:  2022/4/8 on 10:58
 * desc:
 *
 * @author azhon
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