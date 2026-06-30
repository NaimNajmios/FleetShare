# FleetShare: Centralized Vehicle Rental Management System

**FleetShare** is a multi-tenant web platform designed to connect individual vehicle owners with renters. This document serves as the primary development checklist to track the implementation of modules using the **Spring Boot** ecosystem.

-----

## Project Architecture (Spring Boot)

The application follows a standard **Layered Architecture**, leveraging the full power of the Spring ecosystem.

### 1. Core Framework & Configuration
* **Spring Boot**: Auto-configuration and dependency management.
* **Maven**: Build automation and dependency resolution.
* **Properties**: `application.properties` for environment-specific configurations (Dev/Prod).

### 2. Implemented Architecture (Current State)
These elements are currently present in the codebase:

* **Domain Layer (Entities)**:
 * **Inheritance Strategy**: Uses `@Inheritance(strategy = InheritanceType.JOINED)` for the `User` hierarchy (`Renter`, `FleetOwner`, `PlatformAdmin`). This maps to a normalized database schema where shared attributes are in a base table and specific attributes in joined tables.
 * **JPA Annotations**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column` for ORM mapping.
* **Data Access Layer (Repositories)**:
 * **Spring Data JPA**: Interfaces extend `JpaRepository<T, ID>` to provide CRUD operations out-of-the-box without boilerplate code.
 * **Query Methods**: Automatic query generation based on method names (e.g., `findByUsername`).
* **Security Layer**:
 * **Configuration**: `@EnableWebSecurity` and `SecurityFilterChain` bean to define access rules.
 * **Authentication**: `CustomUserDetailsService` implements `UserDetailsService` to load user data from the database.
 * **Encryption**: `BCryptPasswordEncoder` bean for secure password hashing.
 * **Context**: `SecurityContextHolder` to access the currently authenticated user.
* **Web Layer (Controllers)**:
 * **MVC Controllers**: `@Controller` for serving Thymeleaf templates (`.html`).
 * **Model**: Used to pass data from controllers to views.

### 3. Implemented Architecture Features
All features previously listed as "planned" are now implemented:

* **Advanced Data Modeling**: JPA relationships (`@OneToMany`, `@ManyToOne`, `@OneToOne`) across all entities.
* **Async Processing**: `@EnableAsync` with `AsyncConfig` for non-blocking email and background tasks.
* **Validation & Error Handling**: Bean Validation (`@Valid` on DTOs), `@ControllerAdvice` via `GlobalExceptionHandler`, and custom exceptions (`RegistrationException`).
* **Retry & Resilience**: `@EnableRetry` via `RetryConfig` for fault-tolerant external API calls (payment gateway).
* **AOP Audit Logging**: `LoggingAspect` for method-level logging across controllers, services, and repositories.
* **REST API**: JSON endpoints for search, payments, reports, and platform management.
* **External Integrations**: ToyyibPay payment gateway, multi-provider LLM AI assistant (Groq/Cerebras/OpenRouter), Gmail SMTP.

-----

## Module 1: User Management & Security

**Objective:** Handle authentication, authorization, and profile management for all three actors via Spring Security.

### Implementation Checklist

- [x] **[R1 / R9] User Registration**
 - [x] Create `UserRegistrationDTO` to handle form inputs.
 - [x] Implement `UserService.registerUser()` with password encryption.
 - [x] Validation: Use Bean Validation (`@Valid`, `@Email`, `@NotBlank`).
- [x] **[R1 / R9 / R18] Secure Login**
 - [x] Implement `CustomUserDetailsService` loading users from the DB.
 - [x] Configure `SecurityFilterChain` for URL protection.
- [x] **[R22] Role-Based Access Control (RBAC)**
 - [x] **Security:** Annotate Controllers with `@PreAuthorize("hasRole('RENTER')")`.
 - [x] **Security:** Use `@PostAuthorize` to ensure Owners only view their own data.
- [x] **Profile Management**
 - [x] Service method `updateProfile(UserId, UpdateDTO)`.
- [x] **Authentication UI Enhancements**
 - [x] Functional light and dark mode toggle for login and registration pages.

### ️ Implementation Details (Spring Boot)

* **Entities:**
 * `@Entity @Table(name="users") public class User {...}`
 * Inheritance Strategy: `InheritanceType.JOINED` is recommended since you have specific tables (`Renters`, `FleetOwners`, `PlatformAdmins`) linked by `user_id`.
* **Security:**
 * **Bean:** `PasswordEncoder` using `BCryptPasswordEncoder`.
 * **Logic:** `checkActiveBookings(userID)` should be a private method in `UserService` called within `@Transactional deleteUser()`.

-----

## Module 2: Fleet Management (Owner)

**Objective:** Allow Fleet Owners to manage their vehicle inventory and availability.

### Implementation Checklist

- [x] **[R2] Add Vehicle**
 - [x] `VehicleController.addVehicle(@RequestBody VehicleDTO)` endpoint.
 - [x] Handle photo uploads using `MultipartFile` and store paths in DB.
- [x] **[R2] Manage Fleet Inventory**
 - [x] Repository: `findByFleetOwnerId(Long ownerId)`.
 - [x] **CRUD:** Service methods `updateVehicle` and `deleteVehicle`.
- [x] **[R3] Availability Management**
 - [x] Logic to check if vehicle status can be toggled (check dependencies).
- [x] **[R7] Utilization Reports**
 - [x] create a Custom Interface (Projection) for utilization stats to map JPQL results.
- [x] **Customer Management**
 - [x] Customer management module with view and listing capabilities for fleet owners.
- [x] **Owner Dashboard**
 - [x] Dashboard with agenda tracking and metrics summary.

### ️ Implementation Details (Spring Boot)

* **Repository:**
 * `public interface VehicleRepository extends JpaRepository<Vehicle, Long> { ... }`
* **Logic:**
 * **Pricing:** Map `@OneToMany` relationship between `Vehicle` and `VehiclePriceHistory`. Fetch the active rate using a custom `@Query` filtering by `effective_start_date`.
 * **Delete Vehicle:**
 ```java
 @Transactional
 public void deleteVehicle(Long vehicleId) {
 boolean hasActiveBookings = bookingRepository.existsByVehicleIdAndStatusIn(
 vehicleId, List.of(Status.PENDING, Status.CONFIRMED));
 if (hasActiveBookings) throw new ResourceConflictException("Cannot delete vehicle with active bookings");
 vehicleRepository.deleteById(vehicleId);
 }
 ```

-----

## Module 3: Booking & Reservation

**Objective:** Facilitate the search, booking, and approval process.

### Implementation Checklist

- [x] **[R10] Vehicle Search**
 - [x] Implement `VehicleSpecification` (Spring Data JPA Specifications) for dynamic filtering (date, price, location).
- [x] **[R11] Vehicle Details**
 - [x] DTO projection including `Vehicle`, current `Price`, and `Owner` info.
- [x] **[R12] Booking Creation**
 - [x] `BookingService.createBooking()` with `@Transactional`.
 - [x] **Validation:** Custom logic to check date overlaps.
- [x] **[R5] Booking Management**
 - [x] Endpoints: `PATCH /api/bookings/{id}/status`.
 - [x] Logic: Update `BookingStatusLog` automatically on status change.
- [x] **[R15] Booking History**
 - [x] `Pageable` endpoints for history lists (Pagination).
- [x] **Advanced Booking Management**
 - [x] Owner-side booking edit interface (`edit-booking.html`).
 - [x] `BookingPriceSnapshot` for preserving price and terms at the time of booking.
 - [x] Automated status tracking and history logging.
- [x] **Renter Dashboard**
 - [x] Renter home dashboard view displaying user interface and activity overview.

### ️ Implementation Details (Spring Boot)

* **Repository:**
 * **Conflict Detection Query:**
 ```java
 @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.vehicle.id =:vehicleId " +
 "AND b.status IN ('PENDING', 'CONFIRMED') " +
 "AND ((b.startDate BETWEEN:start AND:end) OR (b.endDate BETWEEN:start AND:end))")
 boolean existsOverlappingBooking(Long vehicleId, LocalDateTime start, LocalDateTime end);
 ```
* **Audit Logic:**
 * Use **JPA Entity Listeners** (`@PreUpdate`, `@PostPersist`) or explicit calls in the Service layer to insert records into `BookingStatusLog` whenever `Booking` is modified.

-----

## Module 4: Maintenance Management

**Objective:** Track vehicle health, manage maintenance lifecycle, and prevent bookings during maintenance.

### Implementation Checklist

- [x] **[R4] Maintenance Logging**
 - [x] Full CRUD via `MaintenanceService` (add, update, soft-delete, view by vehicle/owner).
 - [x] Input validation with duplicate detection and business-rule warnings (`validateMaintenanceCreation`).
- [x] **[R3] Auto-Availability Integration**
 - [x] Service Logic: When Maintenance is `IN_PROGRESS`, set Vehicle `status` = `MAINTENANCE`.
- [x] **Maintenance History & Status Tracking**
 - [x] `VehicleMaintenanceLog` entity for full status audit trail (actor, timestamp, remarks).
 - [x] Status lifecycle: `PENDING` → `IN_PROGRESS` → `COMPLETED` / `CANCELLED`.
 - [x] Automatic timestamping: `actualStartTime` on `IN_PROGRESS`, `actualEndTime` on `COMPLETED`.
- [x] **Maintenance Dashboard & Analytics**
 - [x] Owner maintenance dashboard with KPI cards, filtering, and charts.
 - [x] Administrative oversight dashboard covering platform-wide maintenance (`/admin/maintenance`).
 - [x] Detailed historical timeline tracking view for specific records (`view-maintenance.html`) for both Owners and Admins.
 - [x] `MaintenanceStatsDTO` providing monthly aggregation, cost summaries, and average cost per record.
- [x] **Maintenance Parts & Logistics**
 - [x] Parts tracking for maintenance records (`MaintenancePart` entity).
 - [x] Service provider management (`ServiceProvider` entity).

### ️ Implementation Details (Spring Boot)

* **Entity:** `VehicleMaintenance` — unified entity with fields for scheduling, cost tracking (`estimatedCost`, `finalCost`), status enum, and soft-delete support (`isDeleted`, `deletedAt`).
* **Repository:** `VehicleMaintenanceRepository` with queries: `findByIsDeletedFalse`, `findByFleetOwnerIdAndIsDeletedFalse`, `findByVehicleIdAndIsDeletedFalse`.
* **Removed:** Legacy `MaintenanceSchedule` entity, `MaintenanceScheduleDTO`, `MaintenanceScheduleRepository`, `MaintenanceScheduleService`, `maintenance-calendar.html`, `maintenance-schedules.html` — consolidated into the unified `VehicleMaintenance` system.

-----

## Module 5: Payment & Financials

**Objective:** Manage transactions, proof of payment, and revenue reporting.

### Implementation Checklist

- [x] **[R13] Payment Processing**
 - [x] Endpoint to upload Proof (`MultipartFile`).
 - [x] Service to link Payment to Invoice.
- [x] **[R16] Payment History**
 - [x] Repository: `findByInvoice_Renter_Id(Long renterId)`.
- [x] **[R6] Revenue Reports**
 - [x] JPQL Aggregation Query for Owner Revenue.
- [x] **Payment Settings (Owner)**
 - [x] ToyyibPay BYOK (Bring Your Own Key) integration — owners configure their own secret key and category code.
 - [x] QR code image upload for alternative payment (DuitNow / TNG eWallet).
 - [x] Bank account details (bank name, account number, account holder).
- [x] **Dynamic Payment Methods (Renter)**
 - [x] Payment page displays only methods the fleet owner has configured.
 - [x] Card/FPX available only if owner has ToyyibPay configured.
 - [x] Bank Transfer available only if owner has bank info or QR code set.
 - [x] Cash at counter always available.
- [x] **ToyyibPay & Automated Payouts**
 - [x] Integrated ToyyibPay for real-time payment processing and callbacks.
 - [x] **Commission Management:** Automatic platform fee calculation.
 - [x] **Owner Payouts:** Dashboard for owners to track earnings and request payouts.
 - [x] **Failed Payment Workflow:** Automated handling and recovery logic for failed transactions.
- [x] **Invoicing and Receipts**
 - [x] `InvoiceService` and `ReceiptService` implementation for generating professional PDF invoices and payments.
- [x] **Payment Methods Extension**
 - [x] Integration of 'FPX' payment method support in the database schema.

### ️ Implementation Details (Spring Boot)

* **Workflow Implementation:**
 * Use a **State Machine** pattern (or simple Service checks) to ensure transitions:
 `PENDING_VERIFICATION` -> `VERIFIED` -> triggers `Invoice.PAID` -> triggers `Booking.CONFIRMED`.
* **Entities:**
 * `@Entity public class Invoice {...}`
 * `@Entity public class Payment {...}`
 * Relationship: `@OneToMany(mappedBy="invoice")` in Invoice.

-----

## Module 6: Administrator Dashboard

**Objective:** Global oversight and system management.

### Implementation Checklist

- [x] **[R19] User Management**
 - [x] `AdminUserController` with endpoints to `PATCH /users/{id}/active`.
- [x] **[R20] Dispute Resolution**
 - [x] Read-only view of `BookingStatusLog` and `PaymentStatusLog`.
- [x] **[R21] Platform Reporting**
 - [x] **Service:** `AdminReportService` gathering counts via `long userCount = userRepository.count();`.
- [x] **Global Platform Search**
 - [x] Cross-entity search mechanism spanning vehicles, bookings, renters, and fleet owners.
 - [x] Search controller exposing backend REST endpoints.
- [x] **Modular Report Generation**
 - [x] Multi-role report generation controller supporting Admins, Owners, and Renters.
 - [x] Dynamic report configuration UI via standard modals.
 - [x] Professional PDF export capability using FlyingSaucer/IText with custom HTML/CSS templates.
 - [x] Support for AI-generated insight reports and enhanced complex AI report prompts.
- [x] **Enhanced User Management & Analytics**
 - [x] Improved user management dashboard with DTOs, service layer, and corrected profile image rendering.
 - [x] Implementation of platform-wide analytics and management dashboard.

### ️ Implementation Details (Spring Boot)

* **Security:**
 * Annotate the entire `AdminController` with `@PreAuthorize("hasRole('PLATFORM_ADMIN')")`.
* **Audit:**
 * Consider **Spring Data Envers** for easy historical auditing of entities if the custom log tables become too complex to manage manually.

-----

## Frontend UI & Design System

**Objective:** Provide a consistent, responsive, and robust user interface across all roles utilizing native HTML/CSS and Thymeleaf.

### Implementation Checklist

- [x] **Core Style Architecture**
 - [x] Implemented `fleetshare-design-system.css` featuring centralized CSS variables and consistent UI components (cards, badges, modals, typography).
- [x] **Authentication Pages**
 - [x] Redesigned login page (`login.html`) with responsive CSS (`auth.css`) and custom styling.
- [x] **JavaScript Functionality**
 - [x] Included `template.js` for handling sidebar navigation collapse/expansion, UI styling logic, and initial page component setup.
 - [x] Built real-time unified search UI with `search.js` bridging REST API queries to frontend templates.
- [x] **Layout & Templating**
 - [x] Base layouts established: `admin-layout.html` and `owner-layout.html`.
 - [x] Integration of reusable Thymeleaf fragments (sidebar, header, reusable navigation bars, and generic modals).
 - [x] Shared UI components and layout fragments for dashboard consistency.
- [x] **Status Tracking & Timelines**
 - [x] Interactive status timelines for Bookings and Maintenance records.
 - [x] Visual cues for payment verification and processing windows.
- [x] **Visual Enhancements & UI Standardization**
 - [x] Integration of Lottie animations across the Renter Portal for a dynamic user experience.
 - [x] Alignment of filter input heights and form components across Admin and Owner management pages.
 - [x] Standardized professional design for PDF invoice and receipt templates.
- [x] **Navigation Fixes**
 - [x] Segment-based validation for precise sidebar active state highlighting.

-----

## Tech Stack & Environment

**Development Environment:**

* **JDK:** Java 17 or 21 (LTS)
* **Framework:** Spring Boot 3.2.5
* **Build Tool:** Maven 3.8+
* **Database:** MySQL 8.0 (H2 in-memory for tests)

**Core Dependencies:**

* `spring-boot-starter-web` — REST / MVC
* `spring-boot-starter-data-jpa` — Database access via JPA/Hibernate
* `spring-boot-starter-security` — Auth & RBAC (`@EnableMethodSecurity`)
* `spring-boot-starter-validation` — Bean Validation (`@Valid`)
* `spring-boot-starter-mail` — Async email via Gmail SMTP
* `spring-boot-starter-thymeleaf` — Server-side HTML templating
* `thymeleaf-extras-springsecurity6` — Thymeleaf security integration
* `thymeleaf-layout-dialect` — Layout composition (decorator pattern)
* `spring-boot-devtools` — Development-time hot reload
* `spring-retry` + `aspectjweaver` — Fault-tolerant external API calls
* `openhtmltopdf-pdfbox v1.0.10` + `openhtmltopdf-svg-support` — PDF generation (invoices, receipts, reports)
* `poi-ooxml v5.2.5` — Excel spreadsheet export
* `mysql-connector-j` — MySQL JDBC driver
* `h2` — In-memory database for testing
* `spring-dotenv` — `.env` file support for local development secrets

-----

## How to Run

1. **Clone the repository.**
2. **Database Setup:**
 * Create a MySQL database (default dev: `fleetshareplayground` on port 3307).
 * Import schema: `mysql -u root -p fleetshareplayground < db/fleetshare_dump.sql`
 * Or copy `application.properties` and update:
 ```properties
 spring.datasource.url=jdbc:mysql://localhost:3306/fleetshareplayground
 spring.datasource.username=root
 spring.datasource.password=yourpassword
 spring.jpa.hibernate.ddl-auto=none
 ```
3. **Environment:**
 * Copy `.env.example` (or create `.env`) and configure API keys:
   - `GROQ_API_KEY` / `CEREBRAS_API_KEY` / `OPENROUTER_API_KEY` (AI assistant)
   - `TOYYIBPAY_SECRET_KEY` / `TOYYIBPAY_CATEGORY_CODE` (payment gateway)
   - `MAIL_USERNAME` / `MAIL_PASSWORD` (Gmail SMTP)
4. **Build:**
 * Run `mvn clean install`
5. **Run:**
 * Run `mvn spring-boot:run`
6. **Access:**
 * App: `http://localhost:8080`

