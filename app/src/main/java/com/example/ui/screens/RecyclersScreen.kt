package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecyclingCenter
import com.example.ui.components.CertifiedBadge
import com.example.ui.components.DigitalScaleVerifiedBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclersScreen(
    centers: List<RecyclingCenter>,
    onSelectCenterForBooking: (RecyclingCenter) -> Unit
) {
    val context = LocalContext.current
    var selectedCenterDetail by remember { mutableStateOf<RecyclingCenter?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("recyclers_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🛡️", fontSize = 24.sp)
                        Text(
                            text = "Verified & Certified Recycler Network",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All partner centers are authorized under Central/State Pollution Control Boards (CPCB/SPCB) and comply with strict zero-landfill, scientific dismantling standards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        items(centers) { center ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedCenterDetail = center }
                    .testTag("center_card_${center.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = center.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "License: ${center.licenseNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("⭐ ${center.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(" (${center.totalReviews})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = center.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // Certifications
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        center.certifications.take(2).forEach { cert ->
                            CertifiedBadge(text = cert)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Details row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                            Text(
                                text = "${center.distanceKm} km away • ${center.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "🌱 ${center.landfillDivertedTons.toInt()}T Diverted",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${center.phone}"))
                                try { context.startActivity(intent) } catch (e: Exception) {}
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Hub", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onSelectCenterForBooking(center) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("book_with_center_${center.id}")
                        ) {
                            Text("Book with Center", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    selectedCenterDetail?.let { c ->
        AlertDialog(
            onDismissRequest = { selectedCenterDetail = null },
            icon = { Text("🏢", fontSize = 32.sp) },
            title = { Text(c.name, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Govt License: ${c.licenseNumber}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text(c.description, style = MaterialTheme.typography.bodySmall)
                    Divider()
                    Text("Address:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Text("${c.address}, ${c.city}", style = MaterialTheme.typography.bodySmall)
                    Text("Hours: ${c.operatingHours}", style = MaterialTheme.typography.bodySmall)
                    Divider()
                    Text("Certifications:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    c.certifications.forEach { cert ->
                        Text("• $cert", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedCenterDetail = null
                    onSelectCenterForBooking(c)
                }) {
                    Text("Book Doorstep Pickup")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCenterDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}
