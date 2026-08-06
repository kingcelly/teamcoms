package com.aceclub.teamapp.data

import kotlinx.datetime.Instant

// Starter data so the app feels real out of the box.
// Replace with live data once a backend (Firebase/Supabase) is wired in — see README.

private fun iso(value: String): Long = Instant.parse(value).toEpochMilliseconds()

val teams = listOf(
    Team(id = "t1", name = "14U Storm"),
    Team(id = "t2", name = "16U Rally")
)

val roster = listOf(
    Player(
        id = "p1", teamId = "t1", name = "Maya Chen", number = 4, position = "Outside Hitter",
        parent = ParentContact("Lin Chen", "(270) 555-0114", "lin.chen@email.com")
    ),
    Player(
        id = "p2", teamId = "t1", name = "Ava Torres", number = 7, position = "Setter",
        parent = ParentContact("Diego Torres", "(270) 555-0129", "diego.torres@email.com")
    ),
    Player(
        id = "p3", teamId = "t1", name = "Nora Bishop", number = 11, position = "Libero",
        parent = ParentContact("Kate Bishop", "(270) 555-0142", "kate.bishop@email.com")
    ),
    Player(
        id = "p4", teamId = "t2", name = "Jordan Lee", number = 2, position = "Middle Blocker",
        parent = ParentContact("Susan Lee", "(270) 555-0177", "susan.lee@email.com")
    ),
    Player(
        id = "p5", teamId = "t2", name = "Ella Ramirez", number = 9, position = "Opposite",
        parent = ParentContact("Marco Ramirez", "(270) 555-0188", "marco.ramirez@email.com")
    )
)

val seedAnnouncements = listOf(
    Announcement(
        id = "a1", teamId = null, author = "Coach Reyes",
        title = "Gym change this Thursday",
        body = "Practice moves to the West Gym (door 3) this Thursday only. Same 5:30–7:00 time.",
        createdAtEpochMillis = iso("2026-08-01T18:30:00Z"), pinned = true
    ),
    Announcement(
        id = "a2", teamId = "t1", author = "Coach Reyes",
        title = "14U Storm — bring knee pads Saturday",
        body = "We are doing full floor defense drills Saturday. Knee pads are required.",
        createdAtEpochMillis = iso("2026-07-30T14:00:00Z")
    ),
    Announcement(
        id = "a3", teamId = null, author = "Club Office",
        title = "Fall league registration open",
        body = "Registration for the fall league is open through August 20th. Link is in the Payments tab.",
        createdAtEpochMillis = iso("2026-07-28T12:00:00Z")
    )
)

val schedule = listOf(
    ScheduleEvent(
        id = "e1", teamId = "t1", type = EventType.PRACTICE,
        title = "14U Storm Practice", location = "West Gym",
        startEpochMillis = iso("2026-08-06T22:30:00Z"), endEpochMillis = iso("2026-08-07T00:00:00Z")
    ),
    ScheduleEvent(
        id = "e2", teamId = "t2", type = EventType.PRACTICE,
        title = "16U Rally Practice", location = "Main Gym",
        startEpochMillis = iso("2026-08-05T23:00:00Z"), endEpochMillis = iso("2026-08-06T00:30:00Z")
    ),
    ScheduleEvent(
        id = "e3", teamId = "t1", type = EventType.GAME,
        title = "vs. Bowling Green Aces", location = "Southern Rec Center",
        startEpochMillis = iso("2026-08-09T17:00:00Z"), endEpochMillis = iso("2026-08-09T19:00:00Z")
    ),
    ScheduleEvent(
        id = "e4", teamId = "t2", type = EventType.TOURNAMENT,
        title = "Rally Invitational — Day 1", location = "Warren County Sports Complex",
        startEpochMillis = iso("2026-08-16T13:00:00Z"), endEpochMillis = iso("2026-08-16T22:00:00Z")
    )
)

val seedPayments = listOf(
    PaymentDue(id = "pay1", playerId = "p1", label = "Fall league dues", amount = 185, dueDate = "2026-08-15", status = PaymentStatus.DUE),
    PaymentDue(id = "pay2", playerId = "p2", label = "Fall league dues", amount = 185, dueDate = "2026-08-15", status = PaymentStatus.PAID),
    PaymentDue(id = "pay3", playerId = "p3", label = "Fall league dues", amount = 185, dueDate = "2026-08-15", status = PaymentStatus.OVERDUE),
    PaymentDue(id = "pay4", playerId = "p4", label = "Fall league dues", amount = 185, dueDate = "2026-08-15", status = PaymentStatus.PAID),
    PaymentDue(id = "pay5", playerId = "p5", label = "Tournament fee", amount = 60, dueDate = "2026-08-16", status = PaymentStatus.DUE)
)
