package com.aceclub.teamapp.data

enum class UserRole { COACH, ADMIN, PARENT }

// Coaches and admins share the same staff-level permissions throughout the app
// (posting announcements, managing the calendar, seeing every team's payments/roster).
val UserRole.isStaff: Boolean get() = this == UserRole.COACH || this == UserRole.ADMIN

data class AppUser(
    val contact: String, // email or phone used to sign in
    val name: String,
    val role: UserRole,
    val teamId: String?
)

data class Team(
    val id: String,
    val name: String
)

data class ParentContact(
    val name: String,
    val phone: String,
    val email: String
)

data class Player(
    val id: String,
    val teamId: String,
    val name: String,
    val number: Int,
    val position: String,
    val parent: ParentContact
)

data class Coach(
    val id: String,
    val teamId: String,
    val name: String
)

data class Announcement(
    val id: String,
    val teamId: String?, // null = all teams
    val author: String,
    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val pinned: Boolean = false
)

enum class EventType { PRACTICE, WEIGHTLIFTING, GAME, TOURNAMENT }

data class ScheduleEvent(
    val id: String,
    val teamId: String,
    val type: EventType,
    val title: String,
    val location: String,
    val startEpochMillis: Long,
    val endEpochMillis: Long
)

enum class RsvpResponse { YES, NO }

enum class PaymentStatus { PAID, DUE, OVERDUE }

data class PaymentDue(
    val id: String,
    val playerId: String,
    val label: String,
    val amount: Int,
    val dueDate: String, // e.g. "2026-08-15"
    val status: PaymentStatus
)

data class ChatParticipant(
    val name: String,
    val role: String // e.g. "Coach", "Parent", "Club Office", "You"
)

data class ChatThread(
    val id: String,
    val teamId: String?, // null = all teams
    val name: String,
    val lastMessage: String,
    val lastMessageAtEpochMillis: Long,
    val unreadCount: Int = 0,
    val participants: List<ChatParticipant> = emptyList(),
    val relatedPlayerId: String? = null, // set for 1:1 chats about a specific player
    val isParentChat: Boolean = false // true = chatting with the player's parent, false = with the player
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderName: String,
    val body: String,
    val sentAtEpochMillis: Long,
    val fromMe: Boolean = false
)
