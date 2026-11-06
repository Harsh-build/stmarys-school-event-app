package com.example

import com.example.data.model.AttendanceStatus
import com.example.data.model.RegistrationStatus
import com.example.data.model.UserRole
import com.example.data.repository.MarysRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `test authorized student login and role resolution`() = runBlocking {
        val repo = MarysRepository()
        assertEquals(null, repo.currentUser.value)

        val result = repo.signInWithEmailPassword("student@stmarys.edu", "samplepass123")
        assertTrue(result.isSuccess)
        val profile = result.getOrNull()
        assertNotNull(profile)
        assertEquals(UserRole.STUDENT, profile?.role)
        assertEquals("Ananya Sharma", profile?.fullName)
        assertEquals(profile?.uid, repo.currentUser.value?.uid)
    }

    @Test
    fun `test admin authorized login`() = runBlocking {
        val repo = MarysRepository()

        val result = repo.signInWithEmailPassword("a82076505@gmail.com", "adminpass")
        assertTrue(result.isSuccess)
        val profile = result.getOrNull()
        assertNotNull(profile)
        assertEquals(UserRole.ADMIN, profile?.role)
    }

    @Test
    fun `test unauthorized user rejection`() = runBlocking {
        val repo = MarysRepository()

        val result = repo.signInWithEmailPassword("stranger@randomdomain.com", "any_password")
        assertTrue(result.isFailure)
        assertEquals(
            "Your account is not authorized. Please contact the school administrator.",
            result.exceptionOrNull()?.message
        )
        assertEquals(null, repo.currentUser.value)
    }

    @Test
    fun `test event registration and QR token check-in`() = runBlocking {
        val repo = MarysRepository()

        val signInResult = repo.signInWithEmailPassword("student@stmarys.edu", "pass")
        val student = signInResult.getOrThrow()

        // Register for Science Expo
        val (success, msg) = repo.registerStudent("EVT-101", student)
        assertTrue(success)

        val reg = repo.registrations.value.find { it.eventId == "EVT-101" && it.studentUid == student.uid }
        assertNotNull(reg)
        assertEquals(RegistrationStatus.APPROVED, reg!!.registrationStatus)
        assertTrue(reg.qrPassToken.isNotBlank())

        // Verify Check-in via QR Pass Token
        val (checkedIn, checkInMsg) = repo.verifyQRToken(reg.qrPassToken, "Admin Official")
        assertTrue(checkedIn)

        // Verify attendance record
        val att = repo.attendance.value.find { it.eventId == "EVT-101" && it.studentUid == student.uid }
        assertNotNull(att)
        assertEquals(AttendanceStatus.PRESENT, att!!.attendanceStatus)
    }

    @Test
    fun `test create new account through email`() = runBlocking {
        val repo = MarysRepository()

        val result = repo.createAccountWithEmail(
            fullName = "Harsh Patel",
            emailInput = "harsh.patel@stmarys.edu",
            passwordInput = "secret123",
            className = "11th",
            section = "C",
            studentId = "SMSG-2026-9901"
        )

        assertTrue(result.isSuccess)
        val profile = result.getOrNull()
        assertNotNull(profile)
        assertEquals("Harsh Patel", profile?.fullName)
        assertEquals("harsh.patel@stmarys.edu", profile?.email)
        assertEquals(UserRole.STUDENT, profile?.role)
        assertEquals("11th", profile?.className)
        assertEquals("C", profile?.section)
        assertEquals(profile?.uid, repo.currentUser.value?.uid)

        // Verify that they can log out and log back in seamlessly with their registered email
        repo.logout()
        assertEquals(null, repo.currentUser.value)

        val reLogin = repo.signInWithEmailPassword("harsh.patel@stmarys.edu", "secret123")
        assertTrue(reLogin.isSuccess)
        assertEquals("Harsh Patel", reLogin.getOrNull()?.fullName)
    }
}
