package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.Team
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun LoginScreen(
    teams: List<Team>,
    onLogin: (name: String, role: UserRole, teamId: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.PARENT) }
    var teamId by remember { mutableStateOf(teams.first().id) }

    Box(
        modifier = Modifier.fillMaxSize().background(AceColors.court).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .size(44.dp)
                    .background(AceColors.volley, RoundedCornerShape(50))
            )
            Text("Ace Volleyball Club", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Announcements, schedules, rosters and dues — all in one place.",
                color = Color(0xFFC9D8E3),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp)
            )

            Column(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
                    .background(AceColors.surface, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                FieldLabel("Your name")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Kate Bishop") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AceColors.bg,
                        unfocusedContainerColor = AceColors.bg
                    )
                )

                FieldLabel("I am a", topPadding = 18.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ChoiceChip("Parent / Player", role == UserRole.PARENT, Modifier.weight(1f)) { role = UserRole.PARENT }
                    ChoiceChip("Coach", role == UserRole.COACH, Modifier.weight(1f)) { role = UserRole.COACH }
                }

                FieldLabel("Team", topPadding = 18.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    teams.forEach { t ->
                        ChoiceChip(t.name, teamId == t.id, Modifier.weight(1f)) { teamId = t.id }
                    }
                }

                Button(
                    onClick = { onLogin(name.trim(), role, teamId) },
                    enabled = name.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = AceColors.volley),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 22.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
                }
                Text(
                    "Demo sign-in — no password needed. Swap in real accounts when you connect a backend.",
                    fontSize = 11.sp,
                    color = AceColors.inkSoft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = AceColors.inkSoft,
        modifier = Modifier.padding(top = topPadding, bottom = 8.dp)
    )
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = AceColors.court),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
        ) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
        ) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.ink) }
    }
}
