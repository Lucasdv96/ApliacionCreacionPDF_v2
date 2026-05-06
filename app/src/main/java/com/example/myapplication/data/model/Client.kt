package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int = 0,
    val name: String = "",
    val cuit: String = "",
    val address: String = "",
    val city: String = "",
    val province: String = "",
    val phone: String = "",
    val email: String = ""
)
