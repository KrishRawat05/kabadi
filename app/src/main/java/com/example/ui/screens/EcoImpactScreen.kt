package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookingStatus
import com.example.data.model.PickupBooking
import com.example.ui.theme.*

@Composable
fun EcoImpactScreen(
    bookings: List<PickupBooking>,
    onSchedulePickupClick: () -> Unit
) {
    val context = LocalContext.current

    // Aggregate user's environmental metrics from completed + active bookings
    val totalWeightKg = bookings.filter { it.status != BookingStatus.CANCELLED }.sumOf {
        it.actualWeightKg ?: it.estimatedWeightKg
    }
    val totalEarningsPaid = bookings.filter { it.status == BookingStatus.COMPLETED }.sumOf {
        it.actualEarningsPaid ?: it.estimatedEarnings
    }

    val treesSaved = totalWeightKg * 0.017
    val co2ReducedKg = totalWeightKg * 1.85
    val waterSavedLitres = totalWeightKg * 24.5
    val landfillAvoidedKg = totalWeightKg

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("eco_impact_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Art Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eco_impact_art_1791284729095),
                            contentDescription = "Eco Impact Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Your Circular Green Footprint",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Every kilogram of scrap collected prevents open landfills and conserves natural habitats.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImpactStatCard(
                        icon = "🌲",
                        title = "Trees Preserved",
                        value = "%.2f".format(treesSaved),
                        unit = "Trees",
                        bgColor = EcoGreenPrimaryContainerLight,
                        fgColor = EcoGreenOnPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    ImpactStatCard(
                        icon = "💨",
                        title = "CO2 Emissions Saved",
                        value = "%.1f".format(co2ReducedKg),
                        unit = "kg CO2",
                        bgColor = Color(0xFFE0F2FE),
                        fgColor = Color(0xFF0369A1),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImpactStatCard(
                        icon = "💧",
                        title = "Water Conserved",
                        value = "${waterSavedLitres.toInt()}",
                        unit = "Litres",
                        bgColor = Color(0xFFCCFBF1),
                        fgColor = Color(0xFF115E59),
                        modifier = Modifier.weight(1f)
                    )
                    ImpactStatCard(
                        icon = "🗑️",
                        title = "Landfill Diverted",
                        value = "%.1f".format(landfillAvoidedKg),
                        unit = "kg",
                        bgColor = Color(0xFFFEF3C7),
                        fgColor = Color(0xFF92400E),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Total Green Cash Earned
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CashGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("💰", fontSize = 24.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Green Earnings", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${totalEarningsPaid.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Paid directly via Instant UPI & Cash", style = MaterialTheme.typography.labelSmall, color = BadgeCertifiedGreen)
                    }
                }
            }
        }

        // Official Green Citizen Certificate Frame
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("official_certificate_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "DIGITAL GREEN RECYCLER CERTIFICATE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Issued to: Aarav Sharma",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "For active participation in verified zero-landfill scrap recycling and scientific circular economy initiatives.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cert ID: SC-CERT-2026-8819", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                            Text("ISO 14001 Compliant", style = MaterialTheme.typography.labelSmall, color = BadgeCertifiedGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "I recycled ${"%.1f".format(totalWeightKg)} kg of scrap with ScrapCycle and saved ${"%.2f".format(treesSaved)} trees! 🌲 Check out ScrapCycle for certified doorstep recycling.")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Green Certificate")
                            try { context.startActivity(shareIntent) } catch (e: Exception) {}
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("share_cert_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Green Citizen Badge")
                    }
                }
            }
        }

        // Schedule next pickup button
        item {
            FilledTonalButton(
                onClick = onSchedulePickupClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Schedule Another Doorstep Pickup")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ImpactStatCard(
    icon: String,
    title: String,
    value: String,
    unit: String,
    bgColor: Color,
    fgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = fgColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = fgColor.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
