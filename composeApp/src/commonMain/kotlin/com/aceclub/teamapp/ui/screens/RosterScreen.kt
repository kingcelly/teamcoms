package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.Player
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun RosterScreen(
    roster: List<Player>,
    teams: List<Team>,
    currentTeamId: String?,
    role: UserRole,
    onMenuClick: () -> Unit,
    onCall: (phone: String) -> Unit,
    onEmail: (email: String) -> Unit
) {
    val visibleTeams = remember(teams, currentTeamId, role) {
        if (role == UserRole.COACH) teams else teams.filter { it.id == currentTeamId }
    }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = "Roster", subtitle = "Players & parent contacts", onMenuClick = onMenuClick)

        if (roster.isEmpty()) {
            EmptyState(title = "No players yet", subtitle = "Add players once your roster is ready.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleTeams.forEach { team ->
                    item {
                        Text(team.name.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    val players = roster.filter { it.teamId == team.id }.sortedBy { it.number }
                    items(players, key = { it.id }) { player ->
                        PlayerCard(player, onCall, onEmail)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: Player, onCall: (String) -> Unit, onEmail: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(AceColors.sand, RoundedCornerShape(19.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("${player.number}", fontWeight = FontWeight.ExtraBold, color = AceColors.court, fontSize = 15.sp)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(player.name, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text(player.position, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.volleyDeep, modifier = Modifier.padding(top = 1.dp))
            Text("Parent: ${player.parent.name}", fontSize = 12.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 3.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconCircle("\uD83D\uDCDE", onClick = { onCall(player.parent.phone) })
            IconCircle("\u2709\uFE0F", onClick = { onEmail(player.parent.email) })
        }
    }
}

@Composable
private fun IconCircle(emoji: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(AceColors.bg, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 14.sp)
    }
}
