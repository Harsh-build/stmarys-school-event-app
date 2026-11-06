package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EventCard
import com.example.ui.theme.*

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val events by viewModel.filteredEvents.collectAsState()
    val allRegistrations by viewModel.registrations.collectAsState()
    val announcements by viewModel.announcements.collectAsState()

    val userRegistrations = allRegistrations.filter { it.studentUid == currentUser?.uid }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.school_logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, PrimaryGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mary's Connect",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                RoleChip(role = currentUser?.role ?: UserRole.STUDENT)
                            }
                            Text(
                                text = "St. Mary's Sr. Sec. School, Gursarai",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
                    label = { Text("Events", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreenLight
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                val count = if (currentUser?.role == UserRole.STUDENT) userRegistrations.size else allRegistrations.size
                                if (count > 0) {
                                    Badge(containerColor = PrimaryGreen) { Text(count.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = "Passes")
                        }
                    },
                    label = { Text(if (currentUser?.role == UserRole.STUDENT) "My Passes" else "Registrations", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreenLight
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = "Notices") },
                    label = { Text("Notices", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreenLight
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreenLight
                    )
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            when (selectedTab) {
                0 -> SimpleEventsTab(viewModel, events, userRegistrations, currentUser?.role ?: UserRole.STUDENT)
                1 -> SimplePassesTab(viewModel, currentUser, userRegistrations, allRegistrations)
                2 -> SimpleNoticesTab(viewModel, announcements, currentUser?.role == UserRole.ADMIN)
                3 -> SimpleProfileTab(viewModel, currentUser)
            }
        }
    }
}

@Composable
fun SimpleEventsTab(
    viewModel: MainViewModel,
    events: List<EventItem>,
    userRegistrations: List<EventRegistration>,
    role: UserRole
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<EventCategory?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        // Welcome strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("School Events", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Explore and register for upcoming activities", fontSize = 11.sp, color = TextMuted)
            }
            if (role == UserRole.ADMIN) {
                Button(
                    onClick = { viewModel.setShowCreateEvent(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Event", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.setSearchQuery(it)
            },
            placeholder = { Text("Search events, venue or category...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clean Category Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = selectedCat == null,
                    onClick = {
                        selectedCat = null
                        viewModel.setSelectedCategory(null)
                    },
                    label = { Text("All", fontSize = 11.sp) }
                )
            }
            items(EventCategory.values().take(6)) { cat ->
                FilterChip(
                    selected = selectedCat == cat,
                    onClick = {
                        val next = if (selectedCat == cat) null else cat
                        selectedCat = next
                        viewModel.setSelectedCategory(next)
                    },
                    label = { Text(cat.displayName, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("No events match your search.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(events) { evt ->
                    val reg = userRegistrations.find { it.eventId == evt.eventId }
                    EventCard(
                        event = evt,
                        onViewDetails = { viewModel.viewEventDetails(evt) },
                        onRegister = { viewModel.openRegistrationDialog(evt) },
                        isRegistered = reg != null,
                        registrationStatus = reg?.registrationStatus,
                        onViewPass = { viewModel.viewEntryPass(reg) }
                    )
                }
            }
        }
    }
}

@Composable
fun SimplePassesTab(
    viewModel: MainViewModel,
    currentUser: UserProfile?,
    userRegistrations: List<EventRegistration>,
    allRegistrations: List<EventRegistration>
) {
    val isAdmin = currentUser?.role == UserRole.ADMIN
    val listToDisplay = if (isAdmin) allRegistrations else userRegistrations

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAdmin) "All Registrations (${listToDisplay.size})" else "My Event Passes (${listToDisplay.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = if (isAdmin) "Verify students and manage entry passes" else "Digital QR entry passes for your registered activities",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            if (isAdmin) {
                Button(
                    onClick = { viewModel.setShowQRScanner(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan Pass", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (listToDisplay.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No passes found.", color = TextMuted, fontSize = 13.sp)
                    Text("Register for an event from the Events tab.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listToDisplay) { reg ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(reg.eventTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = reg.registrationStatus.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Student: ${reg.studentName} (${reg.className}-${reg.section})", fontSize = 12.sp, color = TextMuted)
                            Text("Entry Token: ${reg.qrPassToken}", fontSize = 11.sp, color = SchoolBlue, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { viewModel.viewEntryPass(reg) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open QR Entry Pass", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleNoticesTab(
    viewModel: MainViewModel,
    announcements: List<AnnouncementItem>,
    canPost: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notice Board (${announcements.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Official announcements from St. Mary's Gursarai", fontSize = 11.sp, color = TextMuted)
            }
            if (canPost) {
                Button(
                    onClick = { viewModel.setShowCreateAnnouncement(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Notice", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(announcements) { ann ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text(ann.publishedAt, fontSize = 10.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ann.message, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Issued by: ${ann.createdBy}", fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleProfileTab(
    viewModel: MainViewModel,
    currentUser: UserProfile?
) {
    val isAdmin = currentUser?.role == UserRole.ADMIN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreenLight)
                        .border(1.5.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.avatarInitial ?: "U",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreenDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(currentUser?.fullName ?: "User", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                Text(currentUser?.email ?: "", fontSize = 12.sp, color = TextMuted)

                Surface(
                    color = PrimaryGreenLight,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = currentUser?.role?.name ?: "STUDENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                if (currentUser?.role == UserRole.STUDENT) {
                    ProfileRow("Class & Section", "${currentUser.className}-${currentUser.section}")
                    ProfileRow("Student ID", currentUser.studentId.ifBlank { "SMSG-GEN" })
                } else {
                    ProfileRow("Access Level", "School Administrator")
                    ProfileRow("Permissions", "Event & Registration Management")
                }
                ProfileRow("School", "St. Mary's Sr. Sec. School, Gursarai")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Tools for Admin
        if (isAdmin) {
            Text("Administrator Quick Tools", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text("Create School Event", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        leadingContent = { Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = PrimaryGreen) },
                        modifier = Modifier.clickable { viewModel.setShowCreateEvent(true) }
                    )
                    ListItem(
                        headlineContent = { Text("Scan / Verify Entry Pass QR", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        leadingContent = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SchoolBlue) },
                        modifier = Modifier.clickable { viewModel.setShowQRScanner(true) }
                    )
                    ListItem(
                        headlineContent = { Text("Post New Announcement", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        leadingContent = { Icon(Icons.Default.Campaign, contentDescription = null, tint = SchoolOrange) },
                        modifier = Modifier.clickable { viewModel.setShowCreateAnnouncement(true) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign Out of Portal", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
    }
}

@Composable
fun RoleChip(role: UserRole) {
    val (bgColor, textColor) = when (role) {
        UserRole.ADMIN -> Pair(PrimaryGreenLight, PrimaryGreen)
        UserRole.STUDENT -> Pair(SchoolOrangeLight, SchoolOrange)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(4.dp)) {
        Text(
            text = role.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}
