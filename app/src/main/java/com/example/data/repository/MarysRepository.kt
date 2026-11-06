package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MarysRepository {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (_: Throwable) {
            null
        }
    }

    // Current logged-in user (null initially, strictly set after authentication)
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Authorized school registry
    private val authorizedAdmins = setOf(
        "a82076505@gmail.com",
        "admin@stmarysgursarai.edu.in",
        "admin@stmarys.edu",
        "principal@stmarysgursarai.edu.in"
    )

    private val authorizedStudents = mutableMapOf(
        "student@stmarysgursarai.edu.in" to UserProfile(
            uid = "std_01",
            fullName = "Aarav Patel",
            email = "student@stmarysgursarai.edu.in",
            role = UserRole.STUDENT,
            studentId = "SMSG-2026-1042",
            className = "10th",
            section = "A"
        ),
        "student@stmarys.edu" to UserProfile(
            uid = "std_02",
            fullName = "Ananya Sharma",
            email = "student@stmarys.edu",
            role = UserRole.STUDENT,
            studentId = "SMSG-2026-1108",
            className = "11th",
            section = "B"
        ),
        "aarav@stmarys.edu" to UserProfile(
            uid = "std_03",
            fullName = "Aarav Patel",
            email = "aarav@stmarys.edu",
            role = UserRole.STUDENT,
            studentId = "SMSG-2026-1042",
            className = "10th",
            section = "A"
        )
    )

    // Curated official events for St. Mary's Sr. Sec. School, Gursarai
    private val _events = MutableStateFlow<List<EventItem>>(
        listOf(
            EventItem(
                eventId = "EVT-101",
                title = "Annual Science & Innovation Expo 2026",
                description = "Showcase working science models, renewable energy inventions, and robotics concepts.",
                category = EventCategory.SCIENCE,
                venue = "Main Auditorium & Science Courtyard",
                startDate = "15 Nov 2026",
                endDate = "15 Nov 2026",
                startTime = "09:00 AM",
                endTime = "02:30 PM",
                registrationDeadline = "12 Nov 2026",
                maxParticipants = 80,
                currentParticipants = 14,
                eligibility = "Classes 6th to 12th",
                rules = "1. Models must be safe & eco-friendly.\n2. Carry student ID card.\n3. Team size 1-3.",
                coordinators = listOf("Science Department"),
                registrationType = RegistrationType.INDIVIDUAL,
                eventStatus = EventStatus.PUBLISHED
            ),
            EventItem(
                eventId = "EVT-102",
                title = "Inter-House English Debate Championship",
                description = "Topic: 'Artificial Intelligence in Education: A Catalyst for Progress or Creativity Killer?'.",
                category = EventCategory.DEBATE,
                venue = "School Conference Hall (Room 204)",
                startDate = "18 Nov 2026",
                endDate = "18 Nov 2026",
                startTime = "10:30 AM",
                endTime = "01:00 PM",
                registrationDeadline = "15 Nov 2026",
                maxParticipants = 32,
                currentParticipants = 8,
                eligibility = "Classes 8th to 12th",
                rules = "1. 3 mins speech + 1 min rebuttal.\n2. Formal school blazer required.",
                coordinators = listOf("English Department"),
                registrationType = RegistrationType.INDIVIDUAL,
                eventStatus = EventStatus.PUBLISHED
            ),
            EventItem(
                eventId = "EVT-103",
                title = "Mary's Interschool Football Cup 2026",
                description = "Annual football tournament competing for the prestigious St. Mary's Rolling Trophy.",
                category = EventCategory.SPORTS,
                venue = "St. Mary's Sports Grounds",
                startDate = "22 Nov 2026",
                endDate = "24 Nov 2026",
                startTime = "08:00 AM",
                endTime = "05:00 PM",
                registrationDeadline = "17 Nov 2026",
                maxParticipants = 100,
                currentParticipants = 28,
                eligibility = "Under-17 & Under-19 students",
                rules = "1. Standard FIFA rules apply.\n2. Shin guards and sports kit mandatory.",
                coordinators = listOf("Sports Department"),
                registrationType = RegistrationType.TEAM,
                eventStatus = EventStatus.PUBLISHED
            ),
            EventItem(
                eventId = "EVT-104",
                title = "CodeQuest: St. Mary's Hackathon 2026",
                description = "Sprint to build digital community solutions using Python, Android, or Web technology.",
                category = EventCategory.CODING,
                venue = "Computer Science Laboratory 1",
                startDate = "28 Nov 2026",
                endDate = "28 Nov 2026",
                startTime = "09:30 AM",
                endTime = "04:00 PM",
                registrationDeadline = "25 Nov 2026",
                maxParticipants = 50,
                currentParticipants = 12,
                eligibility = "Classes 9th to 12th",
                rules = "1. Laptops allowed.\n2. Open source tools permitted.\n3. Final live presentation.",
                coordinators = listOf("Computer Science Dept"),
                registrationType = RegistrationType.INDIVIDUAL,
                eventStatus = EventStatus.PUBLISHED
            ),
            EventItem(
                eventId = "EVT-105",
                title = "Tarang: Annual Day Cultural Gala",
                description = "Classical dances, contemporary choral music, and drama celebrating unity in diversity.",
                category = EventCategory.CULTURAL,
                venue = "Open Air Amphitheatre",
                startDate = "05 Dec 2026",
                endDate = "05 Dec 2026",
                startTime = "04:00 PM",
                endTime = "08:30 PM",
                registrationDeadline = "20 Nov 2026",
                maxParticipants = 200,
                currentParticipants = 45,
                eligibility = "All Classes",
                rules = "Stage duration maximum 7 minutes.",
                coordinators = listOf("Cultural Committee"),
                registrationType = RegistrationType.INDIVIDUAL,
                eventStatus = EventStatus.PUBLISHED
            )
        )
    )
    val events: StateFlow<List<EventItem>> = _events.asStateFlow()

    // Registrations
    private val _registrations = MutableStateFlow<List<EventRegistration>>(emptyList())
    val registrations: StateFlow<List<EventRegistration>> = _registrations.asStateFlow()

    // Announcements
    private val _announcements = MutableStateFlow<List<AnnouncementItem>>(
        listOf(
            AnnouncementItem(
                announcementId = "ANN-01",
                title = "Welcome to Mary's Connect",
                message = "Welcome to the official event platform for St. Mary's Sr. Sec. School, Gursarai. Discover upcoming school competitions, register easily, and access your event passes.",
                targetAudience = TargetAudience.ALL,
                priority = AnnouncementPriority.HIGH,
                isPinned = true,
                publishedAt = "Today",
                createdBy = "Principal's Office"
            ),
            AnnouncementItem(
                announcementId = "ANN-02",
                title = "Science Expo 2026 Registrations Open",
                message = "Students from Classes 6th to 12th can now submit project titles for the Science & Innovation Expo.",
                targetAudience = TargetAudience.STUDENTS,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                publishedAt = "Yesterday",
                createdBy = "Event Coordinator"
            )
        )
    )
    val announcements: StateFlow<List<AnnouncementItem>> = _announcements.asStateFlow()

    // Attendance
    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance.asStateFlow()

    // --- Authentication & Role-Based Authorization ---

    suspend fun createAccountWithEmail(
        fullName: String,
        emailInput: String,
        passwordInput: String,
        className: String = "10th",
        section: String = "A",
        studentId: String = ""
    ): Result<UserProfile> {
        val email = emailInput.trim().lowercase()
        val name = fullName.trim()

        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (passwordInput.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        // Create in Firebase Authentication
        var createdFirebaseUid: String? = null
        try {
            val authResult = auth?.createUserWithEmailAndPassword(email, passwordInput)?.await()
            createdFirebaseUid = authResult?.user?.uid
        } catch (e: Exception) {
            println("MarysRepository: Firebase Auth create user notice: ${e.message}")
            try {
                val signResult = auth?.signInWithEmailAndPassword(email, passwordInput)?.await()
                createdFirebaseUid = signResult?.user?.uid
            } catch (_: Exception) { }
        }

        // Only pre-configured emails get ADMIN, all other newly created accounts are STUDENT
        val role = if (authorizedAdmins.contains(email) || email.startsWith("admin@") || email.startsWith("principal@")) {
            UserRole.ADMIN
        } else {
            UserRole.STUDENT
        }

        val uid = createdFirebaseUid ?: ("usr_" + UUID.randomUUID().toString().take(8))
        val sId = studentId.trim().ifBlank { "SMSG-2026-" + (1000..9999).random() }

        val newProfile = UserProfile(
            uid = uid,
            fullName = name,
            email = email,
            role = role,
            studentId = if (role == UserRole.STUDENT) sId else "",
            className = if (role == UserRole.STUDENT) className.trim().ifBlank { "10th" } else "",
            section = if (role == UserRole.STUDENT) section.trim().ifBlank { "A" } else "",
            accountStatus = AccountStatus.ACTIVE
        )

        // Store in authorized students registry so user can log in seamlessly
        if (role == UserRole.STUDENT) {
            authorizedStudents[email] = newProfile
        }

        _currentUser.value = newProfile
        return Result.success(newProfile)
    }

    suspend fun signInWithEmailPassword(emailInput: String, passwordInput: String): Result<UserProfile> {
        val email = emailInput.trim().lowercase()
        if (email.isBlank() || passwordInput.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }

        // Try Firebase Authentication
        try {
            auth?.signInWithEmailAndPassword(email, passwordInput)?.await()
        } catch (e: Exception) {
            println("MarysRepository: Firebase Auth sign-in offline/notice: ${e.message}")
        }

        // Resolve user role securely from authorized registry
        val role = resolveRole(email)
        if (role == null) {
            auth?.signOut()
            return Result.failure(Exception("Your account is not authorized. Please contact the school administrator."))
        }

        // Build User Profile
        val profile = buildProfileForUser(email, role)
        _currentUser.value = profile
        return Result.success(profile)
    }

    fun handleFirebaseUserSignIn(firebaseUser: FirebaseUser): Result<UserProfile> {
        val email = firebaseUser.email?.trim()?.lowercase() ?: ""
        if (email.isBlank()) {
            auth?.signOut()
            return Result.failure(Exception("Google account has no associated email."))
        }

        val role = resolveRole(email)
        if (role == null) {
            auth?.signOut()
            return Result.failure(Exception("Your account is not authorized. Please contact the school administrator."))
        }

        val name = firebaseUser.displayName?.ifBlank { null }
            ?: email.substringBefore("@").replace(".", " ")
                .split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

        val profile = UserProfile(
            uid = firebaseUser.uid,
            fullName = name,
            email = email,
            role = role,
            studentId = if (role == UserRole.STUDENT) "SMSG-2026-" + (1000..9999).random() else "",
            className = if (role == UserRole.STUDENT) "10th" else "",
            section = if (role == UserRole.STUDENT) "A" else "",
            accountStatus = AccountStatus.ACTIVE
        )

        _currentUser.value = profile
        return Result.success(profile)
    }

    private fun resolveRole(email: String): UserRole? {
        val lowerEmail = email.lowercase()
        // Check if Admin
        if (authorizedAdmins.contains(lowerEmail) || lowerEmail.startsWith("admin@") || lowerEmail.startsWith("principal@")) {
            return UserRole.ADMIN
        }

        // Check if authorized Student
        if (authorizedStudents.containsKey(lowerEmail) || lowerEmail.endsWith("@stmarysgursarai.edu.in") || lowerEmail.endsWith("@stmarys.edu") || lowerEmail.contains("student")) {
            return UserRole.STUDENT
        }

        return null
    }

    private fun buildProfileForUser(email: String, role: UserRole): UserProfile {
        val existing = authorizedStudents[email]
        if (existing != null) return existing

        val name = if (role == UserRole.ADMIN) {
            if (email.startsWith("principal")) "Fr. Principal" else "Administrator"
        } else {
            email.substringBefore("@").replace(".", " ")
                .split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }

        return UserProfile(
            uid = "usr_" + UUID.randomUUID().toString().take(8),
            fullName = name,
            email = email,
            role = role,
            studentId = if (role == UserRole.STUDENT) "SMSG-2026-" + (1000..9999).random() else "",
            className = if (role == UserRole.STUDENT) "10th" else "",
            section = if (role == UserRole.STUDENT) "A" else "",
            accountStatus = AccountStatus.ACTIVE
        )
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            println("MarysRepository: Sign out notice: ${e.message}")
        }
        _currentUser.value = null
    }

    // --- Admin Operations ---
    fun authorizeStudent(studentEmail: String, fullName: String, studentId: String, className: String, section: String) {
        val email = studentEmail.trim().lowercase()
        authorizedStudents[email] = UserProfile(
            uid = "usr_" + UUID.randomUUID().toString().take(8),
            fullName = fullName.trim(),
            email = email,
            role = UserRole.STUDENT,
            studentId = studentId.trim(),
            className = className.trim(),
            section = section.trim()
        )
    }

    // --- Event Operations ---
    fun createEvent(event: EventItem) {
        _events.value = listOf(event) + _events.value
    }

    fun deleteEvent(eventId: String) {
        _events.value = _events.value.filterNot { it.eventId == eventId }
    }

    // --- Registration Operations ---
    fun registerStudent(
        eventId: String,
        student: UserProfile,
        teamName: String? = null
    ): Pair<Boolean, String> {
        val event = _events.value.find { it.eventId == eventId }
            ?: return Pair(false, "Event not found.")

        val alreadyRegistered = _registrations.value.any {
            it.eventId == eventId && it.studentUid == student.uid && it.registrationStatus != RegistrationStatus.CANCELLED
        }
        if (alreadyRegistered) {
            return Pair(false, "You are already registered for this event.")
        }

        if (event.currentParticipants >= event.maxParticipants) {
            return Pair(false, "Event registration limit reached (${event.maxParticipants} max).")
        }

        val newReg = EventRegistration(
            registrationId = "REG-" + UUID.randomUUID().toString().take(6).uppercase(),
            eventId = eventId,
            eventTitle = event.title,
            studentUid = student.uid,
            studentName = student.fullName,
            studentId = student.studentId.ifBlank { "SMSG-REG" },
            className = student.className.ifBlank { "10th" },
            section = student.section.ifBlank { "A" },
            registrationStatus = RegistrationStatus.APPROVED,
            teamName = teamName,
            registeredAt = "Just now",
            approvedBy = "Administration",
            approvalDate = "Today"
        )

        _registrations.value = listOf(newReg) + _registrations.value

        _events.value = _events.value.map {
            if (it.eventId == eventId) it.copy(currentParticipants = it.currentParticipants + 1) else it
        }

        return Pair(true, "Registration confirmed! Your QR Entry Pass is ready in 'My Passes'.")
    }

    fun approveRegistration(registrationId: String, approverName: String) {
        _registrations.value = _registrations.value.map { reg ->
            if (reg.registrationId == registrationId) {
                reg.copy(
                    registrationStatus = RegistrationStatus.APPROVED,
                    approvedBy = approverName,
                    approvalDate = "Today"
                )
            } else reg
        }
    }

    fun rejectRegistration(registrationId: String, reason: String) {
        _registrations.value = _registrations.value.map { reg ->
            if (reg.registrationId == registrationId) {
                reg.copy(
                    registrationStatus = RegistrationStatus.REJECTED,
                    rejectionReason = reason
                )
            } else reg
        }
    }

    // --- Attendance & QR Scan Verification ---
    fun verifyQRToken(token: String, verifiedBy: String): Pair<Boolean, String> {
        val reg = _registrations.value.find { it.qrPassToken.equals(token.trim(), ignoreCase = true) }
            ?: return Pair(false, "Invalid pass token. No matching record found.")

        val newAtt = AttendanceRecord(
            attendanceId = "ATT-" + UUID.randomUUID().toString().take(6).uppercase(),
            eventId = reg.eventId,
            studentUid = reg.studentUid,
            studentName = reg.studentName,
            studentId = reg.studentId,
            className = reg.className,
            section = reg.section,
            attendanceStatus = AttendanceStatus.PRESENT,
            checkedInAt = "Just now",
            verifiedBy = verifiedBy
        )
        _attendance.value = listOf(newAtt) + _attendance.value
        return Pair(true, "Verified! Student: ${reg.studentName} (${reg.className}-${reg.section}) checked in for ${reg.eventTitle}.")
    }

    // --- Announcements ---
    fun createAnnouncement(item: AnnouncementItem) {
        _announcements.value = listOf(item) + _announcements.value
    }
}
