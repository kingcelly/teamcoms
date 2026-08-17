package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aceclub.teamapp.data.ChatMessage
import com.aceclub.teamapp.data.ChatThread
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ChatConversationScreen(
    chat: ChatThread,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }
    val sorted = remember(messages) { messages.sortedBy { it.sentAtEpochMillis } }
    val listState = rememberLazyListState()

    LaunchedEffect(sorted.size) {
        if (sorted.isNotEmpty()) listState.animateScrollToItem(sorted.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = chat.name, onBackClick = onBack)

        if (sorted.isEmpty()) {
            Column(modifier = Modifier.weight(1f)) {
                EmptyState(title = "Say hello", subtitle = "Start the conversation with a message below.")
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sorted, key = { it.id }) { message ->
                    MessageBubble(message)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AceColors.surface)
                .border(width = 1.dp, color = AceColors.line)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Message") },
                modifier = Modifier.weight(1f),
                maxLines = 4
            )
            val canSend = draft.trim().isNotEmpty()
            IconButton(
                onClick = {
                    val trimmed = draft.trim()
                    if (trimmed.isNotEmpty()) {
                        onSend(trimmed)
                        draft = ""
                    }
                },
                enabled = canSend
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (canSend) AceColors.volleyDeep else AceColors.inkSoft
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    ) {
        val bubbleModifier = if (message.fromMe) {
            Modifier.background(AceColors.court, RoundedCornerShape(16.dp))
        } else {
            Modifier
                .background(AceColors.surface, RoundedCornerShape(16.dp))
                .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
        }
        Column(modifier = bubbleModifier.widthIn(max = 260.dp).padding(12.dp)) {
            if (!message.fromMe) {
                Text(message.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AceColors.volleyDeep)
            }
            Text(
                message.body,
                fontSize = 14.sp,
                color = if (message.fromMe) Color.White else AceColors.ink,
                modifier = Modifier.padding(top = if (message.fromMe) 0.dp else 2.dp)
            )
            Text(
                timeLabel(message.sentAtEpochMillis),
                fontSize = 10.sp,
                color = if (message.fromMe) Color(0xFFC9D8E3) else AceColors.inkSoft,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun timeLabel(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val hour12 = if (dt.hour % 12 == 0) 12 else dt.hour % 12
    val suffix = if (dt.hour < 12) "AM" else "PM"
    val minute = dt.minute.toString().padStart(2, '0')
    return "$hour12:$minute $suffix"
}
