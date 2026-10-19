package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PickupBooking
import com.example.data.model.RecyclingCenter
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import com.example.data.model.UserAddress
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {
    // Scrap Rates
    @Query("SELECT * FROM scrap_rates ORDER BY isPopular DESC, name ASC")
    fun getAllRates(): Flow<List<ScrapRateItem>>

    @Query("SELECT * FROM scrap_rates WHERE category = :category ORDER BY name ASC")
    fun getRatesByCategory(category: ScrapCategory): Flow<List<ScrapRateItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(items: List<ScrapRateItem>)

    @Query("SELECT COUNT(*) FROM scrap_rates")
    suspend fun getRateCount(): Int

    // Recycling Centers
    @Query("SELECT * FROM recycling_centers ORDER BY distanceKm ASC")
    fun getAllCenters(): Flow<List<RecyclingCenter>>

    @Query("SELECT * FROM recycling_centers WHERE id = :id LIMIT 1")
    fun getCenterById(id: String): Flow<RecyclingCenter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenters(centers: List<RecyclingCenter>)

    @Query("SELECT COUNT(*) FROM recycling_centers")
    suspend fun getCenterCount(): Int

    // Bookings
    @Query("SELECT * FROM pickup_bookings ORDER BY createdAtTimestamp DESC")
    fun getAllBookings(): Flow<List<PickupBooking>>

    @Query("SELECT * FROM pickup_bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: String): Flow<PickupBooking?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: PickupBooking)

    @Update
    suspend fun updateBooking(booking: PickupBooking)

    @Delete
    suspend fun deleteBooking(booking: PickupBooking)

    // User Addresses
    @Query("SELECT * FROM user_addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<UserAddress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: UserAddress): Long

    @Delete
    suspend fun deleteAddress(address: UserAddress)

    @Query("SELECT COUNT(*) FROM user_addresses")
    suspend fun getAddressCount(): Int
}
