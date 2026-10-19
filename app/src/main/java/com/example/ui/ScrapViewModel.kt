package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ScrapDatabase
import com.example.data.model.BookingStatus
import com.example.data.model.PickupBooking
import com.example.data.model.RecyclingCenter
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import com.example.data.model.UserAddress
import com.example.data.repository.ScrapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class AppTab(val title: String, val iconEmoji: String) {
    HOME("Home", "🏠"),
    RATES("Rate Card", "📋"),
    BOOK("Book Pickup", "🚚"),
    MY_PICKUPS("My Pickups", "📦"),
    RECYCLERS("Recyclers", "🏢"),
    IMPACT("Eco Ledger", "🌿")
}

data class CalculatorEntry(
    val item: ScrapRateItem,
    val quantity: Double
)

data class BookingDraft(
    val selectedItemQuantities: Map<String, Double> = emptyMap(), // itemId -> quantity
    val selectedCenterId: String = "auto", // "auto" or specific center ID
    val pickupDate: String = "Tomorrow",
    val timeSlot: String = "10:00 AM - 01:00 PM",
    val contactName: String = "Aarav Sharma",
    val contactPhone: String = "+91 98765 12345",
    val houseFlat: String = "A-402, Green Meadows",
    val streetAddress: String = "Lotus Boulevard, Sector 18",
    val landmark: String = "Opposite Community Park",
    val pincode: String = "110018",
    val paymentMode: String = "Instant UPI Transfer",
    val upiId: String = "aarav@upi",
    val specialNotes: String = "Elevator available on 4th floor.",
    val requiresDigitalScale: Boolean = true
)

class ScrapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScrapRepository

    init {
        val database = ScrapDatabase.getDatabase(application)
        repository = ScrapRepository(database.scrapDao())
        viewModelScope.launch {
            repository.ensureInitialDataSeeded()
        }
    }

    // Active Tab Navigation
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Rate Card Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ScrapCategory?>(null)
    val selectedCategory: StateFlow<ScrapCategory?> = _selectedCategory.asStateFlow()

    private val _selectedRateDetail = MutableStateFlow<ScrapRateItem?>(null)
    val selectedRateDetail: StateFlow<ScrapRateItem?> = _selectedRateDetail.asStateFlow()

    // Calculator State (itemId -> quantity)
    private val _calculatorQuantities = MutableStateFlow<Map<String, Double>>(
        mapOf(
            "paper-newspaper" to 15.0,
            "paper-cardboard" to 10.0,
            "metal-iron" to 8.0
        )
    )
    val calculatorQuantities: StateFlow<Map<String, Double>> = _calculatorQuantities.asStateFlow()

    // Booking Wizard Draft
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    // Selected Booking Detail Modal / Tracking View
    private val _selectedBooking = MutableStateFlow<PickupBooking?>(null)
    val selectedBooking: StateFlow<PickupBooking?> = _selectedBooking.asStateFlow()

    // Notification / Toast Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Data streams from Repository
    val allRates: StateFlow<List<ScrapRateItem>> = repository.allRates.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allCenters: StateFlow<List<RecyclingCenter>> = repository.allCenters.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allBookings: StateFlow<List<PickupBooking>> = repository.allBookings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allAddresses: StateFlow<List<UserAddress>> = repository.allAddresses.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Filtered rates combining category + query
    val filteredRates: StateFlow<List<ScrapRateItem>> = combine(
        allRates,
        _searchQuery,
        _selectedCategory
    ) { rates, query, category ->
        rates.filter { item ->
            val matchesCategory = category == null || item.category == category
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.category.displayName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tab switcher
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ScrapCategory?) {
        _selectedCategory.value = category
    }

    fun selectRateDetail(item: ScrapRateItem?) {
        _selectedRateDetail.value = item
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // Calculator updates
    fun updateCalculatorQuantity(itemId: String, quantity: Double) {
        val current = _calculatorQuantities.value.toMutableMap()
        if (quantity <= 0.0) {
            current.remove(itemId)
        } else {
            current[itemId] = quantity
        }
        _calculatorQuantities.value = current
    }

    fun clearCalculator() {
        _calculatorQuantities.value = emptyMap()
    }

    // Transfer Calculator items into Booking Draft
    fun transferCalculatorToBooking() {
        _bookingDraft.value = _bookingDraft.value.copy(
            selectedItemQuantities = _calculatorQuantities.value
        )
        _currentTab.value = AppTab.BOOK
        _userMessage.value = "Transferred items from calculator to pickup booking!"
    }

    // Booking draft updates
    fun updateDraftItemQuantity(itemId: String, quantity: Double) {
        val current = _bookingDraft.value.selectedItemQuantities.toMutableMap()
        if (quantity <= 0.0) {
            current.remove(itemId)
        } else {
            current[itemId] = quantity
        }
        _bookingDraft.value = _bookingDraft.value.copy(selectedItemQuantities = current)
    }

    fun updateDraftDate(date: String) {
        _bookingDraft.value = _bookingDraft.value.copy(pickupDate = date)
    }

    fun updateDraftTimeSlot(slot: String) {
        _bookingDraft.value = _bookingDraft.value.copy(timeSlot = slot)
    }

    fun updateDraftCenter(centerId: String) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedCenterId = centerId)
    }

    fun updateDraftContact(name: String, phone: String) {
        _bookingDraft.value = _bookingDraft.value.copy(contactName = name, contactPhone = phone)
    }

    fun updateDraftAddress(house: String, street: String, landmark: String, pincode: String) {
        _bookingDraft.value = _bookingDraft.value.copy(
            houseFlat = house,
            streetAddress = street,
            landmark = landmark,
            pincode = pincode
        )
    }

    fun updateDraftPayment(mode: String, upiId: String) {
        _bookingDraft.value = _bookingDraft.value.copy(paymentMode = mode, upiId = upiId)
    }

    fun updateDraftSpecialNotes(notes: String) {
        _bookingDraft.value = _bookingDraft.value.copy(specialNotes = notes)
    }

    fun applySavedAddressToDraft(address: UserAddress) {
        _bookingDraft.value = _bookingDraft.value.copy(
            contactName = address.fullName,
            contactPhone = address.phoneNumber,
            houseFlat = address.houseFlatNo,
            streetAddress = address.streetAddress,
            landmark = address.landmark,
            pincode = address.pincode
        )
        _userMessage.value = "Loaded address: ${address.label}"
    }

    // Submit Booking
    fun submitPickupBooking() {
        viewModelScope.launch {
            val draft = _bookingDraft.value
            val rates = allRates.value
            val centers = allCenters.value

            val selectedItemsMap = draft.selectedItemQuantities
            if (selectedItemsMap.isEmpty()) {
                _userMessage.value = "Please select at least one scrap item for collection."
                return@launch
            }

            var totalWeight = 0.0
            var totalEarnings = 0.0
            val summaryParts = mutableListOf<String>()

            selectedItemsMap.forEach { (itemId, qty) ->
                val rateItem = rates.find { it.id == itemId }
                if (rateItem != null) {
                    val earnings = rateItem.ratePerUnit * qty
                    totalEarnings += earnings
                    if (rateItem.unit == "kg") {
                        totalWeight += qty
                    } else {
                        // approx weight for piece appliances
                        totalWeight += when (rateItem.category) {
                            ScrapCategory.APPLIANCES -> 25.0 * qty
                            ScrapCategory.E_WASTE -> 3.0 * qty
                            ScrapCategory.VEHICLE -> 80.0 * qty
                            else -> 1.0 * qty
                        }
                    }
                    summaryParts.add("${rateItem.name} ($qty ${rateItem.unit})")
                }
            }

            val assignedCenter = if (draft.selectedCenterId == "auto") {
                centers.minByOrNull { it.distanceKm } ?: centers.firstOrNull()
            } else {
                centers.find { it.id == draft.selectedCenterId } ?: centers.firstOrNull()
            }

            val centerId = assignedCenter?.id ?: "rc-1"
            val centerName = assignedCenter?.name ?: "Certified Scrap Recycling Center"

            val bookingId = "SC-" + (Random.nextInt(10000, 99999))
            val addressFormatted = "${draft.houseFlat}, ${draft.streetAddress}" +
                    if (draft.landmark.isNotBlank()) " (Near ${draft.landmark})" else "" +
                            ", Pincode: ${draft.pincode}"

            val newBooking = PickupBooking(
                id = bookingId,
                date = draft.pickupDate,
                timeSlot = draft.timeSlot,
                addressText = addressFormatted,
                contactName = draft.contactName,
                contactPhone = draft.contactPhone,
                centerId = centerId,
                centerName = centerName,
                status = BookingStatus.BOOKED,
                itemsSummary = summaryParts.joinToString(", "),
                estimatedWeightKg = totalWeight,
                estimatedEarnings = totalEarnings,
                paymentMode = draft.paymentMode,
                upiId = draft.upiId,
                executiveName = "Sunil Verma",
                executivePhone = "+91 98112 34567",
                executiveVehicle = "Electric Eco Cargo (DL-03-EV-2091)",
                specialNotes = draft.specialNotes,
                digitalScaleVerified = true,
                createdAtTimestamp = System.currentTimeMillis()
            )

            repository.createBooking(newBooking)

            // Reset draft
            _bookingDraft.value = BookingDraft()
            _selectedBooking.value = newBooking
            _currentTab.value = AppTab.MY_PICKUPS
            _userMessage.value = "Pickup booked successfully! Tracking ID: $bookingId"
        }
    }

    fun selectBookingDetail(booking: PickupBooking?) {
        _selectedBooking.value = booking
    }

    // Status advance simulation (to showcase live order progression!)
    fun advanceStatus(booking: PickupBooking) {
        viewModelScope.launch {
            repository.advanceBookingStatus(booking)
            val updated = repository.getBookingById(booking.id)
            // If viewing this booking, update selected
            _userMessage.value = "Order status updated for ${booking.id}"
        }
    }

    fun cancelBooking(booking: PickupBooking) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
            _userMessage.value = "Booking ${booking.id} has been cancelled."
        }
    }

    fun startBookingWithSingleItem(item: ScrapRateItem) {
        val current = mapOf(item.id to (if (item.unit == "kg") 10.0 else 1.0))
        _bookingDraft.value = _bookingDraft.value.copy(selectedItemQuantities = current)
        _currentTab.value = AppTab.BOOK
        _userMessage.value = "Added ${item.name} to pickup booking!"
    }
}
