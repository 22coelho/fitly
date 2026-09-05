# Fitly

Android wardrobe app: photograph each item of clothing, tag it with four labels, and the app
randomly suggests an outfit from what you have.

- **Android-only**, no KMP, no iOS.
- **100% offline** — zero network calls.
- **No machine learning** — the generator is pure randomness within the chosen filters; each
  item's dominant colour comes from on-device image analysis (Palette), not ML.

For a full description of the screens and what exists today, see [docs/PRODUCT.md](docs/PRODUCT.md).
For the domain vocabulary, see [CONTEXT.md](CONTEXT.md). For the reasoning behind decisions, see
[docs/adr/](docs/adr/).

## Stack

Kotlin + Jetpack Compose, Material 3, Room, Koin, Coil. Single-module MVI architecture
(`domain/` → `data/` → `presentation/` → `di/`), documented in [CLAUDE.md](CLAUDE.md).

## Running the project

```bash
./gradlew :app:assembleDebug        # build the debug APK
./gradlew :app:testDebugUnitTest    # run the unit test suite
```

Requires the `compileSdk` and AGP versions pinned in
[gradle/libs.versions.toml](gradle/libs.versions.toml) — see
[ADR 0006](docs/adr/0006-pin-compose-stack-to-compilesdk-36.md) before bumping any of them.
