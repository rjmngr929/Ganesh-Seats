package com.my.ganeshseats.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeleteSeatsModel (
    @SerializedName("success") val status: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val seatData: ArrayList<SeatDetail> = ArrayList(),
):Parcelable
