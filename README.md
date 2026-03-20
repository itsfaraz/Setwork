<div align="center">

<br/>

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,100:161b22&height=180&section=header&text=Setwork&fontSize=72&fontColor=e6edf3&fontAlignY=38&desc=Set%20your%20work.%20Get%20it%20done.&descAlignY=60&descSize=18&descColor=8b949e" width="100%"/>

<br/>

[![License: MIT](https://img.shields.io/badge/License-MIT-555555.svg?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Release](https://img.shields.io/badge/Release-v1.0.0-0078D4?style=flat-square)](https://github.com/itsfaraz/Setwork/releases)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-228B22?style=flat-square)](https://github.com/itsfaraz/Setwork/pulls)
![Visitors](https://visitor-badge.laobi.icu/badge?page_id=itsfaraz.Setwork&style=flat-square)

<br/>

[Website](https://setwork.web.app) &nbsp;&nbsp;|&nbsp;&nbsp;
[Download](#download) &nbsp;&nbsp;|&nbsp;&nbsp;
[Architecture](#architecture) &nbsp;&nbsp;|&nbsp;&nbsp;
[For Engineers](#for-engineers-and-learners) &nbsp;&nbsp;|&nbsp;&nbsp;
[Contributing](#contributing) &nbsp;&nbsp;|&nbsp;&nbsp;
[Donate](#support-the-project)

<br/>

</div>

---

## Overview

Setwork is a minimal, offline-first task management application for Android. It is written entirely in Kotlin and built on top of Jetpack Compose, with no cloud dependency, no subscription model, and no telemetry of any kind. All data is stored locally on the device.

The project is structured as a **multi-module ecosystem** — three independent repositories, each with a clearly bounded responsibility, consumed together to form the complete application. This architecture was a deliberate design decision, not an accidental one, and it reflects the way production-grade Android systems are increasingly being built.

This document exists to give engineers — whether they are users, contributors, or students — a thorough understanding of the project: what it does, how it is structured, why certain decisions were made, and how to meaningfully engage with it.

---

## Repositories

The Setwork ecosystem is composed of three repositories:

| Repository | Role |
|---|---|
| [`Setwork`](https://github.com/itsfaraz/Setwork) | Main application — UI, navigation, settings, feature screens |
| [`Setwork-Orchestrator`](https://github.com/itsfaraz/Setwork-Orchestrator) | Library module — notification scheduling and date rendering |
| [`Setwork-Provider`](https://github.com/itsfaraz/Setwork-Provider) | Library module — data provision and content sourcing |

Each module is independently versioned and independently releasable. The Orchestrator is currently at `v1.0.7`, which means it has already iterated beyond the main app's `v1.0.0` — a practical demonstration of why module isolation matters in multi-module systems.

---

## Architecture

### System Diagram

<img width="1142" height="675" alt="mermaid-diagram" src="https://github.com/user-attachments/assets/3953ca11-e4cc-4161-b479-c4be1367920e" />


### Architectural Philosophy

The application follows **Clean Architecture** principles, organized by feature rather than by layer. This is a meaningful distinction. Layer-organized projects (`data/`, `domain/`, `ui/` at the root) tend to couple features together implicitly. Feature-organized projects isolate each vertical slice of functionality, allowing individual features to be developed, tested, and replaced without touching unrelated code.

Each feature module within the main app contains its own `data`, `domain`, and `presentation` sub-packages. Shared infrastructure — repositories, utilities, base classes — lives in `common/`.

The dependency injection strategy is a **Service Locator** via `AppServiceLocator`, rather than a framework like Hilt or Koin. This is a conscious trade-off: less compile-time safety in exchange for reduced complexity, faster build times, and fewer framework abstractions between the engineer and the code. For a project of this scale, it is a defensible decision.

### Main App Package Structure

```
com.designlife.justdo
|
+-- common/
|   +-- data/                  # Repositories, Room database, DataStore
|   +-- domain/                # Use cases, domain models, business rules
|   +-- presentation/          # Reusable Compose components
|   +-- utils/                 # AppServiceLocator, SoftwareUpdateManager
|
+-- container/presentation/    # Navigation shell, ContainerFragment
+-- deck/presentation/         # Deck/board view feature
+-- home/
|   +-- domain/usecase/        # Home-specific business logic
|   +-- presentation/          # HomeFragment, HomeViewModel
+-- note/presentation/         # Note feature
+-- permission/                # PermissionHandler, RequestPermissions
+-- settings/presentation/
|   +-- components/            # Settings UI components
|   +-- entity/                # Settings data models
|   +-- enums/                 # AppTheme, FontSize, ListItemHeight enums
|   +-- events/                # Settings UI event definitions
|   +-- viewmodel/             # SettingViewModel
+-- task/                      # Core task feature
+-- ui.theme/                  # Compose theme: colors, typography, sizing
|
+-- MainActivity.kt            # Single-Activity entry point
```

### Orchestrator Module Structure

```
Setwork-Orchestrator/
+-- app/                       # Sample/test app harness
+-- orchestrator/              # The library module itself
    +-- notification/          # Scheduling, BroadcastReceivers
    +-- date/                  # Date rendering and formatting logic
```

The Orchestrator is the most interesting module architecturally. Notification scheduling on Android is non-trivial — it must survive device reboots, respect Doze mode, handle exact alarm permissions on API 31+, and behave correctly when the system reclaims resources. Extracting this logic into a dedicated, independently versioned module is the correct approach. It can be tested in isolation, updated without redeploying the main app, and reasoned about as a distinct system.

---

## Tech Stack

| Concern | Technology | Notes |
|---|---|---|
| Language | Kotlin | 100% of all three repositories |
| UI | Jetpack Compose + XML | Hybrid: Compose within Fragments |
| Navigation | Navigation Component | Single-Activity, Fragment destinations |
| Architecture | Clean Architecture | Feature-modular, not layer-modular |
| Dependency Provision | Service Locator | `AppServiceLocator`, no DI framework |
| Asynchrony | Kotlin Coroutines + Flow | `collectLatest` for preference streams |
| Local Storage | Room (SQLite) | CursorWindow resized to 100MB |
| Preferences | DataStore | Typed async preference storage |
| Scheduling | Setwork-Orchestrator | Notifications, alarms, date logic |
| Updates | SoftwareUpdateManager | In-app update detection |
| Build System | Gradle (Groovy DSL) | Multi-module configuration |

A note on the 100MB CursorWindow: the default SQLite cursor window in Android is 2MB. The explicit resize in `MainActivity` suggests that the application deals with potentially large result sets — possibly from bulk task imports, long task histories, or note content. This is worth understanding before contributing any code that touches the database layer.

---

## Download

[![GitHub Releases](https://img.shields.io/badge/Download-GitHub%20Releases-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/itsfaraz/Setwork/releases/tag/1.0.0)

**Distribution — coming soon:**

[![F-Droid](https://img.shields.io/badge/F--Droid-Pending-1976D2?style=flat-square&logo=f-droid&logoColor=white)](#)
[![Amazon Appstore](https://img.shields.io/badge/Amazon%20Appstore-Pending-FF9900?style=flat-square&logo=amazon&logoColor=white)](#)

When installing from a GitHub release APK, you will need to permit installation from unknown sources on your device: Settings > Apps > Special App Access > Install Unknown Apps.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 26 minimum (Android 8.0)
- JDK 11 or higher
- Git

### Cloning All Three Modules

```bash
git clone https://github.com/itsfaraz/Setwork.git
git clone https://github.com/itsfaraz/Setwork-Orchestrator.git
git clone https://github.com/itsfaraz/Setwork-Provider.git
```

### Building

```bash
cd Setwork

# Debug build
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Unit tests
./gradlew test

# Release build
./gradlew assembleRelease
```

Build outputs:

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

---

## Permissions

| Permission | Justification |
|---|---|
| `POST_NOTIFICATIONS` | Required to deliver scheduled task reminders |
| `READ_EXTERNAL_STORAGE` | Required to import task backups from device storage |
| `WRITE_EXTERNAL_STORAGE` | Required to write task export files to device storage |

The application requests no location, camera, microphone, or network permissions. The only network activity is the in-app update check via `SoftwareUpdateManager`. There are no analytics SDKs, no crash reporters that phone home, and no advertising identifiers.

---

## For Engineers and Learners

Setwork is a production-quality open-source project, and the codebase was written to be read. If you are a student of Android development or an engineer evaluating modern Android patterns, this section maps the concepts you will encounter to where they live in the code.

### Concepts Demonstrated — Main App

**Single-Activity architecture with Jetpack Navigation**

`MainActivity` is the only Activity in the application. All screens are Fragments, connected through a navigation graph managed by `NavHostFragment`. This is the current recommended approach from Google and reflects how most modern Android apps should be structured. Understanding why this replaced the multi-Activity model — lifecycle complexity, back stack management, shared ViewModel scoping — is foundational knowledge for any Android engineer.

**Jetpack Compose within Fragments**

The app uses a hybrid UI approach: Fragments for navigation, Compose for rendering. `MainActivity` contains a `ComposeView` used specifically to detect and react to dark mode changes. Individual feature screens use Compose within their Fragment's `onCreateView`. This hybrid pattern is common in apps migrating from XML to Compose incrementally, and understanding it is practically valuable.

**Reactive preference management with DataStore and Flow**

`SettingViewModel` exposes preferences as a `Flow`. `MainActivity` collects these with `collectLatest` on a background coroutine, and applies changes — font size, list item height, theme — to system-level UI state. This is the correct pattern for settings that need to propagate globally without manual callbacks or event buses.

**Service Locator pattern**

`AppServiceLocator` provides dependencies on demand without a framework. This is worth studying not because it is always the right choice — Hilt is often better — but because understanding what a DI framework does under the hood requires having written something like a Service Locator first. It is also a legitimate choice in applications where build time is a priority.

**Runtime permission handling**

`PermissionHandler` centralizes all permission logic away from `MainActivity`. The permission launcher uses `ActivityResultContracts.RequestMultiplePermissions()`, which is the modern, non-deprecated approach. The commented-out `MANAGE_EXTERNAL_STORAGE` and `READ_MEDIA_*` permissions are also instructive — they show the evolution of Android's storage permission model across API levels.

**CursorWindow resizing**

The `resizeCursorWindow()` call in `MainActivity` uses reflection to expand the SQLite cursor window to 100MB. This is an advanced, somewhat obscure Android technique. It implies that the application's data layer can produce large query results. Any engineer who plans to work on the database layer should understand this before writing queries, as the default 2MB window will cause `CursorWindowAllocationException` if exceeded.

### Concepts Demonstrated — Orchestrator Module

**Standalone Android library module**

The Orchestrator is a proper Gradle library module with its own build configuration, versioning, and release cycle. It demonstrates how to extract logic that belongs to the application but not to the application's UI into a reusable, independently deployable artifact.

**Notification scheduling on modern Android**

Scheduling reliable notifications on Android is harder than it appears. Exact alarms require the `SCHEDULE_EXACT_ALARM` permission on API 31+. Notifications must be rescheduled after device reboot via a `BOOT_COMPLETED` receiver. Doze mode and App Standby buckets affect delivery timing. The Orchestrator module encapsulates all of this complexity behind a clean interface.

**Date rendering logic as a first-class concern**

Date formatting and relative time display ("Today", "Tomorrow", "3 days ago") is the kind of logic that often gets scattered across ViewModels and UI components. Centralizing it in the Orchestrator module makes it testable, reusable, and consistent across the entire application.

### Recommended Reading Path

For an engineer approaching this codebase seriously, the following order is suggested:

```
1. Read MainActivity.kt in full — understand the app's bootstrap sequence
2. Examine the navigation graph — understand how the app moves between screens
3. Read SettingViewModel — understand how preferences are modeled and observed
4. Read AppServiceLocator — understand the dependency provision strategy
5. Trace a single feature end-to-end (e.g., Home) through data > domain > presentation
6. Read the Orchestrator module — understand notification and date logic
7. Identify something missing or improvable — open an issue or a PR
```

---

## Contributing

Contributions are welcome from engineers at any level of experience. The only requirements are that contributions are thoughtful, that they respect the existing architectural decisions unless there is a clear reason to change them, and that they come with a clear explanation of intent.

### Workflow

```bash
# Fork the repository on GitHub, then:

git clone https://github.com/YOUR_USERNAME/Setwork.git
cd Setwork

git checkout -b fix/your-fix-name
# or
git checkout -b feature/your-feature-name

# Make your changes

git commit -m "Fix: clear description of the change"
# or
git commit -m "Add: clear description of the addition"

git push origin fix/your-fix-name

# Open a Pull Request on GitHub
```

### Commit Message Convention

```
Fix: correct notification not firing after device reboot
Add: subtask support with parent-child task relationship
Refactor: extract date formatting to a dedicated utility class
Docs: update architecture section of README
Test: add unit tests for TaskRepository
```

### What Makes a Good Contribution

A good contribution does one thing well. It does not mix refactoring with new features. It does not introduce new dependencies without justification. It includes a clear description in the PR of what the change does, why it is needed, and how it was tested. It respects the existing module boundaries — changes to notification logic belong in the Orchestrator, not scattered through the main app.

### Types of Contributions Needed

**Defect reports and fixes.** If the application behaves incorrectly, file an issue with steps to reproduce, the expected behavior, the actual behavior, your Android version, and your device model. Logcat output is always helpful.

**Feature development.** New features should be proposed as an issue before implementation begins. This avoids wasted effort on changes that do not align with the project direction.

**Test coverage.** The project would benefit significantly from expanded unit test coverage, particularly for use cases in the `domain` layer and for the Orchestrator's scheduling logic.

**Documentation.** Inline documentation of non-obvious code, architecture decision records (ADRs), and improvements to this README are all valued.

**Translations.** The application is not yet internationalized, but contributions toward i18n infrastructure and language files are welcome.

**Accessibility.** Content descriptions, TalkBack support, and minimum touch target sizing are areas where contributions would have meaningful user impact.

### Code Standards

- Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Match the feature-module Clean Architecture pattern in the existing codebase
- Functions should be small and have a single responsibility
- Public APIs — even internal module APIs — should be documented
- Do not introduce third-party dependencies without opening an issue for discussion first

---

## Roadmap

The following items are planned or under consideration. None of these are guaranteed to ship in any particular order. Contributions toward any of them are welcome.

| Item | Notes |
|---|---|
| Home screen widget | Read-only task overview |
| Recurring tasks | Daily, weekly, monthly recurrence rules |
| Subtasks | Parent-child task hierarchy |
| Task priority levels | High / medium / low with visual differentiation |
| Search and filter | Full-text search across tasks and notes |
| Productivity metrics | Completion rates, streaks, historical view |
| Internationalization | i18n infrastructure + initial language files |
| Test coverage | Unit and UI test suite |
| Optional cloud sync | User-controlled, no default data collection |
| Tablet and foldable layouts | Adaptive UI for large screens |
| F-Droid listing | Pending |
| Amazon Appstore listing | Pending |

---

## Support the Project

Setwork is free and open source. It is maintained in spare time. If the project has been useful to you — as a user, a learner, or a reference — the following forms of support are appreciated.

**Star the repository.** It costs nothing and directly helps other engineers discover the project.

**Share it.** In developer communities, study groups, or with colleagues who might find it useful.

**Contribute.** See the Contributing section above. Code, tests, documentation, and translations are all valued.

**Donate via Bitcoin.** If you would like to support the project financially:

[![Bitcoin](https://img.shields.io/badge/Bitcoin-Donate-F7931A?style=flat-square&logo=bitcoin&logoColor=white)](#)

```
BTC Address: [Your Bitcoin Address Here]
```

Donations go directly toward development time and tooling costs.

---

## About the Author

Built and maintained by **Faraz Sheikh**, a software developer based in India.

[![GitHub](https://img.shields.io/badge/GitHub-itsfaraz-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/itsfaraz)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-itsfrz-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://linkedin.com/in/itsfrz)
[![Portfolio](https://img.shields.io/badge/Portfolio-setwork.web.app-333333?style=flat-square)](https://setwork.web.app)

---

## License

```
MIT License

Copyright (c) 2026 Faraz Sheikh

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,100:161b22&height=80&section=footer" width="100%"/>

Maintained by [Faraz Sheikh](https://github.com/itsfaraz) &nbsp;·&nbsp; MIT License &nbsp;·&nbsp; Made in India

![Visitors](https://visitor-badge.laobi.icu/badge?page_id=itsfaraz.Setwork&style=flat-square)

</div>
