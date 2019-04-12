package connection.rxconnection.model

import java.util.ArrayList

import lombok.Data

@Data
class BaseModelRequestFormData {

    var modelFormData: ArrayList<ModelFormData>? = null
    fun setModelFormData(modelFormData: ArrayList<ModelFormData>): BaseModelRequestFormData {
        this.modelFormData = modelFormData
        return this
    }
}
