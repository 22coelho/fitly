<img src="docs/assets/icon.svg" alt="Fitly app icon" width="72" height="72" align="left" />

# Fitly

Android wardrobe app: photograph each item of clothing, tag it with four labels, and the app
randomly suggests an outfit from what you have.

<br clear="left" />


- **Android-only**, no KMP, no iOS.
- **User data never leaves the device** — no photo, tag, or outfit is ever sent anywhere. One
  narrow, documented exception: ML Kit's own usage telemetry, never the photo or its result — see
  [ADR 0008](docs/adr/0008-ml-kit-type-suggestion-weak-signal.md).
- **No machine learning tailored to this app** — the outfit generator is pure randomness within
  the chosen filters; each item's dominant colour comes from on-device image analysis (Palette),
  not ML. The one exception is Type Suggestion (see [docs/PRODUCT.md](docs/PRODUCT.md)), a
  best-effort guess from a generic on-device image classifier (ML Kit), not a model trained for
  this app — see [ADR 0008](docs/adr/0008-ml-kit-type-suggestion-weak-signal.md).

For a full description of the screens and what exists today, see [docs/PRODUCT.md](docs/PRODUCT.md).
For the domain vocabulary, see [CONTEXT.md](CONTEXT.md). For the reasoning behind decisions, see
[docs/adr/](docs/adr/).

## Stack

Kotlin + Jetpack Compose, Material 3, Room, Koin, Coil, ML Kit. Single-module MVI architecture
(`domain/` → `data/` → `presentation/` → `di/`), documented in [CLAUDE.md](CLAUDE.md).

## Running the project

```bash
./gradlew :app:assembleDebug        # build the debug APK
./gradlew :app:testDebugUnitTest    # run the unit test suite
```

Requires the `compileSdk` and AGP versions pinned in
[gradle/libs.versions.toml](gradle/libs.versions.toml) — see
[ADR 0006](docs/adr/0006-pin-compose-stack-to-compilesdk-36.md) before bumping any of them.
