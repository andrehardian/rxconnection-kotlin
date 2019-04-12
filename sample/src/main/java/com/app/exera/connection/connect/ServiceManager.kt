package com.app.exera.connection.connect

import android.os.Environment
import android.util.Log
import com.app.exera.connection.BuildConfig
import com.app.exera.connection.model.RequestLogin
import connection.rxconnection.connection.ConnectionManager
import connection.rxconnection.connection.ProgressDownloadListener
import java.io.File
import java.util.*

/**
 * Created by AndreHF on 11/14/2017.
 */

class ServiceManager : ConnectionManager(), ProgressDownloadListener {
    fun login(loginRequest: RequestLogin) {
        subscribe(LoginCon(loginRequest, this!!.context!!).setLogInfoRequestResponse(true))
    }

    fun download(s: String) {
        val httpRequest = ConDownloadDB(this!!.context!!, s,
                File(Environment.getExternalStorageDirectory().path),
                this)
        val header = HashMap<String, String>()
        header["version"] = BuildConfig.VERSION_NAME
        httpRequest.setCustomHeader(header)
        subscribe(httpRequest)
    }

    override fun progress(progress: Long) {
        Log.d("test", progress.toString() + "")
    }

    override fun error(body: String) {
        Log.d("test", body)
    }
}
