# FleetShare: Centralized Vehicle Rental Management System

**FleetShare** is a multi-tenant web platform designed to connect individual vehicle owners with renters. This document serves as the primary technical specification and development reference for the modules implemented within the **Spring Boot** ecosystem.

---

## Project Architecture (Spring Boot)

The application follows a standard **Layered Architecture**, leveraging the full power of the Spring ecosystem.

### Core Framework & Configuration
* **Spring Boot**: Auto-configuration and dependency management.
* **Maven**: Build automation and dependency resolution.
* **Properties**: `application.properties` for environment-specific configurations (Dev/Prod).

### Implemented Architecture 
* **Domain Layer (Entities)**:
  * **Inheritance Strategy**: Uses `@Inheritance(strategy = InheritanceType.JOINED)` for the `User` hierarchy (`Renter`, `FleetOwner`, `PlatformAdmin`). This maps to a normalized database schema where shared attributes are in a base table and specific attributes in joined tables.
  * **JPA Annotations**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column` for ORM mapping.
* **Data Access Layer (Repositories)**:
  * **Spring Data JPA**: Interfaces extend `JpaRepository<T, ID>` to provide CRUD operations out-of-the-box.
  * **Query Methods**: Automatic query generation based on method names (e.g., `findByUsername`).
* **Security Layer**:
  * **Configuration**: `@EnableWebSecurity` and `SecurityFilterChain` bean to define access rules.
  * **Authentication**: `CustomUserDetailsService` implements `UserDetailsService` to load user data from the database.
  * **Encryption**: `BCryptPasswordEncoder` bean for secure password hashing.
  * **Context**: `SecurityContextHolder` to access the currently authenticated user.
* **Web Layer (Controllers)**:
  * **MVC Controllers**: `@Controller` for serving Thymeleaf templates (`.html`).
  * **Model**: Used to pass data from controllers to views.

### Advanced Architectural Implementations
* **Data Modeling**: Extensive use of relationships (`@OneToMany`, `@ManyToOne`, `@OneToOne`) and auditing (`@EnableJpaAuditing`, `@EntityListeners`).
* **Business Logic & Transaction Management**:
  * `@Transactional` ensures data consistency across complex operations like bookings.
  * `@EnableAsync` handles non-blocking tasks like sending email notifications via `JavaMailSender`.
* **Validation & Error Handling**: 
  * Integrated Bean Validation (`@Valid`, `@NotNull`) on DTOs.
  * Global exception handling via `GlobalExceptionHandler` (`@ControllerAdvice`).

---

## Core Modules & Features

### 1. User Management & Security
Handles authentication, authorization, and profile management for all platform actors.
* **User Registration & Login**: Implemented secure registration with password encryption and custom user details service.
* **Role-Based Access Control (RBAC)**: Secure endpoints using `@PreAuthorize("hasRole(...)")` and `@PostAuthorize` to isolate data by user roles.
* **Profile Management**: Capabilities for profile updates, including UI enhancements with light and dark mode toggles.

**Implementation Details:**
* Utilizes `InheritanceType.JOINED` for specific user types.
* Password encoding leverages `BCryptPasswordEncoder`.
* Strict service-level checks before account deletion.

### 2. Fleet Management (Owner)
Empowers Fleet Owners to manage their vehicle inventory and availability.
* **Vehicle Inventory**: Add, update, and manage vehicles with photo upload support (`MultipartFile`).
* **Availability & Reporting**: Logic to handle vehicle status toggles, track utilization, and provide owner-specific dashboard metrics.
* **Customer Management**: Interfaces for owners to manage and view their renter details.

**Implementation Details:**
* Custom JPQL queries for extracting utilization statistics.
* Pricing managed through a `@OneToMany` relationship between `Vehicle` and `VehiclePriceHistory`.

### 3. Booking & Reservation
Facilitates the search, booking, and approval processes.
* **Search & Filters**: Implemented `VehicleSpecification` for dynamic filtering (date, price, location).
* **Booking Lifecycle**: `@Transactional` booking creation with conflict detection to prevent overlapping reservations.
* **History & Snapshots**: Automated status tracking (`BookingStatusLog`), paginated history, and price snapshots to lock in rates at booking time.

**Implementation Details:**
* Overlapping bookings prevented using custom `@Query` validations.
* JPA Entity Listeners manage automated audit logging for status changes.

### 4. Maintenance Management
Tracks vehicle health, manages maintenance lifecycle, and automatically prevents bookings during maintenance.
* **Logging & Status**: Full CRUD capabilities for maintenance records with status lifecycles (`PENDING` → `IN_PROGRESS` → `COMPLETED`).
* **Auto-Availability**: Automatically sets vehicle status to `MAINTENANCE` when work is in progress.
* **Dashboards & Analytics**: KPI cards, historical timelines, and detailed maintenance statistics (`MaintenanceStatsDTO`).

**Implementation Details:**
* Unified `VehicleMaintenance` entity supporting scheduling, cost tracking, and soft deletes (`isDeleted`).
* Separate tables for logistics such as `MaintenancePart` and `ServiceProvider`.

### 5. Payment & Financials
Manages transactions, proofs of payment, and revenue distribution.
* **Payment Processing & Gateways**: Integrated ToyyibPay for real-time processing and custom callbacks. BYOK (Bring Your Own Key) capabilities for owners.
* **Dynamic Payment Methods**: Dynamically displays payment options (FPX, Card, Bank Transfer, QR, Cash) based on the owner's configuration.
* **Invoicing & Receipts**: Professional PDF invoice and receipt generation (`InvoiceService`, `ReceiptService`).

**Implementation Details:**
* State Machine logic enforcing correct billing status transitions.
* Platform fee calculation and payout tracking logic managed by `CommissionService`.

### 6. Administrator Dashboard
Provides global oversight and system management.
* **Platform Search & User Management**: Global cross-entity search and full user lifecycle management.
* **Dispute Resolution & Audit**: Read-only views into booking and payment status logs.
* **AI & Reporting**: Advanced report generation using custom LLM prompts via `AiAssistantService`, along with PDF export capabilities for analytics.

**Implementation Details:**
* Endpoint protection via strict `@PreAuthorize("hasRole('PLATFORM_ADMIN')")`.
* Complex analytical aggregation processed through `AdminReportService` and `ReportChartService`.

---

## Frontend UI & Design System

Provides a consistent, responsive, and robust user interface across all roles using native HTML/CSS and Thymeleaf.

* **Core Style Architecture**: `fleetshare-design-system.css` centralizes variables and UI components.
* **JavaScript Integrations**: Unified search UI (`search.js`), interactive status timelines, and base layout logic (`template.js`).
* **Layouts & Templating**: Modular Thymeleaf fragments for sidebars, headers, and modals, ensuring maintainability.
* **Visual Enhancements**: Lottie animations, standardized input sizing, and responsive adjustments across owner, renter, and admin portals.

---

## Tech Stack & Environment

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
* `lombok` (Boilerplate reduction)

---

## How to Run

1. **Clone the repository.**
2. **Database Setup:**
   * Create a MySQL database named `fleetshare`.
   * Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/fleetshare
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
3. **Build:**
   * Run `mvn clean install`
4. **Run:**
   * Run `mvn spring-boot:run`
5. **Access:**
   * API Documentation (Swagger/OpenAPI): `http://localhost:8080/swagger-ui.html`
   * Web App: `http://localhost:8080`

