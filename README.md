# 🚗 FleetShare

A centralized vehicle rental management system built with Spring Boot that connects fleet owners with renters through a multi-tenant web platform.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## ✨ Features

### For Fleet Owners
- **Vehicle Management**: Add, edit, and manage vehicle inventory
- **Availability Control**: Set vehicle status (Available, Rented, Maintenance, Unavailable)
- **Rate Management**: Dynamic pricing with historical tracking
- **Booking Requests**: Review and manage booking requests
- **Utilization Reports**: Track vehicle performance and revenue
- **Maintenance Logging**: Track vehicle maintenance history

### For Renters
- **Vehicle Search**: Filter and search vehicles by location, category, price, and dates
- **Booking Management**: Create, view, and cancel bookings
- **Payment Processing**: Upload payment proofs and track payment status
- **Booking History**: View past and current bookings
- **Vehicle Details**: Access comprehensive vehicle information

### For Platform Administrators
- **User Management**: Manage all platform users (activate/deactivate accounts)
- **Global Oversight**: Platform-wide analytics and reporting
- **Dispute Resolution**: Access audit logs for bookings and payments
- **System Monitoring**: Track platform metrics and usage statistics

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.1-SNAPSHOT
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + BCrypt
- **AOP**: Spring AOP (AspectJ) for logging
- **PDF Generation**: OpenPDF

### Frontend
- **Template Engine**: Thymeleaf
- **UI Framework**: Custom responsive design
- **Layout Dialect**: Thymeleaf Layout Dialect

## 📋 Prerequisites

Before running this application, ensure you have:

- JDK 17 or higher
- Maven 3.8+
- MySQL 8.0+
- An IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd fleetshare
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE fleetshareplayground CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Import Database Schema

```bash
mysql -u root -p fleetshareplayground < db/fleetshare_dump.sql
```

### 4. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update database credentials
spring.datasource.url=jdbc:mysql://localhost:3307/fleetshareplayground
spring.datasource.username=root
spring.datasource.password=your_password

# Update upload directory if needed
app.upload.dir=C:/fleetshare-uploads
```

### 5. Build the Application

```bash
cd fleetshare
mvn clean install
```

## 🏃 Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

### Production Mode

```bash
java -jar target/fleetshare-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 📂 Project Structure

```
fleetshare/
├── src/main/java/com/najmi/fleetshare/
│   ├── FleetshareApplication.java      # Main entry point
│   ├── aspect/                         # AOP logging
│   ├── config/                         # Security & Web config
│   ├── controller/                     # MVC controllers
│   │   ├── AdminController.java
│   │   ├── AuthController.java
│   │   ├── OwnerController.java
│   │   └── RenterController.java
│   ├── dto/                            # Data Transfer Objects
│   ├── entity/                         # JPA entities
│   ├── exception/                      # Custom exceptions
│   ├── repository/                     # Data access layer
│   ├── security/                       # Security components
│   ├── service/                        # Business logic
│   └── util/                           # Utility classes
├── src/main/resources/
│   ├── templates/                      # Thymeleaf templates
│   ├── static/                         # Static assets (CSS, JS, images)
│   └── application.properties          # Configuration
├── src/test/                           # Test files
└── db/                                 # Database dumps
```

## 🗄️ Database Schema

### Core Entities

**User Management**
- `users` - Base user table
- `fleet_owners` - Owner-specific details
- `renters` - Renter-specific details
- `platform_admins` - Admin-specific details

**Vehicle Management**
- `vehicles` - Vehicle inventory
- `vehicle_price_history` - Historical pricing
- `vehicle_maintenance` - Maintenance records
- `vehicle_maintenance_log` - Maintenance audit logs

**Booking System**
- `bookings` - Reservation records
- `booking_status_log` - Booking status history
- `booking_price_snapshot` - Price at booking time

**Payment System**
- `invoices` - Billing information
- `payments` - Payment transactions
- `payment_status_log` - Payment status history

