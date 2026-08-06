package com.aceclub.teamapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    onMenuClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.court)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onMenuClick != null) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Open menu", tint = Color.White)
            }
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 8.dp)) {
            Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Text(subtitle, color = Color(0xFFC9D8E3), fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
        trailing?.invoke()
    }
}