---

## Project Directory Structure
```
src/main/java/com/najmi/fleetshare/
├── FleetshareApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── GlobalExceptionHandler.java
│   ├── OwnerController.java
│   ├── PublicController.java
│   ├── RenterController.java
│   ├── RootController.java
│   ├── SearchController.java
│   └── ToyyibPayController.java
├── dto/
│   └── (Data Transfer Objects for validation & projection)
├── entity/
│   ├── Address.java
│   ├── Booking.java
│   ├── FleetOwner.java
│   ├── Invoice.java
│   ├── Payment.java
│   ├── PlatformAdmin.java
│   ├── Renter.java
│   ├── User.java
│   ├── Vehicle.java
│   ├── VehicleMaintenance.java
│   └── (Other operational and audit entities)
├── repository/
│   └── (Spring Data JPA Repositories for all entities)
├── security/
│   ├── CustomUserDetailsService.java
│   └── CustomUserDetails.java
├── service/
│   ├── AiAssistantService.java
│   ├── BookingService.java
│   ├── CommissionService.java
│   ├── InvoiceService.java
│   ├── MaintenanceService.java
│   ├── PaymentService.java
│   ├── ReceiptService.java
│   ├── ReportService.java
│   ├── ToyyibPayService.java
│   ├── UserManagementService.java
│   ├── VehicleManagementService.java
│   └── (Various other business logic services)
└── util/
    └── SessionHelper.java
```

---

## Security Considerations

* Implement proper input validation and sanitization.
* Use HTTPS in production environment.
* Regular security updates and dependency scanning.
* SQL injection prevention using parameterized queries.
* XSS protection through proper encoding.
* CSRF protection for state-changing operations.

---

## Testing Strategy

* Unit tests for service layer and utilities.
* Integration tests for repository layer.
* API testing for controller endpoints.
* Security testing for authentication and authorization.
* Performance testing for critical workflows.

---

## Deployment Notes

* Configure production database connection.
* Set up proper logging levels and rotation.
* Configure email service for notifications.
* Set up file storage for uploads (configure `app.upload.dir` in application.properties).
* Configure AI assistant API keys in `.env` (`ai.assistant.groq.api-key`, etc.).
* Configure CORS for frontend integration.
* Set up monitoring and health checks.