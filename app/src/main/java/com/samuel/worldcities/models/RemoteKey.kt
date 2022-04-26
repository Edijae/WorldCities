package com.samuel.worldcities.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val id: Int = 1,
    val currentPage: Int?,
    val nextPage: Int?
)
