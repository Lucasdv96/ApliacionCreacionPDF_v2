package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemType(val displayName: String, val icon: String) {
    WINDOW("Ventana", "🪟"),
    DOOR("Puerta", "🚪"),
    RAILING("Baranda", "🛤️"),
    OTHER("Otro", "📦");

    companion object {
        fun fromString(value: String): ItemType = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            OTHER
        }
    }
}
