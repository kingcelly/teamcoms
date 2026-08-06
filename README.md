
# Ace Volleyball Club — Kotlin Multiplatform

The same volleyball club app, rebuilt as **Kotlin Multiplatform with Compose
Multiplatform**: one shared Kotlin codebase drives the UI on both Android and
iOS, instead of maintaining two separate native apps.

> **A note on how this was built:** this sandbox can reach npm, PyPI, and
> GitHub, but not Maven Central or Google's Maven repo — so unlike the earlier
> Expo version, I could not run Gradle here to compile-check this code. I
> wrote and hand-checked it carefully (structure, imports, brace/paren
> balance) but **you'll want to open it in Android Studio as the first step**
> — that's also where any dependency-version mismatch would surface, and I'm
> glad to fix anything that comes up.

## What's in it

Same feature set as before, now in Kotlin/Compose:

- **Announcements** — coaches post club-wide or team-specific updates; pinned
  posts stay on top.
- **Schedule** — practices, games, and tournaments grouped by day, with an
  RSVP toggle for parents.
- **Roster** — players by team, with one-tap call/email to the parent contact
  (via `ACTION_DIAL`/`mailto:` on Android, `tel:`/`mailto:` URLs on iOS).
- **Payments** — dues per player with Paid/Due/Overdue status and a demo
  "Mark paid" action.
- **Profile** — logged-in user info and log-out.
- Role-based demo login (Coach vs. Parent/Player), no backend required to
  try it. State lives in memory only for now (see "Persistence" below).

## Project structure

```
composeApp/
  build.gradle.kts                 KMP + Compose Multiplatform module config
  src/
    commonMain/kotlin/com/aceclub/teamapp/
      App.kt                       root composable: navigation + tab bar
      data/
        Models.kt                  shared data classes & enums
        MockData.kt                starter teams, roster, announcements, schedule, dues
        AppRepository.kt           in-memory state (StateFlow) — swap for a real backend here
      navigation/Screen.kt         hand-rolled nav state (Login / Main tab / New announcement)
      ui/theme/                    design tokens (Color.kt) + Material3 theme (Theme.kt)
      ui/components/               Badge, EmptyState, ScreenHeader — shared building blocks
      ui/screens/                  one file per screen
    androidMain/kotlin/.../MainActivity.kt      Android entry point
    iosMain/kotlin/.../MainViewController.kt    iOS entry point (exposed to Swift)
iosApp/
  iosApp/iOSApp.swift, ContentView.swift        minimal SwiftUI shell that hosts the shared UI
```

## Building it

### Android
Open the **root folder** (`AceVolleyballClub/`) directly in Android Studio
(Koala or newer, with the Kotlin Multiplatform plugin). It will download the
Gradle wrapper and dependencies automatically, then run the `composeApp`
Android configuration on an emulator or device.

If you'd rather use the command line once Android Studio has generated the
wrapper: `./gradlew :composeApp:installDebug`.

### iOS
This zip includes the **Kotlin side** of the iOS app (`iosMain/`) and a
**minimal Swift shell** (`iosApp/iosApp/*.swift`), but not a full `.xcodeproj`
— that's a binary-ish project file that isn't practical to hand-write. The
straightforward path:

1. In Android Studio, use **File → New → New Module... → iOS Application**
   (or run the [KMP wizard](https://kmp.jetbrains.com) once to scaffold an
   `iosApp/` Xcode project) inside this same repo.
2. Replace the generated `iOSApp.swift` / `ContentView.swift` with the two
   files already in `iosApp/iosApp/` here — they're pre-wired to call into
   `MainViewController()` from the shared module.
3. Build and run from Xcode, or via Android Studio's iOS run configuration.

## Persistence

State currently lives only in memory (`AppRepository`, backed by
`StateFlow`) — it resets when the app process is killed. That mirrors the
Expo version before you connect a real backend. Two ways to harden it:

- **Local persistence only** (closest analog to the Expo version's
  AsyncStorage): add `androidx.datastore:datastore-preferences` (KMP-ready)
  and read/write JSON-encoded state in `AppRepository`.
- **Real backend** (recommended for an actual club, so parents' devices stay
  in sync): Firebase (Auth + Firestore + Cloud Messaging) has a Kotlin
  Multiplatform SDK (`gitlive/firebase-kotlin-sdk`) that plugs into this
  same `AppRepository` shape — replace the in-memory `MutableStateFlow`s
  with Firestore-backed flows, and the screens don't need to change since
  they only ever read from `AppRepository`'s public `StateFlow`s.
- For **push notifications**, `Firebase Cloud Messaging` (via the same KMP
  SDK) is the natural fit alongside Firestore.
- For **real payments**, Stripe's mobile SDKs are Android/iOS-native (not
  KMP), so that integration would live in `androidMain`/`iosMain` behind a
  shared `expect`/`actual` interface — the current "Mark paid" button is a
  placeholder for that.

## Design

Palette and type choices live in `ui/theme/Color.kt` / `Theme.kt` — the same
deep court-navy + volleyball-orange palette as the Expo version, translated
to Compose's color/typography system. Update it there to reflect your club's
own colors/logo.

# teamcoms
App that allows user to connect and communicate to admins, coaches, and other players/parents
