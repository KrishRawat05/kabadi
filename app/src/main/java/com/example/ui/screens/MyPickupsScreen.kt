package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.data.model.PickupBooking
import com.example.ui.components.DigitalScaleVerifiedBadge
import com.example.ui.components.PickupStatusStepper
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BadgeCertifiedGreen
import com.example.ui.theme.CashGold
import com.example.ui.theme.EcoGreenPrimaryContainerLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPickupsScreen(
    bookings: List<PickupBooking>,
    selectedBooking: PickupBooking?,
    onSelectBooking: (PickupBooking?) -> Unit,
    onAdvanceStatus: (PickupBooking) -> Unit,
    onCancelBooking: (PickupBooking) -> Unit,
    onNavigateToBook: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Active, 1: Completed
    var showReceiptDialog by remember { mutableStateOf<PickupBooking?>(null) }
    var showCertificateDialog by remember { mutableStateOf<PickupBooking?>(null) }

    val activeList = bookings.filter { it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED }
    val historyList = bookings.filter { it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_pickups_screen")
    ) {
        // Tab row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Active Pickups (${activeList.size})") },
                modifier = Modifier.testTag("tab_active_pickups")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Completed & History (${historyList.size})") },
                modifier = Modifier.testTag("tab_history_pickups")
            )
        }

        val displayedList = if (selectedTab == 0) activeList else historyList

        if (displayedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (selectedTab == 0) "🚚" else "📜", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "No Active Doorstep Pickups" else "No Completed Pickups Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedTab == 0)
                            "Schedule your scrap pickup today and get doorstep digital weighing."
                        else
                            "Your completed pickups, weighing slips, and green certificates will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToBook, shape = RoundedCornerShape(10.dp)) {
                            Text("Schedule Doorstep Pickup")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayedList) { booking ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_card_${booking.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Booking Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Tracking ID: ${booking.id}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${booking.date} • ${booking.timeSlot}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(status = booking.status)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive Status Stepper
                            PickupStatusStepper(currentStatus = booking.status)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Assigned Recycler & Agent
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = booking.centerName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Executive: ${booking.executiveName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = booking.executiveVehicle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                                            FilledTonalIconButton(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.executivePhone}"))
                                                    try { context.startActivity(intent) } catch (e: Exception) {}
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Address & Items Summary
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = booking.addressText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = booking.itemsSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Financial & Weight Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    val isDone = booking.status == BookingStatus.COMPLETED
                                    val weight = if (isDone && booking.actualWeightKg != null) booking.actualWeightKg else booking.estimatedWeightKg
                                    val earnings = if (isDone && booking.actualEarningsPaid != null) booking.actualEarningsPaid else booking.estimatedEarnings

                                    Text(
                                        text = if (isDone) "Actual Verified Weight: ${"%.1f".format(weight)} kg" else "Est. Weight: ${"%.1f".format(weight)} kg",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (isDone) "Total Paid: ₹${earnings?.toInt()}" else "Est. Payout: ₹${earnings?.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) BadgeCertifiedGreen else MaterialTheme.colorScheme.primary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${booking.paymentMode} ${if (booking.upiId.isNotBlank()) "(${booking.upiId})" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Interactive Actions
                            if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onAdvanceStatus(booking) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("advance_status_button_${booking.id}")
                                    ) {
                                        Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val nextAction = when (booking.status) {
                                            BookingStatus.BOOKED -> "Assign Recycler"
                                            BookingStatus.RECYCLER_ASSIGNED -> "Start Transit"
                                            BookingStatus.AGENT_EN_ROUTE -> "Arrived / Weigh"
                                            BookingStatus.WEIGHING_INSPECTION -> "Complete & Pay"
                                            else -> "Next Step"
                                        }
                                        Text(nextAction, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onCancelBooking(booking) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                        modifier = Modifier.testTag("cancel_booking_btn_${booking.id}")
                                    ) {
                                        Text("Cancel", fontSize = 12.sp)
                                    }
                                }
                            } else if (booking.status == BookingStatus.COMPLETED) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledTonalButton(
                                        onClick = { showReceiptDialog = booking },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("view_slip_btn_${booking.id}")
                                    ) {
                                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Weight Slip", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { showCertificateDialog = booking },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("view_cert_btn_${booking.id}")
                                    ) {
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Green Certificate", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Digital Weight Slip Dialog
    showReceiptDialog?.let { b ->
        AlertDialog(
            onDismissRequest = { showReceiptDialog = null },
            icon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = {
                Text("Digital Doorstep Weighing Slip", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order #:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(b.id, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Certified Center:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(b.centerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Verified Scale:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("ISO 9001 Laser Digital (Calibrated)", style = MaterialTheme.typography.bodySmall, color = BadgeCertifiedGreen)
                    }
                    Divider()
                    Text("Items Weighed:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text(b.itemsSummary, style = MaterialTheme.typography.bodySmall)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross Net Weight:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${"%.1f".format(b.actualWeightKg ?: b.estimatedWeightKg)} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount Paid:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("₹${(b.actualEarningsPaid ?: b.estimatedEarnings).toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Status:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("INSTANT TRANSFERRED (${b.paymentMode})", style = MaterialTheme.typography.bodySmall, color = BadgeCertifiedGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showReceiptDialog = null }) {
                    Text("Done")
                }
            }
        )
    }

    // Official Green Recycling Certificate Dialog
    showCertificateDialog?.let { b ->
        val weight = b.actualWeightKg ?: b.estimatedWeightKg
        val trees = weight * 0.017
        val co2 = weight * 1.8
        val water = weight * 25.0

        AlertDialog(
            onDismissRequest = { showCertificateDialog = null },
            icon = { Text("🌱", fontSize = 36.sp) },
            title = {
                Text("Certificate of Green Recycling", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoGreenPrimaryContainerLight,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "OFFICIAL ECO CITIZEN RECOGNITION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Awarded to ${b.contactName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "For responsibly recycling ${"%.1f".format(weight)} kg of household scrap with ${b.centerName}, diverting hazardous waste from local landfills.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌲", fontSize = 20.sp)
                                Text("${"%.2f".format(trees)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Trees Saved", fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💨", fontSize = 20.sp)
                                Text("${"%.1f".format(co2)} kg", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("CO2 Avoided", fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💧", fontSize = 20.sp)
                                Text("${water.toInt()} L", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Water Saved", fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showCertificateDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
