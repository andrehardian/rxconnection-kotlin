package connection.rxconnection.connection

/**
 * Created by AndreHF on 1/27/2017.
 */

interface CallBackOKHttp {
    fun error(exceptionHttpRequest: ExceptionHttpRequest)
    fun <T> success(t: T)
    fun doneDownload()
}
