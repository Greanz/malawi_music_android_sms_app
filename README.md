# malipo_app

"A mobile payment / subscriptions application."

## Getting Started

- to install packages: `flutter pub get`
- to ensure logo link up: `flutter pub run flutter_launcher_icons -f flutter_launcher_icons.yaml`
- then `flutter pub run flutter_launcher_icons`
- to run on android: `flutter run or add [-d #device_id]`
- to get #device_id: `flutter devices`
- to build an apk: `flutter build apk`
- find apk in this path: `build\app\outputs\flutter-apk\app-release.apk`

## About Project

- The following project was developed on with this flutter and dart versions:

Flutter 3.19.4 
------------------------------------------------------------

channel stable • https://github.com/flutter/flutter.git

Framework • revision 68bfaea224 (4 months ago) • 2024-03-20 15:36:31 -0700

Engine • revision a5c24f538d

Tools • Dart 3.3.2 • DevTools 2.31.1

Gradle 8.2.1
------------------------------------------------------------

Build time:   2023-07-10 12:12:35 UTC

Revision:     a38ec64d3c4612da9083cc506a1ccb212afeecaa

Kotlin:       1.8.20

Groovy:       3.0.17

Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023

JVM:          17.0.9 (Amazon.com Inc. 17.0.9+8-LTS)

OS:           Windows 11 10.0 amd64
****

- To ensure smooth transition either remove the source code files 
  and assets and move them into a new project created with your
  desired version or install the exact version above.

## Main Source File Structure

- pubspec.yaml
- readme.md
- flutter_launcher_icons
- assets/
- - icons/ [malipo logos]
- - pngs/ [ad iamges]
- - svgs/ [logos in svg format]
- lib/
- - main.dart
- - constants/
- - controllers/
- - data/
- - models/
- - providers/
- - utils/
- - views/
- - - screen/ [home/, notifications/, profile/, receipts/, registration/, services_page/, settings/, shop/, wallet/]
- - - widgets/ [inputs/, ...]

## Conclusion

- With these details the client will be able to run and build the app for whatever platform desired.