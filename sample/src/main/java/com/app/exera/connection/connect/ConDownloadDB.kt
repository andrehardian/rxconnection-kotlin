package com.app.exera.connection.connect

import android.content.Context

import java.io.File

import connection.rxconnection.connection.HttpRequest
import connection.rxconnection.connection.ProgressDownloadListener

class ConDownloadDB(context: Context, url: String, fileDownload: File,
                    progressDownloadListener: ProgressDownloadListener) :
        HttpRequest<Any, Any>(context, url, fileDownload, progressDownloadListener)