-----

## Codebase Index

### Package Map

Base package: `com.najmi.fleetshare` (11 packages, ~114 Java source files)

| Package | Files | Purpose |
|---|---|---|
| `config/` | 4 | `SecurityConfig`, `WebConfig`, `AsyncConfig`, `RetryConfig` |
| `controller/` | 10 | MVC controllers for all roles + utility controllers |
| `dto/` | 31 | Data Transfer Objects for request/response |
| `entity/` | 18 | JPA entities with joined-table inheritance |
| `exception/` | 1 | `RegistrationException` |
| `repository/` | 17 | Spring Data JPA repositories |
| `security/` | 3 | `CustomUserDetails`, `CustomUserDetailsService`, `CustomAuthenticationSuccessHandler` |
| `service/` | 24 | Business logic layer |
| `util/` | 1 | `SessionHelper` |
| `aspect/` | 1 | `LoggingAspect` (AOP) |
| — | 1 | `FleetshareApplication.java` (main entry) |

### Entity Model (18 entities)

| Entity | Key Relationships |
|---|---|
| `User` (base) | Joined-table inheritance → Renter, FleetOwner, PlatformAdmin |
| `Renter` | extends User |
| `FleetOwner` | extends User; `@OneToMany` → Vehicle; payment config fields |
| `PlatformAdmin` | extends User |
| `Vehicle` | `@ManyToOne` → FleetOwner; `@OneToMany` → Booking, VehiclePriceHistory, VehicleMaintenance |
| `VehiclePriceHistory` | Historical pricing per vehicle |
| `Booking` | `@ManyToOne` → Vehicle, Renter; `@OneToMany` → BookingStatusLog; `@OneToOne` → Invoice |
| `BookingPriceSnapshot` | Frozen price/terms at booking time |
| `BookingStatusLog` | Status change audit trail |
| `Invoice` | `@OneToMany` → Payment |
| `Payment` | Proof-of-payment uploads |
| `PaymentStatusLog` | Payment status audit trail |
| `VehicleMaintenance` | Unified maintenance with status lifecycle; soft-delete support |
| `VehicleMaintenanceLog` | Maintenance status audit trail |
| `MaintenancePart` | Parts tracking per maintenance record |
| `ServiceProvider` | External service provider management |
| `Address` | User addresses with geolocation (lat/lng) |
| `UserRole` | Role enumeration |

