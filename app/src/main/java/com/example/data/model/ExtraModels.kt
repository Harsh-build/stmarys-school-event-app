package com.example.data.model

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    EXCUSED("Excused")
}

data class AttendanceRecord(
    val attendanceId: String,
    val eventId: String,
    val studentUid: String,
    val studentName: String,
    val studentId: String,
    val className: String,
    val section: String,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.PRESENT,
    val checkedInAt: String = "09:15 AM",
    val verifiedBy: String = "Mr. Rajesh Sharma"
)

enum class WinnerPosition(val badgeText: String) {
    FIRST("1st Place (Winner)"),
    SECOND("2nd Place (Runner-up)"),
    THIRD("3rd Place (2nd Runner-up)"),
    SPECIAL_AWARD("Special Recognition"),
    CONSOLATION("Consolation Prize")
}

data class CompetitionResult(
    val resultId: String,
    val eventId: String,
    val eventTitle: String,
    val studentUid: String,
    val winnerName: String,
    val className: String,
    val position: WinnerPosition,
    val award: String,
    val description: String,
    val isPublished: Boolean = true,
    val publishedAt: String = "2026-11-04"
)

data class CertificateRecord(
    val certificateId: String,
    val studentUid: String,
    val studentName: String,
    val studentId: String,
    val className: String,
    val eventId: String,
    val eventTitle: String,
    val position: String,
    val certificateNumber: String = "SMSG/CERT/2026/" + certificateId.takeLast(5).uppercase(),
    val issueDate: String = "04 Nov 2026",
    val principalName: String = "Fr. Principal, St. Mary's Gursarai",
    val verificationStatus: Boolean = true
)

enum class NotificationType {
    EVENT_ANNOUNCEMENT,
    REGISTRATION_STATUS,
    REMINDER,
    RESULT_PUBLISHED
}

data class AppNotification(
    val notificationId: String,
    val recipientUid: String,
    val title: String,
    val message: String,
    val notificationType: NotificationType,
    val isRead: Boolean = false,
    val createdAt: String = "Just now"
)

data class GalleryPhoto(
    val photoId: String,
    val eventId: String,
    val eventTitle: String,
    val title: String,
    val caption: String,
    val date: String,
    val category: EventCategory,
    val isApproved: Boolean = true
)
