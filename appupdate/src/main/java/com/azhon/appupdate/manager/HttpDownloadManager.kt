package com.azhon.appupdate.manager

import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.base.bean.DownloadStatus
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.util.LogUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.manager
 * FileName:    HttpDownloadManager
 * CreateDate:  2022/4/7 on 14:29
 * Desc:
 *
 * @author   azhon
 */

@Suppress("BlockingMethodInNonBlockingContext")
class HttpDownloadManager(private val path: String) : BaseHttpDownloadManager() {
    companion object {
        private const val TAG = "HttpDownloadManager"
    }

    private var shutdown: Boolean = false

    override fun download(apkUrl: String, apkName: String): Flow<DownloadStatus> {
        shutdown = false
        File(path, apkName).let {
            if (it.exists()) it.delete()
        }
        return flow {
            emit(DownloadStatus.Start(Unit))
            connectToDownload(apkUrl, apkName, this)
        }.catch {
            emit(DownloadStatus.Error(it))
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun connectToDownload(
        apkUrl: String, apkName: String, flow: FlowCollector<DownloadStatus>
    ) {
        val con = URL(apkUrl).openConnection() as HttpURLConnection
        con.apply {
            requestMethod = "GET"
            readTimeout = Constant.HTTP_TIME_OUT
            connectTimeout = Constant.HTTP_TIME_OUT
            setRequestProperty("Accept-Encoding", "identity")
        }
        if (con.responseCode == HttpURLConnection.HTTP_OK) {
            val inStream = con.inputStream
            val length = con.contentLength
            var len: Int
            var progress = 0
            val buffer = ByteArray(1024 * 2)
            val file = File(path, apkName)
            FileOutputStream(file).use { out ->
                while (inStream.read(buffer).also { len = it } != -1 && !shutdown) {
                    out.write(buffer, 0, len)
                    progress += len
                    flow.emit(DownloadStatus.Downloading(length, progress))
                }
                out.flush()
            }
            inStream.close()
            if (shutdown) {
                flow.emit(DownloadStatus.Cancel(Unit))
            } else {
                flow.emit(DownloadStatus.Done(file))
            }
        } else if (con.responseCode == HttpURLConnection.HTTP_MOVED_PERM
            || con.responseCode == HttpURLConnection.HTTP_MOVED_TEMP
        ) {
            con.disconnect()
            val locationUrl = con.getHeaderField("Location")
            LogUtil.d(
                TAG,
                "The current url is the redirect Url, the redirected url is $locationUrl"
            )
            connectToDownload(locationUrl, apkName, flow)
        } else {
            val e = SocketTimeoutException("Error: Http response code = ${con.responseCode}")
            flow.emit(DownloadStatus.Error(e))
        }
        con.disconnect()
    }

    override fun cancel() {
        shutdown = true
    }

    override fun release() {
    }
}