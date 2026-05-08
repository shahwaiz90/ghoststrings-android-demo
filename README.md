# GhostStrings Android Showcase 👻🤖

This project demonstrates the powerful "Zero-Code" integration of the GhostStrings SDK on Android.

## 🚀 Features
- **Zero-Code Change**: Uses native `stringResource(R.string...)` in Compose and `context.getString()` in Views.
- **Jetpack Compose Support**: Seamless integration for reactive UI updates.
- **Automatic Refresh**: UI updates instantly when cloud strings are fetched.

## 🛠️ How to Run

1. Open this repository in **Android Studio**.
2. Wait for Gradle to sync. It will automatically download the GhostStrings SDK from Maven Central (`ai.ghoststrings:android-sdk:1.0.0`).
3. Connect an Android device or start an emulator.
4. Hit **Run** (Shift + F10).

## 🧪 Testing the OTA Effect

1. Open the app on your emulator/device.
2. Go to your [GhostStrings Dashboard](https://ghoststrings.ai).
3. Select your project and edit any string value.
4. Click Save.
5. Re-launch the app (or trigger a sync) and watch the text update instantly without deploying a new APK! 🪄

## 📦 Integration Details
This demo application relies on the official Maven Central release of GhostStrings.
You can view the dependency in `app/build.gradle.kts`.
