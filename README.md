# CoMaps ❤️ Google Maps

Android app ID: `de.ii.android.comapsgooglemaps`.

Android share target for both directions:
- **Google Maps → CoMaps:** opens shared coordinates directly.
- **CoMaps → Google Maps link:** converts a shared point and immediately opens the share sheet.

## Usage

Share a place from Google Maps or CoMaps and select **CoMaps ❤️ Google Maps**.
Supports CoMaps compressed links (`comaps.at`, `comaps://`) and `geo:` points.
Generated HTTPS links open in the Google Maps app when installed, otherwise in a browser.

## Build

```bash
./gradlew test lint assembleDebug
```

Requires JDK 17 and Android SDK 35.

BSD-3-Clause; third-party components retain their licenses (see [notices](THIRD_PARTY_NOTICES.md)).

> This app was coded with GPT 6.1 Sol.
