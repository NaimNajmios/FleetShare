# 📋 FleetShare: Centralized Vehicle Rental Management System

**FleetShare** is a multi-tenant web platform designed to connect individual vehicle owners with renters. This document serves as the primary development checklist to track the implementation of modules using the **Spring Boot** ecosystem.

-----

## 🏗️ Project Architecture (Spring Boot)

The application follows a standard **Layered Architecture**, leveraging the full power of the Spring ecosystem.

### 1. Core Framework & Configuration
*   **Spring Boot**: Auto-configuration and dependency management.
*   **Maven**: Build automation and dependency resolution.
*   **Properties**: `application.properties` for environment-specific configurations (Dev/Prod).

### 2. Implemented Architecture (Current State)
These elements are currently present in the codebase:

*   **Domain Layer (Entities)**:
    *   **Inheritance Strategy**: Uses `@Inheritance(strategy = InheritanceType.JOINED)` for the `User` hierarchy (`Renter`, `FleetOwner`, `PlatformAdmin`). This maps to a normalized database schema where shared attributes are in a base table and specific attributes in joined tables.
    *   **JPA Annotations**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column` for ORM mapping.
*   **Data Access Layer (Repositories)**:
    *   **Spring Data JPA**: Interfaces extend `JpaRepository<T, ID>` to provide CRUD operations out-of-the-box without boilerplate code.
    *   **Query Methods**: Automatic query generation based on method names (e.g., `findByUsername`).
*   **Security Layer**:
    *   **Configuration**: `@EnableWebSecurity` and `SecurityFilterChain` bean to define access rules.
    *   **Authentication**: `CustomUserDetailsService` implements `UserDetailsService` to load user data from the database.
    *   **Encryption**: `BCryptPasswordEncoder` bean for secure password hashing.
    *   **Context**: `SecurityContextHolder` to access the currently authenticated user.
*   **Web Layer (Controllers)**:
    *   **MVC Controllers**: `@Controller` for serving Thymeleaf templates (`.html`).
    *   **Model**: Used to pass data from controllers to views.

### 3. Planned / Future Architecture
These elements are planned for upcoming modules to enhance functionality and robustness:

*   **Advanced Data Modeling**:
    *   **Relationships**: `@OneToMany` (Owner -> Vehicles), `@ManyToOne` (Booking -> Vehicle), `@OneToOne` (Booking -> Payment) to model complex data associations.
    *   **Auditing**: `@EnableJpaAuditing` and `@EntityListeners(AuditingEntityListener.class)` to automatically track `createdAt` and `updatedAt` timestamps.
*   **Business Logic & Transaction Management**:
    *   **Transactions**: `@Transactional` on Service methods to ensure data consistency (ACID properties), especially for Booking creation and Payment processing.
    *   **Scheduling**: `@EnableScheduling` and `@Scheduled` (cron jobs) for automated tasks like expiring pending bookings or generating monthly reports.
    *   **Async Processing**: `@EnableAsync` and `@Async` for non-blocking operations like sending email notifications (`JavaMailSender`) to improve response times.
*   **Validation & Error Handling**:
    *   **Bean Validation**: `@Valid`, `@NotNull`, `@Email`, `@Size` on DTOs to ensure data integrity before it reaches the service layer.
    *   **Global Exception Handling**: `@ControllerAdvice` and `@ExceptionHandler` to provide consistent error responses (JSON/HTML) across the application.
    *   **Custom Exceptions**: `@ResponseStatus` on custom exception classes (e.g., `ResourceNotFoundException`) to control HTTP status codes.
*   **API & Integration**:
    *   **REST API**: `@RestController` (combines `@Controller` and `@ResponseBody`) for exposing JSON endpoints for mobile apps or SPA frontends.
    *   **External Calls**: `RestTemplate` or `WebClient` for integrating with third-party services (e.g., Payment Gateways, SMS providers).
*   **Testing Strategy**:
    *   **Integration Tests**: `@SpringBootTest` to load the full application context.
    *   **Slice Tests**: `@WebMvcTest` for controllers and `@DataJpaTest` for repositories to test layers in isolation.

-----

## 🛠 Module 1: User Management & Security

**Objective:** Handle authentication, authorization, and profile management for all three actors via Spring Security.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

* **Entities:**
    * `@Entity @Table(name="users") public class User {...}`
    * Inheritance Strategy: `InheritanceType.JOINED` is recommended since you have specific tables (`Renters`, `FleetOwners`, `PlatformAdmins`) linked by `user_id`.
* **Security:**
    * **Bean:** `PasswordEncoder` using `BCryptPasswordEncoder`.
    * **Logic:** `checkActiveBookings(userID)` should be a private method in `UserService` called within `@Transactional deleteUser()`.

-----

## 🚗 Module 2: Fleet Management (Owner)

**Objective:** Allow Fleet Owners to manage their vehicle inventory and availability.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

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

## 📅 Module 3: Booking & Reservation

**Objective:** Facilitate the search, booking, and approval process.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

* **Repository:**
    * **Conflict Detection Query:**
      ```java
      @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.vehicle.id = :vehicleId " +
             "AND b.status IN ('PENDING', 'CONFIRMED') " +
             "AND ((b.startDate BETWEEN :start AND :end) OR (b.endDate BETWEEN :start AND :end))")
      boolean existsOverlappingBooking(Long vehicleId, LocalDateTime start, LocalDateTime end);
      ```
* **Audit Logic:**
    * Use **JPA Entity Listeners** (`@PreUpdate`, `@PostPersist`) or explicit calls in the Service layer to insert records into `BookingStatusLog` whenever `Booking` is modified.

-----

## 🔧 Module 4: Maintenance Management

**Objective:** Track vehicle health, manage maintenance lifecycle, and prevent bookings during maintenance.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

* **Entity:** `VehicleMaintenance` — unified entity with fields for scheduling, cost tracking (`estimatedCost`, `finalCost`), status enum, and soft-delete support (`isDeleted`, `deletedAt`).
* **Repository:** `VehicleMaintenanceRepository` with queries: `findByIsDeletedFalse`, `findByFleetOwnerIdAndIsDeletedFalse`, `findByVehicleIdAndIsDeletedFalse`.
* **Removed:** Legacy `MaintenanceSchedule` entity, `MaintenanceScheduleDTO`, `MaintenanceScheduleRepository`, `MaintenanceScheduleService`, `maintenance-calendar.html`, `maintenance-schedules.html` — consolidated into the unified `VehicleMaintenance` system.

-----

## 💳 Module 5: Payment & Financials

**Objective:** Manage transactions, proof of payment, and revenue reporting.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

* **Workflow Implementation:**
    * Use a **State Machine** pattern (or simple Service checks) to ensure transitions:
      `PENDING_VERIFICATION` -> `VERIFIED` -> triggers `Invoice.PAID` -> triggers `Booking.CONFIRMED`.
* **Entities:**
    * `@Entity public class Invoice {...}`
    * `@Entity public class Payment {...}`
    * Relationship: `@OneToMany(mappedBy="invoice")` in Invoice.

-----

## 🛡 Module 6: Administrator Dashboard

**Objective:** Global oversight and system management.

### ✅ Checklist

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

### ⚙️ Implementation Details (Spring Boot)

* **Security:**
    * Annotate the entire `AdminController` with `@PreAuthorize("hasRole('PLATFORM_ADMIN')")`.
* **Audit:**
    * Consider **Spring Data Envers** for easy historical auditing of entities if the custom log tables become too complex to manage manually.

-----

## 🎨 Frontend UI & Design System

**Objective:** Provide a consistent, responsive, and robust user interface across all roles utilizing native HTML/CSS and Thymeleaf.

### ✅ Checklist

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

## 💻 Tech Stack & Environment

**Development Environment:**

* **JDK:** Java 17 or 21 (LTS)
* **Framework:** Spring Boot 3.2+
* **Build Tool:** Maven 3.8+
* **Database:** MySQL 8.0

**Core Dependencies:**

* `spring-boot-starter-web` (REST / MVC)
* `spring-boot-starter-data-jpa` (Database Access)
* `spring-boot-starter-security` (Auth & RBAC)
* `spring-boot-starter-validation` (Form Validation)
* `spring-boot-starter-mail` (Email / SMTP)
* `spring-dotenv` (Environment variable management via `.env`)
* `mysql-connector-j` (Driver)
* `lombok` (Optional, for reducing boilerplate code)

-----

## 🚀 How to Run

1.  **Clone the repository.**
2.  **Database Setup:**
    * Create a MySQL database named `fleetshare`.
    * Update `src/main/resources/application.properties`:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/fleetshare
      spring.datasource.username=root
      spring.datasource.password=yourpassword
      spring.jpa.hibernate.ddl-auto=update
      ```
