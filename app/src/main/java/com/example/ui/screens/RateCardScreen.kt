package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.ScrapCategory
import com.example.data.model.ScrapRateItem
import com.example.ui.components.DigitalScaleVerifiedBadge
import com.example.ui.theme.CashGold
import com.example.ui.theme.EcoGreenPrimaryContainerLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateCardScreen(
    rates: List<ScrapRateItem>,
    allRates: List<ScrapRateItem>,
    searchQuery: String,
    selectedCategory: ScrapCategory?,
    calculatorQuantities: Map<String, Double>,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (ScrapCategory?) -> Unit,
    onUpdateCalculatorQty: (String, Double) -> Unit,
    onClearCalculator: () -> Unit,
    onTransferCalculatorToBooking: () -> Unit,
    onSellItemClick: (ScrapRateItem) -> Unit
) {
    var isCalculatorMode by remember { mutableStateOf(false) }
    var itemDetailDialog by remember { mutableStateOf<ScrapRateItem?>(null) }

    // Calculate Calculator Totals
    val totalEarnings = remember(calculatorQuantities, allRates) {
        calculatorQuantities.entries.sumOf { (id, qty) ->
            val item = allRates.find { it.id == id }
            (item?.ratePerUnit ?: 0.0) * qty
        }
    }
    val totalWeight = remember(calculatorQuantities, allRates) {
        calculatorQuantities.entries.sumOf { (id, qty) ->
            val item = allRates.find { it.id == id }
            if (item?.unit == "kg") qty else (qty * 5.0) // approx
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("rate_card_screen")
    ) {
        // Mode Switcher Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !isCalculatorMode,
                        onClick = { isCalculatorMode = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("segment_rates_list")
                    ) {
                        Text("Live Rate Card")
                    }
                    SegmentedButton(
                        selected = isCalculatorMode,
                        onClick = { isCalculatorMode = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("segment_scrap_calculator")
                    ) {
                        Text("Value Calculator")
                    }
                }

                if (!isCalculatorMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search scrap (e.g. Copper, Newspaper, AC, Laptops)...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scrap_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Categories chip row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { onCategorySelect(null) },
                                label = { Text("All Scrap (${allRates.size})") },
                                modifier = Modifier.testTag("filter_category_all")
                            )
                        }
                        items(ScrapCategory.values()) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { onCategorySelect(cat) },
                                label = { Text("${cat.iconEmoji} ${cat.displayName}") },
                                modifier = Modifier.testTag("filter_category_${cat.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        if (isCalculatorMode) {
            // Calculator View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Live Summary Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calculator_summary_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estimated Total Scrap Value",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "₹${totalEarnings.toInt()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (calculatorQuantities.isNotEmpty()) {
                                TextButton(onClick = onClearCalculator) {
                                    Text("Reset")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "Approx Weight: ${"%.1f".format(totalWeight)} kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "${calculatorQuantities.size} Items Selected",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (calculatorQuantities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onTransferCalculatorToBooking,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("transfer_calculator_to_booking_btn")
                            ) {
                                Icon(Icons.Default.ElectricRickshaw, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Book Doorstep Pickup with These Items")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Add / Adjust Estimated Quantities",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allRates) { item ->
                        val qty = calculatorQuantities[item.id] ?: 0.0
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (qty > 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(item.category.iconEmoji, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "₹${item.ratePerUnit.toInt()}/${item.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            val step = if (item.unit == "kg") 5.0 else 1.0
                                            val newQty = (qty - step).coerceAtLeast(0.0)
                                            onUpdateCalculatorQty(item.id, newQty)
                                        },
                                        enabled = qty > 0,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = if (qty == 0.0) "-" else "${qty.toInt()} ${item.unit}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.widthIn(min = 40.dp)
                                    )

                                    FilledTonalIconButton(
                                        onClick = {
                                            val step = if (item.unit == "kg") 5.0 else 1.0
                                            onUpdateCalculatorQty(item.id, qty + step)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Rates List View
            if (rates.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No scrap items found for '$searchQuery'",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Try searching another keyword or clear category filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        DigitalScaleVerifiedBadge(modifier = Modifier.fillMaxWidth())
                    }

                    items(rates) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { itemDetailDialog = item }
                                .testTag("rate_item_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(item.category.iconEmoji, fontSize = 24.sp)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (item.isPopular) {
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "HOT",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CashGold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "₹${item.ratePerUnit.toInt()} / ${item.unit}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "• ${item.acceptedCondition}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onSellItemClick(item) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("sell_button_${item.id}")
                                ) {
                                    Text("Sell", fontSize = 12.sp)
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
    }

    // Detail dialog when tapping any rate card item
    itemDetailDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { itemDetailDialog = null },
            icon = { Text(item.category.iconEmoji, fontSize = 32.sp) },
            title = {
                Text(item.name, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Current Rate: ₹${item.ratePerUnit.toInt()} per ${item.unit}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider()
                    Text(
                        text = "Acceptance Criteria:",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = item.acceptedCondition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌱 Eco Impact:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Saves ${item.ecoCo2PerKg} kg CO2 & ${item.ecoWaterPerKg.toInt()}L water per kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemDetailDialog = null
                        onSellItemClick(item)
                    }
                ) {
                    Text("Schedule Doorstep Pickup")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemDetailDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
