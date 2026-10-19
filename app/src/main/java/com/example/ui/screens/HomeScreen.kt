package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookingStatus
import com.example.data.model.PickupBooking
import com.example.data.model.ScrapRateItem
import com.example.ui.AppTab
import com.example.ui.components.DigitalScaleVerifiedBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    popularRates: List<ScrapRateItem>,
    activeBookings: List<PickupBooking>,
    onNavigateToBook: () -> Unit,
    onNavigateToRates: () -> Unit,
    onNavigateToMyPickups: () -> Unit,
    onNavigateToRecyclers: () -> Unit,
    onNavigateToImpact: () -> Unit,
    onSellItemClick: (ScrapRateItem) -> Unit,
    onTrackBookingClick: (PickupBooking) -> Unit
) {
    val activeBooking = activeBookings.firstOrNull {
        it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Active Booking Quick Alert (if available)
        if (activeBooking != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrackBookingClick(activeBooking) }
                        .testTag("active_booking_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🚚", fontSize = 22.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Active Pickup #${activeBooking.id}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                StatusBadge(status = activeBooking.status)
                            }
                            Text(
                                text = "${activeBooking.date} • ${activeBooking.timeSlot}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Agent: ${activeBooking.executiveName} (${activeBooking.executiveVehicle})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = { onTrackBookingClick(activeBooking) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("track_now_button")
                        ) {
                            Text("Track", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. Hero Card with Image Illustration
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.scrap_hero_banner_1791284714824),
                            contentDescription = "Doorstep Scrap Recycling Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.65f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            DigitalScaleVerifiedBadge()
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Turn Household Scrap into Instant Cash",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Connect with government & ISO certified recycling centers. Guaranteed calibrated digital scales & direct UPI transfer upon doorstep pickup.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToBook,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_book_pickup_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricRickshaw,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Book Pickup")
                            }

                            OutlinedButton(
                                onClick = onNavigateToRates,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_rate_card_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyRupee,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rate Card")
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Sell Rate Highlights (ScrapUncle style rate ticker)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Popular Scrap Rates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Updated real-time with certified recycling market",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onNavigateToRates) {
                        Text("View All")
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    val highlights = popularRates.ifEmpty {
                        emptyList()
                    }.take(6)

                    items(highlights) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onSellItemClick(item) }
                                .testTag("popular_rate_card_${item.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = item.category.iconEmoji,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${item.ratePerUnit.toInt()} / ${item.unit}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onSellItemClick(item) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Sell Now", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Certified Partners & Scale Trust Guarantee
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToRecyclers() }
                    .testTag("recyclers_trust_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Certified Recycling Centers Network",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Every pickup is fulfilled exclusively by ISO 14001, CPCB authorized, or Municipal registered hubs ensuring 0% illegal dumping and 100% scientific recovery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                "🌿 ISO 14001 Certified",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                "⚡ Direct UPI Transfer",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 5. Environmental Impact Ledger Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EcoGreenPrimaryContainerLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToImpact() }
                    .testTag("eco_impact_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌱", fontSize = 24.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Green Recycling Impact",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenOnPrimaryContainer
                        )
                        Text(
                            text = "Track trees saved, CO2 reduced, and download your official Green Citizen Certificate.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EcoGreenOnPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Impact",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 6. How ScrapCycle Works (The Kabadiwala / ScrapUncle 3-step guide)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How ScrapCycle Works",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val steps = listOf(
                        Triple("1", "Schedule Doorstep Pickup", "Select scrap items & choose a convenient time slot."),
                        Triple("2", "Certified Digital Weighing", "Trained executive arrives with an ISO certified digital laser scale."),
                        Triple("3", "Instant UPI / Cash Payout", "Get paid immediately and receive your verified Green Certificate.")
                    )

                    steps.forEachIndexed { index, (num, title, desc) ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Text(num, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 14.dp, top = 2.dp, bottom = 2.dp)
                                    .width(2.dp)
                                    .height(18.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                        }
                    }
                }
            }
        }

        // Bottom space for comfortable scrolling above navigation bar
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
