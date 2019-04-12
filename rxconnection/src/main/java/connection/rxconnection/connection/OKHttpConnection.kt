package connection.rxconnection.connection

import android.content.Context
import com.google.gson.Gson
import connection.rxconnection.model.BaseModelRequestFormData
import connection.rxconnection.model.ModelLog
import okhttp3.*
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

/**
 * Created by AndreHF on 1/27/2017.
 */

class OKHttpConnection<T, E>(private val callBackOKHttp: CallBackOKHttp) : Header() {
    internal var formData: Boolean = false
    internal var connectionTimeOut: Int = 0
    internal var readTimeOut: Int = 0
    internal var writeTimeOut: Int = 0
    internal var logInfoRequestResponse: Boolean = false
    internal var callBackForLog: CallBackForLog? = null
    internal var okHttpClient = OkHttpClient()
    internal var modelLog: ModelLog? = null
    private var utilsQueueOKHttp: UtilsQueueOKHttp<*, *>? = null

    private val unsafeOkHttpClient: OkHttpClient
        get() {
            try {

                val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .supportsTlsExtensions(true)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                        .build()

                val sslContext: SSLContext
                sslContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, null)
                sslContext.createSSLEngine()

                val builder = OkHttpClient.Builder()
                val listSpec = arrayListOf<ConnectionSpec>()
                listSpec.add(spec)
                listSpec.add(ConnectionSpec.MODERN_TLS)
                listSpec.add(ConnectionSpec.COMPATIBLE_TLS)
                builder.connectionSpecs(listSpec)
                builder.sslSocketFactory(sslContext.socketFactory)
                builder.hostnameVerifier { hostname, session -> true }
                builder.connectTimeout(connectionTimeOut.toLong(), TimeUnit.MINUTES)
                builder.readTimeout(readTimeOut.toLong(), TimeUnit.MINUTES)
                builder.writeTimeout(writeTimeOut.toLong(), TimeUnit.MINUTES)

                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

    fun download(url: String, fileDownload: File, progressDownloadListener: ProgressDownloadListener, context: Context) {
        okHttpClient = unsafeOkHttpClient
        val request = Request.Builder().headers(this.headers(context)).url(url).build()
        var response: Response? = null
        try {
            response = okHttpClient.newCall(request).execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (response != null) {
            val code = response.code().toString()
            if (code.startsWith("2")) {
                val inputStream = response.body()!!.byteStream()

                val bufferedInputStream = BufferedInputStream(inputStream)
                try {
                    val outputStream = FileOutputStream(fileDownload)
                    var total: Long = 0
                    val dataFile = ByteArray(1024)
                    var count = bufferedInputStream.read(dataFile)
                    while (count != -1) {
                        total += count.toLong()
                        outputStream.write(dataFile, 0, count)
                        progressDownloadListener.progress(total / response.body()!!.contentLength() * 100)
                    }

                    outputStream.flush()
                    outputStream.close()

                    bufferedInputStream?.close()

                    callBackOKHttp.doneDownload()
                } catch (e: FileNotFoundException) {
                    callBackOKHttp.error(ExceptionHttpRequest(e.message!!, response, e))
                } catch (e: IOException) {
                    callBackOKHttp.error(ExceptionHttpRequest(e.message!!, response, e))
                }

            } else {
                progressDownloadListener.error(utilsQueueOKHttp!!.getBodyString(response))
            }
        }
    }


    fun data(t: T, url: String, eClass: Class<E>, httpMethod: Int, mediaType: MediaType,
             context: Context) {
        okHttpClient = unsafeOkHttpClient
        execute(t, url, eClass, httpMethod, mediaType, context)
    }


    private fun execute(t: T, url: String, eClass: Class<E>,
                        httpMethod: Int, mediaType: MediaType, context: Context) {
        var request: Request? = null
        when (httpMethod) {
            HttpMethod.POST -> {
                var requestBody = createBody(mediaType, t)
                request = Request.Builder().headers(headers(context)).post(requestBody).url(url).build()
            }
            HttpMethod.GET -> request = Request.Builder().headers(headers(context)).url(url).get().build()
            HttpMethod.PUT -> {
                var requestBody = createBody(mediaType, t)
                request = Request.Builder().headers(headers(context)).put(requestBody).url(url).build()
            }
            HttpMethod.DELETE -> {
                var requestBody = createBody(mediaType, t)
                request = Request.Builder().headers(headers(context)).delete(requestBody).url(url).build()
            }
        }
        utilsQueueOKHttp = UtilsQueueOKHttp(modelLog,
                logInfoRequestResponse, eClass, callBackOKHttp, callBackForLog, t)
        okHttpClient.newCall(request!!).enqueue(utilsQueueOKHttp!!)

    }

    private fun createBody(mediaType: MediaType, t: T): RequestBody {
        if (formData) {
            val multipartBodyBuilder = MultipartBody.Builder()
            if (t is BaseModelRequestFormData) {
                val baseModelRequestFormData = t as BaseModelRequestFormData
                multipartBodyBuilder.setType(MultipartBody.FORM)
                if (baseModelRequestFormData.modelFormData != null) {
                    for (modelFormData in baseModelRequestFormData.modelFormData!!) {
                        if (modelFormData.value is File) {
                            multipartBodyBuilder.addFormDataPart(modelFormData.key!!, (modelFormData.value as File)
                                    .name,
                                    RequestBody.create(mediaType, (modelFormData.value as File?)!!))
                        } else {
                            multipartBodyBuilder.addFormDataPart(modelFormData.key!!,
                                    (modelFormData.value as String?)!!)
                        }
                    }
                }
            }
            return multipartBodyBuilder.build()
        } else return if (mediaType.toString().contains("form")) {
            bodyForm(t)
        } else {
            RequestBody.create(mediaType, Gson().toJson(t))
        }
    }


    private fun bodyForm(t: T): RequestBody {
        val objectMap = pojo2Map(t!!)
        val formBody = FormBody.Builder()
        for (key in objectMap.keys) {
            formBody.add(key, objectMap[key].toString())
        }
        return formBody.build()
    }

    companion object {

        fun pojo2Map(obj: Any): Map<String, Any> {
            val hashMap = HashMap<String, Any>()
            try {
                val c = obj.javaClass
                val m = c.methods
                for (i in m.indices) {
                    if (m[i].name.indexOf("get") == 0) {
                        val name = m[i].name.toLowerCase().substring(3, 4) + m[i].name.substring(4)
                        hashMap[name] = if (m[i].invoke(obj, *arrayOfNulls(0)) != null)
                            m[i].invoke(obj,
                                    *arrayOfNulls(0))
                        else
                            Any()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            return hashMap
        }
    }


}