### Controllers (10)

| Controller | Route Prefix | Role |
|---|---|---|
| `RootController` | `/` | Public — landing page routing |
| `AuthController` | `/auth` | Public — login, register |
| `PublicController` | — | Public — browse vehicles |
| `RenterController` | `/renter` | Renter — dashboard, bookings, payments |
| `OwnerController` | `/owner` | Owner — vehicles, bookings, maintenance, payouts, reports, AI assistant |
| `AdminController` | `/admin` | Admin — users, maintenance, reports, analytics |
| `SearchController` | `/api/search` | Global cross-entity search |
| `ToyyibPayController` | — | Payment gateway callbacks |
| `TestEmailController` | — | Email debugging |
| `GlobalExceptionHandler` | — | `@ControllerAdvice` — global error handling |

### Services (24)

| Category | Services |
|---|---|
| **Booking & Rental** | `BookingService`, `VehicleManagementService` |
| **Payment & Finance** | `PaymentService`, `ToyyibPayService`, `CommissionService`, `InvoiceService`, `ReceiptService` |
| **Maintenance** | `MaintenanceService`, `MaintenancePartService`, `PredictiveMaintenanceService`, `ServiceProviderService` |
| **AI Assistant** | `AiAssistantService`, `AiReportFacade`, `QueryClassifier`, `PromptTemplateService` |
| **Reporting** | `ReportService`, `ReportChartService`, `CostAnalyticsService` |
| **User Management** | `UserManagementService`, `UserSessionService`, `RegistrationService` |
| **Communication** | `EmailService`, `EmailServiceImpl` |
| **Infrastructure** | `FileStorageService` |

