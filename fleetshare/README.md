# 📋 FleetShare: Centralized Vehicle Rental Management System

**FleetShare** is a multi-tenant web platform designed to connect individual vehicle owners with renters. This document serves as the primary development checklist to track the implementation of modules using the **Spring Boot** ecosystem.

-----

## 🏗️ Project Architecture (Spring Boot)

The application follows a standard **Layered Architecture**:

1.  **Presentation Layer (`*.controller`)**: REST Controllers exposing API endpoints (or MVC Controllers serving Thymeleaf views).
2.  **Service Layer (`*.service`)**: Contains all business logic (e.g., `checkActiveBookings`, transaction management).
3.  **Data Access Layer (`*.repository`)**: Interfaces extending `JpaRepository` for database interactions.
4.  **Domain Layer (`*.entity`)**: JPA Entities mapped to the MySQL tables defined in `fleetsharedb.sql`.
5.  **Security Layer (`*.security`)**: Spring Security configurations for RBAC.

-----

## 🛠 Module 1: User Management & Security

**Objective:** Handle authentication, authorization, and profile management for all three actors via Spring Security.

### ✅ Checklist

- [ ] **[R1 / R9] User Registration**
    - [ ] Create `UserRegistrationDTO` to handle form inputs.
    - [ ] Implement `UserService.registerUser()` with password encryption.
    - [ ] Validation: Use Bean Validation (`@Valid`, `@Email`, `@NotBlank`).
- [ ] **[R1 / R9 / R18] Secure Login**
    - [ ] Implement `CustomUserDetailsService` loading users from the DB.
    - [ ] Configure `SecurityFilterChain` for URL protection.
- [ ] **[R22] Role-Based Access Control (RBAC)**
    - [ ] **Security:** Annotate Controllers with `@PreAuthorize("hasRole('RENTER')")`.
    - [ ] **Security:** Use `@PostAuthorize` to ensure Owners only view their own data.
- [ ] **Profile Management**
    - [ ] Service method `updateProfile(UserId, UpdateDTO)`.

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

- [ ] **[R2] Add Vehicle**
    - [ ] `VehicleController.addVehicle(@RequestBody VehicleDTO)` endpoint.
    - [ ] Handle photo uploads using `MultipartFile` and store paths in DB.
- [ ] **[R2] Manage Fleet Inventory**
    - [ ] Repository: `findByFleetOwnerId(Long ownerId)`.
    - [ ] **CRUD:** Service methods `updateVehicle` and `deleteVehicle`.
- [ ] **[R3] Availability Management**
    - [ ] Logic to check if vehicle status can be toggled (check dependencies).
- [ ] **[R7] Utilization Reports**
    - [ ] create a Custom Interface (Projection) for utilization stats to map JPQL results.

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

- [ ] **[R10] Vehicle Search**
    - [ ] Implement `VehicleSpecification` (Spring Data JPA Specifications) for dynamic filtering (date, price, location).
- [ ] **[R11] Vehicle Details**
    - [ ] DTO projection including `Vehicle`, current `Price`, and `Owner` info.
- [ ] **[R12] Booking Creation**
    - [ ] `BookingService.createBooking()` with `@Transactional`.
    - [ ] **Validation:** Custom logic to check date overlaps.
- [ ] **[R5] Booking Management**
    - [ ] Endpoints: `PATCH /api/bookings/{id}/status`.
    - [ ] Logic: Update `BookingStatusLog` automatically on status change.
- [ ] **[R15] Booking History**
    - [ ] `Pageable` endpoints for history lists (Pagination).

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

**Objective:** Track vehicle health and prevent bookings during maintenance.

### ✅ Checklist

- [ ] **[R4] Maintenance Logging**
    - [ ] CRUD Endpoints for Maintenance.
- [ ] **[R3] Auto-Availability Integration**
    - [ ] Service Logic: When Maintenance is `ACTIVE`, set Vehicle `status` = `MAINTENANCE`.
- [ ] **Maintenance History**
    - [ ] `List<MaintenanceDTO> getMaintenanceHistory(Long vehicleId)`.

### ⚙️ Implementation Details (Spring Boot)

* **Service Logic:**
    * In `MaintenanceService.createMaintenance()`, perform the overlap check similar to Booking Creation.
    * Use **Spring Events** (`ApplicationEventPublisher`) to decouple logic: Publish `MaintenanceCreatedEvent` -> Listener updates Vehicle Status.

-----

## 💳 Module 5: Payment & Financials

