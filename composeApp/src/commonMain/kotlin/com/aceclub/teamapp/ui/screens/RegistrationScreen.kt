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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RegistrationScreen(
    onRegister: (contact: String, name: String, role: UserRole) -> Boolean,
    onNavigateToLogin: () -> Unit
) {
    var contact by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.PARENT) }
    var error by remember { mutableStateOf<String?>(null) }

    val canSubmit = contact.trim().isNotEmpty() && name.trim().isNotEmpty()

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
            Text("Create your account", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Join your team's announcements, schedule, roster and dues.",
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
                FieldLabel("Email or phone")
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it; error = null },
                    placeholder = { Text("e.g. kate.bishop@email.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AceColors.bg,
                        unfocusedContainerColor = AceColors.bg
                    )
                )

                FieldLabel("Your name", topPadding = 18.dp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Kate Bishop") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AceColors.bg,
                        unfocusedContainerColor = AceColors.bg
                    )
                )

                FieldLabel("I am a", topPadding = 18.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ChoiceChip("Player", role == UserRole.PLAYER, Modifier.weight(1f)) { role = UserRole.PLAYER }
                    ChoiceChip("Parent", role == UserRole.PARENT, Modifier.weight(1f)) { role = UserRole.PARENT }
                    ChoiceChip("Coach", role == UserRole.COACH, Modifier.weight(1f)) { role = UserRole.COACH }
                }

                val currentError = error
                if (currentError != null) {
                    Text(currentError, fontSize = 12.sp, color = AceColors.danger, modifier = Modifier.padding(top = 10.dp))
                }

                Button(
                    onClick = {
                        val success = onRegister(contact.trim(), name.trim(), role)
                        if (!success) error = "An account with that email or phone already exists."
                    },
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = AceColors.volley),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 22.dp)
                ) {
                    Text("Create Account", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
                }

                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Already have an account? Log in", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AceColors.court)
                }
            }
        }
    }
}

@Preview
@Composable
private fun RegistrationScreenPreview() {
    AceVolleyballTheme {
        RegistrationScreen(onRegister = { _, _, _ -> true }, onNavigateToLogin = {})
    }
}
