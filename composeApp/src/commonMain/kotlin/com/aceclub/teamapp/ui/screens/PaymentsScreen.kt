package com.aceclub.teamapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.PaymentDue
import com.aceclub.teamapp.data.PaymentStatus
import com.aceclub.teamapp.data.Player
import com.aceclub.teamapp.data.UserRole
import com.aceclub.teamapp.data.roster
import com.aceclub.teamapp.data.seedPayments
import com.aceclub.teamapp.data.teams
import com.aceclub.teamapp.ui.components.EmptyState
import com.aceclub.teamapp.ui.components.PaymentBadge
import com.aceclub.teamapp.ui.components.ScreenHeader
import com.aceclub.teamapp.ui.theme.AceColors
import com.aceclub.teamapp.ui.theme.AceVolleyballTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PaymentsScreen(
    payments: List<PaymentDue>,
    roster: List<Player>,
    currentTeamId: String?,
    role: UserRole,
    onBack: () -> Unit,
    onPayNow: (paymentId: String) -> Unit,
    onPayInFull: (paymentIds: List<String>) -> Unit
) {
    val visible = remember(payments, roster, currentTeamId, role) {
        if (role == UserRole.ADMIN) payments
        else {
            val teamPlayerIds = roster.filter { it.teamId == currentTeamId }.map { it.id }.toSet()
            payments.filter { it.playerId in teamPlayerIds }
        }
    }
    // Only show overdue items and the single next upcoming due — the rest of a plan's
    // future installments aren't due yet, so they stay hidden until they become "next".
    // Payment history goes in the separate, collapsible "Past transactions" section below.
    val nextAndOverdue = remember(visible) {
        val overdue = visible.filter { it.status == PaymentStatus.OVERDUE }.sortedBy { it.dueDate }
        val nextDue = visible.filter { it.status == PaymentStatus.DUE }.minByOrNull { it.dueDate }
        overdue + listOfNotNull(nextDue)
    }
    val pastTransactions = remember(visible) {
        visible.filter { it.status == PaymentStatus.PAID }.sortedByDescending { it.dueDate }
    }
    val unpaid = remember(visible) { visible.filter { it.status != PaymentStatus.PAID } }
    val totalDue = unpaid.sumOf { it.amount }
    var pastExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(AceColors.bg)) {
        ScreenHeader(title = "Payments", onBackClick = onBack)

        if (role != UserRole.ADMIN && currentTeamId == null) {
            EmptyState(title = "You are currently not assigned to a team. If this is an error please contact your admin.")
        } else if (visible.isEmpty()) {
            EmptyState(title = "No dues right now", subtitle = "Fees and payment requests will show up here.")
        } else {
            Column(modifier = Modifier.weight(1f)) {
                BalanceSummary(totalDue = totalDue)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (nextAndOverdue.isNotEmpty()) {
                        item { SectionLabel("Next & overdue") }
                        items(nextAndOverdue, key = { it.id }) { payment ->
                            PaymentCard(payment, roster.find { it.id == payment.playerId }?.name ?: "", role, onPayNow)
                        }
                    }
                    if (pastTransactions.isNotEmpty()) {
                        item {
                            CollapsibleSectionHeader(
                                title = "Past transactions",
                                count = pastTransactions.size,
                                expanded = pastExpanded,
                                onToggle = { pastExpanded = !pastExpanded },
                                topPadding = if (nextAndOverdue.isNotEmpty()) 8.dp else 0.dp
                            )
                        }
                        if (pastExpanded) {
                            items(pastTransactions, key = { it.id }) { payment ->
                                PaymentCard(payment, roster.find { it.id == payment.playerId }?.name ?: "", role, onPayNow)
                            }
                        }
                    }
                }
            }

            if (role == UserRole.PARENT && totalDue > 0) {
                Button(
                    onClick = { onPayInFull(unpaid.map { it.id }) },
                    colors = ButtonDefaults.buttonColors(containerColor = AceColors.court),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text("Pay in full — $$totalDue", fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        Text(
            "Demo only — payments update this app's local record. Connect a real processor (Stripe, Square) to collect actual payments.",
            fontSize = 11.sp,
            color = AceColors.inkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun BalanceSummary(totalDue: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(AceColors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, AceColors.line, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("BALANCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AceColors.inkSoft)
        if (totalDue > 0) {
            Text(
                "$$totalDue outstanding",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AceColors.court,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Text(
                "$0 — no action required",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AceColors.success,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AceColors.inkSoft,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun CollapsibleSectionHeader(title: String, count: Int, expanded: Boolean, topPadding: Dp, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(top = topPadding, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${title.uppercase()} ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AceColors.inkSoft)
        Text(if (expanded) "▾" else "▸", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court)
    }
}

@Composable
private fun PaymentCard(payment: PaymentDue, playerName: String, role: UserRole, onPayNow: (String) -> Unit) {
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
            Text(planLabel(payment), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AceColors.volleyDeep, modifier = Modifier.padding(top = 4.dp))
            Text("Due ${payment.dueDate}", fontSize = 12.sp, color = AceColors.inkSoft, modifier = Modifier.padding(top = 6.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$${payment.amount}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AceColors.court)
            PaymentBadge(payment.status)
            if (payment.status != PaymentStatus.PAID && role == UserRole.PARENT) {
                Button(
                    onClick = { onPayNow(payment.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = AceColors.volley),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Pay now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
        }
    }
}

private fun planLabel(payment: PaymentDue): String =
    if (payment.totalInstallments <= 1) "One-time payment"
    else "Installment ${payment.installmentNumber} of ${payment.totalInstallments}"

@Preview
@Composable
private fun PaymentsScreenPreview() {
    AceVolleyballTheme {
        PaymentsScreen(
            payments = seedPayments,
            roster = roster,
            currentTeamId = teams.first().id,
            role = UserRole.PARENT,
            onBack = {},
            onPayNow = {},
            onPayInFull = {}
        )
    }
}
