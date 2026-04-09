# Embiggen It!

A simple, no-nonsense large-text display app for Android. Type your message, hit the button, and your text fills the screen in big, readable letters. Perfect for communicating across a room, showing text to someone with low vision, or just displaying information clearly.

No ads. No history. No bloat.

---

## Features

- **Instant input**: Keyboard opens automatically on launch, ready to type
- **Fullscreen display**: 48sp text, wraps and scrolls, never shrinks
- **Full brightness**: Screen goes to max brightness in fullscreen mode, automatically restored on exit
- **Free rotation**: Fullscreen view rotates freely regardless of system orientation lock
- **Light & dark modes**: Clean black-on-white and white-on-dark-grey themes, manually selectable or system-defaulted
- **Tap to dismiss**: Tap anywhere in fullscreen to return to the input screen

## Usage

1. Type your message in the text field
2. Tap **Embiggen It!** to display it fullscreen
3. Tap anywhere on the fullscreen view to go back
4. Use **✕** to clear the text field
5. Use **⚙** to switch between Light, Dark, and System Default color modes

## Building

Requires Android SDK (minSdk 26, targetSdk 34) and JDK 8+.

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

> **Note:** `gradle/wrapper/gradle-wrapper.jar` is not included in the repository.
> Run `gradle wrapper --gradle-version 8.4` once to generate it, or let Android Studio handle it on first sync.
