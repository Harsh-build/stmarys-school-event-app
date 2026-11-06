package com.example.data.model

enum class EventCategory(val displayName: String) {
    ACADEMIC("Academic"),
    CULTURAL("Cultural"),
    SPORTS("Sports"),
    SCIENCE("Science"),
    TECHNOLOGY("Technology"),
    CODING("Coding"),
    ROBOTICS("Robotics"),
    ART_AND_CRAFT("Art & Craft"),
    MUSIC_AND_DANCE("Music & Dance"),
    DEBATE("Debate"),
    QUIZ("Quiz"),
    HACKATHON("Hackathon"),
    CELEBRATION("Celebration"),
    OTHER("Other")
}

enum class EventStatus(val label: String) {
    PUBLISHED("Published"),
    DRAFT("Draft"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class RegistrationType {
    INDIVIDUAL,
    TEAM
}

data class EventItem(
    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val category: EventCategory = EventCategory.ACADEMIC,
    val imageUrl: String = "",
    val venue: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val registrationDeadline: String = "",
    val maxParticipants: Int = 100,
    val currentParticipants: Int = 0,
    val eligibility: String = "Classes 6th to 12th",
    val rules: String = "1. School uniform/dress code required.\n2. Carry student ID card.\n3. Arrive 15 minutes prior.",
    val coordinators: List<String> = listOf("Event Coordinator"),
    val registrationType: RegistrationType = RegistrationType.INDIVIDUAL,
    val eventStatus: EventStatus = EventStatus.PUBLISHED,
    val createdBy: String = "Admin",
    val createdAt: String = "2026-11-01"
)
