package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.ui.AppTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapTopBar(
    selectedCity: String = "Metro Hub",
    onHelplineClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("♻️", fontSize = 20.sp)
                    }
                }
                Column {
                    Text(
                        text = "ScrapCycle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = selectedCity,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Doorstep Certified",
                            style = MaterialTheme.typography.labelSmall,
                            color = BadgeCertifiedGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        actions = {
            AssistChip(
                onClick = onHelplineClick,
                label = { Text("Toll-Free Help", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Support",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("helpline_chip")
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun ScrapBottomNavBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val tabs = listOf(
            AppTab.HOME,
            AppTab.RATES,
            AppTab.BOOK,
            AppTab.MY_PICKUPS,
            AppTab.RECYCLERS,
            AppTab.IMPACT
        )

        tabs.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Text(
                        text = tab.iconEmoji,
                        fontSize = if (isSelected) 22.sp else 18.sp
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun DigitalScaleVerifiedBadge(modifier: Modifier = Modifier) {
    Surface(
        color = EcoTealTertiaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("⚖️", fontSize = 12.sp)
            Text(
                text = "100% Digital Laser Scale Verified",
                style = MaterialTheme.typography.labelSmall,
                color = EcoTealOnTertiaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CertifiedBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatusBadge(status: BookingStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        BookingStatus.BOOKED -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        BookingStatus.RECYCLER_ASSIGNED -> Color(0xFFCCFBF1) to Color(0xFF115E59)
        BookingStatus.AGENT_EN_ROUTE -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        BookingStatus.WEIGHING_INSPECTION -> Color(0xFFEDE9FE) to Color(0xFF5B21B6)
        BookingStatus.COMPLETED -> Color(0xFFDCFCE7) to Color(0xFF166534)
        BookingStatus.CANCELLED -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = status.displayTitle,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PickupStatusStepper(currentStatus: BookingStatus, modifier: Modifier = Modifier) {
    val steps = listOf(
        BookingStatus.BOOKED to "Booked",
        BookingStatus.RECYCLER_ASSIGNED to "Assigned",
        BookingStatus.AGENT_EN_ROUTE to "En Route",
        BookingStatus.WEIGHING_INSPECTION to "Weighing",
        BookingStatus.COMPLETED to "Done & Paid"
    )

    val currentIndex = when (currentStatus) {
        BookingStatus.BOOKED -> 0
        BookingStatus.RECYCLER_ASSIGNED -> 1
        BookingStatus.AGENT_EN_ROUTE -> 2
        BookingStatus.WEIGHING_INSPECTION -> 3
        BookingStatus.COMPLETED -> 4
        BookingStatus.CANCELLED -> -1
    }

    if (currentStatus == BookingStatus.CANCELLED) {
        Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626))
                Text(
                    "This doorstep pickup request was cancelled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF991B1B)
                )
            }
        }
        return
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (status, label) ->
            val isPassed = index <= currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isPassed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                ) {
                    if (isPassed && !isCurrent) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (isPassed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
