package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.PickupBooking
import com.example.data.model.RecyclingCenter
import com.example.data.model.ScrapRateItem
import com.example.data.model.UserAddress

@Database(
    entities = [
        ScrapRateItem::class,
        RecyclingCenter::class,
        PickupBooking::class,
        UserAddress::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScrapDatabase : RoomDatabase() {
    abstract fun scrapDao(): ScrapDao

    companion object {
        @Volatile
        private var INSTANCE: ScrapDatabase? = null

        fun getDatabase(context: Context): ScrapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScrapDatabase::class.java,
                    "scrapcycle_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
