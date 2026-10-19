package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ScrapCategory(val displayName: String, val iconEmoji: String) {
    PAPER("Paper & Books", "📰"),
    METAL("Metals & Scrap", "🥫"),
    PLASTIC("Plastics", "🥤"),
    E_WASTE("E-Waste & Gadgets", "💻"),
    APPLIANCES("Large Appliances", "❄️"),
    VEHICLE("Automobile Scrap", "🚗"),
    OTHER("Glass & Mixed", "🍾")
}

@Entity(tableName = "scrap_rates")
data class ScrapRateItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: ScrapCategory,
    val ratePerUnit: Double, // Price in ₹ or local currency
    val unit: String, // "kg", "piece", "set"
    val description: String,
    val acceptedCondition: String,
    val minimumQuantity: Double = 1.0,
    val isPopular: Boolean = false,
    val ecoCo2PerKg: Double = 1.5, // kg of CO2 saved per kg recycled
    val ecoWaterPerKg: Double = 20.0 // Litres of water saved per kg
)
