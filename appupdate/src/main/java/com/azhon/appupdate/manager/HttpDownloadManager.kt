package com.azhon.appupdate.manager

import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.util.LogUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.CancellationException


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
    private var job: Job? = null

    override fun download(apkUrl: String, apkName: String, listener: OnDownloadListener?) {
        shutdown = false

        File(path, apkName).let {
            if (it.exists()) it.delete()
        }
        val coroutineName = CoroutineName(Constant.COROUTINE_NAME)
        job = GlobalScope.launch(Dispatchers.Main + coroutineName) {
            listener?.start()
            try {
                withContext(Dispatchers.IO + coroutineName) {
                    connectToDownload(apkUrl, apkName, listener)
                }
            } catch (e: CancellationException) {
                if (shutdown) listener?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.error(e)
            }
        }
    }

    private suspend fun connectToDownload(
        apkUrl: String, apkName: String, listener: OnDownloadListener?
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
            val outSteam = FileOutputStream(file)
            while (inStream.read(buffer).also { len = it } != -1 && !shutdown) {
                outSteam.write(buffer, 0, len)
                progress += len
                withContext(Dispatchers.Main) { listener?.downloading(length, progress) }
            }
            outSteam.run {
                flush()
                close()
            }
            inStream.close()
            if (!shutdown) {
                withContext(Dispatchers.Main) { listener?.done(file) }
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
            connectToDownload(locationUrl, apkName, listener)
        } else {
            withContext(Dispatchers.Main) {
                listener?.error(SocketTimeoutException("Error: Http response code = ${con.responseCode}"))
            }
        }
        con.disconnect()
    }

    override fun cancel() {
        shutdown = true
        release()
    }

    override fun release() {
        job?.cancel()
    }
}