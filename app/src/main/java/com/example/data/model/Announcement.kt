package com.example.data.model

enum class TargetAudience(val label: String) {
    ALL("All School"),
    STUDENTS("Students Only"),
    ADMINS("Administration Only")
}

enum class AnnouncementPriority(val label: String) {
    HIGH("High Priority"),
    NORMAL("Normal"),
    LOW("Notice")
}

data class AnnouncementItem(
    val announcementId: String = "",
    val title: String = "",
    val message: String = "",
    val targetAudience: TargetAudience = TargetAudience.ALL,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val isPinned: Boolean = false,
    val publishedAt: String = "2026-11-05 08:30 AM",
    val createdBy: String = "Principal's Office"
)
