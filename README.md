# ScrapCycle - Doorstep Scrap Collection & Certified Recycling Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room%20DB-2.7.0%20(Offline--First)-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Android](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0%2B)-success.svg)](https://developer.android.com)

**ScrapCycle** is a modern, high-polish Android application for doorstep household scrap collection that connects local homeowners with government and ISO-certified recycling centers for seamless, transparent pickups.

Inspired by industry pioneers **[ScrapUncle](https://scrapuncle.com/)** and **[The Kabadiwala](https://www.thekabadiwala.com/)**, ScrapCycle solves common pain points of traditional unorganized scrap selling: fraudulent scales, uncertain prices, manual haggling, and hazardous landfill disposal.

---

## 🚀 Key Features

### 1. 📋 Live Scrap Rate Card & Price Ticker
- **Category Browsing:** Real-time rates categorized across:
  - 📰 **Paper & Cardboard:** Newspapers (₹14/kg), Packaging Cartons (₹11/kg), Office Books (₹12.5/kg), Magazines (₹9/kg)
  - 🥫 **Metals:** Heavy Copper (₹485/kg), Brass/Peetal (₹340/kg), Aluminum (₹120/kg), Iron/Loha (₹32/kg), Tin Cans (₹20/kg)
  - 🥤 **Plastics:** Hard Buckets/Tubs (₹16/kg), PET Bottles (₹13/kg), Soft Poly Film (₹10/kg)
  - 💻 **E-Waste & Electronics:** Scrap Laptops (₹450/pc), Computer CPU (₹350/pc), Green Motherboards (₹260/kg), Car/Inverter Batteries (₹78/kg)
  - ❄️ **Large Home Appliances:** Split AC 1.5T (₹4,200/set), Window AC (₹2,800/pc), Double Door Refrigerator (₹1,350/pc), Washing Machines (₹800/pc)
  - 🚗 **Vehicle & Glass:** Scrap Two-Wheelers (₹3,400/pc), Glass/Beer Bottles (₹2.5/pc), Mixed Household Scrap (₹9/kg)
- **Instant Search:** Real-time search across scrap names and acceptance specifications.
- **Acceptance Criteria Dialog:** Detailed guides on item conditions (clean, dry, motor/compressor intact).

### 2. 🧮 Interactive Scrap Value Calculator
- Calculate approximate scrap earnings before scheduling.
- Quick `+` / `-` quantity steppers with realistic increments (+5 kg for paper/metals, +1 for appliances).
- Live total weight and total payout counter.
- **One-Tap Transfer:** Seamlessly moves all calculated scrap items straight into the Doorstep Pickup booking draft!

### 3. 🚚 Seamless Doorstep Pickup Booking
- **Step 1:** Select scrap items from the catalog or calculator.
- **Step 2:** Choose a verified recycling center or select **Auto-Assign Nearest Certified Hub** for priority dispatch.
- **Step 3:** Schedule convenient date (Today, Tomorrow, Later) & time windows (9 AM - 12 PM, 12 PM - 3 PM, 3 PM - 6 PM).
- **Step 4:** Address management with saved address shortcuts (Home, Office).
- **Step 5:** Payment selection (Instant UPI transfer with UPI handle validation vs Cash on Pickup).
- Guaranteed calibrated **100% Digital Laser Scale** request flag.

### 4. 📦 Live Order Pipeline & Interactive Progress Tracker
- Interactive order lifecycle tracking:
  `BOOKED` ➔ `ASSIGNED` ➔ `EXECUTIVE EN ROUTE` ➔ `DOORSTEP WEIGHING` ➔ `DONE & PAID`
- **Pickup Executive Card:** Executive name (e.g., Sunil Verma), EV Cargo Van registration, and one-tap phone call button.
- **Interactive Pipeline Simulation:** "Advance Step" button for live demo testing of all states.
- **Digital Weighing Slip:** View exact gross net weight, itemized breakdown, and transaction status.

### 5. 🏢 Certified Recycling Centers Directory
- Direct access to partner recycling facilities with:
  - CPCB / SPCB government registration numbers (e.g. `CPCB-REG-2024-0089`)
  - ISO 14001:2015 and Bureau of Indian Standards certifications
  - Specialities (E-Waste dismantlers, metal foundries, paper pulp mills)
  - Landfill diversion counters (e.g. `480 Tons diverted`)
  - Direct telephone dialer integration.

### 6. 🌿 Eco Impact Ledger & Official Green Certificate
- Real-time cumulative environmental ledger:
  - 🌲 **Trees Preserved:** ~0.017 mature trees per kg paper/cardboard
  - 💨 **CO2 Avoided (kg):** ~1.85 kg greenhouse gas mitigated per kg metal/e-waste
  - 💧 **Water Conserved (L):** ~24.5 liters conserved per kg recycled scrap
  - 🗑️ **Landfill Space Saved:** Direct weight prevented from open dumping
  - 💰 **Total Green Earnings:** Direct cash credited to the homeowner
- **Official Digital Green Recycler Certificate:** Generates a personalized certificate of recognition suitable for sharing on social platforms.

---

## 🏛️ Clean Architecture & Tech Stack

```
com.example
├── MainActivity.kt               # Edge-to-edge Compose host & root navigation
├── data
│   ├── db
│   │   ├── Converters.kt         # Room TypeConverters (Enum, Lists)
│   │   ├── ScrapDao.kt           # Reactive queries (Kotlin Flow)
│   │   └── ScrapDatabase.kt      # Room Database singleton
│   ├── model
│   │   ├── ScrapItem.kt          # ScrapRateItem & ScrapCategory models
│   │   ├── RecyclingCenter.kt    # Certified recycling centers model
│   │   ├── PickupBooking.kt      # PickupBooking & BookingStatus models
│   │   └── UserAddress.kt        # User address book model
│   └── repository
│       └── ScrapRepository.kt    # Single source of truth & realistic seed data
└── ui
    ├── ScrapViewModel.kt         # MVVM StateFlow manager for tabs & calculations
    ├── theme
    │   ├── Color.kt              # Eco-Green & Earth Gold semantic palette
    │   ├── Theme.kt              # Material 3 dynamic color scheme configuration
    │   └── Type.kt               # Typography definition
    ├── components
    │   └── CommonComponents.kt   # TopBar, BottomNavBar, Stepper, Verified Badges
    └── screens
        ├── HomeScreen.kt         # Hero banner, popular rates, how it works
        ├── RateCardScreen.kt     # Live rate card, search, & value calculator
        ├── BookPickupScreen.kt   # Multi-step doorstep booking wizard
        ├── MyPickupsScreen.kt    # Active tracking, weight slip, green certificate
        ├── RecyclersScreen.kt    # Certified centers directory
        └── EcoImpactScreen.kt    # Ecological impact ledger & sharable certificate
```

---

## 🛠️ How to Build and Run

### Android Studio / Gradle CLI
1. Open the project root in **Android Studio**.
2. Sync Gradle files (dependencies include AndroidX Room, Jetpack Compose, Material 3, and Kotlin Coroutines).
3. Select an emulator (API 24+) or connect a physical Android device.
4. Run the app:
   ```bash
   gradle :app:installDebug
   ```

### Running Local JVM Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

---

## 🌐 Backend & Production Deployment

For complete backend source code (Node.js/Express and PostgreSQL/PostGIS schemas), Docker configuration, and cloud deployment guides for Google Cloud Run or AWS, please refer to:
👉 **[BACKEND_DEPLOYMENT.md](BACKEND_DEPLOYMENT.md)**
