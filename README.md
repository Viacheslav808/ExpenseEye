<div align="center">

# ExpenseEye
### Secure. Offline-first. Personal finance tracking for Android.

![Android](https://img.shields.io/badge/Android-App-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-Android-orange?logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue)
![Database](https://img.shields.io/badge/Database-Room%20%7C%20SQLite-0E7C86)

ExpenseEye is an Android finance tracker built to help users monitor income, manage expenses, stay within budget, and understand spending habits through reports and visual summaries.

</div>

---

## About the Project
ExpenseEye is a secure, user-friendly mobile application focused on personal finance management. The project is designed around simplicity, reliability, privacy, and offline-first access. Users can record transactions, organize spending by account and category, review balance summaries, manage budgets, and restore/export data without relying on a constant internet connection.

Unlike many finance apps, ExpenseEye is designed around local data ownership. Financial data is stored on the device, which supports privacy, consistent access, and strong performance even when the user is offline.

## Key Features
- Secure login and authentication
- Secure password handling
- Income and expense tracking
- Add, edit, view, and manage transaction history
- Account and category organization
- Balance summaries across accounts
- Budget creation and threshold alerts
- Reports and chart-based spending insights
- Offline-first local storage with Room and SQLite
- SharedPreferences for user settings and saved app state
- JSON backup and restore support
- Android notifications for budget overages

## Current Implementation Highlights
At the current stage of development, the project includes:

- Authentication flow and login screen
- Transaction module backed by Room database entities, DAOs, and MVVM
- Reports dashboard with total spending, category totals, and account breakdowns
- Budgeting engine with real-time evaluation and visual status feedback
- Settings screen with JSON export/import and logout actions
- Notification service for budget overage alerts
- Bottom navigation across core app sections

## Architecture
ExpenseEye follows a layered **MVVM (Model-View-ViewModel)** architecture to keep the codebase modular, maintainable, and scalable.

### Layers
- **Presentation Layer** — Activities, Fragments, and ViewModels that manage UI state and user interactions
- **Core Layer** — Domain logic, repositories, and services such as authentication, budgeting, reporting, and transaction processing
- **Local Storage & Platform Layer** — Room (SQLite), SharedPreferences, Android Keystore, and JSON-based file storage

### Data Flow
```text
UI -> ViewModel -> Repository -> DAO -> Database
```

This separation helps keep business logic out of the UI and supports future growth of the application.

## Core Modules
### Authentication
Protects access to personal financial data through secure login and credential handling.

### Finance / Transactions
Handles accounts, categories, income, expenses, and transaction history. This module acts as the foundation for reporting, budgeting, and notifications.

### Reports
Provides summary views such as total spending, category totals, and account breakdowns based on stored transaction data.

### Budgets
Allows users to set spending limits by category and receive feedback when they are approaching or exceeding a threshold.

### Settings & Backup
Supports export/import through JSON, manages app settings, and provides logout functionality.

### Notifications
Triggers budget-related alerts through Android notifications when spending exceeds defined limits.

## Tech Stack
| Area | Tools / Technologies |
|---|---|
| Platform | Android OS |
| Language | Java |
| IDE | Android Studio |
| Architecture | MVVM, Repository Pattern |
| Database | Room ORM, SQLite |
| Preferences | SharedPreferences |
| Security | Password hashing, Android Keystore |
| Charts | MPAndroidChart |
| Backup / Restore | JSON import/export, Gson |
| Testing / QA | JUnit 4, AndroidJUnitRunner, Android Lint, Database Inspector |
| Collaboration | GitHub, GitHub Projects, Agile workflow |

## Team
This project was developed by **Group 10**.

| Team Member | Contribution Areas |
|---|---|
| **Viacheslav Stekolnikov** | Core finance system, transaction flow, class design |
| **Ryan Moraga** | Functional requirements, system functionality planning, architecture input |
| **Dmitriy Baimakov** | Reports module, use case/activity design, project planning and dependencies |
| **Tom Spencer** | Settings, notifications, ERD, technologies/frameworks |
| **Yassin Mohamed** | Project objectives, authentication module |

## Project Goals
- Help users clearly understand spending and income patterns
- Keep financial data private and available offline
- Provide useful reports, summaries, and budget awareness
- Build a clean Android application with maintainable architecture
- Support future scalability and feature growth

## Roadmap
Planned and ongoing improvements include:

- Improved transaction input experience
- Expanded edit and delete workflows
- Filtering and sorting for transactions
- Richer analytics and reporting views
- Final testing, bug fixing, and UI polish

## Getting Started
```bash
git clone https://github.com/Viacheslav808/ExpenseEye
```

Then:
1. Open the project in **Android Studio**.
2. Allow **Gradle** to sync the dependencies.
3. Start an Android emulator or connect a physical device.
4. Build and run the app.

## Notes
- ExpenseEye is designed as a **local-first** finance tracker.
- Accounts can represent things like chequing, savings, credit card, or loan accounts.
- Transactions are manually entered and organized for reporting and budgeting.
- The repository reflects both project planning deliverables and ongoing implementation work.
