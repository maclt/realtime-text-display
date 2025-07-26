# 🎤 Realtime Speech Display

A **voice-to-text system** that captures spoken Mandarin Chinese on Android devices, converts it to Chinese text, and displays it in real-time on a web dashboard. Perfect for **family use** with secure Firebase backend integration.

## 📱 Features

### Android App
- 🎙️ **Real-time Mandarin speech recognition** (zh-CN)
- 📝 **Chinese text display** with timestamps
- ☁️ **Firebase Firestore integration** for data storage
- 🔐 **Anonymous authentication** for secure access
- 📲 **Offline recording** with online sync
- 👨‍👩‍👧‍👦 **Family-friendly APK distribution**

### Web Dashboard
- 🌐 **Real-time text display** from all family devices
- 🕒 **Chronological speech history** with timestamps
- 🌙 **Beautiful dark theme** interface
- 📱 **Responsive design** for desktop and mobile
- 🔄 **Live updates** without page refresh
- 🌐 **Localhost deployment** for family network access

## 🏗️ Architecture

```
📱 Android App (Kotlin)
   ↓ Mandarin Speech
🧠 Speech Recognition API
   ↓ Chinese Text + Timestamp
☁️ Firebase Firestore
   ↓ Real-time Sync
🌐 Web Dashboard (React + TypeScript)
```

## 🚀 Quick Start

### Prerequisites
- **Android Studio** with SDK 21+ (for Android app)
- **Node.js 18+** (for web dashboard)
- **Firebase project** with Firestore and Authentication enabled
- **Java 24** (or compatible version)

### 1. Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable **Firestore Database** in test mode
3. Enable **Anonymous Authentication**:
   - Go to Authentication > Sign-in method
   - Enable "Anonymous" provider
4. Download `google-services.json` for Android
5. Get web configuration from Project Settings

### 2. Android App Setup

```bash
cd android-app

# Add your google-services.json to app/ directory
cp /path/to/your/google-services.json app/

# Build the app
./gradlew build

# Install on device/emulator
./gradlew installDebug
```

**Distribution to Family:**
```bash
# Build release APK
./gradlew assembleDebug

# APK location for sharing
# android-app/app/build/outputs/apk/debug/app-debug.apk
```

### 3. Web Dashboard Setup

```bash
cd web-dashboard

# Install dependencies
npm install

# Create environment file
cp .env.example .env

# Edit .env with your Firebase config
nano .env
```

**Configure `.env`:**
```env
VITE_FIREBASE_API_KEY=your_api_key_here
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.firebasestorage.app
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_web_app_id
VITE_FIREBASE_MEASUREMENT_ID=your_measurement_id
```

**Start development server:**
```bash
npm run dev
# Open http://localhost:5173
```

## 🔐 Security Features

### Authentication
- **Anonymous Authentication** - No passwords required for family use
- **Device Registration** - Each device gets unique identifier
- **Secure Database Access** - Only authenticated devices can read/write

### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Speech entries - authenticated users only
    match /speechEntries/{document} {
      allow read, write: if request.auth != null;
    }
    
    // Family device registration
    match /familyDevices/{document} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 📊 Data Structure

### Speech Entries Collection (`speechEntries`)
```json
{
  "text": "你好世界",
  "timestamp": "2025-01-26T12:34:56Z",
  "language": "zh-CN"
}
```

### Family Devices Collection (`familyDevices`)
```json
{
  "deviceId": "anonymous_user_id",
  "deviceModel": "Pixel 6a",
  "timestamp": "2025-01-26T12:34:56Z",
  "approved": true
}
```

## 🛠️ Development

### Android App Structure
```
android-app/
├── app/
│   ├── src/main/kotlin/net/taromurakami/realtimespeechdisplay/
│   │   └── MainActivity.kt
│   ├── src/main/res/
│   │   ├── layout/activity_main.xml
│   │   └── values/strings.xml
│   ├── build.gradle.kts
│   └── google-services.json
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

### Web Dashboard Structure
```
web-dashboard/
├── src/
│   ├── App.tsx
│   ├── firebase.ts
│   └── types/SpeechEntry.ts
├── package.json
├── vite.config.ts
└── .env
```

### Building for Production

**Android APK:**
```bash
cd android-app
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/
```

**Web Dashboard:**
```bash
cd web-dashboard
npm run build
# Output: dist/ directory
```

## 🐛 Troubleshooting

### Android App Issues

**APK Installation Failed:**
- Use debug APK instead of unsigned release APK
- Enable "Install unknown apps" in Android settings
- Try USB installation via Android Studio

**Speech Recognition Not Working:**
- Check microphone permission is granted
- Ensure device has internet connection
- Verify Google Speech Recognition is available

**Authentication Failed:**
- Enable Anonymous Authentication in Firebase Console
- Check `google-services.json` is in correct location
- Verify package name matches Firebase configuration

### Web Dashboard Issues

**400 Bad Request from Firebase:**
- Check `.env` file has correct Firebase configuration
- Ensure web app is added to Firebase project
- Verify Firestore security rules allow access

**Real-time Updates Not Working:**
- Check Firebase authentication is successful
- Verify network connection
- Check browser console for errors

### Common Firebase Issues

**Permission Denied:**
- Update Firestore security rules
- Ensure Anonymous Authentication is enabled
- Check user is properly authenticated

**Package Name Mismatch:**
- Android: Update `applicationId` in `build.gradle.kts`
- Ensure it matches Firebase project configuration

## 📞 Family Usage

### Installation for Family Members

1. **Share the APK** via cloud storage, AirDrop, or USB
2. **Install on Android devices** (enable unknown sources)
3. **Grant microphone permission** when prompted
4. **Start recording** Mandarin speech
5. **View on web dashboard** at your local network address

### Usage Tips

- **Speak clearly** in Mandarin for best recognition
- **Use in quiet environment** for accuracy
- **Keep app open** during recording sessions
- **Check web dashboard** for real-time text display

## 🔧 Configuration

### Changing Languages
To support other languages, modify the Android app:

```kotlin
// In MainActivity.kt
recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // For English
recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP") // For Japanese
```

### Customizing UI
- **Android**: Modify XML layouts in `res/layout/`
- **Web**: Update styles in `App.tsx` and theme configuration

## 📄 License

This project is for **private family use**. Not intended for commercial distribution.

## 🤝 Contributing

This is a family project. For issues or improvements:
1. Test thoroughly on family devices
2. Ensure security and privacy are maintained
3. Keep the interface simple for all family members

---

## 📈 Version History

- **v1.0** - Initial release with basic speech recognition

---

**Built with ❤️ for family communication and accessibility**