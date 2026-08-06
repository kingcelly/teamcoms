package com.aceclub.teamapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.AppRepository
import com.aceclub.teamapp.data.RsvpResponse
import com.aceclub.teamapp.navigation.Screen
import com.aceclub.teamapp.navigation.Tab
import com.aceclub.teamapp.ui.screens.AnnouncementsScreen
import com.aceclub.teamapp.ui.screens.ChatsScreen
import com.aceclub.teamapp.ui.screens.LoginScreen
import com.aceclub.teamapp.ui.screens.NewAnnouncementScreen
import com.aceclub.teamapp.ui.screens.PaymentsScreen
import com.aceclub.teamapp.ui.screens.RosterScreen
import com.aceclub.teamapp.ui.screens.ScheduleScreen
import com.aceclub.teamapp.ui.screens.SettingsScreen
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import kotlinx.coroutines.launch

/**
 * Root composable - owns the single AppRepository instance, top-level
 * navigation state, and the platform hooks for phone/email actions that
 * differ between Android and iOS. Announcements is the home screen; every
 * other destination is reached through the hamburger menu drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    onCall: (String) -> Unit = {},
    onEmail: (String) -> Unit = {}
) {
    AceVolleyballTheme {
        val repository = remember { AppRepository() }
        val user by repository.user.collectAsState()
        val announcements by repository.announcements.collectAsState()
        val payments by repository.payments.collectAsState()
        val rsvps by repository.rsvps.collectAsState()

        var screen by remember { mutableStateOf<Screen>(Screen.Login) }

        // Whenever the user logs out, always fall back to the login screen.
        if (user == null && screen !is Screen.Login) {
            screen = Screen.Login
        }

        when (val current = screen) {
            is Screen.Login -> LoginScreen(
                teams = repository.teams,
                onLogin = { name, role, teamId ->
                    repository.login(name, role, teamId)
                    screen = Screen.Main()
                }
            )

            is Screen.NewAnnouncement -> NewAnnouncementScreen(
                teams = repository.teams,
                onCancel = { screen = Screen.Main(Tab.Announcements) },
                onPost = { title, body, teamId ->
                    repository.postAnnouncement(title, body, teamId)
                    screen = Screen.Main(Tab.Announcements)
                }
            )

            is Screen.Main -> {
                val u = user ?: return@AceVolleyballTheme
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val openMenu: () -> Unit = { scope.launch { drawerState.open() } }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        NavDrawerContent(
                            active = current.tab,
                            onSelect = { tab ->
                                screen = Screen.Main(tab)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    when (current.tab) {
                        Tab.Announcements -> AnnouncementsScreen(
                            announcements = announcements,
                            teams = repository.teams,
                            currentTeamId = u.teamId,
                            role = u.role,
                            onMenuClick = openMenu,
                            onNewAnnouncement = { screen = Screen.NewAnnouncement }
                        )
                        Tab.Chats -> ChatsScreen(
                            chats = repository.chats,
                            teams = repository.teams,
                            currentTeamId = u.teamId,
                            onMenuClick = openMenu
                        )
                        Tab.Schedule -> ScheduleScreen(
                            schedule = repository.schedule,
                            teams = repository.teams,
                            currentTeamId = u.teamId,
                            role = u.role,
                            rsvps = rsvps,
                            onMenuClick = openMenu,
                            onRsvp = { eventId, response -> repository.setRsvp(eventId, response) }
                        )
                        Tab.Roster -> RosterScreen(
                            roster = repository.roster,
                            teams = repository.teams,
                            currentTeamId = u.teamId,
                            role = u.role,
                            onMenuClick = openMenu,
                            onCall = onCall,
                            onEmail = onEmail
                        )
                        Tab.Payments -> PaymentsScreen(
                            payments = payments,
                            roster = repository.roster,
                            currentTeamId = u.teamId,
                            role = u.role,
                            onMenuClick = openMenu,
                            onMarkPaid = { id -> repository.markPaid(id) }
                        )
                        Tab.Settings -> SettingsScreen(
                            user = u,
                            teams = repository.teams,
                            onMenuClick = openMenu,
                            onLogout = { repository.logout() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavDrawerContent(active: Tab, onSelect: (Tab) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(vertical = 20.dp)) {
            Text(
                "Ace Volleyball Club",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AceColors.ink,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            HorizontalDivider(color = AceColors.line, modifier = Modifier.padding(bottom = 8.dp))
            Tab.entries.forEach { tab ->
                val isActive = tab == active
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(tab) }
                        .background(if (isActive) AceColors.sand else Color.Transparent)
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tab.icon, fontSize = 18.sp, modifier = Modifier.size(24.dp))
                    Text(
                        tab.label,
                        fontSize = 15.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isActive) AceColors.volleyDeep else AceColors.ink,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
