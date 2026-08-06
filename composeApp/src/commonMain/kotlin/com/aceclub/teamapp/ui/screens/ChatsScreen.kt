package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.ChatThread
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.epochMillisNow
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun ChatsScreen(
    chats: List<ChatThread>,
    teams: List<Team>,
    currentTeamId: String?,
    onMenuClick: () -> Unit
) {
    val visible = remember(chats, currentTeamId) {
        chats
            .filter { it.teamId == null || it.teamId == currentTeamId }
            .sortedByDescending { it.lastMessageAtEpochMillis }
    }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(
            title = "Chats",
            subtitle = currentTeamId?.let { id -> teams.find { it.id == id }?.name },
            onMenuClick = onMenuClick
        )

        if (visible.isEmpty()) {
            EmptyState(title = "No conversations yet", subtitle = "Team and club chats will show up here.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visible, key = { it.id }) { chat ->
                    ChatCard(chat)
                }
            }
        }
    }
}

@Composable
private fun ChatCard(chat: ChatThread) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(AceColors.court, RoundedCornerShape(21.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(chat.name.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(chat.name, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text(
                chat.lastMessage,
                fontSize = 13.sp,
                color = AceColors.inkSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
            Text(timeAgo(chat.lastMessageAtEpochMillis), fontSize = 11.sp, color = AceColors.inkSoft)
            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(AceColors.volley, RoundedCornerShape(999.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${chat.unreadCount}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun timeAgo(epochMillis: Long): String {
    val diff = epochMillisNow() - epochMillis
    val hrs = diff / 3_600_000
    return when {
        hrs < 1 -> "just now"
        hrs < 24 -> "${hrs}h ago"
        else -> "${hrs / 24}d ago"
    }
}
