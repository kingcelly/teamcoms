package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun NewAnnouncementScreen(
    teams: List<Team>,
    onCancel: () -> Unit,
    onPost: (title: String, body: String, teamId: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var teamId by remember { mutableStateOf<String?>(null) } // null = all teams

    val canPost = title.trim().isNotEmpty() && body.trim().isNotEmpty()

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) { Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = AceColors.ink) }
            Text("New announcement", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Button(
                onClick = { onPost(title.trim(), body.trim(), teamId) },
                enabled = canPost,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                elevation = null
            ) { Text("Post", color = AceColors.volleyDeep, fontWeight = FontWeight.ExtraBold) }
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            SectionLabel("Send to")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipView("All teams", teamId == null) { teamId = null }
                }
                items(teams) { t ->
                    FilterChipView(t.name, teamId == t.id) { teamId = t.id }
                }
            }

            SectionLabel("Title", topPadding = 16.dp)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Practice cancelled Friday") },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel("Message", topPadding = 16.dp)
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                placeholder = { Text("Add the details players and parents need...") },
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
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
