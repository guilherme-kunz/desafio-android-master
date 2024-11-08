package com.picpay.desafio.android.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val img: String,
    val name: String,
    val username: String
)