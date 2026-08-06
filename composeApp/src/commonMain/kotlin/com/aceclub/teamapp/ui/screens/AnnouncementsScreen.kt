package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.Announcement
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun AnnouncementsScreen(
    announcements: List<Announcement>,
    teams: List<Team>,
    currentTeamId: String?,
    role: UserRole,
    onMenuClick: () -> Unit,
    onNewAnnouncement: () -> Unit
) {
    val sorted = remember(announcements) {
        announcements.sortedWith(
            compareByDescending<Announcement> { it.pinned }.thenByDescending { it.createdAtEpochMillis }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        com.aceclub.teamapp.ui.components.ScreenHeader(
            title = "Announcements",
            subtitle = currentTeamId?.let { id -> teams.find { it.id == id }?.name },
            onMenuClick = onMenuClick,
            trailing = if (role == UserRole.COACH) {
                {
                    IconButton(
                        onClick = onNewAnnouncement,
                        modifier = Modifier.size(36.dp).background(AceColors.volley, RoundedCornerShape(18.dp))
                    ) { Icon(Icons.Filled.Add, contentDescription = "New announcement", tint = Color.White) }
                }
            } else null
        )

        if (sorted.isEmpty()) {
            com.aceclub.teamapp.ui.components.EmptyState(
                title = "No announcements yet",
                subtitle = if (role == UserRole.COACH) "Tap + to post the first one." else "Check back soon for club updates."
            )
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sorted, key = { it.id }) { item ->
                    AnnouncementCard(item, teams)
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(item: Announcement, teams: List<Team>) {
    val teamName = item.teamId?.let { id -> teams.find { it.id == id }?.name }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (item.pinned) {
                Text("📌 Pinned", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AceColors.volleyDeep)
            } else {
                Text(teamName ?: "All teams", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AceColors.court)
            }
            Text(timeAgo(item.createdAtEpochMillis), fontSize = 12.sp, color = AceColors.inkSoft)
        }
        Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
        Text(item.body, fontSize = 14.sp, color = AceColors.inkSoft, lineHeight = 20.sp)
        Text("— ${item.author}", fontSize = 12.sp, color = AceColors.inkSoft, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 10.dp))
    }
}

private fun timeAgo(epochMillis: Long): String {
    val diff = epochMillisNowSafe() - epochMillis
    val hrs = diff / 3_600_000
    return when {
        hrs < 1 -> "just now"
        hrs < 24 -> "${hrs}h ago"
        else -> "${hrs / 24}d ago"
    }
}

private fun epochMillisNowSafe(): Long = com.aceclub.teamapp.data.epochMillisNow()