**Objective:** Manage transactions, proof of payment, and revenue reporting.

### ✅ Checklist

- [ ] **[R13] Payment Processing**
    - [ ] Endpoint to upload Proof (`MultipartFile`).
    - [ ] Service to link Payment to Invoice.
- [ ] **[R16] Payment History**
    - [ ] Repository: `findByInvoice_Renter_Id(Long renterId)`.
- [ ] **[R6] Revenue Reports**
    - [ ] JPQL Aggregation Query for Owner Revenue.

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

- [ ] **[R19] User Management**
    - [ ] `AdminUserController` with endpoints to `PATCH /users/{id}/active`.
- [ ] **[R20] Dispute Resolution**
    - [ ] Read-only view of `BookingStatusLog` and `PaymentStatusLog`.
- [ ] **[R21] Platform Reporting**
    - [ ] **Service:** `AdminReportService` gathering counts via `long userCount = userRepository.count();`.

### ⚙️ Implementation Details (Spring Boot)

* **Security:**
    * Annotate the entire `AdminController` with `@PreAuthorize("hasRole('PLATFORM_ADMIN')")`.
* **Audit:**
    * Consider **Spring Data Envers** for easy historical auditing of entities if the custom log tables become too complex to manage manually.

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
├── com.fleetshare/
│ ├── FleetShareApplication.java # Main entry point
│ │
│ ├── config/ # Configuration classes
│ │ ├── SecurityConfig.java
│ │ ├── DatabaseConfig.java
│ │ ├── WebConfig.java
│ │ └── OpenApiConfig.java # Swagger/SwaggerUI Config
│ │
│ ├── controller/ # Web layer
│ │ ├── web/ # HTML/MVC controllers (Thymeleaf)
│ │ │ ├── HomeController.java
│ │ │ ├── AuthWebController.java
│ │ │ ├── DashboardController.java # Routing for Owner/Renter dashboards
│ │ │ └── AdminWebController.java
│ │ │
│ │ ├── api/ # REST API controllers
│ │ │ ├── AuthApiController.java
│ │ │ ├── UserApiController.java
│ │ │ ├── VehicleApiController.java # Fleet Management
│ │ │ └── BookingApiController.java # Reservations
│ │ │
│ │ └── dto/ # Data Transfer Objects
│ │ ├── request/
│ │ │ ├── RegisterUserRequest.java
│ │ │ ├── LoginRequest.java
│ │ │ ├── CreateVehicleRequest.java
│ │ │ └── CreateBookingRequest.java
│ │ │
│ │ └── response/
│ │ ├── UserResponse.java
│ │ ├── VehicleResponse.java
│ │ ├── BookingSummaryResponse.java
│ │ ├── ApiResponse.java
│ │ └── ErrorResponse.java
│ │
│ ├── service/ # Business logic layer
│ │ ├── UserService.java
│ │ ├── VehicleService.java
│ │ ├── BookingService.java
│ │ ├── AuthService.java
│ │ ├── EmailService.java
│ │ │
│ │ └── impl/ # Service implementations
│ │ ├── UserServiceImpl.java
│ │ ├── VehicleServiceImpl.java
│ │ └── BookingServiceImpl.java
│ │
│ ├── repository/ # Data access layer
│ │ ├── UserRepository.java
│ │ ├── VehicleRepository.java
│ │ ├── BookingRepository.java
│ │ │
│ │ └── custom/ # Custom repository implementations
│ │ ├── BookingRepositoryCustom.java # For complex overlaps/stats
│ │ └── BookingRepositoryImpl.java
│ │
│ ├── entity/ # JPA entities
│ │ ├── User.java
│ │ ├── Vehicle.java
│ │ ├── Booking.java
│ │ ├── Invoice.java
│ │ ├── Payment.java
│ │ │
│ │ └── enums/ # Enums used in entities
│ │ ├── UserRole.java
│ │ ├── VehicleStatus.java
│ │ ├── BookingStatus.java
│ │ └── PaymentMethod.java
│ │
│ ├── security/ # Security related classes
│ │ ├── JwtUtil.java
│ │ ├── CustomUserDetailsService.java
│ │ ├── JwtAuthenticationFilter.java
│ │ └── SecurityUtils.java
│ │
│ ├── exception/ # Exception handling
│ │ ├── GlobalExceptionHandler.java
│ │ ├── ResourceNotFoundException.java
│ │ ├── BookingConflictException.java # Domain specific exception
│ │ │
│ │ └── handler/ # Exception handlers
│ │ ├── UserExceptionHandler.java
│ │ └── BookingExceptionHandler.java
│ │
│ ├── aspect/ # AOP aspects
│ │ ├── LoggingAspect.java
│ │ ├── PerformanceAspect.java
│ │ └── AuditLogAspect.java # For R23 (Audit Logs)
│ │
│ ├── util/ # Utility classes
│ │ ├── DateUtils.java
│ │ ├── ValidationUtils.java
│ │ └── FileUploadUtils.java # For vehicle/profile images
│ │
│ └── event/ # Application events
│ ├── UserRegistrationEvent.java
│ ├── BookingStatusEvent.java
│ │
│ └── listener/
│ ├── UserRegistrationListener.java
│ └── BookingStatusListener.java # e.g., Sends email on confirmation
│
src/main/resources/
├── application.properties # Main configuration
├── application-dev.properties # Development profile
├── application-prod.properties # Production profile
│
├── static/ # Static files (CSS, JS, images)
│ ├── css/
│ │ ├── style.css
│ │ └── admin.css
│ │
│ ├── js/
│ │ ├── app.js
│ │ └── dashboard.js
│ │
│ ├── images/
│ │ └── logo.png
│ │
│ └── uploads/ # File upload directory
│
├── templates/ # Thymeleaf/HTML templates
│ ├── fragments/ # Reusable template fragments
│ │ ├── header.html
│ │ ├── footer.html
│ │ └── nav.html
│ │
│ ├── layouts/ # Page layouts
│ │ ├── base-layout.html
│ │ ├── admin-layout.html
│ │ └── dashboard-layout.html
│ │
│ ├── home/ # Public pages
│ │ ├── index.html
│ │ └── about.html
│ │
│ ├── auth/
│ │ ├── login.html
│ │ └── register.html
│ │
│ ├── owner/ # Fleet Owner views
│ │ ├── dashboard.html
│ │ ├── my-vehicles.html
│ │ └── booking-requests.html
│ │
│ ├── renter/ # Renter views
│ │ ├── search.html
│ │ └── my-trips.html
│ │
│ ├── admin/ # Admin views
│ │ ├── dashboard.html
│ │ └── users.html
│ │
│ └── error/ # Error pages
│ ├── 404.html
│ ├── 500.html
│ └── access-denied.html
│
└── logback-spring.xml # Logging configuration

