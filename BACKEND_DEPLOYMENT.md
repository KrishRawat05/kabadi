# ScrapCycle: Backend Architecture, Setup & Deployment Guide

This guide provides a production-grade backend implementation and deployment strategy for **ScrapCycle**, connecting homeowners with certified recycling centers for doorstep scrap collection (inspired by [ScrapUncle](https://scrapuncle.com/) and [The Kabadiwala](https://www.thekabadiwala.com/)).

---

## 1. System Architecture Overview

```
                      ┌────────────────────────────────────────┐
                      │    ScrapCycle Android App (Kotlin)     │
                      └──────────────────┬─────────────────────┘
                                         │ HTTPS / REST / WebSockets
                                         ▼
                      ┌────────────────────────────────────────┐
                      │      API Gateway & Reverse Proxy       │
                      │             (NGINX / Caddy)            │
                      └──────────────────┬─────────────────────┘
                                         │
        ┌────────────────────────────────┼────────────────────────────────┐
        ▼                                ▼                                ▼
┌───────────────┐              ┌───────────────────┐            ┌───────────────────┐
│ Auth Service  │              │  Booking Service  │            │  Partner Hub API  │
│ (JWT / OAuth) │              │ (Dispatch Engine) │            │ (CPCB Recyclers)  │
└───────┬───────┘              └─────────┬─────────┘            └─────────┬─────────┘
        │                                │                                │
        └────────────────────────────────┼────────────────────────────────┘
                                         │
                 ┌───────────────────────┴───────────────────────┐
                 ▼                                               ▼
     ┌───────────────────────┐                       ┌───────────────────────┐
     │  PostgreSQL + PostGIS  │                       │      Redis Cache      │
     │  (Relational Database)│                       │   (Live Geo-Queue)    │
     └───────────────────────┘                       └───────────────────────┘
                 │                                               │
                 ▼                                               ▼
     ┌───────────────────────┐                       ┌───────────────────────┐
     │ RazorpayX / Cashfree  │                       │ Bluetooth/IoT Digital │
     │  (Instant UPI Payout) │                       │  Weighing Telemetry   │
     └───────────────────────┘                       └───────────────────────┘
```

---

## 2. Core Functional Requirements

1. **Live Scrap Rate Engine:** Dynamic pricing per kg or unit across categories (Paper, Metals, Plastics, E-Waste, Appliances, Vehicles).
2. **Geo-Location Matcher:** Nearest certified recycling center allocation using PostGIS spatial indexing (`ST_DWithin`).
3. **Executive Dispatch & Live Order Pipeline:**
   - `BOOKED` ➔ `RECYCLER_ASSIGNED` ➔ `AGENT_EN_ROUTE` ➔ `WEIGHING_INSPECTION` ➔ `COMPLETED & PAID`
4. **Instant Digital Payout Engine:** Webhook-driven instant fund transfers via UPI Virtual Payment Address (VPA) upon doorstep scale confirmation.
5. **Certified Environmental Ledger:** Calculates cumulative CO2 mitigation, trees saved, and water conserved, issuing signed digital PDF Green Certificates.

---

## 3. Database Schema (PostgreSQL + PostGIS)

Save the following as `schema.sql`:

```sql
-- Enable PostGIS for geo-proximity queries
CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. Certified Recycling Centers
CREATE TABLE recycling_centers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    license_number VARCHAR(100) UNIQUE NOT NULL, -- CPCB / SPCB license
    city VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(150),
    operating_hours VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    certifications JSONB DEFAULT '[]', -- ['ISO 14001', 'CPCB Registered']
    specialities JSONB DEFAULT '[]',   -- ['E-Waste', 'Metals', 'Paper']
    total_landfill_diverted_kg NUMERIC(12, 2) DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_recycling_centers_location ON recycling_centers USING GIST(location);

-- 2. Scrap Rate Catalog
CREATE TABLE scrap_rates (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL, -- PAPER, METAL, PLASTIC, E_WASTE, APPLIANCES, VEHICLE, OTHER
    rate_per_unit NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(20) DEFAULT 'kg', -- 'kg', 'piece', 'set'
    description TEXT,
    accepted_condition TEXT,
    min_quantity NUMERIC(8, 2) DEFAULT 1.0,
    is_popular BOOLEAN DEFAULT FALSE,
    eco_co2_factor NUMERIC(6, 2) DEFAULT 1.5,
    eco_water_factor NUMERIC(6, 2) DEFAULT 20.0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Doorstep Pickup Bookings
CREATE TABLE pickup_bookings (
    id VARCHAR(30) PRIMARY KEY, -- e.g. 'SC-98214'
    user_id UUID NOT NULL,
    center_id UUID REFERENCES recycling_centers(id),
    status VARCHAR(50) NOT NULL DEFAULT 'BOOKED', -- BOOKED, RECYCLER_ASSIGNED, AGENT_EN_ROUTE, WEIGHING_INSPECTION, COMPLETED, CANCELLED
    pickup_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    address_text TEXT NOT NULL,
    contact_name VARCHAR(150) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    pickup_location GEOGRAPHY(POINT, 4326),
    items_breakdown JSONB NOT NULL,
    estimated_weight_kg NUMERIC(8, 2) NOT NULL,
    estimated_earnings NUMERIC(10, 2) NOT NULL,
    actual_weight_kg NUMERIC(8, 2),
    actual_payout NUMERIC(10, 2),
    payment_mode VARCHAR(50) DEFAULT 'Instant UPI Transfer',
    upi_id VARCHAR(100),
    executive_name VARCHAR(150),
    executive_phone VARCHAR(20),
    executive_vehicle VARCHAR(100),
    digital_scale_verified BOOLEAN DEFAULT TRUE,
    special_notes TEXT,
    payout_transaction_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_pickup_bookings_status ON pickup_bookings(status);
CREATE INDEX idx_pickup_bookings_date ON pickup_bookings(pickup_date);
```

---

## 4. Production Node.js / Express Backend Implementation

Create a backend directory: `server/`

### `package.json`
```json
{
  "name": "scrapcycle-backend",
  "version": "1.0.0",
  "description": "Certified Doorstep Scrap Collection Platform Backend",
  "main": "src/server.js",
  "scripts": {
    "start": "node src/server.js",
    "dev": "nodemon src/server.js"
  },
  "dependencies": {
    "cors": "^2.8.5",
    "dotenv": "^16.4.5",
    "express": "^4.19.2",
    "helmet": "^7.1.0",
    "jsonwebtoken": "^9.0.2",
    "morgan": "^1.10.0",
    "pg": "^8.12.0",
    "zod": "^3.23.8"
  },
  "devDependencies": {
    "nodemon": "^3.1.4"
  }
}
```

### `src/server.js`
```javascript
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const { Pool } = require('pg');
require('dotenv').config();

const app = express();
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan('combined'));

// Database Connection
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
});

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// 1. Get Live Scrap Rates
app.get('/api/rates', async (req, res) => {
  try {
    const { category } = req.query;
    let query = 'SELECT * FROM scrap_rates';
    const params = [];
    if (category) {
      query += ' WHERE category = $1';
      params.push(category);
    }
    query += ' ORDER BY is_popular DESC, name ASC';
    const { rows } = await pool.query(query, params);
    res.json({ success: true, count: rows.length, data: rows });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to fetch scrap rate card' });
  }
});

// 2. Find Nearest Certified Recycler Centers
app.get('/api/centers/nearby', async (req, res) => {
  try {
    const { lat, lng, radiusKm = 15 } = req.query;
    if (!lat || !lng) {
      const { rows } = await pool.query('SELECT * FROM recycling_centers ORDER BY name ASC');
      return res.json({ success: true, data: rows });
    }

    const query = `
      SELECT id, name, license_number, city, address, contact_phone, certifications, specialities,
             ST_Distance(location, ST_MakePoint($1, $2)::geography) / 1000 AS distance_km
      FROM recycling_centers
      WHERE ST_DWithin(location, ST_MakePoint($1, $2)::geography, $3 * 1000)
      ORDER BY distance_km ASC;
    `;
    const { rows } = await pool.query(query, [lng, lat, radiusKm]);
    res.json({ success: true, data: rows });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to find recycling hubs' });
  }
});

// 3. Create Doorstep Pickup Booking
app.post('/api/bookings', async (req, res) => {
  try {
    const {
      contactName,
      contactPhone,
      addressText,
      pickupDate,
      timeSlot,
      itemsBreakdown,
      estimatedWeightKg,
      estimatedEarnings,
      centerId,
      paymentMode,
      upiId,
      specialNotes
    } = req.body;

    const bookingId = 'SC-' + Math.floor(10000 + Math.random() * 90000);

    const insertQuery = `
      INSERT INTO pickup_bookings (
        id, user_id, center_id, status, pickup_date, time_slot,
        address_text, contact_name, contact_phone, items_breakdown,
        estimated_weight_kg, estimated_earnings, payment_mode, upi_id,
        executive_name, executive_phone, executive_vehicle, special_notes
      ) VALUES (
        $1, gen_random_uuid(), $2, 'BOOKED', $3, $4,
        $5, $6, $7, $8,
        $9, $10, $11, $12,
        'Sunil Verma', '+91 98112 34567', 'Eco Electric Van (DL-02-EV-8841)', $13
      ) RETURNING *;
    `;

    const values = [
      bookingId,
      centerId || null,
      pickupDate,
      timeSlot,
      addressText,
      contactName,
      contactPhone,
      JSON.stringify(itemsBreakdown),
      estimatedWeightKg,
      estimatedEarnings,
      paymentMode || 'Instant UPI Transfer',
      upiId || '',
      specialNotes || ''
    ];

    const { rows } = await pool.query(insertQuery, values);
    res.status(201).json({ success: true, booking: rows[0] });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to create booking' });
  }
});

// 4. Update Booking Status (e.g. Executive En Route, Doorstep Weighing, Payment)
app.patch('/api/bookings/:id/status', async (req, res) => {
  try {
    const { id } = req.params;
    const { status, actualWeightKg, actualPayout } = req.body;

    let updateQuery = 'UPDATE pickup_bookings SET status = $1';
    const params = [status];
    let paramIndex = 2;

    if (actualWeightKg !== undefined) {
      updateQuery += `, actual_weight_kg = $${paramIndex++}`;
      params.push(actualWeightKg);
    }
    if (actualPayout !== undefined) {
      updateQuery += `, actual_payout = $${paramIndex++}`;
      params.push(actualPayout);
    }
    if (status === 'COMPLETED') {
      updateQuery += `, completed_at = NOW()`;
    }

    updateQuery += ` WHERE id = $${paramIndex} RETURNING *;`;
    params.push(id);

    const { rows } = await pool.query(updateQuery, params);
    if (rows.length === 0) {
      return res.status(404).json({ error: 'Booking not found' });
    }

    res.json({ success: true, booking: rows[0] });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to update booking status' });
  }
});

// 5. Environmental Impact Ledger
app.get('/api/impact/stats', async (req, res) => {
  try {
    const query = `
      SELECT 
        COALESCE(SUM(actual_weight_kg), 0) AS total_recycled_kg,
        COALESCE(SUM(actual_payout), 0) AS total_paid_inr,
        COUNT(*) FILTER (WHERE status = 'COMPLETED') AS completed_pickups
      FROM pickup_bookings;
    `;
    const { rows } = await pool.query(query);
    const data = rows[0];
    const kg = parseFloat(data.total_recycled_kg);

    res.json({
      success: true,
      data: {
        totalRecycledKg: kg,
        treesSaved: (kg * 0.017).toFixed(2),
        co2MitigatedKg: (kg * 1.85).toFixed(1),
        waterSavedLitres: Math.round(kg * 24.5),
        totalPayoutInr: parseFloat(data.total_paid_inr),
        completedPickups: parseInt(data.completed_pickups, 10)
      }
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to aggregate impact stats' });
  }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`ScrapCycle API listening on port ${PORT}`);
});
```

---

## 5. Dockerization & Compose Setup

### `Dockerfile`
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY src ./src

FROM node:20-alpine
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app ./
EXPOSE 8080
USER node
CMD ["node", "src/server.js"]
```

### `docker-compose.yml`
```yaml
version: '3.8'

services:
  postgres:
    image: postgis/postgis:16-3.4-alpine
    container_name: scrapcycle-postgres
    restart: always
    environment:
      POSTGRES_DB: scrapcycle
      POSTGRES_USER: scrapuser
      POSTGRES_PASSWORD: secretpassword
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./schema.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7-alpine
    container_name: scrapcycle-redis
    ports:
      - "6379:6379"

  api:
    build: .
    container_name: scrapcycle-api
    restart: always
    environment:
      PORT: 8080
      DATABASE_URL: postgres://scrapuser:secretpassword@postgres:5432/scrapcycle
      REDIS_URL: redis://redis:6379
      JWT_SECRET: super_secure_scrap_jwt_key_2026
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis

volumes:
  pgdata:
```

---

## 6. Cloud Deployment Instructions

### Option A: Google Cloud Run (Recommended Serverless Setup)

1. **Create Cloud SQL PostgreSQL Instance (with PostGIS enabled):**
   ```bash
   gcloud sql instances create scrapcycle-db \
       --database-version=POSTGRES_15 \
       --tier=db-f1-micro \
       --region=asia-south1
   ```
2. **Build & Submit Container Image:**
   ```bash
   gcloud builds submit --tag gcr.io/[PROJECT_ID]/scrapcycle-backend
   ```
3. **Deploy to Cloud Run:**
   ```bash
   gcloud run deploy scrapcycle-backend \
       --image gcr.io/[PROJECT_ID]/scrapcycle-backend \
       --platform managed \
       --region asia-south1 \
       --allow-unauthenticated \
       --set-env-vars DATABASE_URL="postgresql://user:pass@/scrapcycle?host=/cloudsql/[INSTANCE_CONNECTION_NAME]"
   ```

### Option B: Render / Railway Deployment

1. Provision a PostgreSQL service in Render/Railway dashboard.
2. Run `schema.sql` inside the DB query console.
3. Link the GitHub repository and set the environment variable:
   - `DATABASE_URL` = `<Render Internal PostgreSQL URL>`
   - `PORT` = `8080`
4. The service will auto-build and assign a secure HTTPS public URL (e.g. `https://scrapcycle-api.onrender.com`).

---

## 7. Mobile App Connection (Retrofit Config)

In `app/build.gradle.kts`, ensure Retrofit is active:
```kotlin
implementation(libs.retrofit)
implementation(libs.converter.moshi)
```

Point your mobile client Retrofit service to the backend:
```kotlin
interface ScrapCycleApi {
    @GET("api/rates")
    suspend fun getRates(): RateResponse

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): BookingResponse

    @GET("api/impact/stats")
    suspend fun getImpactStats(): ImpactResponse
}
```

The app is completely configured for offline-first Room persistence and ready to connect with the cloud API whenever desired!
