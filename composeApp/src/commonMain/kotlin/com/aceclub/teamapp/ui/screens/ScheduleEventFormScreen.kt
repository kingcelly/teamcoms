package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.EventType
import com.aceclub.teamapp.data.ScheduleEvent
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.schedule
import com.aceclub.teamapp.data.teams
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScheduleEventFormScreen(
    teams: List<Team>,
    event: ScheduleEvent?,
    onCancel: () -> Unit,
    onSave: (teamId: String, type: EventType, title: String, location: String, startEpochMillis: Long, endEpochMillis: Long) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isEditing = event != null
    var title by remember { mutableStateOf(event?.title ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var type by remember { mutableStateOf(event?.type ?: EventType.PRACTICE) }
    var teamId by remember { mutableStateOf(event?.teamId ?: teams.first().id) }
    var date by remember { mutableStateOf(event?.let { dateText(it.startEpochMillis) } ?: "") }
    var startTime by remember { mutableStateOf(event?.let { timeText(it.startEpochMillis) } ?: "") }
    var endTime by remember { mutableStateOf(event?.let { timeText(it.endEpochMillis) } ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    val canSave = title.trim().isNotEmpty() && location.trim().isNotEmpty() &&
        date.trim().isNotEmpty() && startTime.trim().isNotEmpty() && endTime.trim().isNotEmpty()

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) { Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = AceColors.ink) }
            Text(if (isEditing) "Edit event" else "New event", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Button(
                onClick = {
                    val start = parseDateTime(date, startTime)
                    val end = parseDateTime(date, endTime)
                    error = when {
                        start == null || end == null -> "Use date as YYYY-MM-DD and time as HH:MM (24-hour)."
                        end <= start -> "End time must be after start time."
                        else -> null
                    }
                    if (start != null && end != null && error == null) {
                        onSave(teamId, type, title.trim(), location.trim(), start, end)
                    }
                },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                elevation = null
            ) { Text("Save", color = AceColors.volleyDeep, fontWeight = FontWeight.ExtraBold) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            SectionLabel("Type")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EventType.entries.toList()) { t ->
                    FilterChipView(eventTypeLabel(t), type == t) { type = t }
                }
            }

            SectionLabel("Team", topPadding = 16.dp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(teams) { t ->
                    FilterChipView(t.name, teamId == t.id) { teamId = t.id }
                }
            }

            SectionLabel("Title", topPadding = 16.dp)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Weight room session") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel("Location", topPadding = 16.dp)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("e.g. West Gym") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel("Date", topPadding = 16.dp)
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel("Time", topPadding = 16.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    placeholder = { Text("Start, e.g. 17:30") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    placeholder = { Text("End, e.g. 19:00") },
                    modifier = Modifier.weight(1f)
                )
            }

            val currentError = error
            if (currentError != null) {
                Text(currentError, fontSize = 12.sp, color = AceColors.danger, modifier = Modifier.padding(top = 10.dp))
            }

            if (isEditing && onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AceColors.danger),
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp)
                ) { Text("Delete event", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: Dp = 0.dp) {
    Text(
        text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = AceColors.inkSoft,
        modifier = Modifier.padding(top = topPadding, bottom = 8.dp)
    )
}

@Composable
private fun FilterChipView(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = AceColors.court), shape = RoundedCornerShape(999.dp)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    } else {
        OutlinedButton(onClick = onClick, shape = RoundedCornerShape(999.dp)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.ink)
        }
    }
}

private fun eventTypeLabel(type: EventType): String = when (type) {
    EventType.PRACTICE -> "Practice"
    EventType.WEIGHTLIFTING -> "Weightlifting"
    EventType.GAME -> "Game"
    EventType.TOURNAMENT -> "Tournament"
}

private fun dateText(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    return "${dt.year}-$month-$day"
}

private fun timeText(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

private fun parseDateTime(dateText: String, timeText: String): Long? {
    val dateParts = dateText.trim().split("-")
    if (dateParts.size != 3) return null
    val year = dateParts[0].toIntOrNull() ?: return null
    val month = dateParts[1].toIntOrNull() ?: return null
    val day = dateParts[2].toIntOrNull() ?: return null

    val timeParts = timeText.trim().split(":")
    if (timeParts.size != 2) return null
    val hour = timeParts[0].toIntOrNull() ?: return null
    val minute = timeParts[1].toIntOrNull() ?: return null

    if (month !in 1..12 || day !in 1..31 || hour !in 0..23 || minute !in 0..59) return null

    return try {
        LocalDateTime(year, month, day, hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    } catch (e: IllegalArgumentException) {
        null
    }
}

@Preview
@Composable
private fun ScheduleEventFormScreenPreview() {
    AceVolleyballTheme {
        ScheduleEventFormScreen(
            teams = teams,
            event = schedule.first(),
            onCancel = {},
            onSave = { _, _, _, _, _, _ -> },
            onDelete = {}
        )
    }
}
