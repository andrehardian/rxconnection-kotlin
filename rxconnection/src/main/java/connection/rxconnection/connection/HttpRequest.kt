package connection.rxconnection.connection

import android.content.Context
import connection.rxconnection.model.BaseResponse
import lombok.Getter
import okhttp3.MediaType
import okhttp3.OkHttpClient
import rx.Observable
import rx.Subscriber
import java.io.File

/**
 * Created by AndreHF on 4/12/2017.
 */

open class HttpRequest<REQUEST, RESPONSE> : CallBackOKHttp, Observable.OnSubscribe<BaseResponse> {
    @Getter
    var request: REQUEST? = null
    @Getter
    var teokHttpConnection: OKHttpConnection<REQUEST, RESPONSE>? = null
        private set
    @Getter
    val context: Context
    private var mediaType: MediaType? = null
    private var eClass: Class<RESPONSE>? = null
    private val url: String
    private var httpMethod: Int = 0
    private var userType: String? = null
    private var subscriber: Subscriber<in BaseResponse>? = null
    private var customHeader: Map<String, String>? = null
    private var formData: Boolean = false
    private var logInfoRequestResponse: Boolean = false
    private var downloadFile: Boolean = false
    private lateinit var fileDownload: File
    private var connectionTimeout = 1
    private var readTimeout = 1
    private var writeTimeout = 1
    private lateinit var progressDownloadListener: ProgressDownloadListener

    private var callBackForLog: CallBackForLog? = null

    @Getter
    private var message: String? = null

    val okhttpClient: OkHttpClient
        get() = teokHttpConnection!!.okHttpClient


    fun setReadTimeout(readTimeout: Int): HttpRequest<REQUEST, RESPONSE> {
        this.readTimeout = readTimeout
        return this
    }

    fun setWriteTimeout(writeTimeout: Int): HttpRequest<REQUEST, RESPONSE> {
        this.writeTimeout = writeTimeout
        return this
    }


    fun setConnectionTimeout(connectionTimeout: Int): HttpRequest<REQUEST, RESPONSE> {
        this.connectionTimeout = connectionTimeout
        return this
    }

    fun setCallBackForLog(callBackForLog: CallBackForLog): HttpRequest<REQUEST, RESPONSE> {
        this.callBackForLog = callBackForLog
        return this
    }

    fun setMessage(message: String): HttpRequest<REQUEST, RESPONSE> {
        this.message = message
        return this
    }

    fun setLogInfoRequestResponse(logInfoRequestResponse: Boolean): HttpRequest<REQUEST, RESPONSE> {
        this.logInfoRequestResponse = logInfoRequestResponse
        return this
    }


    constructor(request: REQUEST, context: Context, resultClass: Class<RESPONSE>, url: String,
                httpMethod: Int) {
        //        super(f);
        this.request = request
        this.context = context
        this.eClass = resultClass
        this.url = url
        this.httpMethod = httpMethod
        teokHttpConnection = OKHttpConnection(this)
        this.mediaType = MediaType.parse(org.androidannotations.api.rest.MediaType.APPLICATION_JSON + "; charset=utf-8")
    }

    constructor(context: Context, url: String, fileDownload: File, progressDownloadListener: ProgressDownloadListener) {
        //        super(f);
        this.progressDownloadListener = progressDownloadListener
        this.fileDownload = fileDownload
        downloadFile = true
        this.context = context
        this.url = url
        teokHttpConnection = OKHttpConnection(this)
    }

    constructor(context: Context, resultClass: Class<RESPONSE>, url: String, httpMethod: Int) {
        //        super(f);
        this.context = context
        this.eClass = resultClass
        this.url = url
        this.httpMethod = httpMethod
        teokHttpConnection = OKHttpConnection(this)
        this.mediaType = MediaType.parse(org.androidannotations.api.rest.MediaType.APPLICATION_JSON + "; charset=utf-8")
    }

    fun setMediaType(mediaType: String): HttpRequest<REQUEST, RESPONSE> {
        this.mediaType = MediaType.parse("$mediaType; charset=utf-8")
        return this
    }

    fun setUserType(userType: String): HttpRequest<REQUEST, RESPONSE> {
        this.userType = userType
        return this
    }

    fun setCustomHeader(customHeader: Map<String, String>): HttpRequest<REQUEST, RESPONSE> {
        this.customHeader = customHeader
        return this
    }

    fun setFormData(formData: Boolean): HttpRequest<REQUEST, RESPONSE> {
        this.formData = formData
        return this
    }


    override fun call(subscriber: Subscriber<in BaseResponse>) {
        this.subscriber = subscriber
        teokHttpConnection!!.customHeader = customHeader
        if (downloadFile) {
            teokHttpConnection!!.download(url, fileDownload, progressDownloadListener, context)
        } else {
            teokHttpConnection!!.connectionTimeOut = connectionTimeout
            teokHttpConnection!!.readTimeOut = readTimeout
            teokHttpConnection!!.writeTimeOut = writeTimeout
            teokHttpConnection!!.formData = formData
            teokHttpConnection!!.logInfoRequestResponse = logInfoRequestResponse
            teokHttpConnection!!.callBackForLog = callBackForLog
            teokHttpConnection!!.data(request!!, url, eClass!!, httpMethod, if (formData)
                MediaType.parse(org.androidannotations.api.rest.MediaType.MULTIPART_FORM_DATA)!!
            else
                mediaType!!, context)
        }
    }

    override fun error(exceptionHttpRequest: ExceptionHttpRequest) {
        exceptionHttpRequest.httpRequest = this
        subscriber!!.onError(exceptionHttpRequest)
    }

    override fun <T> success(t: T) {
        var response: BaseResponse
        try {
            response = t as BaseResponse
        } catch (e: Exception) {
            e.printStackTrace()
            response = BaseResponse()
            response.setError(e.message!!)
        }

        subscriber!!.onNext(response)
    }

    override fun doneDownload() {
        subscriber!!.onCompleted()
    }

}
