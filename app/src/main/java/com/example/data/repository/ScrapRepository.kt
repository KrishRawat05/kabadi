package com.example.data.repository

import com.example.data.db.ScrapDao
import com.example.data.model.BookingStatus
import com.example.data.model.PickupBooking
import com.example.data.model.RecyclingCenter
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import com.example.data.model.UserAddress
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ScrapRepository(private val dao: ScrapDao) {

    val allRates: Flow<List<ScrapRateItem>> = dao.getAllRates()
    val allCenters: Flow<List<RecyclingCenter>> = dao.getAllCenters()
    val allBookings: Flow<List<PickupBooking>> = dao.getAllBookings()
    val allAddresses: Flow<List<UserAddress>> = dao.getAllAddresses()

    fun getRatesByCategory(category: ScrapCategory): Flow<List<ScrapRateItem>> =
        dao.getRatesByCategory(category)

    fun getBookingById(id: String): Flow<PickupBooking?> =
        dao.getBookingById(id)

    suspend fun createBooking(booking: PickupBooking) {
        dao.insertBooking(booking)
    }

    suspend fun updateBooking(booking: PickupBooking) {
        dao.updateBooking(booking)
    }

    suspend fun cancelBooking(booking: PickupBooking) {
        dao.updateBooking(booking.copy(status = BookingStatus.CANCELLED))
    }

    suspend fun advanceBookingStatus(booking: PickupBooking) {
        val nextStatus = when (booking.status) {
            BookingStatus.BOOKED -> BookingStatus.RECYCLER_ASSIGNED
            BookingStatus.RECYCLER_ASSIGNED -> BookingStatus.AGENT_EN_ROUTE
            BookingStatus.AGENT_EN_ROUTE -> BookingStatus.WEIGHING_INSPECTION
            BookingStatus.WEIGHING_INSPECTION -> BookingStatus.COMPLETED
            BookingStatus.COMPLETED -> BookingStatus.COMPLETED
            BookingStatus.CANCELLED -> BookingStatus.CANCELLED
        }

        val updated = if (nextStatus == BookingStatus.COMPLETED && booking.actualWeightKg == null) {
            booking.copy(
                status = nextStatus,
                actualWeightKg = booking.estimatedWeightKg * 1.05,
                actualEarningsPaid = booking.estimatedEarnings * 1.05
            )
        } else {
            booking.copy(status = nextStatus)
        }

        dao.updateBooking(updated)
    }

    suspend fun saveAddress(address: UserAddress): Long {
        return dao.insertAddress(address)
    }

    suspend fun deleteAddress(address: UserAddress) {
        dao.deleteAddress(address)
    }

    suspend fun ensureInitialDataSeeded() {
        if (dao.getRateCount() == 0) {
            dao.insertRates(sampleRates)
        }
        if (dao.getCenterCount() == 0) {
            dao.insertCenters(sampleCenters)
        }
        if (dao.getAddressCount() == 0) {
            dao.insertAddress(
                UserAddress(
                    label = "Home",
                    fullName = "Aarav Sharma",
                    phoneNumber = "+91 98765 12345",
                    houseFlatNo = "A-402, Green Meadows",
                    streetAddress = "Lotus Boulevard, Sector 18",
                    landmark = "Opposite Community Park",
                    pincode = "110018",
                    city = "Metro Hub",
                    isDefault = true
                )
            )
            dao.insertAddress(
                UserAddress(
                    label = "Office",
                    fullName = "Aarav Sharma",
                    phoneNumber = "+91 98765 12345",
                    houseFlatNo = "Unit 305, Tech Horizon Park",
                    streetAddress = "Outer Ring Expressway",
                    landmark = "Tower B, Phase 2",
                    pincode = "110092",
                    city = "Metro Hub",
                    isDefault = false
                )
            )
        }

        // Check if bookings exist, seed 2 realistic demonstration bookings
        // 1 Active (Agent en route with live status tracking)
        // 1 Completed (with receipt & green certificate)
        val initialBookingId = "SC-74192"
        dao.insertBooking(
            PickupBooking(
                id = initialBookingId,
                date = "Today",
                timeSlot = "10:00 AM - 01:00 PM",
                addressText = "A-402, Green Meadows, Sector 18, Metro Hub - 110018",
                contactName = "Aarav Sharma",
                contactPhone = "+91 98765 12345",
                centerId = "rc-1",
                centerName = "EcoHub Circular Recyclers",
                status = BookingStatus.AGENT_EN_ROUTE,
                itemsSummary = "Old Newspapers (18 kg), Brass Fittings (2.5 kg), Iron Scrap (12 kg)",
                estimatedWeightKg = 32.5,
                estimatedEarnings = 1462.0,
                paymentMode = "Instant UPI Transfer",
                upiId = "aarav@upi",
                executiveName = "Sunil Verma",
                executivePhone = "+91 98112 34567",
                executiveVehicle = "Eco Electric Cargo Van (DL-02-EV-8841)",
                specialNotes = "Elevator available on 4th floor. Digital weight slip requested.",
                digitalScaleVerified = true,
                createdAtTimestamp = System.currentTimeMillis() - 7200000
            )
        )

        dao.insertBooking(
            PickupBooking(
                id = "SC-62180",
                date = "Yesterday",
                timeSlot = "02:00 PM - 05:00 PM",
                addressText = "A-402, Green Meadows, Sector 18, Metro Hub - 110018",
                contactName = "Aarav Sharma",
                contactPhone = "+91 98765 12345",
                centerId = "rc-2",
                centerName = "ShredGreen E-Waste Dismantlers",
                status = BookingStatus.COMPLETED,
                itemsSummary = "Broken Laptop (1 pc), Cardboard Boxes (25 kg), Copper Wire (3 kg)",
                estimatedWeightKg = 29.0,
                estimatedEarnings = 2140.0,
                actualWeightKg = 30.2,
                actualEarningsPaid = 2210.0,
                paymentMode = "Instant UPI Transfer",
                upiId = "aarav@upi",
                executiveName = "Rakesh Mohan",
                executivePhone = "+91 98223 99881",
                executiveVehicle = "Electric Three-Wheeler (DL-08-EV-1904)",
                specialNotes = "Completed doorstep weighing. Digital receipt generated.",
                digitalScaleVerified = true,
                createdAtTimestamp = System.currentTimeMillis() - 86400000
            )
        )
    }

    companion object {
        // Full Scrap Rate Card inspired by ScrapUncle & The Kabadiwala
        val sampleRates = listOf(
            // PAPER & CARDBOARD
            ScrapRateItem(
                id = "paper-newspaper",
                name = "Newspaper (Raddi)",
                category = ScrapCategory.PAPER,
                ratePerUnit = 14.0,
                unit = "kg",
                description = "Daily newspapers, printed broadsheets without plastic wrappers.",
                acceptedCondition = "Dry, neatly bundled or tied.",
                isPopular = true,
                ecoCo2PerKg = 1.6,
                ecoWaterPerKg = 26.0
            ),
            ScrapRateItem(
                id = "paper-cardboard",
                name = "Cardboard / Cartons (Gatta)",
                category = ScrapCategory.PAPER,
                ratePerUnit = 11.0,
                unit = "kg",
                description = "Corrugated shipping cartons, packaging boxes, clean paper boards.",
                acceptedCondition = "Flattened, dry, free of heavy grease or tape.",
                isPopular = true,
                ecoCo2PerKg = 1.4,
                ecoWaterPerKg = 22.0
            ),
            ScrapRateItem(
                id = "paper-books",
                name = "Office Paper & Notebooks",
                category = ScrapCategory.PAPER,
                ratePerUnit = 12.5,
                unit = "kg",
                description = "Used school notebooks, textbooks, white print paper, spiral books.",
                acceptedCondition = "Dry condition with binding intact.",
                isPopular = false,
                ecoCo2PerKg = 1.5,
                ecoWaterPerKg = 25.0
            ),
            ScrapRateItem(
                id = "paper-magazines",
                name = "Magazines & Glossy Paper",
                category = ScrapCategory.PAPER,
                ratePerUnit = 9.0,
                unit = "kg",
                description = "Glossy catalog, periodicals, brochures, coated flyers.",
                acceptedCondition = "Clean and dry.",
                isPopular = false,
                ecoCo2PerKg = 1.2,
                ecoWaterPerKg = 20.0
            ),

            // METALS
            ScrapRateItem(
                id = "metal-copper",
                name = "Copper (Heavy / Wire)",
                category = ScrapCategory.METAL,
                ratePerUnit = 485.0,
                unit = "kg",
                description = "Stripped motor copper windings, heavy plumbing pipes, clean wires.",
                acceptedCondition = "Free of thick rubber insulation for premium rate.",
                isPopular = true,
                ecoCo2PerKg = 4.2,
                ecoWaterPerKg = 60.0
            ),
            ScrapRateItem(
                id = "metal-brass",
                name = "Brass / Peetal",
                category = ScrapCategory.METAL,
                ratePerUnit = 340.0,
                unit = "kg",
                description = "Brass utensils, statues, bathroom valve fittings, door hardware.",
                acceptedCondition = "Clean metal without lead weights.",
                isPopular = true,
                ecoCo2PerKg = 3.8,
                ecoWaterPerKg = 45.0
            ),
            ScrapRateItem(
                id = "metal-aluminum",
                name = "Aluminum Scrap",
                category = ScrapCategory.METAL,
                ratePerUnit = 120.0,
                unit = "kg",
                description = "Aluminum cookware, beverage cans, window extrusion profiles, ladder parts.",
                acceptedCondition = "Empty, clean, without steel rivets.",
                isPopular = true,
                ecoCo2PerKg = 8.5,
                ecoWaterPerKg = 55.0
            ),
            ScrapRateItem(
                id = "metal-iron",
                name = "Iron & Steel (Loha)",
                category = ScrapCategory.METAL,
                ratePerUnit = 32.0,
                unit = "kg",
                description = "Cast iron gates, steel rods, grilles, kitchen scrap, auto parts.",
                acceptedCondition = "Any condition accepted. Digital magnetic scale used.",
                isPopular = true,
                ecoCo2PerKg = 1.8,
                ecoWaterPerKg = 25.0
            ),
            ScrapRateItem(
                id = "metal-tin",
                name = "Tin Containers & Cans",
                category = ScrapCategory.METAL,
                ratePerUnit = 20.0,
                unit = "kg",
                description = "Cooking oil tins, paint cans (empty), food tin containers.",
                acceptedCondition = "Completely drained and dry.",
                isPopular = false,
                ecoCo2PerKg = 1.5,
                ecoWaterPerKg = 20.0
            ),

            // PLASTICS
            ScrapRateItem(
                id = "plastic-hard",
                name = "Hard Plastics (Buckets/Tubs)",
                category = ScrapCategory.PLASTIC,
                ratePerUnit = 16.0,
                unit = "kg",
                description = "HDPE/PP chairs, crates, buckets, mugs, storage containers.",
                acceptedCondition = "Rinsed and dry.",
                isPopular = true,
                ecoCo2PerKg = 2.1,
                ecoWaterPerKg = 35.0
            ),
            ScrapRateItem(
                id = "plastic-pet",
                name = "PET Bottles (Water & Soda)",
                category = ScrapCategory.PLASTIC,
                ratePerUnit = 13.0,
                unit = "kg",
                description = "Transparent beverage bottles, sanitizer bottles.",
                acceptedCondition = "Crushed/flattened, caps removed if possible.",
                isPopular = true,
                ecoCo2PerKg = 2.4,
                ecoWaterPerKg = 40.0
            ),
            ScrapRateItem(
                id = "plastic-soft",
                name = "Soft Plastics / Polythene",
                category = ScrapCategory.PLASTIC,
                ratePerUnit = 10.0,
                unit = "kg",
                description = "LDPE clean bubble wrap, stretch films, packing mailers.",
                acceptedCondition = "Free of organic food waste.",
                isPopular = false,
                ecoCo2PerKg = 1.8,
                ecoWaterPerKg = 30.0
            ),

            // E-WASTE & GADGETS
            ScrapRateItem(
                id = "ewaste-laptop",
                name = "Laptops (Dead / Scrapped)",
                category = ScrapCategory.E_WASTE,
                ratePerUnit = 450.0,
                unit = "piece",
                description = "Non-functional old laptops with motherboard and screen intact.",
                acceptedCondition = "Battery preferably attached, charger not mandatory.",
                isPopular = true,
                ecoCo2PerKg = 12.0,
                ecoWaterPerKg = 120.0
            ),
            ScrapRateItem(
                id = "ewaste-cpu",
                name = "Computer CPU Tower",
                category = ScrapCategory.E_WASTE,
                ratePerUnit = 350.0,
                unit = "piece",
                description = "Desktop computer casing with motherboard, SMPS, and internal parts.",
                acceptedCondition = "Complete unit.",
                isPopular = true,
                ecoCo2PerKg = 10.0,
                ecoWaterPerKg = 100.0
            ),
            ScrapRateItem(
                id = "ewaste-pcb",
                name = "Green Motherboards & Circuit Boards",
                category = ScrapCategory.E_WASTE,
                ratePerUnit = 260.0,
                unit = "kg",
                description = "Printed circuit boards (motherboard, telecom cards, ram sticks).",
                acceptedCondition = "Clean electronic boards.",
                isPopular = false,
                ecoCo2PerKg = 15.0,
                ecoWaterPerKg = 150.0
            ),
            ScrapRateItem(
                id = "ewaste-battery",
                name = "Lead-Acid Inverter / Car Battery",
                category = ScrapCategory.E_WASTE,
                ratePerUnit = 78.0,
                unit = "kg",
                description = "Tall tubular inverter battery, car/truck starting batteries.",
                acceptedCondition = "Acid sealed with caps closed for safety.",
                isPopular = true,
                ecoCo2PerKg = 6.5,
                ecoWaterPerKg = 70.0
            ),
            ScrapRateItem(
                id = "ewaste-smartphone",
                name = "Scrap Smartphones / Tablets",
                category = ScrapCategory.E_WASTE,
                ratePerUnit = 130.0,
                unit = "piece",
                description = "Dead or shattered mobile phones, feature phones.",
                acceptedCondition = "Safe eco-disposal guaranteed.",
                isPopular = false,
                ecoCo2PerKg = 5.0,
                ecoWaterPerKg = 50.0
            ),

            // LARGE APPLIANCES
            ScrapRateItem(
                id = "appliance-ac-split",
                name = "Split AC (Indoor + Outdoor Unit 1.5T)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 4200.0,
                unit = "set",
                description = "Complete 1.5 Ton Split AC with copper condenser coil & compressor.",
                acceptedCondition = "Both indoor and outdoor units included.",
                isPopular = true,
                ecoCo2PerKg = 30.0,
                ecoWaterPerKg = 250.0
            ),
            ScrapRateItem(
                id = "appliance-ac-window",
                name = "Window AC (1.5 Ton)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 2800.0,
                unit = "piece",
                description = "Complete window air conditioner with copper coil and fan.",
                acceptedCondition = "With compressor.",
                isPopular = true,
                ecoCo2PerKg = 25.0,
                ecoWaterPerKg = 200.0
            ),
            ScrapRateItem(
                id = "appliance-fridge-double",
                name = "Refrigerator (Double Door)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 1350.0,
                unit = "piece",
                description = "Frost-free double door refrigerator with compressor.",
                acceptedCondition = "Compressor should be intact.",
                isPopular = true,
                ecoCo2PerKg = 18.0,
                ecoWaterPerKg = 160.0
            ),
            ScrapRateItem(
                id = "appliance-fridge-single",
                name = "Refrigerator (Single Door)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 900.0,
                unit = "piece",
                description = "Direct cool single door refrigerator with compressor.",
                acceptedCondition = "Compressor should be intact.",
                isPopular = false,
                ecoCo2PerKg = 14.0,
                ecoWaterPerKg = 120.0
            ),
            ScrapRateItem(
                id = "appliance-washing-machine",
                name = "Washing Machine (Semi / Fully Auto)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 800.0,
                unit = "piece",
                description = "Top load or front load washing machine with motor.",
                acceptedCondition = "Motor must be present.",
                isPopular = true,
                ecoCo2PerKg = 12.0,
                ecoWaterPerKg = 100.0
            ),
            ScrapRateItem(
                id = "appliance-geyser",
                name = "Water Geyser (Instant / Storage)",
                category = ScrapCategory.APPLIANCES,
                ratePerUnit = 350.0,
                unit = "piece",
                description = "Electric or gas water heater with copper/steel tank.",
                acceptedCondition = "Tank enclosed.",
                isPopular = false,
                ecoCo2PerKg = 5.0,
                ecoWaterPerKg = 50.0
            ),

            // VEHICLE & OTHERS
            ScrapRateItem(
                id = "vehicle-twowheeler",
                name = "Scrap Scooter / Bike",
                category = ScrapCategory.VEHICLE,
                ratePerUnit = 3400.0,
                unit = "piece",
                description = "End-of-life two wheeler with chassis and engine block.",
                acceptedCondition = "RC cancellation document assistance provided.",
                isPopular = false,
                ecoCo2PerKg = 45.0,
                ecoWaterPerKg = 400.0
            ),
            ScrapRateItem(
                id = "other-glass-beer",
                name = "Glass Bottles (Beer / Beverage)",
                category = ScrapCategory.OTHER,
                ratePerUnit = 2.5,
                unit = "piece",
                description = "Standard returnable glass bottles, amber or green.",
                acceptedCondition = "Unbroken, rinsed.",
                isPopular = false,
                ecoCo2PerKg = 0.5,
                ecoWaterPerKg = 8.0
            ),
            ScrapRateItem(
                id = "other-mixed-household",
                name = "Mixed Dry Household Scrap",
                category = ScrapCategory.OTHER,
                ratePerUnit = 9.0,
                unit = "kg",
                description = "Mixed dry recyclable waste sorted by our executive at doorstep.",
                acceptedCondition = "Dry waste only.",
                isPopular = true,
                ecoCo2PerKg = 1.2,
                ecoWaterPerKg = 15.0
            )
        )

        // Certified Recycling Centers Directory
        val sampleCenters = listOf(
            RecyclingCenter(
                id = "rc-1",
                name = "EcoHub Circular Recyclers",
                licenseNumber = "CPCB-REG-2024-0089",
                rating = 4.9,
                totalReviews = 1420,
                address = "Plot 42, Eco-Industrial Zone, Phase 1",
                city = "Metro Hub",
                distanceKm = 2.4,
                phone = "+91 11 4455 6677",
                email = "contact@ecohub-recycling.com",
                operatingHours = "08:30 AM - 07:00 PM (Mon - Sun)",
                certifications = listOf("ISO 14001:2015", "CPCB Registered Recycler", "100% Digital Weighing Certified", "Zero-Landfill Facility"),
                specialities = listOf("Metals", "Paper & Packaging", "Plastics"),
                digitalScaleVerified = true,
                landfillDivertedTons = 480.0,
                description = "Premier government-certified circular waste management hub equipped with calibrated laser digital scales and automated metal sorting plants."
            ),
            RecyclingCenter(
                id = "rc-2",
                name = "ShredGreen E-Waste Dismantlers",
                licenseNumber = "SPCB-EWASTE-DISM-552",
                rating = 4.8,
                totalReviews = 980,
                address = "Shed 12, Green Tech Park, Sector 62",
                city = "Metro Hub",
                distanceKm = 3.8,
                phone = "+91 11 8899 1122",
                email = "ops@shredgreen.org",
                operatingHours = "09:00 AM - 06:30 PM (Mon - Sat)",
                certifications = listOf("Govt Authorized E-Waste Facility", "R2 Compliant", "Data Destruction Certified", "Green Certificate Issuer"),
                specialities = listOf("E-Waste & PCB", "Laptops & Appliances", "Batteries"),
                digitalScaleVerified = true,
                landfillDivertedTons = 230.0,
                description = "Specialized e-waste and large home appliances dismantling center ensuring scientific component extraction, precious metal recovery, and certified hard-drive data shredding."
            ),
            RecyclingCenter(
                id = "rc-3",
                name = "Swachh Bharat CleanCity Depot",
                licenseNumber = "MCD-SWACHH-DPT-104",
                rating = 4.9,
                totalReviews = 2150,
                address = "Depot Road, Near Central Goods Yard",
                city = "Metro Hub",
                distanceKm = 1.9,
                phone = "+91 11 2233 4455",
                email = "support@cleancitydepot.in",
                operatingHours = "08:00 AM - 08:00 PM (Daily)",
                certifications = listOf("Municipal Swachh Partner", "ISO 9001:2015", "Govt Verified Scales", "Instant UPI Payout Partner"),
                specialities = listOf("Paper & Cardboard", "Plastics Granules", "Mixed Scrap"),
                digitalScaleVerified = true,
                landfillDivertedTons = 890.0,
                description = "City municipality accredited scrap collection depot with zero-delay doorstep pickup fleet, transparent weighing apps, and immediate direct bank payouts."
            ),
            RecyclingCenter(
                id = "rc-4",
                name = "Apex Metal & Alloy Foundries",
                licenseNumber = "MET-REG-2023-771",
                rating = 4.7,
                totalReviews = 640,
                address = "Industrial Area Block C, Outer Ring Bypass",
                city = "Metro Hub",
                distanceKm = 5.6,
                phone = "+91 11 7766 5544",
                email = "metals@apexfoundry.com",
                operatingHours = "09:00 AM - 07:00 PM (Mon - Sat)",
                certifications = listOf("Bureau of Indian Standards (BIS)", "Industrial Smelter Certified", "High-Precision Crane Scale"),
                specialities = listOf("Copper & Brass", "Iron & Steel", "Automobile Bodies"),
                digitalScaleVerified = true,
                landfillDivertedTons = 620.0,
                description = "Direct smelting and induction foundry purchasing heavy structural scrap, commercial metals, copper, brass, and end-of-life automobiles with top market rates."
            )
        )
    }
}
