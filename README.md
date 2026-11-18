# Powerhold

**Powerhold** is a modern Android application written entirely in Kotlin using Jetpack Compose and **Material 3 Expressive**. It allows you to replace your default digital assistant (Google Assistant) trigger gesture (power button long-press on newer phones, 3-button home hold, or gesture navigation corner swipe) with any remappable action of your choice—without opening the full app UI.

Clicking the app icon opens the full configuration dashboard, while triggering the assistant executes your chosen action instantaneously in the background with zero lag.

---

## 🚀 Key Features

1. **Dual-Trigger Android Architecture**:
   - **Assistant Trigger (`ACTION_ASSIST` + `VoiceInteractionService`)**: Executed when triggering assistant gestures (holding power button, holding home button, or diagonal corner swipe). Uses a translucent, zero-animation activity and a voice interaction session to run the remapped action immediately and dismiss instantly.
   - **Launcher Icon (`ACTION_MAIN` + `CATEGORY_LAUNCHER`)**: Opens the Material 3 Expressive configuration dashboard.

2. **Remappable Actions (Sorted Alphabetically)**:
   - **Quick Toggles & Settings**:
     - *Airplane Mode* (Quick panel / Settings)
     - *Auto-Rotate Toggle* (100% non-root toggle via `WRITE_SETTINGS`)
     - *Battery Saver Toggle* (Direct toggle via ADB or system battery panel)
     - *Bluetooth Quick Panel* (Native floating control panel)
     - *Camera Access* (Sensor Privacy dashboard)
     - *Color Inversion Toggle* (Direct toggle via ADB or Accessibility)
     - *Data Saver Settings* (Quick settings)
     - *Do Not Disturb (DND) Toggle* (100% non-root toggle via Notification Policy)
     - *Extra Dim Toggle* (Direct toggle via ADB or Display settings)
     - *Eye Protection / Night Light* (Direct toggle via ADB or Night Light settings)
     - *Flashlight Toggle* (100% non-root instant torch on/off)
     - *Location Toggle / Panel* (Direct settings / panel)
     - *Microphone Access* (Sensor Privacy dashboard)
     - *Mobile Data Panel* (Native floating cellular panel)
     - *Sound Mode Toggle* (100% non-root cycling: Normal ➔ Vibrate ➔ Silent)
     - *Wi-Fi Panel* (Native floating Internet panel)
   - **Open Installed Apps**:
     - Complete list of installed launcher apps, sorted strictly in alphabetical order.
   - **Launch App Activities**:
     - Comprehensive, searchable list of all individual activities from all installed applications on the device (e.g. `com.google.android.youtube/.UploadActivity`, `com.whatsapp/.ConversationActivity`, etc.), sorted alphabetically by display title.

3. **Material 3 Expressive UI & Dynamic Theming**:
   - Clean Material Design power button adaptive icon with dynamic Material 3 tonal color theming and monochrome themed icon layer.
   - Follows your device's default system theme (dynamic Monet theming on Android 12+).
   - Prominent current trigger status card with instant **Test Action** button.
   - Expressive rounded cards, tonal badges, and fluid search filters.

4. **Non-Root & ADB Support**:
   - Out-of-the-box non-root direct toggles: Flashlight, Sound Mode, Auto-Rotate, Do Not Disturb, App launching, and Activity launching.
   - For direct background toggling of protected settings (Battery Saver, Extra Dim, Night Light, Color Inversion), a one-time ADB permission command is provided in the app:
     ```bash
     adb shell pm grant com.powerhold.app android.permission.WRITE_SECURE_SETTINGS
     ```

---

## 🛠️ Setup as Default Assistant

1. Open Android **Settings** ➔ **Apps** ➔ **Default apps** ➔ **Digital assistant app**.
2. Select **Powerhold**.
3. Now trigger your assistant (hold the Power button, swipe up diagonally from the bottom corner, or long-press Home).
4. Your remapped action will execute instantaneously!

---

## 📂 Project Structure

```
Powerhold/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/powerhold/app/
│   │   │   ├── PowerholdApplication.kt
│   │   │   ├── assistant/
│   │   │   │   ├── ActionExecutor.kt
│   │   │   │   ├── AssistActivity.kt
│   │   │   │   ├── AppVoiceInteractionService.kt
│   │   │   │   ├── AppVoiceInteractionSessionService.kt
│   │   │   │   └── AppVoiceInteractionSession.kt
│   │   │   ├── data/
│   │   │   │   ├── ActionType.kt
│   │   │   │   ├── RemapAction.kt
│   │   │   │   ├── PreferencesManager.kt
│   │   │   │   ├── QuickToggleRepository.kt
│   │   │   │   └── AppListRepository.kt
│   │   │   └── ui/
│   │   │       ├── MainActivity.kt
│   │   │       ├── MainViewModel.kt
│   │   │       ├── screens/
│   │   │       │   ├── MainDashboardScreen.kt
│   │   │       │   ├── QuickTogglesTab.kt
│   │   │       │   ├── InstalledAppsTab.kt
│   │   │       │   ├── AppActivitiesTab.kt
│   │   │       │   └── AdbHelperDialog.kt
│   │   │       └── theme/
│   │   │           ├── Color.kt
│   │   │           ├── Theme.kt
│   │   │           └── Type.kt
│   │   └── res/
│   │       ├── drawable/
│   │       │   ├── ic_launcher_background.xml
│   │       │   ├── ic_launcher_foreground.xml
│   │       │   └── ic_launcher_monochrome.xml
│   │       └── ...
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```