**Supporting**
- `addresses` - User addresses

## 🔐 Security

### Authentication & Authorization

- **Password Hashing**: BCrypt encryption
- **Role-Based Access Control**: Three user roles
  - `FLEET_OWNER`: Manage vehicles and bookings
  - `RENTER`: Browse and book vehicles
  - `PLATFORM_ADMIN`: Full system administration

### Security Features

- CSRF protection
- Input validation and sanitization
- Secure file upload handling
- SQL injection prevention (parameterized queries)
- XSS protection through proper encoding
- Secure password policies
- Path traversal protection
- Security headers configuration

## 🎯 User Roles & Permissions

### Fleet Owner
- Add/edit/delete vehicles
- Manage vehicle availability
- Set vehicle pricing
- Review and approve/reject bookings
- View utilization reports
- Track maintenance

### Renter
- Browse and search vehicles
- Create and manage bookings
- View booking history
- Upload payment proofs
- Track payment status

### Platform Admin
- Manage all users (activate/deactivate)
- View platform-wide reports
- Access audit logs
- Resolve disputes
- Monitor system metrics

## 📊 API Endpoints

### Public Endpoints
```
GET  /                    # Landing page
GET  /login               # Login page
POST /register            # User registration
POST /auth/login          # Authentication
```

### Owner Endpoints
```
GET  /owner/dashboard
GET  /owner/vehicles
POST /owner/vehicles/add
GET  /owner/bookings
PATCH /owner/bookings/{id}/status
GET  /owner/reports
```

### Renter Endpoints
```
GET  /renter/dashboard
GET  /vehicles/search
GET  /vehicles/{id}
POST /renter/bookings
GET  /renter/bookings
POST /renter/payments
```

### Admin Endpoints
```
GET  /admin/dashboard
GET  /admin/users
PATCH /admin/users/{id}/status
GET  /admin/reports
GET  /admin/disputes
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FleetshareApplicationTests

# Run tests with coverage
mvn clean test jacoco:report
```

## 📝 Environment Variables

Create `.env` file or set in `application.properties`:

```properties
# Database
DB_URL=jdbc:mysql://localhost:3307/fleetshareplayground
DB_USERNAME=root
DB_PASSWORD=your_password

# File Upload
UPLOAD_DIR=C:/fleetshare-uploads
MAX_FILE_SIZE=10MB
```

## 🚢 Deployment

### Docker Deployment

```bash
# Build Docker image
docker build -t fleetshare:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3307/fleetshareplayground \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  fleetshare:latest
```

### Production Checklist

- [ ] Configure production database
- [ ] Set up SSL/HTTPS
- [ ] Configure file storage
- [ ] Set up email service
- [ ] Enable logging and monitoring
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
- [ ] Review security settings
- [ ] Update CORS configuration
- [ ] Configure health checks

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Spring Boot best practices
- Use meaningful variable and method names
- Write unit tests for business logic
- Use DTOs for API communication
- Follow RESTful API design principles
- Add proper documentation comments

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Scopes: `auth`, `owner`, `renter`, `admin`, `booking`, `payment`, `vehicle`

## 🐛 Troubleshooting

### Common Issues

**Database Connection Error**
- Ensure MySQL is running on port 3307
- Check database credentials in `application.properties`
- Verify database exists and is accessible

**File Upload Issues**
- Check upload directory permissions
- Verify `app.upload.dir` exists and is writable
- Ensure file size doesn't exceed 10MB limit

**Login Issues**
- Check BCrypt password encoding
- Verify user account is active in database
- Review security configuration

**Static Resources Not Loading**
- Clear browser cache
- Check static resource path configuration
- Verify files exist in `src/main/resources/static/`

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Naim Najmi** - Initial development

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Thymeleaf team for the template engine
- Open source community

---

Made with ❤️ for vehicle rental management
