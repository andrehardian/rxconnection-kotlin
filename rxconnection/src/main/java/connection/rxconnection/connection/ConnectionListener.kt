package connection.rxconnection.connection

/**
 * Created by AndreHF on 1/27/2017.
 */

interface ConnectionListener {
    fun onSuccessWithData(o: Any)
    fun onSuccessNull()
    fun onMessageSuccess(s: String)
    fun onError(o: Any, httpRequest: HttpRequest<*, *>?)
    fun unAuthorized(httpRequest: HttpRequest<*, *>, messageError: String)
}