### Configuration (4)

| Config | Key Annotations |
|---|---|
| `SecurityConfig` | `@EnableWebSecurity`, `@EnableMethodSecurity`, `BCryptPasswordEncoder`, CSP headers |
| `WebConfig` | Static resource handling for uploads directory |
| `AsyncConfig` | `@EnableAsync` for non-blocking email/background tasks |
| `RetryConfig` | `@EnableRetry` for fault-tolerant external API calls |

### Frontend Architecture

**Templating**: Thymeleaf with `thymeleaf-layout-dialect` for layout composition

**Layouts** (4): `admin-layout`, `owner-layout`, `renter-layout`, `public-layout`

**Fragments** (23 reusable): sidebars, navbars, footers, pagination, search results, report widgets, modals, vehicle filters, toast notifications

**Template directories** (by role):
- `admin/` — 18 templates
- `owner/` — 20 templates
- `renter/` — 10 templates
- `public/` — Landing + browse vehicles
- `auth/` — Login, register (with light/dark mode toggle)
- `email/` — 7 HTML email templates (welcome, booking, payment, etc.)
- `pdf/` — 4 PDF templates (invoice, receipt, report, AI report)
- `error/` — 403, 404, 500, generic error
- `fragments/` — 23 reusable UI parts
- `pages/ui-features/` — Buttons, dropdowns, typography demos

