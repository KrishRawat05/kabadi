package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recycling_centers")
data class RecyclingCenter(
    @PrimaryKey
    val id: String,
    val name: String,
    val licenseNumber: String, // e.g. "CPCB-REG-2024-889"
    val rating: Double,
    val totalReviews: Int,
    val address: String,
    val city: String,
    val distanceKm: Double,
    val phone: String,
    val email: String,
    val operatingHours: String,
    val certifications: List<String>, // "ISO 14001:2015", "Govt Authorized E-Waste Recycler", etc.
    val specialities: List<String>, // "Metals", "E-Waste", "Industrial Paper"
    val digitalScaleVerified: Boolean = true,
    val landfillDivertedTons: Double = 120.0,
    val description: String
)
