package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.BookingStatus
import com.example.data.model.ScrapCategory

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromScrapCategory(category: ScrapCategory): String {
        return category.name
    }

    @TypeConverter
    fun toScrapCategory(name: String): ScrapCategory {
        return try {
            ScrapCategory.valueOf(name)
        } catch (e: Exception) {
            ScrapCategory.OTHER
        }
    }

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(name: String): BookingStatus {
        return try {
            BookingStatus.valueOf(name)
        } catch (e: Exception) {
            BookingStatus.BOOKED
        }
    }
}
