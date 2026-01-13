# Capline - Revenue Cycle Management System

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Key Components](#key-components)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Deployment](#deployment)
- [Development Guidelines](#development-guidelines)
- [Troubleshooting](#troubleshooting)

---

## Overview

**Capline** is a comprehensive Revenue Cycle Management (RCM) system designed for dental practice management. The system facilitates claim processing, billing operations, treatment plan management, and data synchronization between EagleSoft (dental practice management software) and cloud-based systems.

### Key Features

- **Claim Management**: Fetch, process, assign, and track dental insurance claims
- **Treatment Plan Processing**: Handle treatment plans and generate IVF (Insurance Verification Forms)
- **Data Replication**: Synchronize data between EagleSoft database and cloud storage
- **User Management**: Role-based access control with multiple teams (Billing, Internal Audit, Quality, etc.)
- **Office & Client Management**: Manage multiple dental offices and clients
- **Reporting & Analytics**: Generate reports, track pendency, and monitor claim status
- **PDF Generation**: Generate IVF forms and other documents in PDF format
- **Google Sheets Integration**: Import claims and data from Google Sheets
- **Web Scraping**: Automated data extraction from insurance portals

---

## Architecture

The system follows a **microservices-oriented architecture** with three main backend services and two frontend applications:

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend Applications                    │
├──────────────────────────┬──────────────────────────────────┤
│   RCM Client App         │   Rule Engine Client App         │
│   (Angular 15)           │   (Angular 6)                    │
└──────────┬───────────────┴──────────────┬───────────────────┘
           │                              │
           │                              │
┌──────────▼───────────────┐  ┌───────────▼──────────────────┐
│   RCM Backend Service    │  │  Rule Engine Backend Service │
│   (Spring Boot 2.7.3)    │  │  (Spring Boot 2.0.2)         │
└──────────┬───────────────┘  └───────────┬──────────────────┘
           │                              │
           │                              │
           └───────────┬──────────────────┘
                       │
           ┌───────────▼───────────────────┐
           │  Data Replication Service     │
           │  (Spring Boot 2.5.2)          │
           └───────────┬───────────────────┘
                       │
        ┌──────────────┴─────────────┐
        │                            │
┌───────▼────────┐          ┌────────▼────────┐
│  MySQL (RCM)   │          │  EagleSoft DB   │
│  Cloud DB      │          │  (Sybase)       │
└────────────────┘          └─────────────────┘
```

---

## Technology Stack

### Backend

- **Java 8**
- **Spring Boot** (2.0.2 - 2.7.3)
- **Spring Security** (JWT-based authentication)
- **Spring Data JPA** / **Hibernate**
- **MySQL** (Primary database)
- **Sybase** (EagleSoft database connection)
- **Maven** (Build tool)

### Frontend

- **Angular** (6.0.0 and 15.2.0)
- **TypeScript**
- **RxJS**
- **Angular Router**
- **ng2-pdf-viewer** (PDF viewing)
- **ng2-datepicker** (Date picker)
- **ngx-csv** (CSV export)

### Additional Libraries

- **iText PDF** (PDF generation)
- **Selenium** / **HtmlUnit** (Web scraping)
- **Google APIs** (Drive & Sheets integration)
- **Jackson** (JSON processing)
- **Swagger** (API documentation)
- **Lombok** (Code generation)

---

## Project Structure

```
capline/
├── rule_engine/
│   ├── rcm/                          # RCM Backend Service
│   │   ├── src/main/java/           # Java source code
│   │   │   └── com/tricon/rcm/
│   │   │       ├── api/controller/   # REST controllers
│   │   │       ├── db/entity/       # JPA entities
│   │   │       ├── service/impl/    # Business logic
│   │   │       ├── security/        # Security configuration
│   │   │       └── util/           # Utilities
│   │   ├── src/main/resources/     # Configuration files
│   │   └── pom.xml                  # Maven dependencies
│   │
│   ├── rcm-client-app/              # RCM Frontend (Angular 15)
│   │   ├── src/app/                 # Angular components
│   │   │   ├── claims/              # Claim management
│   │   │   ├── billing-claims/      # Billing operations
│   │   │   ├── production/         # Production dashboard
│   │   │   ├── search-claims/      # Claim search
│   │   │   ├── login/              # Authentication
│   │   │   └── service/            # Angular services
│   │   ├── package.json            # Node dependencies
│   │   └── angular.json            # Angular configuration
│   │
│   ├── ruleengine/                  # Rule Engine Backend
│   │   ├── src/main/java/          # Java source code
│   │   │   └── com/tricon/ruleengine/
│   │   │       ├── api/controller/ # REST endpoints
│   │   │       ├── service/       # Business services
│   │   │       ├── dao/          # Data access objects
│   │   │       └── pdf/         # PDF generation
│   │   └── pom.xml
│   │
│   ├── ruleengine-client-app/      # Rule Engine Frontend (Angular 6)
│   │   └── src/app/               # Angular components
│   │
│   └── es_data_replication/        # Data Replication Service
│       └── esdatareplication/
│           ├── src/main/java/     # Replication logic
│           └── pom.xml
│
├── database/                        # Database scripts
├── certifcates/                    # SSL certificates
├── ivf Form/                       # IVF form templates (XSLT)
└── production_issues/              # Production notes
```

---

## Key Components

### 1. RCM Service (`rule_engine/rcm`)

**Purpose**: Main backend service for claim management and business operations.

**Key Features**:

- User authentication and authorization (JWT)
- Claim CRUD operations
- Claim assignment and workflow management
- Office and client management
- Team-based access control
- Reporting and analytics
- Email notifications

**Main Controllers**:

- `RcmController`: Claim operations
- `UserController`: User management
- `AdminController`: Administrative functions
- `MasterController`: Master data (offices, teams, roles)

### 2. Rule Engine Service (`rule_engine/ruleengine`)

**Purpose**: Business rule validation, PDF generation, and data scraping.

**Key Features**:

- Treatment plan validation
- IVF form generation (PDF)
- Web scraping for insurance portals
- Google Sheets integration
- Treatment plan processing
- User input question management

**Main Controllers**:

- `RcmController`: Integration with RCM service
- `ScrappingController`: Web scraping operations
- `ReportController`: Report generation
- `RuleEngineValidationController`: Rule validation

### 3. Data Replication Service (`rule_engine/es_data_replication`)

**Purpose**: Synchronize data between EagleSoft (local) and cloud database.

**Key Features**:

- Scheduled data replication (cron-based)
- Pull data from EagleSoft Sybase database
- Push data to cloud MySQL database
- Handle multiple tables (Patients, Providers, Treatment Plans, etc.)
- Data transformation and validation

**Replicated Tables**:

- Patients
- Providers
- Employers
- Treatment Plans
- Treatment Plan Items
- Transactions
- Appointments
- Planned Services

### 4. RCM Client App (`rule_engine/rcm-client-app`)

**Purpose**: Modern Angular 15 frontend for claim management.

**Key Modules**:

- **Login**: User authentication
- **Claims**: Claim viewing and editing
- **Billing Claims**: Billing operations
- **Production**: Production dashboard
- **Search Claims**: Advanced claim search
- **List of Claims**: Claim listing with filters
- **All Pendency**: Pending claims tracking
- **Claim Assignment**: Assign claims to users/teams
- **Manage Office**: Office management
- **Manage Client**: Client management
- **User Settings**: User profile management
- **Reconciliation**: Financial reconciliation

### 5. Rule Engine Client App (`rule_engine/ruleengine-client-app`)

**Purpose**: Angular 6 frontend for rule engine operations (legacy).

---

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 8**

   ```bash
   java -version  # Should show 1.8.x
   ```

2. **Maven 3.6+**

   ```bash
   mvn -version
   ```

3. **Node.js and npm**

   - For RCM Client App: Node.js 14.17.0+ (use nvm)
   - For Rule Engine Client App: Node.js 8.x+

   ```bash
   nvm install 14.17.0
   nvm use 14.17.0
   ```

4. **MySQL 5.7+** or **MySQL 8.0+**

   ```bash
   mysql --version
   ```

5. **EagleSoft Database Access** (for data replication)

   - Sybase database connection
   - Configuration file at `c:/es/config.properties`

6. **Application Server** (for production)
   - Apache Tomcat 8.5+ or 9.x

### Required Accounts & Services

- **Google Cloud Platform**: For Google Drive and Sheets API access
- **Email Server**: SMTP configuration for email notifications
- **SSL Certificates**: For HTTPS (stored in `certifcates/`)

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd capline
```

### 2. Database Setup

#### Create MySQL Databases

```sql
-- Create RCM database
CREATE DATABASE rcm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create Rule Engine database
CREATE DATABASE ruleengine_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create Replication database
CREATE DATABASE replication_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Run Database Scripts

Execute SQL scripts from `database/` directory and any initialization scripts in the respective modules.

### 3. Backend Setup

#### RCM Service

```bash
cd rule_engine/rcm
mvn clean install
```

#### Rule Engine Service

```bash
cd rule_engine/ruleengine
mvn clean initialize package
```

#### Data Replication Service

```bash
cd rule_engine/es_data_replication/esdatareplication
mvn clean install
```

### 4. Frontend Setup

#### RCM Client App

```bash
cd rule_engine/rcm-client-app
npm install
```

#### Rule Engine Client App

```bash
cd rule_engine/ruleengine-client-app
npm install
```

### 5. Build Frontend Applications

#### RCM Client App

```bash
cd rule_engine/rcm-client-app
ng build --configuration production
```

#### Rule Engine Client App

```bash
cd rule_engine/ruleengine-client-app
ng build --prod
```

---

## Configuration

### Application Properties

Each service has its own `application.properties` file. Create environment-specific files:

#### RCM Service (`rule_engine/rcm/src/main/resources/`)

**application-dev.properties**:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/rcm_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Google API Configuration
google.api.credentials.path=/path/to/credentials.json
google.drive.folder.id=your_folder_id

# File Storage
file.upload.dir=/opt/project/tricon/files
```

**application-prod.properties**:

```properties
spring.profiles.active=prod
# Override with production values
```

#### Rule Engine Service

Similar configuration structure in `rule_engine/ruleengine/src/main/resources/`

#### Data Replication Service

**application.properties**:

```properties
# Scheduler Configuration
scheduler.startcron=0 0 2 * * ?  # Run daily at 2 AM

# EagleSoft Database (configured via c:/es/config.properties)
# Local ES Database
es.db.url=jdbc:sybase:Tds:localhost:port
es.db.username=es_user
es.db.password=es_password

# Cloud Database
spring.datasource.url=jdbc:mysql://cloud-host:3306/replication_db
spring.datasource.username=cloud_user
spring.datasource.password=cloud_password
```

### EagleSoft Configuration

Create `c:/es/config.properties`:

```properties
port=5000
dbuser=es_username
dbpassword=es_password
```

### Google API Setup

1. Create a Google Cloud Project
2. Enable Google Drive API and Google Sheets API
3. Create OAuth 2.0 credentials
4. Download credentials JSON file
5. Configure path in `application.properties`

### SSL Certificates

Place SSL certificates in:

- `certifcates/server/` - Server certificates
- `certifcates/client/` - Client certificates

---

## Usage

### Running in Development Mode

#### Backend Services

**RCM Service**:

```bash
cd rule_engine/rcm
mvn spring-boot:run
# Or
java -jar target/rcm.war
```

**Rule Engine Service**:

```bash
cd rule_engine/ruleengine
mvn spring-boot:run
```

**Data Replication Service**:

```bash
cd rule_engine/es_data_replication/esdatareplication
java -jar target/esdatareplication-0.0.1-SNAPSHOT.jar
```

#### Frontend Applications

**RCM Client App**:

```bash
cd rule_engine/rcm-client-app
ng serve --proxy-config proxy.conf.json
# Access at http://localhost:4200
```

**Rule Engine Client App**:

```bash
cd rule_engine/ruleengine-client-app
ng serve --proxy-config proxy.conf.json
```

### Running in Production

1. **Build WAR files**:

   ```bash
   cd rule_engine/rcm
   mvn clean package
   # WAR file: target/rcm.war
   ```

2. **Deploy to Tomcat**:

   ```bash
   cp target/rcm.war /opt/tomcat/webapps/
   ```

3. **Build and deploy frontend**:
   ```bash
   cd rule_engine/rcm-client-app
   ng build --configuration production
   # Copy dist/rcm-client-app/* to webapps/rcm/
   ```

### Default Users

- **Admin**: `admin_admin` / `R&D00`
- **Test User**: `test_test_cl` / `12345678`
- **Dev User**: `dev` / `12345678`

---

## API Documentation

### Authentication

All API endpoints (except login) require JWT authentication.

**Login Endpoint**:

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

**Using JWT Token**:

```
Authorization: Bearer <token>
```

### Key API Endpoints

#### Claim Management

- `POST /api/fetch-claims-from-source` - Fetch claims from EagleSoft or Google Sheets
- `GET /api/claims/{uuid}` - Get claim details
- `PUT /api/claims/{uuid}` - Update claim
- `POST /api/claims/assign` - Assign claim to user/team
- `GET /api/list-of-claims` - List claims with filters
- `GET /api/all-pendency` - Get pending claims
- `POST /api/claims/archive` - Archive claim
- `POST /api/claims/unarchive` - Unarchive claim

#### User Management

- `POST /register` - Register new user (Admin only)
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile
- `POST /api/user/reset-password` - Reset password

#### Master Data

- `GET /master/getoffices` - Get all offices
- `GET /master/getteams` - Get all teams
- `GET /master/getroles` - Get all roles

#### Production & Reporting

- `GET /api/production/dashboard` - Production dashboard
- `GET /api/reports/pendency` - Pendency report
- `GET /api/reconciliation` - Reconciliation data

### Swagger Documentation

Access Swagger UI at:

- RCM Service: `http://localhost:8080/swagger-ui.html`
- Rule Engine Service: `http://localhost:8081/swagger-ui.html`

---

## Database Schema

### Key Entities

#### RCM Database

**Core Entities**:

- `RcmUser` - User accounts
- `RcmCompany` - Company/Client information
- `RcmOffice` - Dental office locations
- `RcmTeam` - Teams (Billing, Quality, etc.)
- `RcmRole` - User roles
- `RcmClaims` - Insurance claims
- `RcmClaimDetail` - Claim line items
- `RcmClaimAssignment` - Claim assignments
- `RcmClaimSection` - Claim workflow sections
- `RcmInsurance` - Insurance company information
- `RcmTreatmentPlan` - Treatment plans

**Workflow Entities**:

- `RcmClaimNotes` - Claim notes and comments
- `RcmClaimAttachment` - File attachments
- `RcmClaimLog` - Audit logs
- `RcmRebillingSection` - Rebilling information
- `RcmPatientStatementSection` - Patient statement data

### Database Relationships

```
RcmCompany (1) ──< (N) RcmOffice
RcmOffice (1) ──< (N) RcmClaims
RcmUser (N) ──< (N) RcmTeam (Many-to-Many)
RcmClaims (1) ──< (N) RcmClaimDetail
RcmClaims (1) ──< (N) RcmClaimAssignment
RcmClaims (1) ──< (N) RcmClaimSection
```

---

## Deployment

### Production Checklist

1. **Environment Configuration**:

   - Set `spring.profiles.active=prod` in `application.properties`
   - Update database URLs to production
   - Configure production email server
   - Set up SSL certificates

2. **Build Process**:

   ```bash
   # Build backend
   mvn clean package -DskipTests

   # Build frontend
   cd rule_engine/rcm-client-app
   ng build --configuration production
   ```

3. **File Permissions**:

   ```bash
   sudo chmod -R 777 /opt/project/tricon/files
   ```

4. **Tomcat Configuration**:

   - Deploy WAR files to `webapps/`
   - Configure `server.xml` for HTTPS
   - Set JVM memory: `-Xmx2048m -Xms1024m`

5. **Database Migration**:

   - Run database migration scripts
   - Verify all tables are created
   - Set up database backups

6. **Monitoring**:
   - Set up application logs
   - Configure health checks
   - Monitor scheduled tasks

### Production Issues Notes

Refer to `production_issues/issues.txt` for known issues and solutions:

- Ensure only one version of `commons-lang-2.6.jar` in WAR
- Verify file permissions on `/opt/project/tricon/files`
- Check for duplicate dependencies

---

## Development Guidelines

### Code Structure

- **Controllers**: Handle HTTP requests/responses
- **Services**: Business logic implementation
- **Repositories**: Data access layer (JPA)
- **DTOs**: Data transfer objects for API
- **Entities**: JPA entity classes
- **Utils**: Utility classes and helpers

### Naming Conventions

- **Java Classes**: PascalCase (e.g., `ClaimServiceImpl`)
- **Methods**: camelCase (e.g., `getClaimById`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Angular Components**: PascalCase (e.g., `ClaimComponent`)
- **Angular Services**: camelCase with suffix `Service` (e.g., `claimService`)

### Adding New Features

1. **Backend**:

   - Create entity class in `db/entity/`
   - Create repository interface extending `JpaRepository`
   - Implement service in `service/impl/`
   - Create controller in `api/controller/`
   - Add DTOs in `dto/` package

2. **Frontend**:
   - Generate component: `ng generate component feature-name`
   - Create service: `ng generate service feature-name`
   - Add routing in `app-routing.module.ts`
   - Implement permission guards if needed

### Testing

```bash
# Run backend tests
mvn test

# Run frontend tests
cd rule_engine/rcm-client-app
ng test
```

---

## Troubleshooting

### Common Issues

1. **Database Connection Errors**:

   - Verify database is running
   - Check connection URL and credentials
   - Ensure database exists

2. **JWT Token Expired**:

   - Token expires after 24 hours (default)
   - User needs to login again

3. **File Upload Failures**:

   - Check file permissions on upload directory
   - Verify disk space
   - Check file size limits

4. **EagleSoft Connection Issues**:

   - Verify `c:/es/config.properties` exists
   - Check Sybase database is accessible
   - Verify network connectivity

5. **Frontend Build Errors**:

   - Clear `node_modules` and reinstall: `rm -rf node_modules && npm install`
   - Check Node.js version compatibility
   - Verify Angular CLI version

6. **CORS Issues**:
   - Ensure `@CrossOrigin` annotation on controllers
   - Check proxy configuration in `proxy.conf.json`

### Logs

- **Backend Logs**: Check `logs/` directory or application server logs
- **Frontend Logs**: Browser console (F12)
- **Replication Logs**: Check scheduled task logs in data replication service

---

## Additional Resources

### IVF Forms

IVF (Insurance Verification Forms) templates are located in `ivf Form/`:

- XSLT templates for PDF generation
- HTML templates for form rendering
- Sample PDFs for reference

### Certificates

SSL certificates for secure communication:

- Server certificates: `certifcates/server/`
- Client certificates: `certifcates/client/`
- See `certifcates/How to create certicficates.txt` for setup

### Database Queries

Reference queries and scripts in `database/query.txt`

---

## License

[Specify your license here]

## Contact & Support

For issues, questions, or contributions, please contact the development team.

---

**Last Updated**: [Current Date]
**Version**: 1.0.0
