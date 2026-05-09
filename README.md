\# 🏦 SecureBank — Digital Banking Management System



<div align="center">



!\[SecureBank Banner](https://img.shields.io/badge/SecureBank-Digital%20Banking-1a3c5e?style=for-the-badge\&logo=bank\&logoColor=white)



\[!\[Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=flat-square\&logo=spring-boot)](https://spring.io/projects/spring-boot)

\[!\[Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?style=flat-square\&logo=spring-security)](https://spring.io/projects/spring-security)

\[!\[Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-005F0F?style=flat-square\&logo=thymeleaf)](https://www.thymeleaf.org/)

\[!\[PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square\&logo=postgresql\&logoColor=white)](https://www.postgresql.org/)

\[!\[Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square\&logo=docker\&logoColor=white)](https://www.docker.com/)

\[!\[Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square\&logo=java\&logoColor=white)](https://openjdk.org/)

\[!\[Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square\&logo=apache-maven\&logoColor=white)](https://maven.apache.org/)

\[!\[License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)



\*\*A production-grade, full-stack digital banking platform built with Spring Boot 3, Spring Security 6, Thymeleaf, and PostgreSQL. Features multi-role access control, real-time fund transfers, loan management, fixed deposits, PDF statements, HTML email notifications, REST APIs, and Docker deployment.\*\*



\[Features](#-features) • \[Tech Stack](#-tech-stack) • \[Architecture](#-architecture) • \[Getting Started](#-getting-started) • \[API Docs](#-rest-api) • \[Screenshots](#-screenshots) • \[Deployment](#-docker-deployment)



</div>



\---



\## 📋 Table of Contents



\- \[About the Project](#-about-the-project)

\- \[Features](#-features)

\- \[Tech Stack](#-tech-stack)

\- \[Architecture](#-architecture)

\- \[Project Structure](#-project-structure)

\- \[Getting Started](#-getting-started)

&#x20; - \[Prerequisites](#prerequisites)

&#x20; - \[Local Setup](#local-setup)

&#x20; - \[Docker Setup](#docker-setup)

\- \[Default Credentials](#-default-credentials)

\- \[REST API](#-rest-api)

\- \[Email Notifications](#-email-notifications)

\- \[Scheduled Jobs](#-scheduled-jobs)

\- \[Screenshots](#-screenshots)

\- \[Contributing](#-contributing)

\- \[License](#-license)



\---



\## 📖 About the Project



\*\*SecureBank\*\* is a comprehensive digital banking management system that simulates real-world banking operations. It is built as a major full-stack project demonstrating enterprise-level Java development skills including:



\- \*\*Multi-role security\*\* with Spring Security 6

\- \*\*Real banking workflows\*\* — account management, fund transfers, loans, FDs

\- \*\*Production-grade email system\*\* using Thymeleaf HTML templates

\- \*\*REST API\*\* for potential mobile app integration

\- \*\*PDF statement generation\*\* using iText

\- \*\*Scheduled background jobs\*\* — monthly statements, FD maturity

\- \*\*Full audit trail\*\* — every action logged with user, IP, and timestamp

\- \*\*Docker containerization\*\* for production deployment



> This project is designed to showcase advanced Spring Boot skills for placement and resume purposes.



\---



\## ✨ Features



\### 🔐 Authentication \& Security

\- Session-based authentication with Spring Security 6

\- Role-based access control — `ADMIN`, `MANAGER`, `TELLER`, `CUSTOMER`

\- Account lockout after 5 failed login attempts

\- Email OTP for password reset

\- CSRF protection for all web forms

\- API endpoints with CSRF disabled for REST clients

\- Password encryption with BCrypt (strength 12)

\- Concurrent session management (max 1 session)



\### 👤 Customer Module

\- Self-registration with manager approval workflow

\- Personal dashboard with balance overview

\- Fund transfers with reference number generation

\- Transaction history with credit/debit indicators

\- Loan application with live EMI calculator

\- Fixed Deposit creation with maturity preview

\- PDF account statement download

\- Profile management with password change



\### 🏢 Manager Module

\- Approve/reject customer registrations (auto-creates savings account)

\- Approve/reject loan applications with remarks

\- View branch-level customer and loan statistics

\- Create and manage teller accounts

\- Customer detail view with loan history



\### 🧑‍💼 Teller Module

\- Cash deposit to customer accounts

\- Cash withdrawal from customer accounts

\- Account lookup by account number

\- View all active customers



\### ⚡ Admin Module

\- System-wide user management (all roles)

\- Activate/deactivate/unlock any user account

\- View all transactions across the system

\- Full audit log with action-based filtering

\- Reports dashboard with Chart.js visualizations

&#x20; - Transaction breakdown (donut chart)

&#x20; - Loan application status (bar chart)

&#x20; - User distribution (progress bars)

&#x20; - Bank balance, transaction volume, FD stats



\### 📧 Email Notifications (HTML Templates)

\- Welcome email on registration

\- Account activation with account number

\- Transaction alert (credit + debit) with balance

\- OTP email for password reset

\- Loan approval/rejection with EMI breakdown

\- Monthly account statement (automated)

\- Fixed Deposit maturity notification with breakdown

\- Password change confirmation

\- Account lock notification



\### 🏦 Banking Features

\- Multiple account types (SAVINGS, CURRENT)

\- Fixed Deposits with tiered interest rates (4.5% – 8%)

\- Loan management with EMI calculation (10.5% p.a.)

\- Unique account number generation

\- Transaction reference number (TXN + UUID)

\- Complete audit trail for every operation

\- PDF statements with professional bank layout



\### 🚀 REST API (v1)

\- Authentication endpoints (register, login, me, dashboard)

\- Account endpoints (list, detail, balance)

\- Transaction endpoints (history, transfer, recent)

\- Loan endpoints (list, apply, detail)

\- Consistent JSON response wrapper

\- Global exception handler

\- Interactive API documentation page



\---



\## 🛠 Tech Stack



| Category | Technology | Version |

|---|---|---|

| \*\*Backend Framework\*\* | Spring Boot | 3.2.0 |

| \*\*Security\*\* | Spring Security | 6.x |

| \*\*Template Engine\*\* | Thymeleaf | 3.x |

| \*\*Database\*\* | PostgreSQL | 15 |

| \*\*ORM\*\* | Spring Data JPA / Hibernate | 6.x |

| \*\*PDF Generation\*\* | iText | 5.5.13.3 |

| \*\*Email\*\* | Spring Mail + JavaMailSender | — |

| \*\*Validation\*\* | Spring Validation (Jakarta) | — |

| \*\*Build Tool\*\* | Apache Maven | 3.9.x |

| \*\*Language\*\* | Java | 17 |

| \*\*Containerization\*\* | Docker + Docker Compose | Latest |

| \*\*Charts\*\* | Chart.js (CDN) | 4.4.0 |

| \*\*Fonts\*\* | Plus Jakarta Sans (Google Fonts) | — |



\---



\## 🏗 Architecture



```

┌─────────────────────────────────────────────────────────────┐

│                        CLIENT LAYER                          │

│   Browser (Thymeleaf UI)    │    REST Client (Postman/App)  │

└──────────────────┬──────────┴────────────────┬──────────────┘

&#x20;                  │                            │

┌──────────────────▼────────────────────────────▼──────────────┐

│                    SPRING SECURITY LAYER                       │

│   Session Auth │ Role-Based Access │ CSRF │ Account Lockout   │

└──────────────────────────────┬───────────────────────────────┘

&#x20;                               │

┌───────────────────────────────▼──────────────────────────────┐

│                    CONTROLLER LAYER                            │

│  AuthController │ CustomerController │ ManagerController       │

│  TellerController │ AdminController │ API Controllers (v1)     │

└───────────────────────────────┬──────────────────────────────┘

&#x20;                               │

┌───────────────────────────────▼──────────────────────────────┐

│                     SERVICE LAYER                              │

│  UserService │ AccountService │ TransactionService            │

│  LoanService │ FixedDepositService │ EmailService             │

│  TellerService │ ManagerService │ PdfStatementService         │

└───────────────────────────────┬──────────────────────────────┘

&#x20;                               │

┌───────────────────────────────▼──────────────────────────────┐

│                   REPOSITORY LAYER                             │

│  UserRepository │ AccountRepository │ TransactionRepository   │

│  LoanRepository │ FDRepository │ AuditLogRepository           │

│  OtpTokenRepository                                           │

└───────────────────────────────┬──────────────────────────────┘

&#x20;                               │

┌───────────────────────────────▼──────────────────────────────┐

│                    DATABASE LAYER                              │

│                     PostgreSQL 15                             │

│  users │ accounts │ transactions │ loans │ fixed\_deposits     │

│  audit\_logs │ otp\_tokens                                      │

└──────────────────────────────────────────────────────────────┘

```



\### Role-Based URL Routing



| Role | Dashboard URL | Access Scope |

|---|---|---|

| `ADMIN` | `/admin/dashboard` | Full system access |

| `MANAGER` | `/manager/dashboard` | Branch management |

| `TELLER` | `/teller/dashboard` | Teller operations |

| `CUSTOMER` | `/customer/dashboard` | Own account only |



\---



\## 📁 Project Structure



```

securebank/

├── src/

│   ├── main/

│   │   ├── java/com/securebank/

│   │   │   ├── api/                        # REST API controllers

│   │   │   │   ├── AccountApiController.java

│   │   │   │   ├── AuthApiController.java

│   │   │   │   ├── LoanApiController.java

│   │   │   │   └── TransactionApiController.java

│   │   │   ├── config/                     # Configuration classes

│   │   │   │   ├── AsyncConfig.java

│   │   │   │   ├── CustomAuthSuccessHandler.java

│   │   │   │   ├── CustomAuthenticationFailureHandler.java

│   │   │   │   ├── DataSeeder.java

│   │   │   │   └── SecurityConfig.java

│   │   │   ├── controller/                 # MVC controllers

│   │   │   │   ├── admin/

│   │   │   │   │   └── AdminController.java

│   │   │   │   ├── customer/

│   │   │   │   │   ├── CustomerController.java

│   │   │   │   │   ├── CustomerFDController.java

│   │   │   │   │   └── CustomerLoanController.java

│   │   │   │   ├── manager/

│   │   │   │   │   └── ManagerController.java

│   │   │   │   ├── teller/

│   │   │   │   │   └── TellerController.java

│   │   │   │   └── AuthController.java

│   │   │   ├── dto/                        # Data Transfer Objects

│   │   │   │   ├── request/

│   │   │   │   │   ├── ApiLoginRequest.java

│   │   │   │   │   ├── FDRequest.java

│   │   │   │   │   ├── LoanApplicationRequest.java

│   │   │   │   │   ├── LoanReviewRequest.java

│   │   │   │   │   ├── RegisterRequest.java

│   │   │   │   │   └── TransferRequest.java

│   │   │   │   └── response/

│   │   │   │       ├── AccountResponse.java

│   │   │   │       ├── ApiResponse.java

│   │   │   │       ├── DashboardResponse.java

│   │   │   │       └── TransactionResponse.java

│   │   │   ├── exception/

│   │   │   │   └── GlobalExceptionHandler.java

│   │   │   ├── model/                      # JPA Entities

│   │   │   │   ├── enums/

│   │   │   │   │   ├── AccountType.java

│   │   │   │   │   ├── LoanStatus.java

│   │   │   │   │   ├── Role.java

│   │   │   │   │   ├── TransactionStatus.java

│   │   │   │   │   └── TransactionType.java

│   │   │   │   ├── Account.java

│   │   │   │   ├── AuditLog.java

│   │   │   │   ├── FixedDeposit.java

│   │   │   │   ├── Loan.java

│   │   │   │   ├── OtpToken.java

│   │   │   │   ├── Transaction.java

│   │   │   │   └── User.java

│   │   │   ├── repository/                 # JPA Repositories

│   │   │   │   ├── AccountRepository.java

│   │   │   │   ├── AuditLogRepository.java

│   │   │   │   ├── FixedDepositRepository.java

│   │   │   │   ├── LoanRepository.java

│   │   │   │   ├── OtpTokenRepository.java

│   │   │   │   ├── TransactionRepository.java

│   │   │   │   └── UserRepository.java

│   │   │   ├── scheduler/

│   │   │   │   └── BankingScheduler.java

│   │   │   ├── service/                    # Business Logic

│   │   │   │   ├── AccountService.java

│   │   │   │   ├── CustomUserDetailsService.java

│   │   │   │   ├── EmailService.java

│   │   │   │   ├── FixedDepositService.java

│   │   │   │   ├── LoanService.java

│   │   │   │   ├── ManagerService.java

│   │   │   │   ├── PdfStatementService.java

│   │   │   │   ├── TellerService.java

│   │   │   │   ├── TransactionService.java

│   │   │   │   └── UserService.java

│   │   │   ├── util/

│   │   │   │   ├── AccountNumberUtil.java

│   │   │   │   └── OtpUtil.java

│   │   │   └── SecureBankApplication.java

│   │   └── resources/

│   │       ├── templates/

│   │       │   ├── admin/

│   │       │   │   ├── audit-logs.html

│   │       │   │   ├── dashboard.html

│   │       │   │   ├── reports.html

│   │       │   │   ├── transactions.html

│   │       │   │   └── users.html

│   │       │   ├── auth/

│   │       │   │   ├── access-denied.html

│   │       │   │   ├── forgot-password.html

│   │       │   │   ├── login.html

│   │       │   │   ├── register.html

│   │       │   │   └── reset-password.html

│   │       │   ├── customer/

│   │       │   │   ├── dashboard.html

│   │       │   │   ├── fixed-deposits.html

│   │       │   │   ├── loans.html

│   │       │   │   ├── profile.html

│   │       │   │   ├── transactions.html

│   │       │   │   └── transfer.html

│   │       │   ├── emails/

│   │       │   │   ├── account-activation.html

│   │       │   │   ├── account-locked.html

│   │       │   │   ├── fd-maturity.html

│   │       │   │   ├── loan-status.html

│   │       │   │   ├── monthly-statement.html

│   │       │   │   ├── otp.html

│   │       │   │   ├── password-changed.html

│   │       │   │   ├── transaction-alert.html

│   │       │   │   └── welcome.html

│   │       │   ├── manager/

│   │       │   │   ├── customer-detail.html

│   │       │   │   ├── customers.html

│   │       │   │   ├── dashboard.html

│   │       │   │   ├── loans.html

│   │       │   │   └── tellers.html

│   │       │   ├── teller/

│   │       │   │   ├── dashboard.html

│   │       │   │   ├── deposit.html

│   │       │   │   └── withdraw.html

│   │       │   └── api-docs.html

│   │       ├── static/

│   │       │   └── css/

│   │       │       └── auth.css

│   │       └── application.yml

│   └── test/

│       └── java/com/securebank/

│           └── SecureBankApplicationTests.java

├── .env                                    # Environment variables (git-ignored)

├── .gitignore

├── docker-compose.yml

├── Dockerfile

├── pom.xml

└── README.md

```



\---



\## 🚀 Getting Started



\### Prerequisites



Make sure you have the following installed:



| Tool | Version | Download |

|---|---|---|

| Java JDK | 17+ | \[Download](https://adoptium.net/) |

| Maven | 3.9+ | \[Download](https://maven.apache.org/download.cgi) |

| PostgreSQL | 15+ | \[Download](https://www.postgresql.org/download/) |

| Docker Desktop | Latest | \[Download](https://www.docker.com/products/docker-desktop/) |

| Eclipse STS / IntelliJ | Latest | \[STS](https://spring.io/tools) / \[IntelliJ](https://www.jetbrains.com/idea/) |

| Git | Latest | \[Download](https://git-scm.com/) |



\---



\### Local Setup



\#### 1. Clone the Repository



```bash

git clone https://github.com/yourusername/securebank.git

cd securebank

```



\#### 2. Create PostgreSQL Database



Open pgAdmin or psql and run:



```sql

CREATE DATABASE securebank;

```



\#### 3. Configure `application.yml`



Update `src/main/resources/application.yml`:



```yaml

spring:

&#x20; datasource:

&#x20;   url: jdbc:postgresql://localhost:5432/securebank

&#x20;   username: postgres

&#x20;   password: YOUR\_POSTGRES\_PASSWORD    # ← change this



&#x20; mail:

&#x20;   username: your-gmail@gmail.com      # ← change this

&#x20;   password: xxxx-xxxx-xxxx-xxxx       # ← Gmail App Password

```



> \*\*Get Gmail App Password:\*\*

> Google Account → Security → 2-Step Verification → App Passwords → Generate



\#### 4. Build the Project



```bash

./mvnw clean install -DskipTests

```



\#### 5. Run the Application



```bash

./mvnw spring-boot:run

```



Or in Eclipse STS:

```

Right click SecureBankApplication.java → Run As → Spring Boot App

```



\#### 6. Access the Application



```

http://localhost:8080

```



You will be redirected to the login page automatically.



\---



\### Docker Setup



\#### 1. Create `.env` File



Create `.env` in the project root:



```env

POSTGRES\_PASSWORD=SecureBank@2024

MAIL\_USERNAME=your-gmail@gmail.com

MAIL\_PASSWORD=your-16-char-app-password

BANK\_NAME=SecureBank

```



\#### 2. Build JAR



```bash

./mvnw clean package -DskipTests

```



\#### 3. Start with Docker Compose



```bash

docker-compose up --build

```



\#### 4. Run in Background



```bash

docker-compose up --build -d

```



\#### 5. View Logs



```bash

docker-compose logs -f app

```



\#### 6. Stop Containers



```bash

docker-compose down

```



\#### 7. Full Reset (delete data)



```bash

docker-compose down -v

```



\---



\## 🔑 Default Credentials



> \*\*Note:\*\* These credentials are seeded automatically on first startup by `DataSeeder.java`.



| Role | Email | Password | Redirect |

|---|---|---|---|

| \*\*Admin\*\* | `admin@securebank.com` | `Admin@123` | `/admin/dashboard` |

| \*\*Manager\*\* | `manager@securebank.com` | `Manager@123` | `/manager/dashboard` |

| \*\*Customer\*\* | `customer@securebank.com` | `Customer@123` | `/customer/dashboard` |

| \*\*Teller\*\* | Created via Manager panel | `Teller@123` | `/teller/dashboard` |



> ⚠️ Change all default passwords in production.



\---



\## 📊 Database Schema



```

┌─────────────┐     ┌──────────────┐     ┌─────────────────┐

│    users    │────<│   accounts   │────<│  transactions   │

├─────────────┤     ├──────────────┤     ├─────────────────┤

│ id          │     │ id           │     │ id              │

│ full\_name   │     │ account\_num  │     │ from\_account\_id │

│ email       │     │ account\_type │     │ to\_account\_id   │

│ password    │     │ balance      │     │ amount          │

│ role        │     │ active       │     │ type            │

│ active      │     │ user\_id (FK) │     │ status          │

│ locked      │     │ created\_at   │     │ description     │

│ failed\_att  │     └──────────────┘     │ reference\_num   │

│ phone       │                          │ timestamp       │

│ address     │     ┌──────────────┐     └─────────────────┘

│ created\_at  │────<│    loans     │

└─────────────┘     ├──────────────┤     ┌─────────────────┐

&#x20;                   │ id           │     │ fixed\_deposits  │

&#x20;                   │ user\_id (FK) │     ├─────────────────┤

&#x20;                   │ amount       │     │ id              │

&#x20;                   │ tenure\_months│     │ user\_id (FK)    │

&#x20;                   │ interest\_rate│     │ account\_id (FK) │

&#x20;                   │ status       │     │ principal\_amt   │

&#x20;                   │ remarks      │     │ interest\_rate   │

&#x20;                   │ applied\_at   │     │ tenure\_months   │

&#x20;                   │ reviewed\_at  │     │ start\_date      │

&#x20;                   └──────────────┘     │ maturity\_date   │

&#x20;                                        │ active          │

┌─────────────────┐  ┌──────────────┐   └─────────────────┘

│   audit\_logs    │  │  otp\_tokens  │

├─────────────────┤  ├──────────────┤

│ id              │  │ id           │

│ user\_id         │  │ email        │

│ username        │  │ otp          │

│ action          │  │ purpose      │

│ entity          │  │ used         │

│ details         │  │ expires\_at   │

│ ip\_address      │  └──────────────┘

│ timestamp       │

└─────────────────┘

```



\---



\## 🌐 REST API



\*\*Base URL:\*\* `http://localhost:8080/api/v1`



\*\*Interactive Docs:\*\* `http://localhost:8080/auth/api-docs`



\### Authentication



| Method | Endpoint | Auth | Description |

|---|---|---|---|

| `POST` | `/auth/register` | ❌ Public | Register new customer |

| `POST` | `/auth/login` | ❌ Public | Authenticate user |

| `GET` | `/auth/me` | ✅ Required | Get current user info |

| `GET` | `/auth/dashboard` | ✅ Required | Full dashboard data |



\### Accounts



| Method | Endpoint | Auth | Description |

|---|---|---|---|

| `GET` | `/accounts` | ✅ Required | Get all my accounts |

| `GET` | `/accounts/{id}` | ✅ Required | Get account by ID |

| `GET` | `/accounts/balance` | ✅ Required | Get total balance |



\### Transactions



| Method | Endpoint | Auth | Description |

|---|---|---|---|

| `GET` | `/transactions` | ✅ Required | Transaction history |

| `POST` | `/transactions/transfer` | ✅ Required | Fund transfer |

| `GET` | `/transactions/recent` | ✅ Required | Recent 10 transactions |



\### Loans



| Method | Endpoint | Auth | Description |

|---|---|---|---|

| `GET` | `/loans` | ✅ Required | Get my loan applications |

| `POST` | `/loans/apply` | ✅ Required | Apply for loan |

| `GET` | `/loans/{id}` | ✅ Required | Get loan by ID |



\### Sample Request — Fund Transfer



```bash

curl -X POST http://localhost:8080/api/v1/transactions/transfer \\

&#x20; -H "Content-Type: application/json" \\

&#x20; -b "JSESSIONID=your-session-id" \\

&#x20; -d '{

&#x20;   "toAccountNumber": "SB1234567890",

&#x20;   "amount": 5000.00,

&#x20;   "description": "Rent payment"

&#x20; }'

```



\### Sample Response



```json

{

&#x20; "success": true,

&#x20; "message": "Transfer successful! Ref: TXNABC123DEF456",

&#x20; "data": "TXNABC123DEF456",

&#x20; "status": 200,

&#x20; "timestamp": "2024-01-15T10:30:00"

}

```



\---



\## 📧 Email Notifications



| Email | Template | Trigger |

|---|---|---|

| Welcome | `emails/welcome.html` | Customer registers |

| Account Activation | `emails/account-activation.html` | Manager approves account |

| Transaction Alert | `emails/transaction-alert.html` | Every fund transfer |

| OTP Verification | `emails/otp.html` | Password reset request |

| Loan Status | `emails/loan-status.html` | Loan approved / rejected |

| Monthly Statement | `emails/monthly-statement.html` | 1st of every month (auto) |

| FD Maturity | `emails/fd-maturity.html` | FD maturity date reached |

| Password Changed | `emails/password-changed.html` | Password updated |

| Account Locked | `emails/account-locked.html` | 5 failed login attempts |



All emails are:

\- \*\*HTML-formatted\*\* using Thymeleaf templates

\- \*\*Sent asynchronously\*\* (`@Async`) to avoid blocking requests

\- \*\*Professional design\*\* with bank branding



\---



\## ⏰ Scheduled Jobs



| Job | Schedule | Description |

|---|---|---|

| Monthly Statements | `0 0 8 1 \* \*` | 1st of every month at 8 AM |

| FD Maturity Check | `0 0 9 \* \* \*` | Every day at 9 AM |

| Daily Health Check | `0 0 6 \* \* \*` | Every day at 6 AM |

| Weekly Cleanup | `0 0 2 \* \* SUN` | Every Sunday at 2 AM |



\---



\## 💎 Fixed Deposit Interest Rates



| Tenure | Interest Rate |

|---|---|

| Up to 3 months | 4.5% p.a. |

| 3 – 6 months | 5.5% p.a. |

| 6 – 12 months | 6.5% p.a. |

| 12 – 24 months | 7.0% p.a. |

| 24 – 36 months | 7.5% p.a. |

| Above 36 months | 8.0% p.a. |



\---



\## 🔒 Security Features



\- \*\*BCrypt Password Encoding\*\* — strength factor 12

\- \*\*Account Lockout\*\* — locked after 5 failed login attempts

\- \*\*Email Alert on Lock\*\* — customer notified immediately

\- \*\*CSRF Protection\*\* — all web forms protected

\- \*\*Session Management\*\* — max 1 concurrent session

\- \*\*Role-Based Access\*\* — URL-level and method-level security

\- \*\*OTP Expiry\*\* — 5 minutes validity

\- \*\*Audit Logging\*\* — every sensitive action logged with IP address

\- \*\*API Security\*\* — CSRF disabled only for `/api/\*\*` routes

\- \*\*Input Validation\*\* — all forms validated with Jakarta Bean Validation



\---



\## 📄 PDF Statement



Generated using \*\*iText 5\*\* library with:

\- Professional bank header with gradient

\- Account holder information

\- Complete transaction history table

\- Color-coded credit (green) and debit (red) amounts

\- Summary section (total credits, debits, balance)

\- Header/footer on every page

\- Disclaimer text



\*\*Download URL:\*\* `GET /customer/statement/download?accountId={id}`



\---



\## 🧪 Testing the Application



\### Complete Test Flow



```

1\. Open http://localhost:8080/auth/register

2\. Register a new customer account

3\. Login as Manager (manager@securebank.com / Manager@123)

4\. Go to Manager → Customers → Approve the new customer

5\. Login as the new customer

6\. Dashboard shows ₹0 balance (new account)

7\. Login as Teller (create via Manager → Tellers)

8\. Teller → Cash Deposit → deposit ₹50,000 to customer account

9\. Login as Customer

10\. Customer → Fund Transfer → transfer to SB1234567890

11\. Customer → Loans → Apply for loan → check EMI preview

12\. Login as Manager → Loans → Approve the loan

13\. Customer balance increases by loan amount

14\. Customer → Fixed Deposits → Create FD

15\. Customer → Fixed Deposits → Download PDF Statement

16\. Admin → Reports → View charts and analytics

17\. Admin → Audit Logs → Filter by action

18\. Admin → Users → Activate/Deactivate/Unlock

```



\### Test REST API with Postman



```

1\. POST /api/v1/auth/login

&#x20;  {"email":"customer@securebank.com","password":"Customer@123"}



2\. GET /api/v1/auth/dashboard

&#x20;  (uses session from step 1)



3\. GET /api/v1/accounts



4\. POST /api/v1/loans/apply

&#x20;  {"amount":50000,"tenureMonths":24}

```



\---



\## 🐳 Docker Deployment



\### Production Dockerfile



```dockerfile

FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn

COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests



FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/securebank-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT \["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

```



\### Useful Docker Commands



```bash

\# Build image

docker build -t securebank .



\# Start all services

docker-compose up --build -d



\# View logs

docker-compose logs -f app



\# Check running containers

docker-compose ps



\# Access database

docker exec -it securebank-db psql -U postgres -d securebank



\# Check email env vars

docker exec securebank-app env | grep MAIL



\# Stop all

docker-compose down



\# Stop and remove volumes

docker-compose down -v

```



\---



\## ⚙️ Configuration Reference



\### Environment Variables



| Variable | Description | Default |

|---|---|---|

| `SPRING\_DATASOURCE\_URL` | PostgreSQL connection URL | localhost:5432/securebank |

| `SPRING\_DATASOURCE\_USERNAME` | Database username | postgres |

| `SPRING\_DATASOURCE\_PASSWORD` | Database password | — |

| `SPRING\_MAIL\_HOST` | SMTP host | smtp.gmail.com |

| `SPRING\_MAIL\_PORT` | SMTP port | 587 |

| `SPRING\_MAIL\_USERNAME` | Gmail address | — |

| `SPRING\_MAIL\_PASSWORD` | Gmail App Password | — |

| `APP\_BANK\_NAME` | Bank display name | SecureBank |

| `APP\_OTP\_EXPIRY\_MINUTES` | OTP validity in minutes | 5 |



\---



\## 🤝 Contributing



Contributions are welcome! Please follow these steps:



1\. Fork the repository

2\. Create a feature branch: `git checkout -b feature/amazing-feature`

3\. Commit your changes: `git commit -m 'Add amazing feature'`

4\. Push to branch: `git push origin feature/amazing-feature`

5\. Open a Pull Request



\### Code Style Guidelines



\- Follow standard Java naming conventions

\- Add Javadoc for all public methods

\- Write unit tests for new service methods

\- Keep controllers thin — business logic in services only

\- Use constructor injection (not `@Autowired` field injection)



\---



\## 🐛 Known Issues \& Troubleshooting



\### Emails Not Sending



```bash

\# Check SMTP connectivity from Docker container

docker exec -it securebank-app sh

nc -zv smtp.gmail.com 587



\# Verify environment variables loaded

docker exec securebank-app env | grep MAIL



\# Check app logs for email errors

docker-compose logs app | grep -i "email\\|smtp\\|mail"

```



\### Database Connection Failed



```bash

\# Check PostgreSQL is running

docker-compose ps postgres



\# Check PostgreSQL logs

docker-compose logs postgres



\# Test connection manually

docker exec -it securebank-db psql -U postgres -d securebank -c "\\dt"

```



\### Lombok Not Working (Eclipse STS)



```bash

\# Run Lombok installer

java -jar \~/.m2/repository/org/projectlombok/lombok/1.18.46/lombok-1.18.46.jar

\# Select STS installation → Install/Update → Restart STS

```



\### Port 8080 Already in Use



```bash

\# Windows

netstat -ano | findstr :8080

taskkill /PID <pid> /F



\# Linux/Mac

lsof -i :8080

kill -9 <pid>

```



\---



\## 📈 Project Stats



| Metric | Count |

|---|---|

| Java Classes | 50+ |

| REST API Endpoints | 11 |

| HTML Templates | 25+ |

| Email Templates | 9 |

| Database Tables | 7 |

| Spring Security Roles | 4 |

| Scheduled Jobs | 4 |

| Lines of Code | \~5,000+ |



\---



\## 📜 License



This project is licensed under the MIT License — see the \[LICENSE](LICENSE) file for details.



```

MIT License



Copyright (c) 2024 SecureBank



Permission is hereby granted, free of charge, to any person obtaining a copy

of this software and associated documentation files (the "Software"), to deal

in the Software without restriction, including without limitation the rights

to use, copy, modify, merge, publish, distribute, sublicense, and/or sell

copies of the Software...

```



\---



\## 👨‍💻 Author



\*\*Vishal\*\* — \*Full Stack Java Developer\*



\- 📧 Email: your-email@example.com

\- 💼 LinkedIn: \[linkedin.com/in/yourprofile](https://linkedin.com/in/yourprofile)

\- 🐙 GitHub: \[github.com/yourusername](https://github.com/yourusername)



\---



\## 🙏 Acknowledgements



\- \[Spring Boot Documentation](https://spring.io/projects/spring-boot)

\- \[Spring Security Reference](https://docs.spring.io/spring-security/reference/)

\- \[Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

\- \[iText PDF Library](https://itextpdf.com/)

\- \[PostgreSQL Documentation](https://www.postgresql.org/docs/)

\- \[Chart.js Documentation](https://www.chartjs.org/docs/)

\- \[Plus Jakarta Sans Font](https://fonts.google.com/specimen/Plus+Jakarta+Sans)



\---



<div align="center">



\*\*⭐ If this project helped you, please give it a star!\*\*



Made with ❤️ using Spring Boot



</div>



