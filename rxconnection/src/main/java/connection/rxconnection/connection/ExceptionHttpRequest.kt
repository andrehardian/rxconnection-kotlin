package connection.rxconnection.connection

import java.io.IOException

import lombok.Getter
import lombok.Setter
import okhttp3.Response

/**
 * Created by AndreHF on 4/12/2017.
 */

class ExceptionHttpRequest : RuntimeException {

    @Getter
    lateinit var url: String
    @Getter
    var response: Response
    @Getter
    lateinit var kind: Kind
    @Getter
    @Setter
    var httpRequest: HttpRequest<*, *>? = null
    enum class Kind {
        NETWORK, HTTP, UNEXPECTED
    }

    constructor(
            message: String, url: String?, response: Response?,
            kind: Kind, exception: Throwable?) : super(message, exception) {
        this.url = url!!
        this.response = response!!
        this.kind = kind
    }

    constructor(
            message: String, response: Response,
            exception: Throwable) : super(message, exception) {
        this.response = response
    }

    companion object {

        fun httpError(url: String, response: Response): ExceptionHttpRequest {
            val message = response.code().toString() + " " + response.message()
            return ExceptionHttpRequest(message, url, response, Kind.HTTP, null)
        }

        fun networkError(exception: IOException): ExceptionHttpRequest {
            return ExceptionHttpRequest(exception.message!!, null, null, Kind.NETWORK, exception)
        }

        fun unexpectedError(exception: Throwable): ExceptionHttpRequest {
            return ExceptionHttpRequest(exception.message!!, null, null, Kind.UNEXPECTED,
                    exception)
        }
    }

}
