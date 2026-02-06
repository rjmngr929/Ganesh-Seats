package com.my.ganeshseats.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockChangeResModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: ArrayList<ChangedStockData> = ArrayList(),
):Parcelable

@Parcelize
data class ChangedStockData (
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = "",
    @SerializedName("car_brand_name") val carBrandName: String? = "",
    @SerializedName("car_model_name") val carModelName: String? = "",
    @SerializedName("category_name") val categoryName: String? = "",
    @SerializedName("material") val material: String? = "",
    @SerializedName("available_stock") val availableStock: Int? = 0,
    @SerializedName("stock_status") val stockStatus: String? = "",
    @SerializedName("image") val image: String? = "",
    @SerializedName("manufacturers") val manufacturers: ArrayList<ManufacturerDetail> = ArrayList()
):Parcelable