**Static assets**:
- `css/` — Custom styles (auth, landing, renter, browse-vehicles-ux)
- `js/` — `auth.js`, `analytics.js`, `report-config.js`
- `assets/` — Bootstrap dashboard template with SCSS pipeline, ~25 JS files (charts, data tables, booking management, search)
- `fonts/Roboto/` — Full font family
- `vendors/` — ti-icons, feather icons

**Design System**: `assets/css/fleetshare-design-system.css` with CSS custom properties for consistent theming

### Key Directories

| Directory | Contents |
|---|---|
| `db/` | `fleetshare_dump.sql` — MySQL schema dump with all tables, indexes, and seed data |
| `md/` | 13 prompt engineering markdown files for AI-assisted development (ADR, code review, UI/UX, module enhancement, etc.) |
| `project_file_reference/` | Thesis drafts (PDF + text) |
| `.vscode/` | VS Code workspace settings |

### Test Structure

```
src/test/java/com.najmi.fleetshare/
├── FleetshareApplicationTests.java          # Context load test
├── controller/
│   ├── AuthControllerTest.java              # Auth flow tests
│   └── RenterControllerTest.java            # Renter flow tests
├── performance/
│   └── VehicleFilteringPerformanceTest.java  # Search performance benchmarks
└── service/                                  # (empty — tests pending)
```

