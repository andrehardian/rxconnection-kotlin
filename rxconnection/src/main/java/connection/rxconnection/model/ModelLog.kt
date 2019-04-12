package connection.rxconnection.model

import lombok.Data

/**
 * Created by AndreHF on 3/1/2018.
 */

@Data
class ModelLog {
    var error: String? = null
    var header: String? = null
    var body: String? = null
    var url: String? = null
    var exp: Long = 0
    var name: String? = null
    var httpCode: Int = 0
    var isSaved: Boolean = false
}
