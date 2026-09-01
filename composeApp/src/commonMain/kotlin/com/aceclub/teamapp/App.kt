package com.aceclub.teamapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aceclub.teamapp.data.AppRepository
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.navigation.Routes
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
 * Root composable - owns the single AppRepository instance and the NavController.
 * Announcements is the home screen; every other main destination is reached through
 * the bottom nav bar; Payments and other one-off screens are pushed on top of it.
 * All destinations share one NavHost/back stack — tab switches use saveState/restoreState
 * so each tab keeps its own scroll position and drill-down state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AceVolleyballTheme {
        val repository = remember { AppRepository() }
        val user by repository.user.collectAsState()
        val announcements by repository.announcements.collectAsState()
        val payments by repository.payments.collectAsState()
        val chats by repository.chats.collectAsState()
        val chatMessages by repository.chatMessages.collectAsState()
        val schedule by repository.schedule.collectAsState()

        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val activeTab = Tab.entries.find { it.route == currentRoute }

        // Whenever the user logs out, always fall back to the login screen.
        LaunchedEffect(user, currentRoute) {
            if (user == null && currentRoute != Routes.LOGIN && currentRoute != Routes.REGISTER) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            }
        }

        Scaffold(
            containerColor = AceColors.bg,
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                if (activeTab != null) {
                    val chatUnreadCount = user?.let { u ->
                        chats.filter { it.teamId == null || it.teamId == u.teamId }.sumOf { it.unreadCount }
                    } ?: 0
                    AppBottomNav(
                        active = activeTab,
                        chatUnreadCount = chatUnreadCount,
                        onSelect = { tab ->
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLogin = { contact, password ->
                            val success = repository.login(contact, password)
                            if (success) {
                                navController.navigate(Routes.ANNOUNCEMENTS) {
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                }
                            }
                            success
                        },
                        onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                    )
                }

                composable(Routes.REGISTER) {
                    RegistrationScreen(
                        onRegister = { contact, name, role ->
                            val success = repository.register(contact, name, role)
                            if (success) {
                                navController.navigate(Routes.ANNOUNCEMENTS) {
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                }
                            }
                            success
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                composable(Routes.ANNOUNCEMENTS) {
                    val u = user ?: return@composable
                    AnnouncementsScreen(
                        announcements = announcements,
                        teams = repository.teams,
                        currentTeamId = u.teamId,
                        role = u.role,
                        onNewAnnouncement = { navController.navigate(Routes.NEW_ANNOUNCEMENT) }
                    )
                }

                composable(Routes.NEW_ANNOUNCEMENT) {
                    NewAnnouncementScreen(
                        teams = repository.teams,
                        onCancel = { navController.popBackStack() },
                        onPost = { title, body, teamId ->
                            repository.postAnnouncement(title, body, teamId)
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.CHATS) {
                    val u = user ?: return@composable
                    ChatsScreen(
                        chats = chats,
                        teams = repository.teams,
                        currentTeamId = u.teamId,
                        role = u.role,
                        onOpenChat = { chatId ->
                            repository.markChatRead(chatId)
                            navController.navigate(Routes.chatConversation(chatId))
                        }
                    )
                }

                composable(
                    route = Routes.CHAT_CONVERSATION,
                    arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                ) { entry ->
                    val chatId = entry.arguments?.getString("chatId") ?: return@composable
                    val chat = chats.find { it.id == chatId }
                    if (chat == null) {
                        LaunchedEffect(chatId) { navController.popBackStack() }
                    } else {
                        ChatConversationScreen(
                            chat = chat,
                            messages = chatMessages.filter { it.chatId == chatId },
                            onBack = { navController.popBackStack() },
                            onOpenDetails = { navController.navigate(Routes.chatDetails(chatId)) },
                            onSend = { body -> repository.sendChatMessage(chatId, body) }
                        )
                    }
                }

                composable(
                    route = Routes.CHAT_DETAILS,
                    arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                ) { entry ->
                    val chatId = entry.arguments?.getString("chatId") ?: return@composable
                    val chat = chats.find { it.id == chatId }
                    if (chat == null) {
                        LaunchedEffect(chatId) { navController.popBackStack() }
                    } else {
                        ChatDetailsScreen(
                            chat = chat,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(Routes.SCHEDULE) {
                    val u = user ?: return@composable
                    ScheduleScreen(
                        schedule = schedule,
                        teams = repository.teams,
                        currentTeamId = u.teamId,
                        role = u.role,
                        onAddEvent = { navController.navigate(Routes.scheduleEventForm()) },
                        onEditEvent = { event -> navController.navigate(Routes.scheduleEventForm(event.id)) },
                        onDeleteEvent = { eventId -> repository.deleteScheduleEvent(eventId) }
                    )
                }

                composable(
                    route = Routes.SCHEDULE_EVENT_FORM,
                    arguments = listOf(navArgument("eventId") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { entry ->
                    val eventId = entry.arguments?.getString("eventId")
                    val editingEvent = eventId?.let { id -> schedule.find { it.id == id } }
                    if (eventId != null && editingEvent == null) {
                        LaunchedEffect(eventId) { navController.popBackStack() }
                    } else {
                        ScheduleEventFormScreen(
                            teams = repository.teams,
                            event = editingEvent,
                            onCancel = { navController.popBackStack() },
                            onSave = { teamId, type, title, location, start, end ->
                                if (editingEvent == null) {
                                    repository.addScheduleEvent(teamId, type, title, location, start, end)
                                } else {
                                    repository.updateScheduleEvent(editingEvent.id, teamId, type, title, location, start, end)
                                }
                                navController.popBackStack()
                            },
                            onDelete = if (editingEvent != null) {
                                {
                                    repository.deleteScheduleEvent(editingEvent.id)
                                    navController.popBackStack()
                                }
                            } else null
                        )
                    }
                }

                composable(Routes.ROSTER) {
                    val u = user ?: return@composable
                    RosterScreen(
                        roster = repository.roster,
                        coaches = repository.coaches,
                        teams = repository.teams,
                        currentTeamId = u.teamId,
                        role = u.role,
                        onChatWithCoach = { coach ->
                            val team = repository.teams.find { it.id == coach.teamId }
                            if (team != null) {
                                val chatId = repository.openTeamChat(team)
                                navController.navigate(Routes.chatConversation(chatId))
                            }
                        },
                        onChatWithPlayer = { player ->
                            val chatId = repository.openChatWithPlayer(player)
                            navController.navigate(Routes.chatConversation(chatId))
                        },
                        onChatWithParent = { player ->
                            val chatId = repository.openChatWithParent(player)
                            navController.navigate(Routes.chatConversation(chatId))
                        }
                    )
                }

                composable(Routes.SETTINGS) {
                    val u = user ?: return@composable
                    SettingsScreen(
                        user = u,
                        teams = repository.teams,
                        onOpenPayments = { navController.navigate(Routes.PAYMENTS) },
                        onLogout = { repository.logout() }
                    )
                }

                composable(Routes.PAYMENTS) {
                    val u = user ?: return@composable
                    if (u.role != UserRole.PARENT && u.role != UserRole.ADMIN) {
                        LaunchedEffect(u) { navController.popBackStack() }
                    } else {
                        PaymentsScreen(
                            payments = payments,
                            roster = repository.roster,
                            currentTeamId = u.teamId,
                            role = u.role,
                            onBack = { navController.popBackStack() },
                            onPayNow = { id -> repository.payInstallment(id) },
                            onPayInFull = { ids -> repository.payInFull(ids) }
                        )
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
