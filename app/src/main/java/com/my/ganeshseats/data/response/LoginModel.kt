package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName

class LoginModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val loginResponse: LoginDataModel = LoginDataModel(),
)

class LoginDataModel (
    @SerializedName("token") val authToken: String = "",
    @SerializedName("token_type") val tokenType: String = "",
    @SerializedName("expires_in") val tokenExpiry: Long = 0,
    @SerializedName("user") val userData: UserDataModel = UserDataModel(),
)

class UserDataModel (
    @SerializedName("id") val userId: Int = 0,
    @SerializedName("name") val userName: String = "",
    @SerializedName("email") val userEmail: String = "",
)

