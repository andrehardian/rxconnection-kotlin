package com.app.exera.connection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.app.exera.connection.connect.ServiceManager
import com.app.exera.connection.model.RequestLogin
import connection.rxconnection.connection.ConnectionListener
import connection.rxconnection.connection.HttpRequest

class Main2Activity : AppCompatActivity(), ConnectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            download("https://i.ytimg.com/vi/fYDWCjZ3FLg/maxresdefault.jpg")
        } else {
            ActivityCompat.requestPermissions(this
                    , arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download("https://i.ytimg.com/vi/fYDWCjZ3FLg/maxresdefault.jpg")
            }
        }
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
