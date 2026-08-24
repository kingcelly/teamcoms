package com.aceclub.teamapp.navigation

// String-route based navigation (androidx.navigation.compose, multiplatform). The bottom
// nav bar's five tabs and the modal-style screens pushed on top of them all live in one
// NavHost; switching tabs uses saveState/restoreState so each tab keeps its own scroll
// position and back stack. See App.kt for the NavHost wiring.

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ANNOUNCEMENTS = "announcements"
    const val NEW_ANNOUNCEMENT = "newAnnouncement"
    const val CHATS = "chats"
    const val CHAT_CONVERSATION = "chatConversation/{chatId}"
    const val CHAT_DETAILS = "chatDetails/{chatId}"
    const val SCHEDULE = "schedule"
    const val SCHEDULE_EVENT_FORM = "scheduleEventForm?eventId={eventId}"
    const val ROSTER = "roster"
    const val SETTINGS = "settings"
    const val PAYMENTS = "payments"

    fun chatConversation(chatId: String) = "chatConversation/$chatId"
    fun chatDetails(chatId: String) = "chatDetails/$chatId"
    fun scheduleEventForm(eventId: String? = null) =
        if (eventId != null) "scheduleEventForm?eventId=$eventId" else "scheduleEventForm"
}

enum class Tab(val label: String, val icon: String, val route: String) {
    Announcements("Announcements", "📢", Routes.ANNOUNCEMENTS),
    Chats("Chats", "💬", Routes.CHATS),
    Schedule("Schedule", "📅", Routes.SCHEDULE),
    Roster("Roster", "👥", Routes.ROSTER),
    Settings("Settings", "⚙️", Routes.SETTINGS)
}
