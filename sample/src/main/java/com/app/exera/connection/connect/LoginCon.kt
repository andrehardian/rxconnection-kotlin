package com.app.exera.connection.connect

import android.content.Context

import com.app.exera.connection.model.LoginResponse
import com.app.exera.connection.model.RequestLogin

import connection.rxconnection.connection.HttpMethod
import connection.rxconnection.connection.HttpRequest

/**
 * Created by AndreHF on 11/14/2017.
 */

class LoginCon(loginRequest: RequestLogin, context: Context) :
        HttpRequest<RequestLogin, LoginResponse>(loginRequest, context, LoginResponse::class.java,
                URL.LOGIN, HttpMethod.POST)
