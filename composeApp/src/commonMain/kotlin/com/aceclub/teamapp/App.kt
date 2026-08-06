package com.aceclub.teamapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.AppRepository
import com.aceclub.teamapp.data.RsvpResponse
import com.aceclub.teamapp.navigation.Screen
import com.aceclub.teamapp.navigation.Tab
import com.aceclub.teamapp.ui.screens.AnnouncementsScreen
import com.aceclub.teamapp.ui.screens.LoginScreen
import com.aceclub.teamapp.ui.screens.NewAnnouncementScreen
import com.aceclub.teamapp.ui.screens.PaymentsScreen
import com.aceclub.teamapp.ui.screens.ProfileScreen
import com.aceclub.teamapp.ui.screens.RosterScreen
import com.aceclub.teamapp.ui.screens.ScheduleScreen
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme

/**
 * Root composable — owns the single AppRepository instance, top-level
 * navigation state, and the platform hooks for phone/email actions that
 * differ between Android and iOS.
 */
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
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        when (current.tab) {
                            Tab.Announcements -> AnnouncementsScreen(
                                announcements = announcements,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onNewAnnouncement = { screen = Screen.NewAnnouncement }
                            )
                            Tab.Schedule -> ScheduleScreen(
                                schedule = repository.schedule,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                rsvps = rsvps,
                                onRsvp = { eventId, response -> repository.setRsvp(eventId, response) }
                            )
                            Tab.Roster -> RosterScreen(
                                roster = repository.roster,
                                teams = repository.teams,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onCall = onCall,
                                onEmail = onEmail
                            )
                            Tab.Payments -> PaymentsScreen(
                                payments = payments,
                                roster = repository.roster,
                                currentTeamId = u.teamId,
                                role = u.role,
                                onMarkPaid = { id -> repository.markPaid(id) }
                            )
                            Tab.Profile -> ProfileScreen(
                                user = u,
                                teams = repository.teams,
                                onLogout = { repository.logout() }
                            )
                        }
                    }
                    BottomTabBar(current.tab) { tab -> screen = Screen.Main(tab) }
                }
            }
        }
    }
}

private val TAB_ICONS = mapOf(
    Tab.Announcements to "\uD83D\uDCE2",
    Tab.Schedule to "\uD83D\uDCC5",
    Tab.Roster to "\uD83D\uDC65",
    Tab.Payments to "\uD83D\uDCB3",
    Tab.Profile to "\uD83D\uDC64"
)

@Composable
private fun BottomTabBar(active: Tab, onSelect: (Tab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(AceColors.surface)
            .border(width = 1.dp, color = AceColors.line),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Tab.entries.forEach { tab ->
            val isActive = tab == active
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(TAB_ICONS[tab] ?: "", fontSize = 16.sp)
                Text(
                    tab.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) AceColors.volleyDeep else AceColors.inkSoft
                )
            }
        }
    }
}