3.  **Build:**
    * Run `mvn clean install`
4.  **Run:**
    * Run `mvn spring-boot:run`
5.  **Access:**
    * API Documentation (Swagger/OpenAPI): `http://localhost:8080/swagger-ui.html` (if dependency added).
    * App: `http://localhost:8080`

-----

## 📂 Project Directory Structure
src/main/java/
├── com.najmi.fleetshare/
│   ├── FleetshareApplication.java      # Main entry point
│   ├── TestPasswordEncoder.java        # Utility for generating BCrypt passwords
│   │
│   ├── config/                         # Configuration classes
│   │   └── SecurityConfig.java         # Spring Security configuration
│   │
│   ├── controller/                     # Web layer (MVC Controllers)
│   │   ├── AdminController.java        # Admin dashboard routes
│   │   ├── AuthController.java         # Authentication routes
│   │   ├── OwnerController.java        # Fleet Owner routes
│   │   └── RenterController.java       # Renter routes
│   │
│   ├── dto/                            # Data Transfer Objects
│   │   ├── AdminDetails.java
│   │   ├── OwnerDetails.java
│   │   ├── RenterDetails.java
│   │   └── SessionUser.java            # Session-scoped user data
│   │
│   ├── entity/                         # JPA Entities
│   │   ├── User.java                   # Base user entity
│   │   ├── FleetOwner.java             # Owner specific attributes
│   │   ├── Renter.java                 # Renter specific attributes
│   │   ├── PlatformAdmin.java          # Admin specific attributes
│   │   └── UserRole.java               # Role enumeration
│   │
│   ├── repository/                     # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── FleetOwnerRepository.java
│   │   ├── RenterRepository.java
│   │   └── PlatformAdminRepository.java
│   │
│   ├── security/                       # Security Components
│   │   ├── CustomUserDetailsService.java
│   │   ├── CustomUserDetails.java
│   │   └── CustomAuthenticationSuccessHandler.java
│   │
│   ├── service/                        # Business Logic
│   │   ├── UserSessionService.java     # Session management service
│   │   ├── AiAssistantService.java     # Multi-provider LLM AI Data Assistant
│   │   ├── MaintenanceService.java     # Maintenance CRUD, validation, stats
│   │   ├── BookingService.java         # Booking lifecycle & bulk fetch
│   │   ├── VehicleManagementService.java # Vehicle fleet operations
│   │   ├── PaymentService.java         # Payment processing & history
│   │   ├── ToyyibPayService.java       # Payment gateway integration
│   │   ├── CommissionService.java      # Platform fee & payout logic
│   │   ├── InvoiceService.java         # Billing & Invoice management
│   │   ├── ReceiptService.java         # Payment receipt generation
│   │   ├── EmailService.java           # Async email with HTML templates
│   │   ├── ReportService.java          # Modular reporting & PDF export
│   │   ├── QueryClassifier.java        # AI query categorization component
│   │   ├── PromptTemplateService.java  # Dynamic LLM prompt management
│   │   └── PredictiveMaintenanceService.java # Predictive analytics
│   │
│   └── util/                           # Utilities
│       └── SessionHelper.java
│
src/main/resources/
├── application.properties              # Main configuration
├── static/                             # Static assets (CSS, JS, Images)
└── templates/                          # Thymeleaf templates
    ├── admin/                          # Admin views
    ├── auth/                           # Login/Register views
    ├── email/                          # HTML email templates
    ├── fragments/                      # Reusable UI fragments
    ├── layouts/                        # Base layouts
    ├── owner/                          # Owner views
    ├── pages/                          # Miscellaneous pages
    ├── pdf/                            # HTML templates for PDF generation (receipts, reports)
    ├── public/                         # Public access views
    ├── renter/                         # Renter views
    └── index.html                      # Landing page

