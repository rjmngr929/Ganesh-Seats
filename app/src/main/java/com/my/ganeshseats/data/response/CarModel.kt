package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName


class CarModelRes (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val carResponse: CarDataModel = CarDataModel(),
)

class CarDataModel (
    @SerializedName("data") val carModelData: ArrayList<CarDetailModel> = ArrayList(),
    @SerializedName("total_in_stock_qty") val totalStock: Int? = null,
    @SerializedName("actions") val carActions: CarActionsModel = CarActionsModel()
)

class CarDetailModel (
    @SerializedName("id") val carModelId: Int? = 0,
    @SerializedName("car_brand_id") val brandId: Int? = 0,
    @SerializedName("name") val modelName: String? = "",
    @SerializedName("image") val modelImage: String? = "",
    @SerializedName("total_seat_covers") val seatCoverAvailable: String? = "",
    @SerializedName("status") val carModelStatus: String? = ""
)

class CarActionsModel (
    @SerializedName("create") val createAction: Boolean = false,
    @SerializedName("edit") val editAction: Boolean = false
)
