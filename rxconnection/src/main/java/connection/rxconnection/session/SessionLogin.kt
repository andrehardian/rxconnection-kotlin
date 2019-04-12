package connection.rxconnection.session

import android.content.Context
import android.content.SharedPreferences

class SessionLogin(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val sessionName = "login"
    private val key = "token"

    var token: String?
        get() = sharedPreferences.getString(key, null)
        set(token) {
            editor.putString(key, token)
            editor.commit()
        }

    init {
        sharedPreferences = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun clearToken() {
        editor.clear()
        editor.commit()
    }
}
