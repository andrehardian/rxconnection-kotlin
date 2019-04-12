package connection.rxconnection.connection

import okhttp3.ResponseBody

interface ProgressDownloadListener {
    fun progress(progress: Long)

    fun error(body: String)
}
