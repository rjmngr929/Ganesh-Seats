package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName


class CategoryResModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val categoryManufactureData: CategoryManufactureData = CategoryManufactureData(),
)

class CategoryManufactureData (
    @SerializedName("categories") val categoryAryData: ArrayList<CategoryDataModel> = ArrayList(),
    @SerializedName("manufacturers") val manufactureAryData: ArrayList<ManufactureModelSelect> = ArrayList(),
)

class CategoryDataModel (
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = "",
//    @SerializedName("image") val image: String? = "",
//    @SerializedName("description") val description: String? = "",
//    @SerializedName("status") val status: String? = "",
)

class ManufactureModelSelect (
    @SerializedName("id") val manufactureId: Int? = 0,
    @SerializedName("name") val manufactureName: String? = ""
)


