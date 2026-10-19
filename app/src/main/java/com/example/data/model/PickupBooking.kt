package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus(val displayTitle: String, val badgeColorHex: Long) {
    BOOKED("Booking Confirmed", 0xFF2563EB),
    RECYCLER_ASSIGNED("Recycler Assigned", 0xFF0D9488),
    AGENT_EN_ROUTE("Pickup Executive En Route", 0xFFD97706),
    WEIGHING_INSPECTION("Doorstep Weighing", 0xFF7C3AED),
    COMPLETED("Completed & Paid", 0xFF16A34A),
    CANCELLED("Cancelled", 0xFFDC2626)
}

data class BookingItemEntry(
    val itemId: String,
    val itemName: String,
    val quantity: Double,
    val unit: String,
    val ratePerUnit: Double,
    val estimatedSubtotal: Double
)

@Entity(tableName = "pickup_bookings")
data class PickupBooking(
    @PrimaryKey
    val id: String, // e.g. "SC-98214"
    val date: String, // "Today, 12 Oct", or "2026-10-12"
    val timeSlot: String, // "09:00 AM - 12:00 PM", "12:00 PM - 03:00 PM", "03:00 PM - 06:00 PM"
    val addressText: String,
    val contactName: String,
    val contactPhone: String,
    val centerId: String,
    val centerName: String,
    val status: BookingStatus,
    val itemsSummary: String, // e.g. "Newspaper (15 kg), Copper (2 kg)"
    val estimatedWeightKg: Double,
    val estimatedEarnings: Double,
    val actualWeightKg: Double? = null,
    val actualEarningsPaid: Double? = null,
    val paymentMode: String = "Instant UPI", // "Instant UPI", "Cash on Pickup"
    val upiId: String = "",
    val executiveName: String = "Rahul Sharma",
    val executivePhone: String = "+91 98765 43210",
    val executiveVehicle: String = "Eco Electric Van (DL-01-EV-4412)",
    val specialNotes: String = "",
    val digitalScaleVerified: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
