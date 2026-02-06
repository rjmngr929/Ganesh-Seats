package com.my.ganeshseats.data.response

import com.google.gson.annotations.SerializedName

class BrandModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val brandResponse: BrandDataModel = BrandDataModel(),
)

class BrandDataModel (
    @SerializedName("data") val brandData: ArrayList<BrandData> = ArrayList(),
    @SerializedName("total_in_stock_qty") val totalStock: String? = null,
    @SerializedName("actions") val brandActions: BrandActionsModel = BrandActionsModel()
)

class BrandData (
    @SerializedName("id") val brandId: Int? = 0,
    @SerializedName("name") val brandName: String? = "",
    @SerializedName("image") val brandImage: String? = null,
    @SerializedName("status") val brandStatus: String? = ""
)

class BrandActionsModel (
    @SerializedName("create") val createAction: Boolean = false,
    @SerializedName("edit") val editAction: Boolean = false
)
