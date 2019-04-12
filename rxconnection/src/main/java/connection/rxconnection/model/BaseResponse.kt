package connection.rxconnection.model

/**
 * Created by AndreHF on 1/27/2017.
 */
class BaseResponse {
    var error: String? = null
    var code: Int = 0
    var data = null
    fun setCode(code: Int): BaseResponse {
        this.code = code
        return this
    }

    fun setError(error: String): BaseResponse {
        this.error = error
        return this
    }
}