Test profile: `application-test.properties` — H2 in-memory with MySQL compatibility mode

-----

## Additional Requirements

### R23: Audit Logging
- [x] AOP-based audit logging via `LoggingAspect` (method entry/exit, exception tracking)
- [x] `audit_logs` database table for persistent audit trail
- [x] Status change tracking via `BookingStatusLog`, `PaymentStatusLog`, `VehicleMaintenanceLog` entities

### R24: Notification System
- [x] Implement email sending service with HTML template support and asynchronous processing
- [ ] Add in-app notifications for real-time updates
- [ ] Support for SMS notifications (optional)

### R25: Reporting & Analytics
- [x] Generate utilization reports for fleet owners
- [x] Platform-wide analytics for administrators
- [x] FleetShare AI Data Assistant with multi-provider LLM support (Groq / Cerebras / OpenRouter) for administrators and owners
- [x] **AI Query Classification:** Automated categorization of user queries for targeted fleet insights.
- [x] Financial reporting and revenue tracking (PDF export for Invoices/Receipts).

-----

## Security Considerations

- Implement proper input validation and sanitization
- Use HTTPS in production environment
- Regular security updates and dependency scanning
- SQL injection prevention using parameterized queries
- XSS protection through proper encoding
- CSRF protection for state-changing operations

