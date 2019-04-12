package com.app.exera.connection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.exera.connection.connect.ServiceManager
import com.app.exera.connection.model.RequestLogin
import connection.rxconnection.connection.ConnectionListener
import connection.rxconnection.connection.HttpRequest

class Main2Activity : AppCompatActivity(),ConnectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        download("https://www.bmw-driver.net/forum/attachment.php?attachmentid=10953&d=1286998273")
    }

    private fun download(s: String) {
        (ServiceManager().setContext(this).setConnectionListener(this) as ServiceManager)
                .download(s)
    }

    private fun login() {
        (ServiceManager().setContext(this).setConnectionListener(this) as ServiceManager)
                .login(RequestLogin().setUsername("kanibal@me.com").setPassword("kanibal"))
    }

    override fun onSuccessWithData(o: Any) {

    }

    override fun onSuccessNull() {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }

    override fun onMessageSuccess(s: String) {}

    override fun onError(o: Any, httpRequest: HttpRequest<*, *>?) {
        Toast.makeText(this, o as String, Toast.LENGTH_LONG)
        //error 400,403,500,etc
    }

    override fun unAuthorized(httpRequest: HttpRequest<*, *>, messageError: String) {
        //error 401 auto logout unauthorized
    }

}
