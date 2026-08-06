package com.aceclub.teamapp.data

enum class UserRole { COACH, PARENT }

data class AppUser(
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

data class Announcement(
    val id: String,
    val teamId: String?, // null = all teams
    val author: String,
    val title: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val pinned: Boolean = false
)

enum class EventType { PRACTICE, GAME, TOURNAMENT }

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
