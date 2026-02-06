package com.my.raido.models.Database.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chat")
data class ChatModel(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    var message: String?,
    var dateTime: String?,
    var userType: String?,
){
    fun copyWith(
        message: String? = null,
        dateTime: String? = null,
        userType: String? = null
    ) = ChatModel(
        message = message ?: this.message,
        dateTime = dateTime ?: this.dateTime,
        userType = userType ?: this.userType
    )
}