src/test/java/
└── com.najmi.fleetshare/
    └── FleetshareApplicationTests.java # Context load tests

-----

## 📋 Additional Requirements

### R23: Audit Logging
- Implement comprehensive audit logging using Spring AOP
- Track user actions, system events, and security-related activities
- Store audit logs in a dedicated database table

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

## 🔐 Security Considerations

- Implement proper input validation and sanitization
- Use HTTPS in production environment
- Regular security updates and dependency scanning
- SQL injection prevention using parameterized queries
- XSS protection through proper encoding
- CSRF protection for state-changing operations

-----

## 🧪 Testing Strategy

- Unit tests for service layer and utilities
- Integration tests for repository layer
- API testing for controller endpoints
- Security testing for authentication and authorization
- Performance testing for critical workflows

-----

## 📝 Deployment Notes

- Configure production database connection
- Set up proper logging levels and rotation
- Configure email service for notifications
- Set up file storage for uploads (configure `app.upload.dir` in application.properties):
  - `app.upload.profile-images` — User profile photos
  - `app.upload.payment-proofs` — Payment receipt uploads
  - `app.upload.qr-codes` — Owner payment QR code images
- Configure AI assistant API keys in `.env` (`ai.assistant.groq.api-key`, `ai.assistant.cerebras.api-key`, `ai.assistant.openrouter.api-key`)
- Configure CORS for frontend integration
- Set up monitoring and health checks

