package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_addresses")
data class UserAddress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String, // "Home", "Office", "Parents' Place"
    val fullName: String,
    val phoneNumber: String,
    val houseFlatNo: String,
    val streetAddress: String,
    val landmark: String = "",
    val pincode: String,
    val city: String = "Metro City",
    val isDefault: Boolean = false
) {
    fun formattedAddress(): String {
        val lm = if (landmark.isNotBlank()) " (Near $landmark)" else ""
        return "$houseFlatNo, $streetAddress$lm, $city - $pincode"
    }
}
