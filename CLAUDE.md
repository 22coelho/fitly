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

Do **not** `import androidx.compose.foundation.layout.weight`. It resolves through the
`RowScope`/`ColumnScope` receiver; the explicit import fails to compile.

## Design system

Every screen builds from `presentation/designsystem/`, never from Material3 directly, and this is
enforced by `DesignSystemBoundaryTest` (scans every `*Screen.kt` for a direct
`androidx.compose.material3.*` import) — the only imports it lets through are `MaterialTheme`,
`Text`, `Icon`, and `SnackbarHostState`, because none of those carry a shape, elevation, or colour
role of their own. Wrap anything else, don't import it into a screen.

- **The palette is generated, not hand-picked.** Six tonal palettes derived from a terracotta seed
  (`Color.kt`), with two deliberate departures from the standard Material 3 tone mapping — both
  measured against WCAG contrast, not eyeballed. Regenerate from the seed rather than hand-editing
  a value; the reasoning and the departures are in
  `docs/adr/0007-terracotta-palette-over-classic-material3.md`.
- **Material 3 Expressive is not available**, and not just because of the ADR 0006 ceiling —
  `material3:1.4.0` resolves fine without needing compileSdk 37, but `MaterialExpressiveTheme` and
  `MotionScheme` are `internal` in that version, and forcing it drops the transitive
  `material-icons-core` dependency and breaks every `Icons.Default.*`. Stayed on classic
  `MaterialTheme`; see ADR 0007 before trying again.
- **A domain enum becomes user-facing text in exactly one place**: `presentation/Labels.kt`
  (`XEnum.labelRes: Int`, resolved via `stringResource`). `domain/` never sees a string resource;
  the enum itself never knows a UI exists. `LabelsTest` guards against two values silently sharing
  one resource (a copy-paste that would make every dress read as a shirt).
- **`FilterRow` vs `FitlySegmentedRow`**: a required field with 3–4 values (Season, Condition) is a
  `FitlySegmentedRow` — segmented reads as "pick exactly one", which a chip row does not. A
  required field with 5 values (Type, Occasion) is a `FilterRow` with `showAllOption = false` — a
  segmented row that wide gets unreadable on a phone. An actual filter (Wardrobe, Home's occasion
  picker), where "any value" is a genuine state, is a `FilterRow` with `showAllOption = true`
  (the default).
- **`ClothingItemForm`** (`presentation/wardrobe/`) is the one form both Add item and Item detail
  render — the same four fields over a blank item or an existing one. Don't fork it per screen.
- **`ClothingPhoto`** paints its own letterbox from the item's extracted dominant colour
  (`PhotoFit.Contain`, used wherever the photo doesn't fill its bounds) rather than leaving it
  blank or default-grey — this is also what a photo-less `@Preview` falls back to, so previews
  don't need real image files. `PhotoFit.Cover` (grids) skips the backdrop and crops instead.
  `contentColorOn(background)` picks black-or-white ink by the WCAG formula for anything drawn on
  top of a colour that can't be known ahead of time (the favourite heart over a garment photo);
  `ContentColorTest` pins the one silent failure mode — a pale garment giving pale-on-pale ink.
- **Previews use `preview/PreviewData.kt`**, kept in `main` (not `test`) because previews compile
  against the main source set. Sample items carry no real photo path on purpose, to exercise the
  dominant-colour fallback above.
- **The large title on each tab** (`FitlyLargeTopAppBar`) collapses on scroll via
  `rememberFitlyScrollBehavior()` + `Modifier.collapsingTopBar(behavior)` — both are needed, on the
  app bar and the scrolling content respectively, or it won't collapse. `FitlyScrollBehavior` wraps
  the experimental Material type so a screen never has to opt into it itself.

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
- A few tests check the codebase itself rather than behaviour, so a rule in this file doesn't
  quietly stop being true: `DesignSystemBoundaryTest` (see Design system, above), `LabelsTest`
  (every enum value maps to a distinct string resource), `ContentColorTest` (the contrast picker's
  known failure direction).

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
- **Keep this file current.** When a change introduces a pattern, a gotcha, a pinned version, or a
  shared abstraction a future session would need, update the relevant section here as part of that
  change — don't wait to be asked.