-----

## 🔄 Database Schema Overview

### Core Tables:
- `users` - Base user table with common attributes
- `renters` - Renter-specific details (extends users)
- `fleet_owners` - Owner-specific details (extends users), includes payment config (`toyyibpay_secret_key`, `toyyibpay_category_code`, `payment_qr_url`, `bank_name`, `bank_account_number`, `bank_account_holder`)
- `platform_admins` - Admin-specific details (extends users)
- `vehicles` - Vehicle inventory with status and pricing
- `vehicle_price_history` - Historical pricing data
- `bookings` - Reservation records
- `booking_status_log` - Audit trail for booking status changes
- `vehiclemaintenance` - Vehicle maintenance records with status lifecycle & soft-delete
- `vehicle_maintenance_log` - Audit trail for maintenance status changes
- `invoices` - Billing information
- `payments` - Payment transactions
- `audit_logs` - System audit trail
- `addresses` - User address with geolocation (latitude/longitude)

### Key Relationships:
- One-to-Many: User → Vehicles (for owners)
- One-to-Many: Vehicle → Bookings
- One-to-Many: Booking → BookingStatusLog
- One-to-Many: Invoice → Payments
- One-to-Many: VehicleMaintenance → VehicleMaintenanceLog

-----

## 🎯 API Endpoints Summary

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

## 📊 Monitoring & Logging

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

## 🔧 Development Guidelines

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

## 🚀 Production Checklist

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