package com.aceclub.teamapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aceclub.teamapp.data.EventType
import com.aceclub.teamapp.data.PaymentStatus
import com.aceclub.teamapp.data.RsvpResponse
import com.aceclub.teamapp.ui.theme.AceColors

private data class Tone(val bg: androidx.compose.ui.graphics.Color, val fg: androidx.compose.ui.graphics.Color, val label: String)

private fun toneFor(paymentStatus: PaymentStatus): Tone = when (paymentStatus) {
    PaymentStatus.PAID -> Tone(AceColors.successBg, AceColors.success, "Paid")
    PaymentStatus.DUE -> Tone(AceColors.warnBg, AceColors.warn, "Due")
    PaymentStatus.OVERDUE -> Tone(AceColors.dangerBg, AceColors.danger, "Overdue")
}

private fun toneFor(rsvp: RsvpResponse?): Tone = when (rsvp) {
    RsvpResponse.YES -> Tone(AceColors.successBg, AceColors.success, "Going")
    RsvpResponse.NO -> Tone(AceColors.dangerBg, AceColors.danger, "Can't go")
    null -> Tone(AceColors.sand, AceColors.inkSoft, "No response")
}

private fun toneFor(type: EventType): Tone = when (type) {
    EventType.PRACTICE -> Tone(AceColors.sand, AceColors.court, "Practice")
    EventType.GAME -> Tone(AceColors.warnBg, AceColors.volleyDeep, "Game")
    EventType.TOURNAMENT -> Tone(AceColors.goldBg, androidx.compose.ui.graphics.Color(0xFF9A7414), "Tournament")
}

@Composable
fun PaymentBadge(status: PaymentStatus) = TonedBadge(toneFor(status))

@Composable
fun RsvpBadge(response: RsvpResponse?) = TonedBadge(toneFor(response))

@Composable
fun EventTypeBadge(type: EventType) = TonedBadge(toneFor(type))

@Composable
private fun TonedBadge(tone: Tone) {
    Text(
        text = tone.label,
        color = tone.fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(tone.bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
