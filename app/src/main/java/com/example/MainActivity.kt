package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.ScrapViewModel
import com.example.ui.components.ScrapBottomNavBar
import com.example.ui.components.ScrapTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ScrapCycleApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapCycleApp(
    viewModel: ScrapViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allRates by viewModel.allRates.collectAsStateWithLifecycle()
    val filteredRates by viewModel.filteredRates.collectAsStateWithLifecycle()
    val allCenters by viewModel.allCenters.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val allAddresses by viewModel.allAddresses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val calculatorQuantities by viewModel.calculatorQuantities.collectAsStateWithLifecycle()
    val bookingDraft by viewModel.bookingDraft.collectAsStateWithLifecycle()
    val selectedBooking by viewModel.selectedBooking.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Back handler: navigate back to Home if in another tab
    BackHandler(enabled = currentTab != AppTab.HOME) {
        viewModel.setTab(AppTab.HOME)
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scrap_cycle_root_scaffold"),
        topBar = {
            ScrapTopBar(
                selectedCity = "Metro Hub",
                onHelplineClick = { showHelpDialog = true }
            )
        },
        bottomBar = {
            ScrapBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        popularRates = allRates.filter { it.isPopular },
                        activeBookings = allBookings,
                        onNavigateToBook = { viewModel.setTab(AppTab.BOOK) },
                        onNavigateToRates = { viewModel.setTab(AppTab.RATES) },
                        onNavigateToMyPickups = { viewModel.setTab(AppTab.MY_PICKUPS) },
                        onNavigateToRecyclers = { viewModel.setTab(AppTab.RECYCLERS) },
                        onNavigateToImpact = { viewModel.setTab(AppTab.IMPACT) },
                        onSellItemClick = { item ->
                            viewModel.startBookingWithSingleItem(item)
                        },
                        onTrackBookingClick = { booking ->
                            viewModel.selectBookingDetail(booking)
                            viewModel.setTab(AppTab.MY_PICKUPS)
                        }
                    )
                }

                AppTab.RATES -> {
                    RateCardScreen(
                        rates = filteredRates,
                        allRates = allRates,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        calculatorQuantities = calculatorQuantities,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.setCategoryFilter(it) },
                        onUpdateCalculatorQty = { id, qty -> viewModel.updateCalculatorQuantity(id, qty) },
                        onClearCalculator = { viewModel.clearCalculator() },
                        onTransferCalculatorToBooking = { viewModel.transferCalculatorToBooking() },
                        onSellItemClick = { item ->
                            viewModel.startBookingWithSingleItem(item)
                        }
                    )
                }

                AppTab.BOOK -> {
                    BookPickupScreen(
                        draft = bookingDraft,
                        allRates = allRates,
                        allCenters = allCenters,
                        allAddresses = allAddresses,
                        onUpdateDraftQty = { id, qty -> viewModel.updateDraftItemQuantity(id, qty) },
                        onUpdateDate = { viewModel.updateDraftDate(it) },
                        onUpdateTimeSlot = { viewModel.updateDraftTimeSlot(it) },
                        onUpdateCenter = { viewModel.updateDraftCenter(it) },
                        onUpdateContact = { name, phone -> viewModel.updateDraftContact(name, phone) },
                        onUpdateAddress = { house, street, landmark, pincode ->
                            viewModel.updateDraftAddress(house, street, landmark, pincode)
                        },
                        onUpdatePayment = { mode, upi -> viewModel.updateDraftPayment(mode, upi) },
                        onUpdateSpecialNotes = { viewModel.updateDraftSpecialNotes(it) },
                        onApplySavedAddress = { viewModel.applySavedAddressToDraft(it) },
                        onSubmitBooking = { viewModel.submitPickupBooking() },
                        onNavigateToRates = { viewModel.setTab(AppTab.RATES) }
                    )
                }

                AppTab.MY_PICKUPS -> {
                    MyPickupsScreen(
                        bookings = allBookings,
                        selectedBooking = selectedBooking,
                        onSelectBooking = { viewModel.selectBookingDetail(it) },
                        onAdvanceStatus = { viewModel.advanceStatus(it) },
                        onCancelBooking = { viewModel.cancelBooking(it) },
                        onNavigateToBook = { viewModel.setTab(AppTab.BOOK) }
                    )
                }

                AppTab.RECYCLERS -> {
                    RecyclersScreen(
                        centers = allCenters,
                        onSelectCenterForBooking = { center ->
                            viewModel.updateDraftCenter(center.id)
                            viewModel.setTab(AppTab.BOOK)
                        }
                    )
                }

                AppTab.IMPACT -> {
                    EcoImpactScreen(
                        bookings = allBookings,
                        onSchedulePickupClick = { viewModel.setTab(AppTab.BOOK) }
                    )
                }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("ScrapCycle Doorstep Helpline", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Need assistance scheduling scrap pickup, bulk industrial scrap disposal, or digital weighing queries?", style = MaterialTheme.typography.bodySmall)
                    Divider()
                    Text("📞 Toll-Free: 1800-200-SCRAP (72727)", fontWeight = FontWeight.Bold)
                    Text("💬 WhatsApp Support: +91 98765 00112", style = MaterialTheme.typography.bodySmall)
                    Text("✉️ Email: support@scrapcycle.app", style = MaterialTheme.typography.bodySmall)
                    Text("⏰ Operating: 8:00 AM - 8:00 PM (All 7 Days)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showHelpDialog = false
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:180020072727"))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    }
                ) {
                    Text("Call Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
