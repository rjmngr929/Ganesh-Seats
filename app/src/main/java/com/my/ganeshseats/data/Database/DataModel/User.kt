package com.my.ganeshseats.data.Database.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uid: String = "",
    var userId: Int?,
    var userName: String?,
    var userEmail: String?


){
    fun copyWith(
        userId: Int? = null,
        userName: String? = null,
        userEmail: String? = null
    ) = User(
        userId = userId ?: this.userId,
        userName = userName ?: this.userName,
        userEmail = userEmail ?: this.userEmail

    )
}
