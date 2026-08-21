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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.Coach
import com.aceclub.teamapp.data.Player
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.data.coaches
import com.aceclub.teamapp.data.roster
import com.aceclub.teamapp.data.teams
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RosterScreen(
    roster: List<Player>,
    coaches: List<Coach>,
    teams: List<Team>,
    currentTeamId: String?,
    role: UserRole,
    onChatWithCoach: (Coach) -> Unit,
    onChatWithPlayer: (Player) -> Unit,
    onChatWithParent: (Player) -> Unit
) {
    val visibleTeams = remember(teams, currentTeamId, role) {
        if (role == UserRole.ADMIN) teams else teams.filter { it.id == currentTeamId }
    }
    val collapsible = visibleTeams.size > 1
    var collapsedTeamIds by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = "Roster", subtitle = "Coaches, players & parents")

        if (role != UserRole.ADMIN && currentTeamId == null) {
            EmptyState(title = "You are currently not assigned to a team. If this is an error please contact your admin.")
        } else if (roster.isEmpty() && coaches.isEmpty()) {
            EmptyState(title = "No roster yet", subtitle = "Coaches and players will show up here.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleTeams.forEach { team ->
                    val isCollapsed = collapsible && team.id in collapsedTeamIds
                    item {
                        TeamHeader(
                            team = team,
                            collapsible = collapsible,
                            collapsed = isCollapsed,
                            onToggle = {
                                collapsedTeamIds = if (team.id in collapsedTeamIds) {
                                    collapsedTeamIds - team.id
                                } else {
                                    collapsedTeamIds + team.id
                                }
                            }
                        )
                    }

                    if (!isCollapsed) {
                        val teamCoaches = coaches.filter { it.teamId == team.id }
                        if (teamCoaches.isNotEmpty()) {
                            item { SubsectionLabel("Coaches") }
                            items(teamCoaches, key = { "coach-${it.id}" }) { coach ->
                                CoachCard(coach, onChat = { onChatWithCoach(coach) })
                            }
                        }

                        val players = roster.filter { it.teamId == team.id }.sortedBy { it.number }
                        if (players.isNotEmpty()) {
                            item { SubsectionLabel("Players") }
                            items(players, key = { "player-${it.id}" }) { player ->
                                PlayerCard(player, onChat = { onChatWithPlayer(player) })
                            }

                            item { SubsectionLabel("Parents") }
                            items(players, key = { "parent-${it.id}" }) { player ->
                                ParentCard(player, onChat = { onChatWithParent(player) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamHeader(team: Team, collapsible: Boolean, collapsed: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (collapsible) Modifier.clickable(onClick = onToggle) else Modifier)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(team.name.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court)
        if (collapsible) {
            Text(
                if (collapsed) "▸" else "▾",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AceColors.court
            )
        }
    }
}

@Composable
private fun SubsectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AceColors.inkSoft,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun CoachCard(coach: Coach, onChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(AceColors.court, RoundedCornerShape(19.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(coach.name.firstOrNull()?.uppercase() ?: "?", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 15.sp)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(coach.name, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text("Coach", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.volleyDeep, modifier = Modifier.padding(top = 1.dp))
        }

        IconCircle("💬", onClick = onChat)
    }
}

@Composable
private fun PlayerCard(player: Player, onChat: () -> Unit) {
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
        }

        IconCircle("💬", onClick = onChat)
    }
}

@Composable
private fun ParentCard(player: Player, onChat: () -> Unit) {
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
            Text(player.parent.name.firstOrNull()?.uppercase() ?: "?", fontWeight = FontWeight.ExtraBold, color = AceColors.court, fontSize = 15.sp)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(player.parent.name, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text("Parent · ${player.name}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.volleyDeep, modifier = Modifier.padding(top = 1.dp))
        }

        IconCircle("💬", onClick = onChat)
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

@Preview
@Composable
private fun RosterScreenPreview() {
    AceVolleyballTheme {
        RosterScreen(
            roster = roster,
            coaches = coaches,
            teams = teams,
            currentTeamId = teams.first().id,
            role = UserRole.COACH,
            onChatWithCoach = {},
            onChatWithPlayer = {},
            onChatWithParent = {}
        )
    }
}
