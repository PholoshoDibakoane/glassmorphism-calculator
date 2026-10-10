# Glassmorphism Calculator

A responsive glassmorphism calculator available as a web app, with separate native Android and iOS source projects.

## Web app

Open `index.html` in a modern browser. The web version uses plain HTML, CSS, and JavaScript and has no build dependencies.

## Native apps

Download the native source projects from [`native-apps/`](./tree/main/native-apps), where each platform is provided as a separate archive with its project folder structure preserved.

- Android: download `android-native-project.zip`, extract it, then open the extracted folder in Android Studio. Requires JDK 17 and Android SDK 35 or newer; minimum Android version is API 24.
- iOS: download `ios-native-project.zip`, extract it on a Mac, then open `GlassCalculator.xcodeproj` in Xcode 16 or later. Minimum iOS version is iOS 17.

Native archives contain source projects, not installable APK or App Store packages. iOS builds require macOS and Xcode.

Both native apps support arithmetic precedence, negative numbers, decimals, clear/delete, and invalid-result handling.
