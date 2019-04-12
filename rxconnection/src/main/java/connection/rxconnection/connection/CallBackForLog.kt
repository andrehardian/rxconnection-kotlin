package connection.rxconnection.connection

import connection.rxconnection.model.ModelLog

/**
 * Created by AndreHF on 3/8/2018.
 */

interface CallBackForLog {
    fun log(modelLog: ModelLog)
}
