package com.example

import com.example.data.model.BookingStatus
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testScrapRateCalculation() {
        val copperItem = ScrapRateItem(
            id = "metal-copper",
            name = "Copper",
            category = ScrapCategory.METAL,
            ratePerUnit = 485.0,
            unit = "kg",
            description = "Copper wire",
            acceptedCondition = "Dry"
        )
        val quantity = 4.5
        val earnings = copperItem.ratePerUnit * quantity
        assertEquals(2182.5, earnings, 0.01)
    }

    @Test
    fun testEcoImpactMetrics() {
        val paperWeightKg = 100.0
        val treesSaved = paperWeightKg * 0.017
        val co2ReducedKg = paperWeightKg * 1.85
        val waterSavedLitres = paperWeightKg * 24.5

        assertEquals(1.7, treesSaved, 0.001)
        assertEquals(185.0, co2ReducedKg, 0.001)
        assertEquals(2450.0, waterSavedLitres, 0.001)
    }

    @Test
    fun testBookingStatusDisplay() {
        assertEquals("Booking Confirmed", BookingStatus.BOOKED.displayTitle)
        assertEquals("Pickup Executive En Route", BookingStatus.AGENT_EN_ROUTE.displayTitle)
        assertEquals("Completed & Paid", BookingStatus.COMPLETED.displayTitle)
    }
}
