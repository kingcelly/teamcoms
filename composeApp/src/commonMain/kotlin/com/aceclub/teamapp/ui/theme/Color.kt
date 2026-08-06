package com.aceclub.teamapp.ui.theme

import androidx.compose.ui.graphics.Color

// Design tokens — "night match" palette: deep court navy, volleyball orange,
// sand lines. Built for a volleyball club, not a generic Material default.
object AceColors {
    val court = Color(0xFF0F3A57)       // primary — deep court navy
    val courtDark = Color(0xFF0A2740)   // headers, active tab
    val volley = Color(0xFFFF8A24)      // accent — volleyball orange
    val volleyDeep = Color(0xFFE06A0F)
    val sand = Color(0xFFF2E8D8)        // warm secondary surface
    val bg = Color(0xFFFAF7F2)          // app background
    val surface = Color(0xFFFFFFFF)
    val ink = Color(0xFF182430)         // primary text
    val inkSoft = Color(0xFF5C6B78)     // secondary text
    val line = Color(0xFFE7DFCF)        // hairline dividers
    val success = Color(0xFF2E9E6F)     // paid / confirmed
    val successBg = Color(0xFFE4F3EC)
    val warn = Color(0xFFE0602A)        // due soon
    val warnBg = Color(0xFFFCEDE0)
    val danger = Color(0xFFD14343)      // overdue / cancelled
    val dangerBg = Color(0xFFFBE6E6)
    val gold = Color(0xFFD8A83A)        // tournament / highlight
    val goldBg = Color(0xFFF4E9CE)
}
