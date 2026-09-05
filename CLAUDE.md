# FITLY

Android wardrobe app: photograph your clothes, tag them, and get a random outfit suggestion.
Offline-only, no network calls, no ML.

- `CONTEXT.md` — domain glossary. Read it before naming anything; it also lists terms to avoid.
- `docs/PRODUCT.md` — what the product is, the features and screens that exist today, and the
  known gaps. Read it before adding or changing user-facing behaviour.
- `docs/adr/` — decisions and their rationale. Read the relevant one before changing that area.

## Commands

```bash
./gradlew :app:testDebugUnitTest    # full unit suite
./gradlew :app:assembleDebug        # build the APK
```

Test results land in `app/build/test-results/testDebugUnitTest/*.xml` — Gradle prints
`BUILD SUCCESSFUL` without a test count, so check the XML when you need to confirm what ran.

## Dependency ceiling — do not bump blindly

Every version below was reached by hitting a build failure. Changing one usually breaks the build.
They are one interlocked block — the rationale and the condition for revisiting them all together
is in `docs/adr/0006-pin-compose-stack-to-compilesdk-36.md`.

- **Never apply `org.jetbrains.kotlin.android`.** AGP 9.0.1 has built-in Kotlin support; adding the
  plugin fails the build. There is no `kotlinOptions {}` block for the same reason.
- **compileSdk is 36.1, and that caps Compose.** Compose BOM stays at `2025.09.00`; newer BOMs
  demand compileSdk 37 + AGP 9.1. The same ceiling pins activity-compose, lifecycle and
  navigation-compose. Revisit all of these together when moving to AGP 9.1.
- **Coil is pinned to 3.0.0** — 3.6.x drags in Compose 1.12 / compileSdk 37.
- **`material-icons-extended` is deliberately absent** (same version conflict). Use the core icon
  set: `Icons.Default.*`, `Icons.AutoMirrored.Filled.*`.
- **KSP versions are bare** (`2.3.11`), not the old `<kotlin>-<ksp>` format.

## Architecture

Single module, layered by package (ADR 0004): `domain/`, `data/`, `presentation/`, `di/`.

- **`domain/` must never import `android.*` or `androidx.*`.** It is plain Kotlin, unit-testable
  without Robolectric.
- **Errors are `Result<D, E>`** (`domain/util/Result.kt`) with `DataError.Local`, consumed via
  `onSuccess`/`onFailure`. Data sources wrap their bodies in `safeLocalCall` (`data/util/`).
- **Presentation is MVI**: one `State` / `Action` / `Event` / `ViewModel` per screen, plus a
  `XRoute` composable (injects the ViewModel via `koinViewModel()`, observes events through
  `ObserveAsEvents`) wrapping a stateless `XScreen` composable that takes `state` and `onAction`.
- **Every ViewModel is registered in `di/PresentationModule.kt`.** A missing binding is not a
  compile error — it crashes the moment the screen opens.
- **Re-entrancy guard**: an action that persists something sets its status enum to a busy value
  **synchronously, before `viewModelScope.launch`**, and returns early when the status is not
  `IDLE`. This is what blocks double-tap races; keep the ordering.
- **Shared display type**: `Outfit.resolve(itemsById)` → `ResolvedOutfit` turns slot ids into
  `ClothingItem`s. Both Home and History use it — don't reintroduce per-screen copies.

### Navigation trap

A nested graph's identity route must be a **different type** from its own start destination, or
`navigation<X>(startDestination = X)` throws at runtime and the app crashes on every launch. This
is why `WardrobeGraphRoute` exists as a marker separate from `WardrobeRoute`.

### Compose notes

- Do **not** `import androidx.compose.foundation.layout.weight`. It resolves through the
  `RowScope`/`ColumnScope` receiver; the explicit import fails to compile.
- `FilterRow` takes `showAllOption`. Keep it `true` for real filters (Wardrobe, Home's occasion
  picker) where "any value" is a genuine state; pass `false` on forms where the field is required.

## Testing

JUnit 5 (`de.mannodermaus.android-junit`), AssertK, Turbine. Robolectric-backed Room tests still
use JUnit4, bridged by the vintage engine.

- **Build ViewModels inside each `@Test`, never as a class property.** Property initialisers run
  before `MainDispatcherExtension` sets `Dispatchers.Main`. Register the extension as
  `companion object { @JvmField @RegisterExtension val ... = MainDispatcherExtension() }`.
- **Await both emissions.** An action that flips status synchronously and then completes
  asynchronously produces *two* state emissions. Turbine's next `awaitItem()` returns the
  intermediate busy state, not the final one — assert on it, then await again. Skipping this looks
  exactly like a bug in the ViewModel and is the single easiest way to lose an hour here.
- **Fakes live in `test/kotlin/com/fitly/fakes/`** and expose `*Error` fields to force failures and
  `*Gate: CompletableDeferred?` fields to suspend a call mid-flight (used for re-entrancy tests).
- **Robolectric test classes need `@Config(application = Application::class)`**, otherwise Koin
  throws `KoinApplicationAlreadyStartedException` across classes.
- Shared fixtures: `testutil/ClothingItemFixtures.kt` (`testClothingItem()`).

## Working agreements

- **No time or duration estimates.** Not in plans, not in summaries.
- **TDD**: confirm the seams with the user before writing any test, then one seam → one test → one
  implementation per cycle. Red before green.
- **UI work is not done until it has been run and visually verified in the emulator.** Unit tests
  passing is not sufficient — the navigation crash above passed both the test suite and a code
  review before the emulator caught it.
- **Commits are fine; never push.** Do not add a `Co-Authored-By` trailer to commits in this repo.
- **CameraX / in-app capture is deferred** and out of scope. Photos come from the Android Photo
  Picker. Do not start it without being asked.
- UI is intentionally minimal for now — enough to prove the layers beneath it work.
