package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.MarysRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MarysRepository = MarysRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val events = repository.events
    val registrations = repository.registrations
    val announcements = repository.announcements
    val attendance = repository.attendance

    // 4 simple tabs: 0: Events, 1: My Passes / Registrations, 2: Notices, 3: Profile & Admin Tools
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Search and filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<EventCategory?>(null)
    val selectedCategory: StateFlow<EventCategory?> = _selectedCategory.asStateFlow()

    // Dialog states
    private val _selectedEventForDetail = MutableStateFlow<EventItem?>(null)
    val selectedEventForDetail: StateFlow<EventItem?> = _selectedEventForDetail.asStateFlow()

    private val _selectedEventForRegistration = MutableStateFlow<EventItem?>(null)
    val selectedEventForRegistration: StateFlow<EventItem?> = _selectedEventForRegistration.asStateFlow()

    private val _selectedRegistrationForPass = MutableStateFlow<EventRegistration?>(null)
    val selectedRegistrationForPass: StateFlow<EventRegistration?> = _selectedRegistrationForPass.asStateFlow()

    private val _showCreateEventDialog = MutableStateFlow(false)
    val showCreateEventDialog: StateFlow<Boolean> = _showCreateEventDialog.asStateFlow()

    private val _showQRScannerDialog = MutableStateFlow(false)
    val showQRScannerDialog: StateFlow<Boolean> = _showQRScannerDialog.asStateFlow()

    private val _showCreateAnnouncementDialog = MutableStateFlow(false)
    val showCreateAnnouncementDialog: StateFlow<Boolean> = _showCreateAnnouncementDialog.asStateFlow()

    // Feedback message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered events
    val filteredEvents: StateFlow<List<EventItem>> = combine(
        events,
        searchQuery,
        selectedCategory
    ) { allEvents, query, cat ->
        allEvents.filter { event ->
            val matchesQuery = query.isBlank() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.venue.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true)
            val matchesCategory = cat == null || event.category == cat
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSelectedTab(index: Int) { _selectedTab.value = index }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedCategory(cat: EventCategory?) { _selectedCategory.value = cat }

    fun showSnackbar(msg: String) { _snackbarMessage.value = msg }
    fun clearSnackbar() { _snackbarMessage.value = null }

    fun viewEventDetails(event: EventItem?) { _selectedEventForDetail.value = event }
    fun openRegistrationDialog(event: EventItem?) { _selectedEventForRegistration.value = event }
    fun viewEntryPass(reg: EventRegistration?) { _selectedRegistrationForPass.value = reg }

    fun setShowCreateEvent(show: Boolean) { _showCreateEventDialog.value = show }
    fun setShowQRScanner(show: Boolean) { _showQRScannerDialog.value = show }
    fun setShowCreateAnnouncement(show: Boolean) { _showCreateAnnouncementDialog.value = show }

    // --- Authentication ---

    fun signInWithEmailPassword(
        email: String,
        password: String,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.signInWithEmailPassword(email, password)
            result.onSuccess { profile ->
                onSuccess(profile)
                val roleName = if (profile.role == UserRole.ADMIN) "Administrator" else "Student"
                showSnackbar("Welcome, ${profile.fullName} ($roleName)")
            }.onFailure { exception ->
                onError(exception.localizedMessage ?: "Sign-in failed.")
            }
        }
    }

    fun handleGoogleSignInSuccess(
        firebaseUser: FirebaseUser,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val result = repository.handleFirebaseUserSignIn(firebaseUser)
        result.onSuccess { profile ->
            onSuccess(profile)
            val roleName = if (profile.role == UserRole.ADMIN) "Administrator" else "Student"
            showSnackbar("Signed in as ${profile.fullName} ($roleName)")
        }.onFailure { exception ->
            onError(exception.localizedMessage ?: "Your account is not authorized. Please contact the school administrator.")
        }
    }

    fun logout() {
        repository.logout()
        showSnackbar("Signed out successfully.")
    }

    // --- Event Operations ---

    fun createEvent(event: EventItem) {
        repository.createEvent(event)
        showSnackbar("Event '${event.title}' created successfully!")
    }

    fun deleteEvent(eventId: String) {
        repository.deleteEvent(eventId)
        showSnackbar("Event deleted.")
    }

    fun registerCurrentStudent(eventId: String, teamName: String? = null) {
        val user = currentUser.value
        if (user == null) {
            showSnackbar("Please sign in first.")
            return
        }
        val (success, msg) = repository.registerStudent(eventId, user, teamName)
        showSnackbar(msg)
    }

    fun approveRegistration(registrationId: String) {
        val approver = currentUser.value?.fullName ?: "Administrator"
        repository.approveRegistration(registrationId, approver)
        showSnackbar("Registration approved.")
    }

    fun rejectRegistration(registrationId: String, reason: String) {
        repository.rejectRegistration(registrationId, reason)
        showSnackbar("Registration rejected.")
    }

    fun verifyQRToken(token: String) {
        val verifier = currentUser.value?.fullName ?: "Staff"
        val (success, msg) = repository.verifyQRToken(token, verifier)
        showSnackbar(msg)
    }

    fun createAnnouncement(announcement: AnnouncementItem) {
        repository.createAnnouncement(announcement)
        showSnackbar("Notice posted successfully!")
    }
}
