package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName


class AddBrandResModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val brandData: AddBrandData = AddBrandData(),
)

class AddBrandData (
    @SerializedName("id") val brandId: Int? = 0,
    @SerializedName("name") val brandName: String? = "",
    @SerializedName("image") val brandImage: String? = "",
    @SerializedName("status") val status: String? = "",
)

