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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.EventType
import com.aceclub.teamapp.data.ScheduleEvent
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.data.isStaff
import com.aceclub.teamapp.data.schedule
import com.aceclub.teamapp.data.teams
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.EventTypeBadge
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScheduleScreen(
    schedule: List<ScheduleEvent>,
    teams: List<Team>,
    currentTeamId: String?,
    role: UserRole,
    onAddEvent: () -> Unit = {},
    onEditEvent: (ScheduleEvent) -> Unit = {},
    onDeleteEvent: (eventId: String) -> Unit = {}
) {
    val grouped = remember(schedule, currentTeamId) {
        schedule
            .filter { currentTeamId == null || it.teamId == currentTeamId }
            .sortedBy { it.startEpochMillis }
            .groupBy { dayLabel(it.startEpochMillis) }
    }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(
            title = "Schedule",
            subtitle = currentTeamId?.let { id -> teams.find { it.id == id }?.name } ?: "All teams",
            trailing = if (role.isStaff) {
                {
                    IconButton(
                        onClick = onAddEvent,
                        modifier = Modifier.size(36.dp).background(AceColors.volley, RoundedCornerShape(18.dp))
                    ) { Icon(Icons.Filled.Add, contentDescription = "New event", tint = Color.White) }
                }
            } else null
        )

        if (role != UserRole.ADMIN && currentTeamId == null) {
            EmptyState(title = "You are currently not assigned to a team. If this is an error please contact your admin.")
        } else if (grouped.isEmpty()) {
            EmptyState(
                title = "Nothing on the calendar",
                subtitle = if (role.isStaff) "Tap + to schedule practice, weightlifting, games or tournaments." else "Practices, games and tournaments will show up here."
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                grouped.forEach { (day, events) ->
                    item {
                        Text(day.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(events, key = { it.id }) { event ->
                        EventCard(event, role, onEditEvent, onDeleteEvent)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: ScheduleEvent,
    role: UserRole,
    onEditEvent: (ScheduleEvent) -> Unit,
    onDeleteEvent: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(timeLabel(event.startEpochMillis), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AceColors.court, modifier = Modifier.width(68.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                EventTypeBadge(event.type)
                if (role.isStaff) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onEditEvent(event) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit event", tint = AceColors.inkSoft)
                        }
                        IconButton(onClick = { onDeleteEvent(event.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete event", tint = AceColors.danger)
                        }
                    }
                }
            }
            Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink, modifier = Modifier.padding(top = 6.dp, bottom = 4.dp))
            Text("📍 ${event.location}", fontSize = 13.sp, color = AceColors.inkSoft)
        }
    }
}

private fun localDateTime(epochMillis: Long) =
    Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())

private fun dayLabel(epochMillis: Long): String {
    val dt = localDateTime(epochMillis)
    val dayNames = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${dayNames[dt.dayOfWeek.ordinal]}, ${monthNames[dt.monthNumber - 1]} ${dt.dayOfMonth}"
}

private fun timeLabel(epochMillis: Long): String {
    val dt = localDateTime(epochMillis)
    val hour12 = if (dt.hour % 12 == 0) 12 else dt.hour % 12
    val suffix = if (dt.hour < 12) "AM" else "PM"
    val minute = dt.minute.toString().padStart(2, '0')
    return "$hour12:$minute $suffix"
}

@Preview
@Composable
private fun ScheduleScreenPreview() {
    AceVolleyballTheme {
        ScheduleScreen(
            schedule = schedule,
            teams = teams,
            currentTeamId = teams.first().id,
            role = UserRole.PARENT
        )
    }
}
