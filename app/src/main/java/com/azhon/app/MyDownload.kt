package com.azhon.app

import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.listener.OnDownloadListener


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.app
 * FileName:    MyDownload
 * CreateDate:  2022/4/8 on 11:23
 * Desc:
 *
 * @author   azhon
 */

class MyDownload : BaseHttpDownloadManager() {

    override fun download(apkUrl: String, apkName: String, listener: OnDownloadListener?) {
    }

    override fun cancel() {
    }

    override fun release() {
    }
}