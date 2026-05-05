package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val id: Int = 0,
    val companyName: String = "",
    val companyRut: String = "",
    val companyAddress: String = "",
    val companyPhone: String = "",
    val companyEmail: String = "",
    val companyCity: String = "",
    val logoPath: String = "", // Path to logo image
    val primaryColor: String = "#4CAF50", // MB Green
    val secondaryColor: String = "#333333", // Black
    val termsCond: String = ""
)
