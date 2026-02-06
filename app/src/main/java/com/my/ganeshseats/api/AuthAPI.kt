package com.my.ganeshseats.api

import com.my.ganeshseats.data.response.LoginModel
import com.my.ganeshseats.data.response.ResponseModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthAPI {

    @FormUrlEncoded
    @POST("mobile/login")
    suspend fun login(@Field("email") email: String, @Field("password") password: String) : Response<LoginModel>




}