package connection.rxconnection.connection

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import connection.rxconnection.model.BaseResponse
import connection.rxconnection.model.ModelLog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.Reader

class UtilsQueueOKHttp<T, E>(private var modelLog: ModelLog?, private val logInfoRequestResponse: Boolean, private val eClass: Class<E>,
                             private val callBackOKHttp: CallBackOKHttp, private val callBackForLog: CallBackForLog?, private val requestData: T) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        printLog(call.request(), e.message!!, "0")
        catchSuccessNull(null, e.message!!, null)
    }

    override fun onResponse(call: Call, response: Response) {
        var baseResponse: BaseResponse
        val log = getBodyString(response)
        val code = response.code().toString()
        printLog(call.request(), log, code)
        try {
            if (code.startsWith("2")) {
                var json: E? = null
                try {
                    json = GsonBuilder().setLenient().create().fromJson(log, eClass)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }

                baseResponse = BaseResponse()
                baseResponse.setCode(response.code())
                if (json as Nothing) {
                    catchSuccessNull(response, log, null)
                } else {
                    baseResponse.data = json
                    callBackOKHttp.success<Any>(baseResponse)
                }
            } else {
                catchSuccessNull(response, log, null)
            }
        } catch (e: ExceptionHttpRequest) {
            e.printStackTrace()
            catchSuccessNull(response, log, e)
        }

    }

    private fun catchSuccessNull(response: Response?, error: String, throwable: Throwable?) {
        if (response!!.code().toString().startsWith("2"))
            callBackOKHttp.success<Any>(BaseResponse().setCode(response.code()).setError(error))
        else {
            val exceptionHttpRequest = ExceptionHttpRequest(error, response!!, if (throwable == null)
                Throwable() else throwable)
            callBackOKHttp.error(exceptionHttpRequest)
        }
    }

    private fun printLog(request: Request, response: String, httpCode: String?) {
        try {
            modelLog = ModelLog()
            modelLog!!.body = Gson().toJson(requestData)
            modelLog!!.url = request.url().toString()
            modelLog!!.header = request.headers().toString()
            modelLog!!.error = response
            if (httpCode != null && httpCode.length > 0)
                modelLog!!.httpCode = Integer.parseInt(httpCode)
            callBackForLog?.log(modelLog!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (logInfoRequestResponse) {
            try {
                val s = ("Info\n" + "url : " + request.url() + "\nbody requestData : " + request.body()!!.toString()
                        + "\nrequestData header : " + request.headers() +
                        "\nresponse body : " + response)
                Log.i("rxconnection_log", s)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    fun getBodyString(response: Response): String {
        var error = ""

        try {
            error = response.body()!!.string()
        } catch (e: IOException) {
            e.printStackTrace()
            var value: Int
            var reader: Reader? = null
            try {
                reader = response.body()!!.charStream()
                value = reader!!.read()
                while (value != -1) {
                    error += value.toChar()
                    value = reader!!.read()
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            } finally {
                try {
                    if (reader != null) {
                        reader.close()
                    }
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }

        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            var value: Int
            var reader: Reader? = null
            try {
                reader = response.body()!!.charStream()
                value = reader!!.read()
                while (value != -1) {
                    error += value.toChar()
                    value = reader!!.read()
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
            }
        }

        return error
    }


}
