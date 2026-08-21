package com.aceclub.teamapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.AppRepository
import com.aceclub.teamapp.data.RsvpResponse
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.navigation.Screen
import com.aceclub.teamapp.navigation.Tab
import com.aceclub.teamapp.ui.screens.AnnouncementsScreen
import com.aceclub.teamapp.ui.screens.ChatConversationScreen
import com.aceclub.teamapp.ui.screens.ChatDetailsScreen
import com.aceclub.teamapp.ui.screens.ChatsScreen
import com.aceclub.teamapp.ui.screens.LoginScreen
import com.aceclub.teamapp.ui.screens.NewAnnouncementScreen
import com.aceclub.teamapp.ui.screens.PaymentsScreen
import com.aceclub.teamapp.ui.screens.RegistrationScreen
import com.aceclub.teamapp.ui.screens.RosterScreen
import com.aceclub.teamapp.ui.screens.ScheduleEventFormScreen
import com.aceclub.teamapp.ui.screens.ScheduleScreen
import com.aceclub.teamapp.ui.screens.SettingsScreen
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme

/**
 * Root composable - owns the single AppRepository instance and top-level
 * navigation state. Announcements is the home screen; every other main
 * destination is reached through the bottom nav bar; Payments and other
 * one-off screens are pushed on top of it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AceVolleyballTheme {
        val repository = remember { AppRepository() }
        val user by repository.user.collectAsState()
        val announcements by repository.announcements.collectAsState()
        val payments by repository.payments.collectAsState()
        val rsvps by repository.rsvps.collectAsState()
        val chats by repository.chats.collectAsState()
        val chatMessages by repository.chatMessages.collectAsState()
        val schedule by repository.schedule.collectAsState()

        var screen by remember { mutableStateOf<Screen>(Screen.Login) }

        // Whenever the user logs out, always fall back to the login screen.
        if (user == null && screen !is Screen.Login && screen !is Screen.Register) {
            screen = Screen.Login
        }

        when (val current = screen) {
            is Screen.Login -> LoginScreen(
                onLogin = { contact, password ->
                    val success = repository.login(contact, password)
                    if (success) screen = Screen.Main()
                    success
                },
                onNavigateToRegister = { screen = Screen.Register }
            )

            is Screen.Register -> RegistrationScreen(
                onRegister = { contact, name, role ->
                    val success = repository.register(contact, name, role)
                    if (success) screen = Screen.Main()
                    success
                },
                onNavigateToLogin = { screen = Screen.Login }
            )

            is Screen.NewAnnouncement -> NewAnnouncementScreen(
                teams = repository.teams,
                onCancel = { screen = Screen.Main(Tab.Announcements) },
                onPost = { title, body, teamId ->
                    repository.postAnnouncement(title, body, teamId)
                    screen = Screen.Main(Tab.Announcements)
                }
            )

            is Screen.ChatConversation -> {
                val chat = chats.find { it.id == current.chatId }
                if (chat == null) {
                    screen = Screen.Main(Tab.Chats)
                } else {
                    ChatConversationScreen(
                        chat = chat,
                        messages = chatMessages.filter { it.chatId == current.chatId },
                        onBack = { screen = Screen.Main(Tab.Chats) },
                        onOpenDetails = { screen = Screen.ChatDetails(current.chatId) },
                        onSend = { body -> repository.sendChatMessage(current.chatId, body) }
                    )
                }
            }

            is Screen.ChatDetails -> {
                val chat = chats.find { it.id == current.chatId }
                if (chat == null) {
                    screen = Screen.Main(Tab.Chats)
                } else {
                    ChatDetailsScreen(
                        chat = chat,
                        onBack = { screen = Screen.ChatConversation(current.chatId) }
                    )
                }
            }

            is Screen.ScheduleEventForm -> {
                val editingEvent = current.eventId?.let { id -> schedule.find { it.id == id } }
                if (current.eventId != null && editingEvent == null) {
                    screen = Screen.Main(Tab.Schedule)
                } else {
                    ScheduleEventFormScreen(
                        teams = repository.teams,
                        event = editingEvent,
                        onCancel = { screen = Screen.Main(Tab.Schedule) },
                        onSave = { teamId, type, title, location, start, end ->
                            if (editingEvent == null) {
                                repository.addScheduleEvent(teamId, type, title, location, start, end)
                            } else {
                                repository.updateScheduleEvent(editingEvent.id, teamId, type, title, location, start, end)
                            }
                            screen = Screen.Main(Tab.Schedule)
                        },
                        onDelete = if (editingEvent != null) {
                            {
                                repository.deleteScheduleEvent(editingEvent.id)
                                screen = Screen.Main(Tab.Schedule)
                            }
                        } else null
                    )
                }
            }

            is Screen.Payments -> {
                val u = user ?: return@AceVolleyballTheme
                if (u.role != UserRole.PARENT && u.role != UserRole.ADMIN) {
                    screen = Screen.Main(Tab.Settings)
                } else {
                    PaymentsScreen(
                        payments = payments,
                        roster = repository.roster,
                        currentTeamId = u.teamId,
                        role = u.role,
                        onBack = { screen = Screen.Main(Tab.Settings) },
                        onPayNow = { id -> repository.payInstallment(id) },
                        onPayInFull = { ids -> repository.payInFull(ids) }
                    )
                }
            }

            is Screen.Main -> {
                val u = user ?: return@AceVolleyballTheme
                val chatUnreadCount = chats
                    .filter { it.teamId == null || it.teamId == u.teamId }
                    .sumOf { it.unreadCount }

                Scaffold(
                    containerColor = AceColors.bg,
                    bottomBar = {
                        AppBottomNav(
                            active = current.tab,
                            chatUnreadCount = chatUnreadCount,
                            onSelect = { tab -> screen = Screen.Main(tab) }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (current.tab) {
                            Tab.Announcements -> AnnouncementsScreen(
                                announcements = announcements,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onNewAnnouncement = { screen = Screen.NewAnnouncement }
                            )
                            Tab.Chats -> ChatsScreen(
                                chats = chats,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onOpenChat = { chatId ->
                                    repository.markChatRead(chatId)
                                    screen = Screen.ChatConversation(chatId)
                                }
                            )
                            Tab.Schedule -> ScheduleScreen(
                                schedule = schedule,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                rsvps = rsvps,
                                onRsvp = { eventId, response -> repository.setRsvp(eventId, response) },
                                onAddEvent = { screen = Screen.ScheduleEventForm() },
                                onEditEvent = { event -> screen = Screen.ScheduleEventForm(event.id) },
                                onDeleteEvent = { eventId -> repository.deleteScheduleEvent(eventId) }
                            )
                            Tab.Roster -> RosterScreen(
                                roster = repository.roster,
                                coaches = repository.coaches,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onChatWithCoach = { coach ->
                                    val team = repository.teams.find { it.id == coach.teamId }
                                    if (team != null) {
                                        val chatId = repository.openTeamChat(team)
                                        screen = Screen.ChatConversation(chatId)
                                    }
                                },
                                onChatWithPlayer = { player ->
                                    val chatId = repository.openChatWithPlayer(player)
                                    screen = Screen.ChatConversation(chatId)
                                },
                                onChatWithParent = { player ->
                                    val chatId = repository.openChatWithParent(player)
                                    screen = Screen.ChatConversation(chatId)
                                }
                            )
                            Tab.Settings -> SettingsScreen(
                                user = u,
                                teams = repository.teams,
                                onOpenPayments = { screen = Screen.Payments },
                                onLogout = { repository.logout() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomNav(active: Tab, chatUnreadCount: Int, onSelect: (Tab) -> Unit) {
    NavigationBar(containerColor = AceColors.surface, contentColor = AceColors.ink) {
        Tab.entries.forEach { tab ->
            val badgeCount = if (tab == Tab.Chats) chatUnreadCount else 0
            NavigationBarItem(
                selected = tab == active,
                onClick = { onSelect(tab) },
                icon = {
                    if (badgeCount > 0) {
                        BadgedBox(badge = { Badge { Text("$badgeCount") } }) {
                            Text(tab.icon, fontSize = 20.sp)
                        }
                    } else {
                        Text(tab.icon, fontSize = 20.sp)
                    }
                },
                label = { Text(tab.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AceColors.volleyDeep,
                    selectedTextColor = AceColors.volleyDeep,
                    unselectedIconColor = AceColors.inkSoft,
                    unselectedTextColor = AceColors.inkSoft,
                    indicatorColor = AceColors.sand
                )
            )
        }
    }
}
