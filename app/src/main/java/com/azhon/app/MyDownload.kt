package com.azhon.app

import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.base.bean.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * createDate: 2022/4/8 on 11:23
 * desc:
 *
 * @author azhon
 */

class MyDownload : BaseHttpDownloadManager() {


    override fun download(
        apkUrl: String, apkName: String
    ): Flow<DownloadStatus> {
        return flow { }
    }

    override fun cancel() {
    }

    override fun release() {
    }
}