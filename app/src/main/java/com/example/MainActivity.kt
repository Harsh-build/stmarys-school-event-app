package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.theme.StMarysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StMarysTheme {
                val viewModel: MainViewModel = viewModel()
                MarysConnectSimpleApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MarysConnectSimpleApp(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    // Dialog states
    val selectedEventForDetail by viewModel.selectedEventForDetail.collectAsState()
    val selectedEventForRegistration by viewModel.selectedEventForRegistration.collectAsState()
    val selectedRegistrationForPass by viewModel.selectedRegistrationForPass.collectAsState()
    val showCreateEvent by viewModel.showCreateEventDialog.collectAsState()
    val showQrScanner by viewModel.showQRScannerDialog.collectAsState()
    val showCreateAnnouncement by viewModel.showCreateAnnouncementDialog.collectAsState()

    val allRegistrations by viewModel.registrations.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (currentUser == null) {
                AuthScreen(
                    onSignInWithEmail = { email, password, onSuccess, onError ->
                        viewModel.signInWithEmailPassword(email, password, onSuccess, onError)
                    },
                    onCreateAccountWithEmail = { fullName, email, password, className, section, studentId, onSuccess, onError ->
                        viewModel.createAccountWithEmail(fullName, email, password, className, section, studentId, onSuccess, onError)
                    },
                    onGoogleSignInSuccess = { firebaseUser ->
                        viewModel.handleGoogleSignInSuccess(
                            firebaseUser = firebaseUser,
                            onSuccess = {},
                            onError = { err -> viewModel.showSnackbar(err) }
                        )
                    },
                    onErrorOccurred = { err ->
                        viewModel.showSnackbar(err)
                    }
                )
            } else {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    // --- Modals ---

    // 1. Create Event Dialog (Staff)
    if (showCreateEvent) {
        CreateEventDialog(
            onDismiss = { viewModel.setShowCreateEvent(false) },
            onSubmit = { newEvt ->
                viewModel.createEvent(newEvt)
            }
        )
    }

    // 2. Event Detail Dialog
    selectedEventForDetail?.let { evt ->
        val isReg = allRegistrations.any { it.eventId == evt.eventId && it.studentUid == currentUser?.uid }
        EventDetailDialog(
            event = evt,
            onDismiss = { viewModel.viewEventDetails(null) },
            onRegister = {
                viewModel.viewEventDetails(null)
                viewModel.openRegistrationDialog(evt)
            },
            isRegistered = isReg
        )
    }

    // 3. Register Event Dialog
    selectedEventForRegistration?.let { evt ->
        currentUser?.let { student ->
            RegisterEventDialog(
                event = evt,
                student = student,
                onDismiss = { viewModel.openRegistrationDialog(null) },
                onConfirm = { teamName, _ ->
                    viewModel.registerCurrentStudent(evt.eventId, teamName)
                }
            )
        }
    }

    // 4. Digital QR Entry Pass Dialog
    selectedRegistrationForPass?.let { reg ->
        QRCodeDialog(
            registration = reg,
            onDismiss = { viewModel.viewEntryPass(null) },
            onDownload = {
                viewModel.showSnackbar("QR Pass saved!")
            }
        )
    }

    // 5. QR Scanner Check-In Dialog
    if (showQrScanner) {
        QRScanVerificationDialog(
            onDismiss = { viewModel.setShowQRScanner(false) },
            onVerifyToken = { token ->
                viewModel.verifyQRToken(token)
            }
        )
    }

    // 6. Post Announcement Dialog
    if (showCreateAnnouncement) {
        PostAnnouncementDialog(
            onDismiss = { viewModel.setShowCreateAnnouncement(false) },
            onSubmit = { announcement ->
                viewModel.createAnnouncement(announcement)
            }
        )
    }
}
