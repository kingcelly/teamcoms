package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.AppUser
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.data.teams
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(user: AppUser?, teams: List<Team>, onMenuClick: () -> Unit, onLogout: () -> Unit) {
    val team = teams.find { it.id == user?.teamId }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = "Settings", onMenuClick = onMenuClick)

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(AceColors.court, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(user?.name?.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(user?.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink, modifier = Modifier.padding(top = 10.dp))
            Text(
                "${roleLabel(user?.role)} · ${team?.name ?: ""}",
                fontSize = 13.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(AceColors.surface, RoundedCornerShape(16.dp))
                .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
        ) {
            ProfileRow("🔔  Notification preferences") {}
            ProfileRow("👪  Manage players in household") {}
            ProfileRow("↩\uFE0F  Log out", danger = true, onClick = onLogout)
        }
    }
}

private fun roleLabel(role: UserRole?): String = when (role) {
    UserRole.COACH -> "Coach"
    UserRole.ADMIN -> "Club Admin"
    UserRole.PARENT -> "Parent / Player"
    null -> ""
}

@Composable
private fun ProfileRow(label: String, danger: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (danger) AceColors.danger else AceColors.ink)
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    AceVolleyballTheme {
        SettingsScreen(
            user = AppUser(name = "Kate Bishop", role = UserRole.PARENT, teamId = teams.first().id),
            teams = teams,
            onMenuClick = {},
            onLogout = {}
        )
    }
}
