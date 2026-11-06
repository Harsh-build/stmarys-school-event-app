package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (EventItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(EventCategory.ACADEMIC) }
    var venue by remember { mutableStateOf("Main Auditorium") }
    var startDate by remember { mutableStateOf("25 Nov 2026") }
    var startTime by remember { mutableStateOf("10:00 AM") }
    var maxParticipants by remember { mutableStateOf("60") }
    var registrationDeadline by remember { mutableStateOf("22 Nov 2026") }
    var eligibility by remember { mutableStateOf("Classes 6th to 12th") }
    var rules by remember { mutableStateOf("1. School uniform mandatory.\n2. Carry student ID card.") }
    var registrationType by remember { mutableStateOf(RegistrationType.INDIVIDUAL) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_event_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create School Event",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PrimaryGreen
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title *") },
                    placeholder = { Text("e.g. Annual Science Exhibition") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        EventCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maxParticipants,
                        onValueChange = { maxParticipants = it },
                        label = { Text("Max Seats") },
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Event Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = registrationDeadline,
                    onValueChange = { registrationDeadline = it },
                    label = { Text("Registration Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = eligibility,
                    onValueChange = { eligibility = it },
                    label = { Text("Eligibility Criteria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Registration Type selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Type: ", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    FilterChip(
                        selected = registrationType == RegistrationType.INDIVIDUAL,
                        onClick = { registrationType = RegistrationType.INDIVIDUAL },
                        label = { Text("Individual") }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = registrationType == RegistrationType.TEAM,
                        onClick = { registrationType = RegistrationType.TEAM },
                        label = { Text("Team") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newEvent = EventItem(
                                eventId = "EVT-" + UUID.randomUUID().toString().take(5).uppercase(),
                                title = title.trim(),
                                description = description.trim().ifBlank { "Official school event for St. Mary's Gursarai students." },
                                category = category,
                                venue = venue.trim(),
                                startDate = startDate.trim(),
                                endDate = startDate.trim(),
                                startTime = startTime.trim(),
                                endTime = "02:00 PM",
                                registrationDeadline = registrationDeadline.trim(),
                                maxParticipants = maxParticipants.toIntOrNull() ?: 50,
                                eligibility = eligibility.trim(),
                                rules = rules.trim(),
                                registrationType = registrationType,
                                eventStatus = EventStatus.PUBLISHED
                            )
                            onSubmit(newEvent)
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Event")
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(
    event: EventItem,
    onDismiss: () -> Unit,
    onRegister: (() -> Unit)? = null,
    isRegistered: Boolean = false,
    registrationStatus: RegistrationStatus? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("event_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = event.category)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.description,
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Detail Rows
                DetailRow(icon = Icons.Default.CalendarMonth, label = "Date & Time", value = "${event.startDate} (${event.startTime} - ${event.endTime})")
                DetailRow(icon = Icons.Default.Place, label = "Venue", value = event.venue)
                DetailRow(icon = Icons.Default.Timelapse, label = "Registration Deadline", value = event.registrationDeadline)
                DetailRow(icon = Icons.Default.School, label = "Eligibility", value = event.eligibility)
                DetailRow(icon = Icons.Default.Groups, label = "Participation Type", value = event.registrationType.name)
                DetailRow(icon = Icons.Default.SupervisorAccount, label = "Event Coordinators", value = event.coordinators.joinToString(", "))

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Event Rules & Guidelines:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDark
                )
                Text(
                    text = event.rules,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (isRegistered) {
                    Surface(
                        color = PrimaryGreenLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "You are registered! Status: ${registrationStatus?.title ?: "Submitted"}",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreenDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                } else if (onRegister != null && event.eventStatus == EventStatus.PUBLISHED) {
                    Button(
                        onClick = {
                            onDismiss()
                            onRegister()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register for this Event")
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterEventDialog(
    event: EventItem,
    student: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (teamName: String?, teamMembers: List<String>) -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var teamMember1 by remember { mutableStateOf("") }
    var teamMember2 by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("register_event_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Event Registration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryGreen
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Student Profile Info (Read-only as per prompt instructions)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Student Details (from Profile):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Name: ${student.fullName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("Class & Section: ${student.className}-${student.section}", fontSize = 12.sp)
                        Text("Admission ID: ${student.studentId}", fontSize = 12.sp)
                        Text("Email: ${student.email}", fontSize = 12.sp)
                    }
                }

                if (event.registrationType == RegistrationType.TEAM) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Team Information:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Team Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = teamMember1,
                        onValueChange = { teamMember1 = it },
                        label = { Text("Team Member 1 (Name & Class)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = teamMember2,
                        onValueChange = { teamMember2 = it },
                        label = { Text("Team Member 2 (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val members = listOfNotNull(
                            student.fullName,
                            teamMember1.trim().ifBlank { null },
                            teamMember2.trim().ifBlank { null }
                        )
                        onConfirm(teamName.trim().ifBlank { null }, members)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Registration")
                }
            }
        }
    }
}

@Composable
fun QRScanVerificationDialog(
    onDismiss: () -> Unit,
    onVerifyToken: (String) -> Unit
) {
    var tokenInput by remember { mutableStateOf("") }

    val sampleTokens = listOf(
        "MC-REG-001" to "Aarav Patel (Science Expo)",
        "MC-REG-002" to "Aarav Patel (Hackathon)",
        "MC-REG-004" to "Ananya Sharma (Debate)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("qr_scanner_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan Entry Pass",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryGreen
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    text = "Enter or scan the student's entry pass token to verify registration & mark attendance.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it.uppercase() },
                    label = { Text("Registration Token (e.g. MC-REG-001)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Quick Scan Shortcuts (Demo Passes):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                sampleTokens.forEach { (tok, desc) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { tokenInput = tok }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tok, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SchoolBlue)
                            Text(desc, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (tokenInput.isNotBlank()) {
                            onVerifyToken(tokenInput.trim())
                            onDismiss()
                        }
                    },
                    enabled = tokenInput.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify & Check-In Student")
                }
            }
        }
    }
}

@Composable
fun CsvPreviewDialog(
    filename: String,
    content: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = filename,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryGreen
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = content,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextDark,
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Downloaded Successfully")
                }
            }
        }
    }
}

@Composable
fun NotificationsDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onMarkRead: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .padding(8.dp)
                .testTag("notifications_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onMarkAllRead) {
                            Text("Mark all read", fontSize = 11.sp, color = PrimaryGreen)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No notifications yet", color = TextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(notifications) { notif ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (notif.isRead) Color(0xFFF8FAFC) else Color(0xFFE8F5E9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMarkRead(notif.notificationId) }
                            ) {
                                Row(modifier = Modifier.padding(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (notif.isRead) Color.Transparent else PrimaryGreen)
                                            .align(Alignment.Top)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextDark
                                        )
                                        Text(
                                            text = notif.message,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Text(
                                            text = notif.createdAt,
                                            fontSize = 9.sp,
                                            color = PrimaryGreen,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, color = TextMuted)
            Text(value, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PostAnnouncementDialog(
    onDismiss: () -> Unit,
    onSubmit: (AnnouncementItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var target by remember { mutableStateOf(TargetAudience.ALL) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Post School Notice", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title *") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Notice Details *") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Target Audience:", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TargetAudience.values().take(3).forEach { aud ->
                        FilterChip(
                            selected = target == aud,
                            onClick = { target = aud },
                            label = { Text(aud.label, fontSize = 10.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            onSubmit(
                                AnnouncementItem(
                                    announcementId = "ANN-" + UUID.randomUUID().toString().take(6),
                                    title = title.trim(),
                                    message = message.trim(),
                                    targetAudience = target,
                                    priority = AnnouncementPriority.NORMAL,
                                    publishedAt = "Just now",
                                    createdBy = "Staff Office"
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = title.isNotBlank() && message.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Notice")
                }
            }
        }
    }
}

