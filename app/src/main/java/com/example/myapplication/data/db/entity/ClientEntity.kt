package com.example.myapplication.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val cuit: String = "",
    val address: String = "",
    val city: String = "",
    val province: String = "",
    val phone: String = "",
    val email: String = ""
)
