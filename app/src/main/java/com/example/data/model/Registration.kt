package com.example.data.model

enum class RegistrationStatus(val title: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled")
}

data class EventRegistration(
    val registrationId: String,
    val eventId: String,
    val eventTitle: String,
    val studentUid: String,
    val studentName: String,
    val studentId: String,
    val className: String,
    val section: String,
    val registrationStatus: RegistrationStatus = RegistrationStatus.PENDING,
    val teamName: String? = null,
    val teamMembers: List<String> = emptyList(),
    val rejectionReason: String? = null,
    val registeredAt: String = "2026-11-03 10:15 AM",
    val approvedBy: String? = null,
    val approvalDate: String? = null,
    val qrPassToken: String = "MC-REG-" + registrationId.takeLast(6).uppercase()
)
