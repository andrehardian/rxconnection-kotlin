package connection.rxconnection.connection

import connection.rxconnection.model.BaseResponse
import rx.Subscriber

/**
 * Created by AndreHF on 4/12/2017.
 */

class BaseServiceResponse(var connectionListener: ConnectionListener) :
        Subscriber<BaseResponse>() {

    private var callBackSubscriber: CallBackSubscriber? = null

    fun setCallBackSubscriber(callBackSubscriber: CallBackSubscriber): BaseServiceResponse{
        this.callBackSubscriber = callBackSubscriber
        return this
    }

    override fun onCompleted() {
        callBackSubscriber!!.onServiceFinish()
        unsubscribe()
    }

    override fun onError(e: Throwable) {
        callBackSubscriber!!.onServiceFinish()
        if (e is ExceptionHttpRequest) {
            val response = e.response
            if (response.code() == 401) {
                connectionListener.unAuthorized(e.httpRequest!!,
                        e.message!!)
            } else {
                connectionListener.onError(e.message!!,
                        e.httpRequest!!)
            }
        } else {
            connectionListener.onError(e.message!!, null)
        }
    }

    override fun onNext(responseBaseResponse: BaseResponse?) {
        callBackSubscriber!!.onServiceFinish()
        if (responseBaseResponse != null) {
            if (responseBaseResponse.code.toString().startsWith("2")) {
                if (responseBaseResponse.data != null)
                    connectionListener.onSuccessWithData(responseBaseResponse.data!!)
                else if (responseBaseResponse.error != null && responseBaseResponse.error!!
                                .length > 0)
                    connectionListener.onMessageSuccess(responseBaseResponse.error!!)
                else
                    connectionListener.onSuccessNull()
            }
        }
    }

}
