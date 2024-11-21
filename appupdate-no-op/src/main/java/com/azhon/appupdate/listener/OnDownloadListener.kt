package com.azhon.appupdate.listener

import java.io.File

/**
 * createDate:  2022/4/7 on 10:27
 * desc:
 *
 * @author azhon
 */

interface OnDownloadListener {
    /**
     * start download
     */
    fun start()

    /**
     *
     * @param max      file length
     * @param progress downloaded file size
     */
    fun downloading(max: Int, progress: Int)

    /**
     * @param apk
     */
    fun done(apk: File)

    /**
     * cancel download
     */
    fun cancel()

    /**
     *
     * @param e
     */
    fun error(e: Throwable)
}