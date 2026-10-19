package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecyclingCenter
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import com.example.data.model.UserAddress
import com.example.ui.BookingDraft
import com.example.ui.components.DigitalScaleVerifiedBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPickupScreen(
    draft: BookingDraft,
    allRates: List<ScrapRateItem>,
    allCenters: List<RecyclingCenter>,
    allAddresses: List<UserAddress>,
    onUpdateDraftQty: (String, Double) -> Unit,
    onUpdateDate: (String) -> Unit,
    onUpdateTimeSlot: (String) -> Unit,
    onUpdateCenter: (String) -> Unit,
    onUpdateContact: (String, String) -> Unit,
    onUpdateAddress: (String, String, String, String) -> Unit,
    onUpdatePayment: (String, String) -> Unit,
    onUpdateSpecialNotes: (String) -> Unit,
    onApplySavedAddress: (UserAddress) -> Unit,
    onSubmitBooking: () -> Unit,
    onNavigateToRates: () -> Unit
) {
    var showAddItemDialog by remember { mutableStateOf(false) }

    // Computations
    val selectedItemCount = draft.selectedItemQuantities.size
    val totalEstimatedEarnings = remember(draft.selectedItemQuantities, allRates) {
        draft.selectedItemQuantities.entries.sumOf { (id, qty) ->
            val item = allRates.find { it.id == id }
            (item?.ratePerUnit ?: 0.0) * qty
        }
    }
    val totalEstimatedWeight = remember(draft.selectedItemQuantities, allRates) {
        draft.selectedItemQuantities.entries.sumOf { (id, qty) ->
            val item = allRates.find { it.id == id }
            if (item?.unit == "kg") qty else (qty * 4.0)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("book_pickup_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Scrap Items Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("1", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text(
                                text = "Selected Scrap Items",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        FilledTonalButton(
                            onClick = { showAddItemDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_scrap_item_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (draft.selectedItemQuantities.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📦", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "No scrap items selected yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Tap 'Add Item' or browse our Rate Card to select recyclables.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddItemDialog = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Choose From Catalog")
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            draft.selectedItemQuantities.forEach { (id, qty) ->
                                val item = allRates.find { it.id == id }
                                if (item != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(item.category.iconEmoji, fontSize = 20.sp)
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "₹${item.ratePerUnit.toInt()}/${item.unit} • Est: ₹${(item.ratePerUnit * qty).toInt()}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        val step = if (item.unit == "kg") 5.0 else 1.0
                                                        onUpdateDraftQty(id, (qty - step).coerceAtLeast(0.0))
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                                }

                                                Text(
                                                    text = "${qty.toInt()} ${item.unit}",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                IconButton(
                                                    onClick = {
                                                        val step = if (item.unit == "kg") 5.0 else 1.0
                                                        onUpdateDraftQty(id, qty + step)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Approx Total Weight: ${"%.1f".format(totalEstimatedWeight)} kg",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Est. Payout: ₹${totalEstimatedEarnings.toInt()}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Step 2: Certified Recycling Center
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("2", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = "Certified Recycling Center",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 1: Auto Assign (Default)
                    val isAuto = draft.selectedCenterId == "auto"
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAuto) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                        border = if (isAuto) CardDefaults.outlinedCardBorder() else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateCenter("auto") }
                            .testTag("center_option_auto")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isAuto,
                                onClick = { onUpdateCenter("auto") }
                            )
                            Column {
                                Text(
                                    text = "Auto-Assign Nearest Certified Hub",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Fastest pickup dispatch • Guaranteed 100% Digital Weighing Scale",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Center Specific Options
                    allCenters.forEach { center ->
                        val isSelected = draft.selectedCenterId == center.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateCenter(center.id) }
                                .testTag("center_option_${center.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onUpdateCenter(center.id) }
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = center.name,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "⭐ ${center.rating}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "${center.licenseNumber} • ${center.distanceKm} km away",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Schedule Date & Time Slot
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("3", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = "Pickup Date & Time Slot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val dates = listOf("Today", "Tomorrow", "In 2 Days")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dates.forEach { d ->
                            FilterChip(
                                selected = draft.pickupDate == d,
                                onClick = { onUpdateDate(d) },
                                label = { Text(d) },
                                modifier = Modifier.testTag("date_chip_$d")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Time Window", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val slots = listOf(
                        "09:00 AM - 12:00 PM",
                        "12:00 PM - 03:00 PM",
                        "03:00 PM - 06:00 PM"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        slots.forEach { slot ->
                            FilterChip(
                                selected = draft.timeSlot == slot,
                                onClick = { onUpdateTimeSlot(slot) },
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                label = { Text(slot) },
                                modifier = Modifier.fillMaxWidth().testTag("slot_chip_$slot")
                            )
                        }
                    }
                }
            }
        }

        // Step 4: Homeowner Contact & Address
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("4", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text(
                                text = "Doorstep Address & Contact",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (allAddresses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Saved Addresses", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            items(allAddresses) { addr ->
                                AssistChip(
                                    onClick = { onApplySavedAddress(addr) },
                                    label = { Text("${addr.label} (${addr.houseFlatNo})") },
                                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = draft.contactName,
                        onValueChange = { onUpdateContact(it, draft.contactPhone) },
                        label = { Text("Contact Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_contact_name")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.contactPhone,
                        onValueChange = { onUpdateContact(draft.contactName, it) },
                        label = { Text("Phone Number for Pickup Executive Call") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_contact_phone")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.houseFlat,
                        onValueChange = { onUpdateAddress(it, draft.streetAddress, draft.landmark, draft.pincode) },
                        label = { Text("Flat / House / Floor No.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_house_flat")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.streetAddress,
                        onValueChange = { onUpdateAddress(draft.houseFlat, it, draft.landmark, draft.pincode) },
                        label = { Text("Society / Street Address") },
                        modifier = Modifier.fillMaxWidth().testTag("input_street_address")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = draft.landmark,
                            onValueChange = { onUpdateAddress(draft.houseFlat, draft.streetAddress, it, draft.pincode) },
                            label = { Text("Landmark (Optional)") },
                            modifier = Modifier.weight(1f).testTag("input_landmark")
                        )
                        OutlinedTextField(
                            value = draft.pincode,
                            onValueChange = { onUpdateAddress(draft.houseFlat, draft.streetAddress, draft.landmark, it) },
                            label = { Text("Pincode") },
                            modifier = Modifier.weight(1f).testTag("input_pincode")
                        )
                    }
                }
            }
        }

        // Step 5: Payout Mode & Special Instructions
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("5", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = "Payout Mode & Doorstep Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val paymentModes = listOf("Instant UPI Transfer", "Cash on Pickup")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        paymentModes.forEach { mode ->
                            FilterChip(
                                selected = draft.paymentMode == mode,
                                onClick = { onUpdatePayment(mode, draft.upiId) },
                                label = { Text(mode) },
                                modifier = Modifier.testTag("pay_mode_${mode.replace(" ", "_")}")
                            )
                        }
                    }

                    if (draft.paymentMode == "Instant UPI Transfer") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = draft.upiId,
                            onValueChange = { onUpdatePayment(draft.paymentMode, it) },
                            label = { Text("Your UPI ID (e.g. mobile@upi, name@okaxis)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_upi_id")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draft.specialNotes,
                        onValueChange = onUpdateSpecialNotes,
                        label = { Text("Special Doorstep Notes (e.g. Elevator available, Bring big sack)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_special_notes")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    DigitalScaleVerifiedBadge()
                }
            }
        }

        // Step 6: Confirmation Action Button
        item {
            Button(
                onClick = onSubmitBooking,
                enabled = draft.selectedItemQuantities.isNotEmpty() && draft.contactPhone.isNotBlank() && draft.houseFlat.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_pickup_booking_button")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (draft.selectedItemQuantities.isEmpty()) "Select at least 1 scrap item" else "Confirm Doorstep Pickup (Est. ₹${totalEstimatedEarnings.toInt()})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal dialog to add more scrap items into draft
    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Scrap Items to Pickup") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 380.dp)
                ) {
                    items(allRates) { item ->
                        val qty = draft.selectedItemQuantities[item.id] ?: 0.0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(item.category.iconEmoji, fontSize = 20.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("₹${item.ratePerUnit.toInt()}/${item.unit}", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val step = if (item.unit == "kg") 5.0 else 1.0
                                            onUpdateDraftQty(item.id, (qty - step).coerceAtLeast(0.0))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "${qty.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            val step = if (item.unit == "kg") 5.0 else 1.0
                                            onUpdateDraftQty(item.id, qty + step)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAddItemDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}
