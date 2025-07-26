# realtime text display development plan

## App Overview

This project is a **voice-to-text system** that captures spoken Mandarin Chinese on an Android device, converts it to Chinese text, and then stores that text as **time-series data** in a Firebase backend. A connected **web dashboard** displays this data in real time.

## Features

### Android App

* Records audio using the phone’s microphone.
* Performs **speech-to-text conversion** in Mandarin Chinese.
* Sends the recognized Chinese text along with a timestamp to Firebase Firestore.
* Works offline for recording and pushes data online when connected.

### Firebase Backend

* Stores Chinese text entries with their timestamps in Firestore.
* Provides real-time updates to connected clients (web dashboard).
* Optionally supports user authentication for secure access.

### Web Dashboard

* Fetches and displays the Chinese text entries in **chronological order**.
* Updates instantly when new speech entries are added by the Android app.
* Allows users to view and monitor speech data in a clean interface.
* Can be hosted with Firebase Hosting for easy access from any browser.

### Why this app is useful

* Allows you to **capture spoken Mandarin conversations** and see them in written form.
* Provides a **time-ordered history** of speech entries for review or sharing.
* The real-time web view is ideal for family members or collaborators to monitor incoming data live.

## Environment 
### Android App
* openjdk 24.0.2 2025-07-15
* Android SDK 14, 15, 16

## Overall Roadmap

| **Phase**          | **Step**                                                                                  | **Tools/Tech**                        | **Output**                                             |
| ------------------ | ----------------------------------------------------------------------------------------- | ------------------------------------- | ------------------------------------------------------ |
| **1. Setup**       | 1. Create Firebase project in [Firebase Console](https://console.firebase.google.com).    | Firebase                              | Firebase backend ready with Firestore DB and Hosting.  |
|                    | 2. Enable Firestore Database & Authentication (optional).                                 | Firebase                              | Database ready to store time-series speech entries.    |
|                    | 3. Download `google-services.json` and prepare Firebase config for Android & Web.         | Firebase                              | Configuration files added to Android and Web projects. |
| **2. Android App** | 4. Initialize Android Studio project (Kotlin).                                            | Android Studio, Kotlin                | Base Android app ready.                                |
|                    | 5. Add Firebase SDK dependencies (Firestore, Analytics).                                  | Firebase Android SDK                  | Android app connected to Firebase.                     |
|                    | 6. Implement speech recognition (Mandarin) using `SpeechRecognizer`.                      | Android SpeechRecognizer API          | App converts Mandarin speech to Chinese text (汉字).     |
|                    | 7. Send recognized text + timestamp to Firestore.                                         | Firebase Firestore                    | Speech entries stored in DB in real time.              |
| **3. Web App**     | 8. Initialize React.js project.                                                           | React.js                              | Base web app created.                                  |
|                    | 9. Add Firebase SDK for Web and connect to Firestore.                                     | Firebase Web SDK                      | Web app connected to Firebase DB.                      |
|                    | 10. Implement real-time listener for `speechEntries` collection in Firestore.             | Firestore `onSnapshot()`              | Web app updates live when new entries arrive.          |
|                    | 11. Display Chinese text entries in chronological order with timestamps.                  | React.js + Firestore                  | Clean time-series view of speech entries.              |
| **4. Deployment**  | 12. Deploy Web App to Firebase Hosting.                                                   | Firebase Hosting                      | Live web dashboard accessible online.                  |
|                    | 13. Build and export Android app as APK.                                                  | Android Studio                        | APK file ready for sideloading or distribution.        |
| **5. Optional**    | 14. Add Authentication to restrict access (e.g., only you/family can view the dashboard). | Firebase Authentication               | Secure access to web app and database.                 |
|                    | 15. Polish UI (dark mode, responsive design).                                             | Tailwind CSS / Material UI (optional) | User-friendly mobile and desktop experience.           |


## System Architecture

```
Android App (Kotlin)
 Microphone → Speech-to-Text (Chinese) → Firestore DB

Firebase Backend
 Firestore: Stores text entries with timestamps

Web App (React)
 Real-time display of Chinese text entries (updates instantly)
```

## Key Deliverables

* Android APK: Records and sends Chinese speech-to-text data.
* Web Dashboard: Shows entries in real time.
* Firebase Backend: Handles database, hosting, and optional authentication.
