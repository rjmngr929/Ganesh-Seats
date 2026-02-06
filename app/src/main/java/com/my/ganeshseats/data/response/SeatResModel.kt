package com.my.ganeshseats.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeatResModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val seatData: SeatData = SeatData(),
):Parcelable

@Parcelize
data class SeatData (
    @SerializedName("categories") val seatData: ArrayList<SeatCategoryModel> = ArrayList(),
    @SerializedName("total_available_sets") val totalAvailableSets: Int? = 0,
    @SerializedName("actions") val categoryAction: CategoryAction = CategoryAction(),
):Parcelable

@Parcelize
data class SeatCategoryModel (
    @SerializedName("category_name") val categoryName: String? = "",
    @SerializedName("category_total_in_stock_qty") val totalStock: Int? = 0,
    @SerializedName("in_stock") val seatDetailInStock: ArrayList<SeatDetail> = ArrayList(),
    @SerializedName("out_of_stock") val seatDetailOutStock: ArrayList<SeatDetail> = ArrayList()
):Parcelable

@Parcelize
data class SeatDetail (
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = "",
    @SerializedName("car_brand_name") val carBrandName: String? = "",
    @SerializedName("car_model_name") val carModelName: String? = "",
    @SerializedName("category_name") val categoryName: String? = "",
    @SerializedName("material") val material: String? = null,
    @SerializedName("available_stock") val availableStock: Int? = 0,
    @SerializedName("stock_status") val stockStatus: String? = "",
    @SerializedName("image") val seatImage: String? = null,
    @SerializedName("manufacturers") val manufacturersDetail: ArrayList<ManufacturerDetail> = ArrayList(),
    var isSelected: Boolean = false
): Parcelable

@Parcelize
data class ManufacturerDetail (
    @SerializedName("name") val manufatureName: String? = "",
    @SerializedName("mobile") val manufatureNumber: String? = "",
    @SerializedName("sets_manufactured") val manufacturedSet: Int? = 0,
    @SerializedName("created_at") val date: String? = "",
): Parcelable

@Parcelize
data class CategoryAction (
    @SerializedName("create") val createAction: Boolean = false,
    @SerializedName("edit") val editAction: Boolean = false,
):Parcelable