-----

## Testing Strategy

- Unit tests for service layer and utilities
- Integration tests for repository layer
- API testing for controller endpoints
- Security testing for authentication and authorization
- Performance testing for critical workflows

-----

## Deployment Notes

### Docker
A multi-stage `Dockerfile` is included for containerized deployment. Build and run:
```bash
docker build -t fleetshare .
docker run -p 8080:8080 --env-file .env fleetshare
```

### Railway (Production)
A `Procfile` and `application-prod.properties` are pre-configured for Railway deployment:
- Reads DB connection from Railway-provided env vars (`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`)
- Uses ephemeral filesystem (uploads stored in Railway's temp storage)
- Configure AI assistant API keys and ToyyibPay credentials as Railway secrets

### General Deployment
- Configure production database connection (update `application-prod.properties`)
- Set up proper logging levels and rotation
- Configure email service for notifications (Gmail SMTP via `.env`)
- Set up persistent file storage for uploads (`app.upload.dir`):
 - `app.upload.profile-images` — User profile photos
 - `app.upload.payment-proofs` — Payment receipt uploads
 - `app.upload.qr-codes` — Owner payment QR code images
- Configure AI assistant API keys: `GROQ_API_KEY`, `CEREBRAS_API_KEY`, `OPENROUTER_API_KEY`
- Set up monitoring and health checks

-----

## Database Schema Overview

### Core Tables:
- `users` — Base user table with common attributes (joined-table inheritance)
- `renters` — Renter-specific details (extends users)
- `fleet_owners` — Owner-specific details (extends users), includes payment config (`toyyibpay_secret_key`, `toyyibpay_category_code`, `payment_qr_url`, `bank_name`, `bank_account_number`, `bank_account_holder`)
- `platform_admins` — Admin-specific details (extends users)
- `vehicles` — Vehicle inventory with status and pricing
- `vehicle_price_history` — Historical pricing data
- `bookings` — Reservation records
- `booking_price_snapshot` — Frozen price and terms at time of booking
- `booking_status_log` — Audit trail for booking status changes
- `vehiclemaintenance` — Vehicle maintenance records with status lifecycle & soft-delete
- `vehicle_maintenance_log` — Audit trail for maintenance status changes
- `maintenance_part` — Parts and materials tracking per maintenance record
- `service_providers` — External service provider management
- `invoices` — Billing information
- `payments` — Payment transactions
- `payment_status_log` — Audit trail for payment status changes
- `audit_logs` — System audit trail (AOP-based)
- `addresses` — User address with geolocation (latitude/longitude)

### Key Relationships:
- One-to-Many: User → Vehicles (for owners)
- One-to-Many: Vehicle → Bookings, VehiclePriceHistory, VehicleMaintenance
- One-to-Many: Booking → BookingStatusLog
- One-to-One: Booking → Invoice
- One-to-Many: Invoice → Payments
- One-to-Many: VehicleMaintenance → VehicleMaintenanceLog, MaintenancePart

-----

## API Endpoints Summary

### Public Endpoints:
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/vehicles/search` - Vehicle search with filters

### Renter Endpoints:
- `GET /api/renter/bookings` - Booking history
- `POST /api/renter/bookings` - Create new booking
- `GET /api/renter/payments` - Payment history

### Owner Endpoints:
- `GET /api/owner/vehicles` - Manage vehicle fleet
- `POST /api/owner/vehicles` - Add new vehicle
- `GET /api/owner/bookings` - Booking requests
- `GET /owner/maintenance` - Maintenance dashboard with KPI and charts
- `GET /owner/maintenance/{id}` - Detailed maintenance tracking timeline
- `POST /owner/maintenance` - Create maintenance record
- `POST /owner/maintenance/{id}/status` - Update maintenance status
- `GET /owner/payouts` - Owner earnings and payout dashboard
- `GET /owner/ai-reports` - AI Data Assistant
- `GET /api/owner/reports/utilization` - Utilization reports
- `POST /owner/profile` - Update profile (includes bank info & ToyyibPay config)
- `POST /owner/profile/qr` - Upload payment QR code image
- `POST /owner/profile/image` - Upload profile photo

### Admin Endpoints:
- `GET /api/admin/users` - User management
- `GET /admin/maintenance` - Platform-wide maintenance oversight
- `GET /admin/maintenance/{id}` - Detailed maintenance tracking view
- `GET /api/admin/reports/platform` - Platform analytics
- `PATCH /api/admin/users/{id}/status` - User status management
- `GET /api/search` - Global platform search endpoint

-----

## Monitoring & Logging

### Application Metrics:
- Request/response times
- Error rates and types
- Database query performance
- Memory and CPU usage
- Active users and sessions

### Business Metrics:
- Booking conversion rates
- Vehicle utilization percentages
- Revenue by period
- Customer satisfaction scores
- Maintenance downtime

-----

## Development Guidelines

### Code Standards:
- Follow Spring Boot best practices
- Use meaningful variable and method names
- Implement comprehensive error handling
- Write unit tests for all business logic
- Use DTOs for API communication
- Follow RESTful API design principles

### Security Practices:
- Never log sensitive data
- Validate all user inputs
- Use parameterized queries
- Implement proper session management
- Regular dependency updates
- Security headers configuration

-----

## Production Implementation Checklist

### Pre-Launch:
- [ ] Database backup strategy in place
- [ ] SSL certificate configured
- [ ] Environment variables set
- [ ] Log rotation configured
- [ ] Monitoring alerts setup
- [ ] Load testing completed
- [ ] Security audit performed
- [ ] Disaster recovery plan documented

### Post-Launch:
- [ ] Performance monitoring active
- [ ] Error tracking implemented
- [ ] User feedback collection
- [ ] Regular health checks
- [ ] Backup verification
- [ ] Security patch management

---

*This document serves as the comprehensive development guide for FleetShare implementation. Regular updates should be made as the project evolves.*