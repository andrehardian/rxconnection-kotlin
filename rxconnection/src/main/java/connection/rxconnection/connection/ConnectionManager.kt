package connection.rxconnection.connection

import android.app.ProgressDialog
import android.content.Context
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by AndreHF on 4/12/2017.
 */

open class ConnectionManager : CallBackSubscriber {

    var context: Context? = null
    var isShow = true
        private set
    protected var requestSize = 0

    var connectionListener: ConnectionListener? = null

    var progressDialog: ProgressDialog? = null

    fun setContext(context: Context): ConnectionManager {
        this.context = context
        return this
    }

    fun showDialog(b: Boolean): ConnectionManager {
        isShow = b
        return this
    }

    fun setConnectionListener(connectionListener: ConnectionListener): ConnectionManager {
        this.connectionListener = connectionListener
        return this
    }

    protected fun subscribe(httpRequest: HttpRequest<*, *>) {
        try {
            if (progressDialog == null) {
                progressDialog = ProgressDialog(context)
                progressDialog!!.setCancelable(false)
            }
            if (progressDialog != null && progressDialog!!.isShowing && isShow) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        requestSize += 1
        Observable.create(httpRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(BaseServiceResponse(connectionListener!!).setCallBackSubscriber(this))
    }

    protected fun subscribe(httpRequest: HttpRequest<*, *>, message: String) {

        try {
            if (progressDialog == null && context != null) {
                progressDialog = ProgressDialog(context)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setMessage(message)
            }
            if (!progressDialog!!.isShowing && isShow) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        requestSize += 1
        Observable.create(httpRequest.setMessage(message))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(BaseServiceResponse(connectionListener!!).setCallBackSubscriber(this))
    }

    override fun onServiceFinish() {
        if (progressDialog!!.isShowing && requestSize == 1) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (requestSize > 0)
            requestSize -= 1
    }
}
