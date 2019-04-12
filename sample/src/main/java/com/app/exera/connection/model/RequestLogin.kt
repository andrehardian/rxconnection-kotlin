package com.app.exera.connection.model

import com.google.gson.annotations.SerializedName

import lombok.Data

@Data
class RequestLogin : java.io.Serializable {

    var password: String? = null
    @SerializedName("grant_type")
    private var grantType = "password"
    @SerializedName("client_secret")
    private var clientSecret = "iBAhimye9KtrTP9tYsGHXW6XyTMczDhGDmaraudy"
    @SerializedName("client_id")
    private var clientId = "3"
    var username: String? = null

    fun setPassword(password: String): RequestLogin {
        this.password = password
        return this
    }

    fun setUsername(username: String): RequestLogin {
        this.username = username
        return this
    }

}
