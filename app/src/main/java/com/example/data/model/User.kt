package com.example.data.model

enum class UserRole {
    ADMIN,
    STUDENT
}

enum class AccountStatus {
    ACTIVE,
    INACTIVE,
    PENDING
}

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val studentId: String = "",
    val className: String = "",
    val section: String = "",
    val phoneNumber: String = "",
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,
    val createdAt: String = "2026-11-05",
    val avatarInitial: String = fullName.firstOrNull()?.uppercase() ?: "U"
)
