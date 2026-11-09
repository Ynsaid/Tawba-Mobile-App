# Tawba Mobile App

**Tawba Mobile App** is a native Android application built with **Kotlin** and **Jetpack Compose**.  
It helps users access daily Islamic prayer times through a clean, modern, and easy-to-use interface.

The app fetches prayer times from the **Aladhan Prayer Times API** and displays them in a simple and responsive Compose-based UI.

## Overview

Tawba is designed to provide a practical and spiritually useful mobile experience for Android users.  
It focuses on displaying accurate daily prayer times based on the user’s selected city and country, while maintaining a clean and intuitive interface.

The application follows the **MVVM architecture** and uses **Kotlin Coroutines** and **StateFlow** to handle asynchronous data loading and reactive UI updates.

## Features

- Native Android application built with Kotlin
- Modern UI powered by Jetpack Compose
- Fetches daily prayer times from the Aladhan API
- Displays Fajr, Dhuhr, Asr, Maghrib, and Isha prayer times
- Supports city and country-based prayer time lookup
- Uses Kotlin Coroutines for asynchronous network requests
- Exposes prayer-time data through StateFlow for reactive UI updates
- Clean MVVM structure separating UI, ViewModel, and data logic

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI framework | Jetpack Compose |
| Architecture | MVVM |
| State management | Kotlin Coroutines + StateFlow |
| Networking | HttpURLConnection |
| Prayer times API | [Aladhan API](https://aladhan.com/prayer-times-api) |
| JSON parsing | org.json |
| Build system | Gradle |

## Screenshots

Add screenshots of the app here.

```text
<img src="screenshots/home.png" width="300" />
```

## Prayer Times API

The app uses the [Aladhan Prayer Times API](https://aladhan.com/prayer-times-api) to retrieve prayer times based on a selected city and country.

Example API request:

```http
GET [https://api.aladhan.com/v1/timingsByCity?city=Algiers&country=DZ&method=2](https://api.aladhan.com/v1/timingsByCity?city=Algiers&country=DZ&method=2)
```

The app currently retrieves and displays the following prayer times:

- Fajr
- Dhuhr
- Asr
- Maghrib
- Isha

## How It Works

1. The user opens the app.
2. The `PrayerViewModel` sends a request to the Aladhan API.
3. The API returns prayer times in JSON format.
4. The ViewModel parses the response and creates a `PrayerTimes` object.
5. The prayer times are exposed through `StateFlow`.
6. The Jetpack Compose UI observes the state and displays the updated prayer times.

## Requirements

- Android Studio
- JDK 17 or later
- Android SDK
- Minimum Android SDK version: [e.g., API 24]
- Target Android SDK version: [e.g., API 35]
- Internet connection to fetch prayer times

## Getting Started

### Clone the repository

```bash
git clone [https://github.com/Ynsaid/Tawba-Mobile-App.git](https://github.com/Ynsaid/Tawba-Mobile-App.git)
cd Tawba-Mobile-App
```

### Open in Android Studio

1. Open Android Studio.
2. Select **File → Open**.
3. Choose the cloned `Tawba-Mobile-App` folder.
4. Wait for Gradle to sync the project.
5. Run the app on an emulator or a physical Android device.

### Run the app

Click **Run ▶** in Android Studio, or use the following command:

```bash
./gradlew installDebug
```

## Build a Release APK

```bash
./gradlew assembleRelease
```

The generated APK will be located at:

```text
app/build/outputs/apk/release/
```

## Roadmap

- [ ] Add location-based prayer time detection
- [ ] Add Qibla direction feature
- [ ] Add prayer-time notifications
- [ ] Add dark mode support
- [ ] Add offline caching for prayer times
- [ ] Improve error handling and loading states
- [ ] Add Arabic and English language support
- [ ] Add unit and UI tests
- [ ] Publish the app to Google Play

## Contributing

Contributions are welcome.  
Please open an issue first to discuss a proposed feature or improvement, then submit a pull request with a clear explanation of the changes.

## Contact

- GitHub: [@Ynsaid](https://github.com/Ynsaid)
- Email: [younesaid2003@gmail.com]
