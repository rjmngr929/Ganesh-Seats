package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName



class AddModelRes (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val carModelData: CarModelData = CarModelData(),
)

class CarModelData (
    @SerializedName("id") val modelId: Int? = 0,
    @SerializedName("car_brand_id") val brandId: String? = "",
    @SerializedName("name") val modelName: String? = "",
    @SerializedName("image") val modelImage: String? = "",
    @SerializedName("status") val status: String? = "",
    @SerializedName("total_seat_covers") val totalSeatCovers: Int? = 0,
)
