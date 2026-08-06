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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.PaymentDue
import com.aceclub.teamapp.data.PaymentStatus
import com.aceclub.teamapp.data.Player
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.PaymentBadge
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors

@Composable
fun PaymentsScreen(
    payments: List<PaymentDue>,
    roster: List<Player>,
    currentTeamId: String?,
    role: UserRole,
    onMenuClick: () -> Unit,
    onMarkPaid: (paymentId: String) -> Unit
) {
    val visible = remember(payments, roster, currentTeamId, role) {
        if (role == UserRole.COACH) payments
        else {
            val teamPlayerIds = roster.filter { it.teamId == currentTeamId }.map { it.id }.toSet()
            payments.filter { it.playerId in teamPlayerIds }
        }
    }
    val totalDue = visible.filter { it.status != PaymentStatus.PAID }.sumOf { it.amount }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(
            title = "Payments",
            subtitle = if (totalDue > 0) "$$totalDue outstanding" else "All caught up",
            onMenuClick = onMenuClick
        )

        if (visible.isEmpty()) {
            EmptyState(title = "No dues right now", subtitle = "Fees and payment requests will show up here.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visible, key = { it.id }) { payment ->
                    PaymentCard(payment, roster.find { it.id == payment.playerId }?.name ?: "", role, onMarkPaid)
                }
            }
        }

        Text(
            "Demo only — \"Mark paid\" updates this app's local record. Connect a real processor (Stripe, Square) to collect actual payments.",
            fontSize = 11.sp,
            color = AceColors.inkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun PaymentCard(payment: PaymentDue, playerName: String, role: UserRole, onMarkPaid: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(payment.label, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.ink)
            Text(playerName, fontSize = 13.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 2.dp))
            Text("Due ${payment.dueDate}", fontSize = 12.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 6.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$${payment.amount}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court)
            PaymentBadge(payment.status)
            if (payment.status != PaymentStatus.PAID && role != UserRole.COACH) {
                Button(
                    onClick = { onMarkPaid(payment.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = AceColors.volley),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Mark paid", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
        }
    }
}
