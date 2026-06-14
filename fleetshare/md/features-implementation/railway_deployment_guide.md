# 🚀 FleetShare — Complete Railway Deployment Guide

> **Audience:** Beginner-friendly. Every step is explained from scratch.
> **Stack:** Spring Boot 4.x · MySQL · Thymeleaf · Docker · Railway

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Prerequisites](#2-prerequisites)
3. [Step 1 — Push Code to GitHub](#step-1--push-code-to-github)
4. [Step 2 — Create a Railway Project](#step-2--create-a-railway-project)
5. [Step 3 — Provision MySQL Database](#step-3--provision-mysql-database)
6. [Step 4 — Seed Your Database](#step-4--seed-your-database)
7. [Step 5 — Fix the Dockerfile](#step-5--fix-the-dockerfile)
8. [Step 6 — Deploy the Spring Boot App](#step-6--deploy-the-spring-boot-app)
9. [Step 7 — Configure Environment Variables](#step-7--configure-environment-variables)
10. [Step 8 — Persistent File Uploads (Railway Volume)](#step-8--persistent-file-uploads-railway-volume)
11. [Step 9 — Generate a Public URL](#step-9--generate-a-public-url)
12. [Step 10 — Configure ToyyibPay Callbacks](#step-10--configure-toyyibpay-callbacks)
13. [Step 11 — Post-Deployment Verification](#step-11--post-deployment-verification)
14. [Troubleshooting](#troubleshooting)
15. [Cost Estimation](#cost-estimation)

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    RAILWAY PROJECT                       │
│                                                         │
│  ┌─────────────────┐       ┌──────────────────────┐     │
│  │  MySQL Service   │◄─────│  FleetShare Service  │     │
│  │  (Managed DB)    │       │  (Docker Container)  │     │
│  │                  │       │                      │     │
│  │  Auto variables: │       │  Dockerfile build    │     │
│  │  MYSQLHOST       │       │  Port: $PORT         │     │
│  │  MYSQLPORT       │       │                      │     │
│  │  MYSQLUSER       │       │  ┌────────────────┐  │     │
│  │  MYSQLPASSWORD   │       │  │ /app/uploads   │  │     │
│  │  MYSQLDATABASE   │       │  │ (Volume Mount) │  │     │
│  └─────────────────┘       │  └────────────────┘  │     │
│                             └──────────────────────┘     │
│                                      │                   │
│                              *.up.railway.app            │
└─────────────────────────────────────────────────────────┘
         ▲                        ▲
         │                        │
    ToyyibPay Callbacks      Browser Access
```

**Why Docker?** Your project already has a `Dockerfile`. Railway detects it automatically and builds from it — no extra config needed. Docker gives you full control over the Java version, build steps, and directory structure.

---

## 2. Prerequisites

Before you begin, make sure you have:

| Item | Why You Need It |
|------|----------------|
| **GitHub account** | Railway deploys from GitHub repos |
| **Railway account** | Sign up at [railway.app](https://railway.app) — use GitHub login |
| **MySQL client** (local) | To import the database dump. Use MySQL Workbench, DBeaver, or CLI |
| **Your `.env` values** | API keys for Groq, ToyyibPay, Gmail — you'll enter these in Railway |

> [!IMPORTANT]
> Railway offers a **30-day trial with $5 credit**. After that, the **Hobby plan ($5/month)** is required. There is no permanent free tier.

---

## Step 1 — Push Code to GitHub

If your code is not already on GitHub:

```powershell
# From your project root
cd "c:\Users\NAIM\Documents\UMT\FYP\Project Repo\fleetshare\fleetshare"

# Initialize git (skip if already initialized)
git init
git remote add origin https://github.com/YOUR_USERNAME/fleetshare.git

# Ensure secrets are NOT committed
# Your .gitignore already excludes .env files ✅

git add .
git commit -m "Prepare for Railway deployment"
git push -u origin main
```

> [!CAUTION]
> **Never commit your `.env` file or API keys!** Your `.gitignore` already has rules for this — double-check that `src/main/resources/.env` is listed there (it is ✅).

---

## Step 2 — Create a Railway Project

1. Go to [railway.app](https://railway.app) and log in with GitHub
2. Click **"New Project"** in the top-right
3. Select **"Deploy from GitHub Repo"**
4. Authorize Railway to access your GitHub account
5. Select your **fleetshare** repository
6. Railway will create a project — **do NOT deploy yet**, we need to set up the database first

> [!TIP]
> You can click **"Add a Service"** and select "Empty Service" to defer the initial deploy while you configure everything.

---

## Step 3 — Provision MySQL Database

### 3.1 — Add MySQL to Your Project

1. In your Railway project dashboard, click **"+ New"**
2. Select **"Database"** → **"MySQL"**
3. Railway will spin up a managed MySQL instance in seconds

### 3.2 — Find Your Connection Credentials

1. Click on the **MySQL service** card
2. Go to the **"Variables"** tab
3. You'll see these auto-generated variables:

| Variable | Example Value | Description |
|----------|--------------|-------------|
| `MYSQLHOST` | `monorail.proxy.rlwy.net` | Database host |
| `MYSQLPORT` | `28193` | Database port |
| `MYSQLUSER` | `root` | Database username |
| `MYSQLPASSWORD` | `aB3cD4eF5gH6` | Database password |
| `MYSQLDATABASE` | `railway` | Database name |
| `MYSQL_URL` | `mysql://root:...@.../railway` | Full connection string |

> [!NOTE]
> These variables are **automatically available** to all services in the same project via Railway's internal networking. Your `application-prod.properties` already references them correctly:
> ```properties
> spring.datasource.url=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}
> spring.datasource.username=${MYSQLUSER}
> spring.datasource.password=${MYSQLPASSWORD}
> ```

---

## Step 4 — Seed Your Database

Your project has `ddl-auto=none`, so the schema won't be auto-created. You **must** import the SQL dump.

### Option A — MySQL CLI (Recommended)

```powershell
# Copy the connection details from Railway's Variables tab
# Then run this from your project root:

mysql -h monorail.proxy.rlwy.net -P 28193 -u root -p railway < db/fleetshare_dump.sql
```

It will prompt for the password — paste the `MYSQLPASSWORD` value from Railway.

### Option B — MySQL Workbench (GUI)

1. Open MySQL Workbench
2. Create a **New Connection**:
   - **Hostname:** `monorail.proxy.rlwy.net` (from `MYSQLHOST`)
   - **Port:** `28193` (from `MYSQLPORT`)
   - **Username:** `root` (from `MYSQLUSER`)
   - **Password:** paste `MYSQLPASSWORD`
3. Connect to the server
4. Go to **Server** → **Data Import**
5. Select **"Import from Self-Contained File"**
6. Browse to `db/fleetshare_dump.sql`
7. Set **Default Target Schema** to `railway` (or the `MYSQLDATABASE` value)
8. Click **"Start Import"**

### Option C — Railway SQL Console

1. Click on the MySQL service in Railway
2. Go to the **"Data"** tab
3. Paste the contents of `fleetshare_dump.sql` into the SQL console
4. Execute

> [!WARNING]
> The dump file was exported from MariaDB 10.4. Railway uses MySQL 8.x. If you encounter syntax errors, the most common fix is removing the `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci` from CREATE TABLE statements, or removing backtick differences. In practice, this dump should import cleanly.

---

## Step 5 — Fix the Dockerfile

> [!IMPORTANT]
> Your current Dockerfile has a **critical bug** in the `ENTRYPOINT` line. The exec form (`[...]`) does NOT expand environment variables like `${PORT}`. This will cause Railway to fail health checks.

### Current (Broken):
```dockerfile
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

### Fixed — Use Shell Form:
```dockerfile
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]
```

**Why?** Docker's exec form (`["cmd", "arg"]`) runs the command directly without a shell, so `${PORT}` is treated as a literal string. Wrapping it in `sh -c` ensures the shell expands the variable.

### Complete Fixed Dockerfile:

```dockerfile
# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make the wrapper executable and download dependencies (this caches the layer)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Create the lightweight production image
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create the uploads directory for persistent storage
RUN mkdir -p /app/uploads/vehicles /app/uploads/profiles /app/uploads/payments /app/uploads/qrcodes

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Railway uses the PORT environment variable)
EXPOSE 8080

# Run the application using the production profile
# MUST use sh -c for environment variable expansion
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]
```

**Key changes:**
1. **`ENTRYPOINT`** uses `sh -c` for proper `$PORT` expansion
2. **`mkdir -p`** creates all subdirectories (`vehicles`, `profiles`, `payments`, `qrcodes`) upfront
3. Removed the `VOLUME` instruction (Railway manages volumes via its dashboard, not Dockerfile)

---

## Step 6 — Deploy the Spring Boot App

### 6.1 — Connect GitHub Repo

If you haven't already:
1. In your Railway project, click **"+ New"** → **"GitHub Repo"**
2. Select your **fleetshare** repository
3. Railway will detect the `Dockerfile` and start building

### 6.2 — Watch the Build

1. Click on your service card
2. Go to the **"Deployments"** tab
3. Click the active deployment to see live build logs
4. The build will:
   - Pull `eclipse-temurin:17-jdk-jammy`
   - Run `./mvnw dependency:go-offline` (this takes 2-5 minutes the first time)
   - Run `./mvnw clean package -DskipTests`
   - Create the production image

> [!TIP]
> If the build fails due to Spring Boot 4.x snapshot dependencies, make sure the Spring Snapshots repository in your `pom.xml` is accessible. Railway builds have internet access, so this should work.

---

## Step 7 — Configure Environment Variables

This is the **most critical step**. Click your FleetShare service → **"Variables"** tab → **"New Variable"**.

### 7.1 — Database Variables (Auto-Linked)

If both services are in the same project, Railway can auto-reference them. Click **"Add Reference"** and select your MySQL service. This creates:

| Variable | Source |
|----------|--------|
| `MYSQLHOST` | Auto from MySQL service |
| `MYSQLPORT` | Auto from MySQL service |
| `MYSQLUSER` | Auto from MySQL service |
| `MYSQLPASSWORD` | Auto from MySQL service |
| `MYSQLDATABASE` | Auto from MySQL service |

### 7.2 — Application Variables (Manual)

Add these one by one:

| Variable | Value | Notes |
|----------|-------|-------|
| `APP_BASE_URL` | `https://your-app.up.railway.app` | Update after generating domain (Step 9) |
| `MAIL_USERNAME` | `fleetshare.my@gmail.com` | Your Gmail address |
| `MAIL_PASSWORD` | `bduqcajwaboallqu` | Gmail App Password (not your Gmail password) |
| `MAIL_FROM` | `fleetshare.my@gmail.com` | Sender address |
| `GROQ_API_KEY` | `gsk_FdEEOV...` | From [console.groq.com](https://console.groq.com) |
| `TOYYIBPAY_PLATFORM_SECRET_KEY` | `bzq4tz7l-40ru-...` | From ToyyibPay dashboard |
| `TOYYIBPAY_PLATFORM_CATEGORY_CODE` | `0khcgakb` | From ToyyibPay dashboard |
| `TOYYIBPAY_PLATFORM_USERNAME` | `NaimNajmi` | Your ToyyibPay username |
| `AI_PROVIDER` | `groq` | Or `cerebras` / `openrouter` |

> [!CAUTION]
> **Do NOT paste your actual API keys into this guide if sharing it.** The values above are from your local `.env` — enter them only in Railway's Variables panel, which is encrypted and secure.

### 7.3 — How Variables Map to Your Code

Your `application-prod.properties` already reads these via `${VARIABLE_NAME}` syntax:

```
spring.mail.username=${MAIL_USERNAME}        ← reads MAIL_USERNAME env var
spring.mail.password=${MAIL_PASSWORD}        ← reads MAIL_PASSWORD env var
ai.assistant.groq.api-key=${GROQ_API_KEY}    ← reads GROQ_API_KEY env var
app.base-url=${APP_BASE_URL:https://...}     ← reads APP_BASE_URL with fallback
```

Spring Boot automatically picks up environment variables — no code changes needed.

---

## Step 8 — Persistent File Uploads (Railway Volume)

> [!IMPORTANT]
> **Without a Volume, all uploaded files (vehicle images, profile photos, payment proofs, QR codes) will be DELETED on every redeploy.** Railway containers have ephemeral filesystems.

### 8.1 — Create a Volume

1. Click your **FleetShare service** in Railway
2. Go to the **"Volumes"** tab (or **Settings** → **Volumes**)
3. Click **"Create Volume"**
4. Set the **Mount Path** to: `/app/uploads`
5. Give it a name like `fleetshare-uploads`
6. Click **"Create"**

### 8.2 — How It Works

```
Container Filesystem (ephemeral)         Railway Volume (persistent)
┌─────────────────────────────┐         ┌──────────────────────────┐
│ /app/                       │         │                          │
│   ├── app.jar               │         │  /app/uploads/ ◄─── MOUNTED
│   └── uploads/ ─────────────┼────────►│    ├── vehicles/         │
│        (mount point)        │         │    ├── profiles/         │
│                             │         │    ├── payments/         │
└─────────────────────────────┘         │    └── qrcodes/          │
                                        └──────────────────────────┘
                                        Survives redeploys ✅
```

### 8.3 — Why This Works With Your Code

Your `application-prod.properties` already sets:
```properties
app.upload.dir=/app/uploads
app.upload.vehicle-images=${app.upload.dir}/vehicles
app.upload.profile-images=${app.upload.dir}/profiles
app.upload.payment-proofs=${app.upload.dir}/payments
app.upload.qr-codes=${app.upload.dir}/qrcodes
```

And your `FileStorageService.java` creates the subdirectories on startup via `@PostConstruct`:
```java
@PostConstruct
public void init() {
    Files.createDirectories(Paths.get(vehicleImagesDir));
    Files.createDirectories(Paths.get(profileImagesDir));
    Files.createDirectories(Paths.get(paymentProofsDir));
    Files.createDirectories(Paths.get(qrCodesDir));
}
```

And your `WebConfig.java` serves them at `/uploads/**`:
```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + uploadDir + "/");
```

**Everything is already configured.** The Volume just needs to be mounted at `/app/uploads`.

### 8.4 — Volume Size Limits

| Railway Plan | Volume Limit |
|-------------|-------------|
| Trial | 0.5 GB |
| Hobby ($5/mo) | 5 GB |
| Pro ($20/mo) | 50 GB+ |

For a demo/FYP project, the Hobby plan's 5 GB is more than sufficient.

---

## Step 9 — Generate a Public URL

1. Click your **FleetShare service**
2. Go to **"Settings"** tab
3. Scroll to **"Public Networking"**
4. Click **"Generate Domain"**
5. Railway gives you a URL like: `fleetshare-production.up.railway.app`

### Update APP_BASE_URL

Now go back to **Variables** and update:

```
APP_BASE_URL = https://fleetshare-production.up.railway.app
```

This is critical because your `application-prod.properties` uses it:
```properties
app.base-url=${APP_BASE_URL:https://your-app.up.railway.app}
```

And your ToyyibPay callbacks use `app.base-url` to build return/callback URLs.

### Custom Domain (Optional)

If you have your own domain:
1. In **Settings** → **Public Networking**, click **"+ Custom Domain"**
2. Enter your domain (e.g., `fleetshare.com.my`)
3. Railway gives you DNS records (CNAME + TXT)
4. Add those records at your domain registrar
5. Railway auto-provisions SSL via Let's Encrypt

---

## Step 10 — Configure ToyyibPay Callbacks

### 10.1 — Production vs Development

Your `application-prod.properties` already switches to the **production** ToyyibPay API:
```properties
# Dev uses: https://dev.toyyibpay.com
# Prod uses: https://toyyibpay.com
toyyibpay.api.base-url=https://toyyibpay.com
```

> [!WARNING]
> **`dev.toyyibpay.com`** is for sandbox testing only. For real payments, you must use `toyyibpay.com`. Make sure your ToyyibPay account is verified for production.

### 10.2 — Callback URL Configuration

When creating bills via ToyyibPay API, your app sends callback/return URLs like:

```
Return URL:   https://fleetshare-production.up.railway.app/renter/payment/toyyibpay/return
Callback URL: https://fleetshare-production.up.railway.app/renter/payment/toyyibpay/callback
```

These are built using `${app.base-url}` — which is why `APP_BASE_URL` must be correctly set.

### 10.3 — ToyyibPay Dashboard Settings

If ToyyibPay requires you to whitelist callback domains:
1. Log in to [toyyibpay.com](https://toyyibpay.com)
2. Go to your account settings
3. Add your Railway URL as an allowed callback domain

---

## Step 11 — Post-Deployment Verification

### Checklist

After your app is live, verify each component:

| # | Check | How to Verify |
|---|-------|---------------|
| 1 | **App loads** | Visit `https://your-app.up.railway.app` — you should see the landing page |
| 2 | **Login works** | Try logging in as `admin@rentmy.com.my` |
| 3 | **Database connected** | If login works, the DB is connected |
| 4 | **Images display** | Check if vehicle images load on the browse page |
| 5 | **File upload** | Upload a new profile photo — verify it persists after a page refresh |
| 6 | **Email works** | Trigger a registration or booking — check if email arrives |
| 7 | **ToyyibPay** | Create a test booking and go through the payment flow |
| 8 | **AI Assistant** | As an owner/admin, try the AI Data Assistant |
| 9 | **PDF generation** | Download an invoice PDF |
| 10 | **Redeploy persistence** | Push a commit, wait for redeploy, verify uploaded images still exist |

### Viewing Logs

1. Click your FleetShare service → **"Deployments"** tab
2. Click the active deployment
3. View live application logs (same as `app.log` locally)
4. Look for:
   - `Started FleetshareApplication in X seconds` — app is up
   - Any `ERROR` or `Exception` — something went wrong

---

## Troubleshooting

### ❌ Build Fails: "Could not resolve dependencies"

**Cause:** Spring Boot 4.0.1-SNAPSHOT requires the Spring Snapshots repository.

**Fix:** Your `pom.xml` already has the snapshot repository. Ensure it's accessible:
```xml
<repositories>
    <repository>
        <id>spring-snapshots</id>
        <url>https://repo.spring.io/snapshot</url>
    </repository>
</repositories>
```

If snapshots are unstable, consider pinning to a stable release.

---

### ❌ App Starts But Returns 502 Bad Gateway

**Cause:** Railway can't reach your app's port.

**Fix:** Ensure:
1. Your Dockerfile ENTRYPOINT uses `sh -c` (see Step 5)
2. `application-prod.properties` has `server.port=${PORT:8080}`
3. The `PORT` env var is automatically set by Railway — don't override it manually

---

### ❌ "Access denied for user 'root'@..."

**Cause:** Database variables not linked.

**Fix:**
1. Go to your FleetShare service → Variables
2. Click **"Add Reference"** → select the MySQL service
3. Ensure `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE` all exist

---

### ❌ Images Disappear After Redeploy

**Cause:** No Railway Volume attached.

**Fix:** Follow [Step 8](#step-8--persistent-file-uploads-railway-volume) to create and mount a volume at `/app/uploads`.

---

### ❌ ToyyibPay Payment Returns "Invalid Callback"

**Cause:** `APP_BASE_URL` is wrong or not set.

**Fix:** Update the variable to your exact Railway URL (with `https://`, no trailing slash):
```
APP_BASE_URL=https://fleetshare-production.up.railway.app
```

---

### ❌ Emails Not Sending

**Cause:** Gmail blocks "less secure app" access.

**Fix:** You must use a **Gmail App Password** (not your regular password):
1. Go to [myaccount.google.com](https://myaccount.google.com)
2. **Security** → **2-Step Verification** (must be enabled)
3. **App passwords** → Generate one for "Mail"
4. Use that 16-character password as `MAIL_PASSWORD` in Railway

---

## Cost Estimation

| Resource | Hobby Plan Cost |
|----------|----------------|
| **FleetShare Service** (Docker) | ~$5/mo (based on usage) |
| **MySQL Database** | ~$1-3/mo (based on storage) |
| **Volume Storage** (5GB max) | Included |
| **Networking/SSL** | Included |
| **Total Estimate** | **~$5-8/month** |

Railway charges based on actual resource consumption (CPU hours + RAM + storage), not fixed pricing. For a low-traffic FYP demo, costs will be minimal.

---

## Quick Reference: Complete Variable List

| Variable | Example | Required? |
|----------|---------|-----------|
| `MYSQLHOST` | *(auto from DB service)* | ✅ Auto |
| `MYSQLPORT` | *(auto from DB service)* | ✅ Auto |
| `MYSQLUSER` | *(auto from DB service)* | ✅ Auto |
| `MYSQLPASSWORD` | *(auto from DB service)* | ✅ Auto |
| `MYSQLDATABASE` | *(auto from DB service)* | ✅ Auto |
| `APP_BASE_URL` | `https://xyz.up.railway.app` | ✅ Manual |
| `MAIL_USERNAME` | `fleetshare.my@gmail.com` | ✅ Manual |
| `MAIL_PASSWORD` | `bduqcajwaboallqu` | ✅ Manual |
| `MAIL_FROM` | `fleetshare.my@gmail.com` | ✅ Manual |
| `GROQ_API_KEY` | `gsk_...` | ✅ Manual |
| `AI_PROVIDER` | `groq` | ⚡ Optional (defaults to groq) |
| `TOYYIBPAY_PLATFORM_SECRET_KEY` | `bzq4tz7l-...` | ✅ Manual |
| `TOYYIBPAY_PLATFORM_CATEGORY_CODE` | `0khcgakb` | ✅ Manual |
| `TOYYIBPAY_PLATFORM_USERNAME` | `NaimNajmi` | ✅ Manual |
| `CEREBRAS_API_KEY` | `...` | ⚡ Optional |
| `OPENROUTER_API_KEY` | `...` | ⚡ Optional |

---

## Summary: Deployment Flowchart

```
GitHub Push
    │
    ▼
Railway Detects Dockerfile
    │
    ▼
Stage 1: Maven Build (mvnw clean package)
    │
    ▼
Stage 2: Create JRE Image + app.jar
    │
    ▼
Container Starts: java -jar app.jar
    │
    ├── Reads $PORT from Railway ──► Binds to correct port
    ├── Reads $MYSQL* vars ────────► Connects to Railway MySQL
    ├── Reads $MAIL_* vars ────────► Connects to Gmail SMTP
    ├── Reads $GROQ_API_KEY ───────► Enables AI Assistant
    ├── Reads $TOYYIBPAY_* ────────► Enables payment gateway
    └── Reads /app/uploads (Volume)► Persistent file storage
    │
    ▼
✅ App is live at https://your-app.up.railway.app
```

---

> [!TIP]
> **Before deploying, apply the Dockerfile fix from Step 5.** This is the single most important change — without it, Railway cannot route traffic to your app.
