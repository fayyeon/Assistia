<div align="center">
  
# **Powerhold**

Simple assistant shortcut remapper for Android. Replace the Google Assistant / Gemini shortcut on your phone with a shortcut to another functionality. No root required.
</div>

---

## Key Features
- 🍥 Modern Material 3 Expressive design
- 🫴 Easy to use UI with one-hand reachability principles
- 🖼️ Optimized on Landscape Mode

---

## Remap Functionalities

- 🔧 Quick Panel Toggles & System Actions
- 🌐 Websites Directly Opened on Your Default Browser
- 📱 Installed Applications
- 🏃🏻 App Activities

---

## Optional ADB Setup (For Direct Background Toggles)

Certain privileged toggles (Airplane Mode, Battery Saver, GPS Location, NFC, Extra Dim, Night Light, etc.) are restricted by Android from being toggled silently in the background by 3rd-party apps.

Powerhold handles this gracefully:
- **Without ADB**: Remapper displays the native floating panel or settings panel (e.g. `Settings.Panel.ACTION_INTERNET_CONNECTIVITY`), allowing a 1-tap flip without.
- **With ADB (Optional)**: If you run this single command once from a PC or on-device wireless debugging tool (such as Shizuku), Remapper gains permission to toggle them silently in the background:

```bash
adb shell pm grant com.powerhold android.permission.WRITE_SECURE_SETTINGS
```

Use ADB tools on your PC or you can use Shizuku + Canta on Android.

---

## License
Licensed under the [GNU General Public License v3.0](LICENSE).
