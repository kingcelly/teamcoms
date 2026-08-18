package com.aceclub.teamapp.navigation

// Simple hand-rolled navigation — six destinations reachable from a
// hamburger menu, plus one modal-style screen. Announcements is home.
// Swap for androidx.navigation.compose's multiplatform Navigation library
// later if the flow grows more complex; the screens don't need to change.

enum class Tab(val label: String, val icon: String) {
    Announcements("Announcements", "📢"),
    Chats("Chats", "💬"),
    Schedule("Schedule", "📅"),
    Roster("Roster", "👥"),
    Payments("Payments", "💳"),
    Settings("Settings", "⚙️")
}

sealed class Screen {
    data object Login : Screen()
    data object Register : Screen()
    data class Main(val tab: Tab = Tab.Announcements) : Screen()
    data object NewAnnouncement : Screen()
    data class ChatConversation(val chatId: String) : Screen()
    data class ChatDetails(val chatId: String) : Screen()
    data class ScheduleEventForm(val eventId: String? = null) : Screen()
}
