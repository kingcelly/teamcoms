package com.aceclub.teamapp.navigation

// Simple hand-rolled navigation — five tabs plus one modal-style screen.
// Swap for androidx.navigation.compose's multiplatform Navigation library
// later if the flow grows more complex; the screens don't need to change.

enum class Tab(val label: String) {
    Announcements("Announcements"),
    Schedule("Schedule"),
    Roster("Roster"),
    Payments("Payments"),
    Profile("Profile")
}

sealed class Screen {
    data object Login : Screen()
    data class Main(val tab: Tab = Tab.Announcements) : Screen()
    data object NewAnnouncement : Screen()
}
