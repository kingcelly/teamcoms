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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.ChatParticipant
import com.aceclub.teamapp.data.ChatThread
import com.aceclub.teamapp.data.seedChats
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatDetailsScreen(
    chat: ChatThread,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = chat.name, subtitle = "Conversation details", onBackClick = onBack)

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(AceColors.court, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.name.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(chat.name, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink, modifier = Modifier.padding(top = 10.dp))
            Text(
                "${chat.participants.size} ${if (chat.participants.size == 1) "participant" else "participants"}",
                fontSize = 13.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            "PARTICIPANTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AceColors.inkSoft,
            modifier = Modifier.padding(horizontal = 20.dp, bottom = 8.dp)
        )

        if (chat.participants.isEmpty()) {
            EmptyState(title = "No participants listed", subtitle = "Participants for this conversation aren't available yet.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chat.participants, key = { it.name }) { participant ->
                    ParticipantCard(participant)
                }
            }
        }
    }
}

@Composable
private fun ParticipantCard(participant: ChatParticipant) {
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
            Text(participant.name.firstOrNull()?.uppercase() ?: "?", color = AceColors.court, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(participant.name, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text(participant.role, fontSize = 13.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 1.dp))
        }
    }
}

@Preview
@Composable
private fun ChatDetailsScreenPreview() {
    AceVolleyballTheme {
        ChatDetailsScreen(chat = seedChats.first { it.id == "c2" }, onBack = {})
    }
}
