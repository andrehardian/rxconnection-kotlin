package com.app.exera.connection.model

import com.google.gson.annotations.SerializedName

/**
 * Created by AndreHF on 11/14/2017.
 */

class LoginResponse {
    @SerializedName("full_name")
    val fullName: String? = null
    val address: String? = null
    val phone: String? = null
}
