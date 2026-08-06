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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.EventType
import com.aceclub.teamapp.data.RsvpResponse
import com.aceclub.teamapp.data.ScheduleEvent
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.EventTypeBadge
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ScheduleScreen(
    schedule: List<ScheduleEvent>,
    teams: List<Team>,
    currentTeamId: String?,
    role: UserRole,
    rsvps: Map<String, RsvpResponse>,
    onMenuClick: () -> Unit,
    onRsvp: (eventId: String, response: RsvpResponse) -> Unit
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
            onMenuClick = onMenuClick
        )

        if (grouped.isEmpty()) {
            EmptyState(title = "Nothing on the calendar", subtitle = "Practices, games and tournaments will show up here.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                grouped.forEach { (day, events) ->
                    item {
                        Text(day.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(events, key = { it.id }) { event ->
                        EventCard(event, role, rsvps[event.id], onRsvp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: ScheduleEvent, role: UserRole, rsvp: RsvpResponse?, onRsvp: (String, RsvpResponse) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(timeLabel(event.startEpochMillis), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AceColors.court, modifier = Modifier.width(68.dp))
        Column(modifier = Modifier.weight(1f)) {
            EventTypeBadge(event.type)
            Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink, modifier = Modifier.padding(top = 6.dp, bottom = 4.dp))
            Text("📍 ${event.location}", fontSize = 13.sp, color = AceColors.inkSoft)

            if (role != UserRole.COACH) {
                Row(modifier = Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RsvpButton("Going", rsvp == RsvpResponse.YES) { onRsvp(event.id, RsvpResponse.YES) }
                    RsvpButton("Can't go", rsvp == RsvpResponse.NO) { onRsvp(event.id, RsvpResponse.NO) }
                }
            }
        }
    }
}

@Composable
private fun RsvpButton(label: String, active: Boolean, onClick: () -> Unit) {
    if (active) {
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = AceColors.sand), shape = RoundedCornerShape(999.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AceColors.ink)
        }
    } else {
        OutlinedButton(onClick = onClick, shape = RoundedCornerShape(999.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AceColors.inkSoft)
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
