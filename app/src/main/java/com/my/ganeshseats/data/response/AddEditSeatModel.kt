package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName


class AddEditSeatModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val seatData: ArrayList<AddSeatDataModel> = ArrayList(),
)

class AddSeatDataModel (
    @SerializedName("id") val seatId: Int? = 0,
    @SerializedName("name") val seatName: String? = "",
    @SerializedName("car_brand_name") val carBrandName: String? = "",
    @SerializedName("car_model_name") val carModelName: String? = "",
    @SerializedName("category_name") val categoryName: String? = "",
    @SerializedName("material") val material: String? = "",
    @SerializedName("available_stock") val availableStock: Int? = 0,
    @SerializedName("stock_status") val stockStatus: String? = "",
    @SerializedName("image") val seatImage: String? = "",
    @SerializedName("manufacturers") val manufactures: ArrayList<ManufacturerDetail> = ArrayList(),
)

