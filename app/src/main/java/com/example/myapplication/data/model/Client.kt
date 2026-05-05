package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int = 0,
    val name: String = "",
    val rut: String = "",
    val address: String = "",
    val city: String = "",
    val commune: String = "",
    val phone: String = "",
    val email: String = ""
)
