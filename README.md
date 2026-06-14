# Convo - MangoApps Android Assignment

Convo is A modern Android application built with the latest Android development tools, architecture patterns, and best practices. The app demonstrates device data handling, runtime permissions, REST API integration, pagination, clean architecture, and scalable UI development.

## 🏗️ Project Structure

### ⚙️ Feature Based Modularisation
App is modularized as per the features
```
Convo
├── app/                  # Main application module (Navigation & DI setup)
├── core/                 # Shared infrastructure modules
│   ├── data/             # Local storage & Shared Preferences
│   ├── network/          # API services & Networking
│   └── ui/               # Design system & Common UI components
└── feature/              # Feature-specific modules
    ├── contacts/         # Local & Remote contacts management
    ├── sms/              # SMS message viewing & name resolution
    └── call_log/         # Categorized call history
```

### 📦 Module Level Structure (Clean Architecture)
Each feature module follows a strict layered architecture:
```
feature/contacts/
├── data/                # Data Layer
│   ├── model/           # Data Transfer Objects (DTOs)
│   ├── repo/            # Repository Implementations
│   └── paging/          # PagingSource & Pagination logic
├── domain/              # Domain Layer (Pure Kotlin)
│   ├── model/           # Domain Entities
│   ├── contract/        # Repository Interfaces
│   └── usecase/         # Business Logic (Use Cases)
├── ui/                  # Presentation Layer (UI)
│   ├── screen/          # ViewModels & Screen Composables
│   ├── components/      # Reusable UI components
│   └── util/            # Feature-specific utilities
└── di/                  # Hilt modules for feature dependencies
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM + Clean Architecture |
| UI | Jetpack Compose |
| Async | Kotlin Coroutines + Flow / StateFlow |
| Dependency Injection | Hilt |
| Networking | Retrofit + OkHttp |
| Image Loading | Coil |
| Pagination | Paging 3 |
| Navigation | Navigation Drawer + Type-Safe Compose Navigation |
| Design System | Material Design 3 |
| Data Persistence | SharedPreferences |

**Minimum SDK:** 24  
**Target SDK:** 36

---
## 🔐 Permissions

| Permission | Used For |
|---|---|
| `READ_CONTACTS` | Local Contacts |
| `READ_CALL_LOG` | Call Logs |
| `READ_SMS` | SMS Inbox |

Permissions are requested **only when the corresponding module is accessed** — not at app launch. If a permission is denied, other modules continue to function independently.
 
---

## ✨ Features

### 📱 1. Main App Screen (`:app`)
- **Central Navigation Hub**: Uses a Navigation Drawer for seamless switching between major features.
- **Dynamic Theming**: Real-time Light/Dark mode toggle with persistence using SharedPreferences.
- **Edge-to-Edge UI**: Fully immersive experience using Android's latest `enableEdgeToEdge`.
- **State Persistence**: Remembers the last visited screen across app restarts.
- **Collapsible Top Bar**: Interactive `TopAppBar` that responds to nested scroll events.

### 👥 2. Contacts Screen (`:feature:contacts`)
- **Dual Source Data**: Displays both local device contacts and remote contacts from a REST API.
- **Efficient Pagination**: Implements **Paging 3** for remote contacts to handle large datasets smoothly.
- **Tabbed Interface**: Clean separation between "Local" and "Remote" contact views.
- **Pull-to-Refresh**: Easily sync and update contact lists with native gestures.
- **Avatar Resolution**: Auto-generates letter-based avatars or displays contact photos.
- **Direct Action**: Users are redirected to Dialer and SMS for phone contacts, or to Email support if an email ID is present.

### 📞 3. Call Logs Screen (`:feature:call_log`)
- **Categorized History**: Separate tabs for **Incoming**, **Outgoing**, and **Missed** calls.
- **Direct Action**: Single-tap to initiate a call back via the system dialer.
- **Detailed Metadata**: Displays call duration, timestamps, and formatted phone numbers.
- **Real-time Sync**: Fetches and formats the latest device call logs instantly.

### 💬 4. SMS Screen (`:feature:sms`)
- **Intelligent Name Resolution**: Automatically resolves sender phone numbers to contact names using the device's address book.
- **Message Preview**: Displays message snippets and timestamps in an easy-to-read list.
- **Detail View**: Interactive dialog to view the full content of any message.
- **Permission Awareness**: Dynamically handles combined `READ_SMS` and `READ_CONTACTS` permissions for a feature-complete experience.

---

## ✅ Submission Checklist

- [x] No crashes
- [x] Runtime permissions handled correctly (on-demand, with denied/permanently-denied states)
- [x] Pagination implemented with Paging 3
- [x] Swipe-to-refresh working on all screens
- [x] Empty state handling (No contacts / No call logs / No SMS)
- [x] Clean Architecture followed (Data → Domain → Presentation)
- [x] Latest stable dependencies used
- [x] README included

---

## 🚀 Bonus Checklist

- [x] **Fully Jetpack Compose UI**: 100% declarative UI built with Compose, no legacy XML layouts.
- [x] **Modular Architecture**: Advanced multi-module setup split into `:app`, `:core`, and `:feature`.
- [x] **Dark Mode Support**: Full support for Light/Dark themes with persistent user preference.
- [x] **Type-Safe Compose Navigation**: Using Kotlin Serialization for compile-time safety in navigation routes.
- [x] **Real-time SMS Name Resolution**: Dynamically matching SMS sender numbers against the device contact list.
- [x] **Collapsible Toolbars**: Seamless integration with Nested Scrolling for a modern, fluid UI feel.
- [x] **Animations & Transitions**: Subtle animations using `animateColorAsState` and `Crossfade` for theme switching.
- [x] **Efficient Data Layer**: Use of Repository pattern with local caching (SharedPrefs) and Paging 3.
- [x] **Clean Modular DI**: Scoped Hilt modules for each feature ensures clean dependency boundaries.
- [x] **Offline Caching**: Persistent storage of user theme preferences and navigation state.
- [x] **Clean Git commit history**: Systematic and meaningful commit messages following best practices.
- [x] **CI-CD Pipeline**: Integrated Github actions build pipeline and Firebase App Distribution for testing deployments.

---

## 📄 License

This project is created for educational/assignment purposes.