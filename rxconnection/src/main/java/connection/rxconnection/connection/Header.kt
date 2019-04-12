package connection.rxconnection.connection

import android.content.Context
import connection.rxconnection.session.SessionLogin
import okhttp3.Headers

/**
 * Created by AndreHF on 4/12/2017.
 */

open class Header {
    var customHeader: Map<String, String>? = null

    protected fun headers(context: Context): Headers {
        val sessionLogin = SessionLogin(context)
        var builder = Headers.Builder()
        if (customHeader != null && customHeader!!.size > 0) {
            for (key in customHeader!!.keys) {
                builder.add(key, customHeader!![key])
            }
        } else {
            if (sessionLogin.token != null) {
                builder.add("Authorization", sessionLogin.token!!)
            }
        }
        return builder.build()
    }

}