src/test/java/ # Test classes
├── com.fleetshare/
│ ├── controller/
│ │ ├── VehicleApiControllerTest.java
│ │ └── BookingApiControllerTest.java
│ │
│ ├── service/
│ │ ├── VehicleServiceTest.java
│ │ └── BookingServiceTest.java
│ │
│ └── integration/ # Integration tests
│ └── BookingFlowIntegrationTest.java
| ___________


## 📋 Additional Requirements

### R23: Audit Logging
- Implement comprehensive audit logging using Spring AOP
- Track user actions, system events, and security-related activities
- Store audit logs in a dedicated database table

### R24: Notification System
- Implement email notifications for booking confirmations, status updates
- Add in-app notifications for real-time updates
- Support for SMS notifications (optional)

### R25: Reporting & Analytics
- Generate utilization reports for fleet owners
- Platform-wide analytics for administrators
- Financial reporting and revenue tracking

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
- Set up file storage for vehicle images
- Configure CORS for frontend integration
- Set up monitoring and health checks

-----

## 🔄 Database Schema Overview

### Core Tables:
- `users` - Base user table with common attributes
- `renters` - Renter-specific details (extends users)
- `fleet_owners` - Owner-specific details (extends users)
- `platform_admins` - Admin-specific details (extends users)
- `vehicles` - Vehicle inventory with status and pricing
- `vehicle_price_history` - Historical pricing data
- `bookings` - Reservation records
- `booking_status_log` - Audit trail for booking status changes
- `maintenance_logs` - Vehicle maintenance records
- `invoices` - Billing information
- `payments` - Payment transactions
- `audit_logs` - System audit trail

### Key Relationships:
- One-to-Many: User → Vehicles (for owners)
- One-to-Many: Vehicle → Bookings
- One-to-Many: Booking → BookingStatusLog
- One-to-Many: Invoice → Payments

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
- `GET /api/owner/reports/utilization` - Utilization reports

### Admin Endpoints:
- `GET /api/admin/users` - User management
- `GET /api/admin/reports/platform` - Platform analytics
- `PATCH /api/admin/users/{id}/status` - User status management

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