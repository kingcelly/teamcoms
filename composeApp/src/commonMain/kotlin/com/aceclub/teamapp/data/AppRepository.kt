package com.aceclub.teamapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * In-memory app state, holding the same shape of data the mobile app will
 * eventually pull from a real backend (Firebase/Supabase — see README).
 *
 * A single instance is created in App() and passed down; there is no
 * on-device persistence yet, so state resets when the process is killed.
 * Swap this class's internals for backend calls without touching the UI —
 * screens only ever read from the exposed StateFlows.
 */
class AppRepository {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user: StateFlow<AppUser?> = _user.asStateFlow()

    private val _announcements = MutableStateFlow(seedAnnouncements)
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _payments = MutableStateFlow(seedPayments)
    val payments: StateFlow<List<PaymentDue>> = _payments.asStateFlow()

    private val _rsvps = MutableStateFlow<Map<String, RsvpResponse>>(emptyMap())
    val rsvps: StateFlow<Map<String, RsvpResponse>> = _rsvps.asStateFlow()

    private val _chats = MutableStateFlow(seedChats)
    val chats: StateFlow<List<ChatThread>> = _chats.asStateFlow()

    private val _chatMessages = MutableStateFlow(seedChatMessages)
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    val teams get() = com.aceclub.teamapp.data.teams
    val roster get() = com.aceclub.teamapp.data.roster
    val schedule get() = com.aceclub.teamapp.data.schedule

    fun login(name: String, role: UserRole, teamId: String?) {
        _user.value = AppUser(name = name, role = role, teamId = teamId)
    }

    fun logout() {
        _user.value = null
    }

    fun postAnnouncement(title: String, body: String, teamId: String?) {
        val author = _user.value?.name ?: "Coach"
        val new = Announcement(
            id = "a${epochMillisNow()}",
            teamId = teamId,
            author = author,
            title = title,
            body = body,
            createdAtEpochMillis = epochMillisNow(),
            pinned = false
        )
        _announcements.value = listOf(new) + _announcements.value
    }

    fun setRsvp(eventId: String, response: RsvpResponse) {
        _rsvps.value = _rsvps.value + (eventId to response)
    }

    fun markPaid(paymentId: String) {
        _payments.value = _payments.value.map {
            if (it.id == paymentId) it.copy(status = PaymentStatus.PAID) else it
        }
    }

    fun sendChatMessage(chatId: String, body: String) {
        val sender = _user.value?.name ?: "Me"
        val now = epochMillisNow()
        val message = ChatMessage(
            id = "msg${now}",
            chatId = chatId,
            senderName = sender,
            body = body,
            sentAtEpochMillis = now,
            fromMe = true
        )
        _chatMessages.value = _chatMessages.value + message
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(lastMessage = body, lastMessageAtEpochMillis = now, unreadCount = 0) else it
        }
    }

    fun markChatRead(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(unreadCount = 0) else it
        }
    }
}

expect fun epochMillisNow(): Long
