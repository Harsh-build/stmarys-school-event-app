package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("stat_card_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: EventItem,
    onViewDetails: () -> Unit,
    onRegister: (() -> Unit)? = null,
    isRegistered: Boolean = false,
    registrationStatus: RegistrationStatus? = null,
    onViewPass: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("event_card_${event.eventId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Status / Registration State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = event.category)

                if (isRegistered && registrationStatus != null) {
                    RegistrationStatusBadge(status = registrationStatus)
                } else {
                    EventStatusBadge(status = event.eventStatus)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description Snippet
            Text(
                text = event.description,
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.startDate} • ${event.startTime}",
                    fontSize = 11.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = SchoolBlue,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.venue,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Participants Progress Bar
            val progress = (event.currentParticipants.toFloat() / event.maxParticipants.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.currentParticipants}/${event.maxParticipants} Registered",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = if (event.currentParticipants >= event.maxParticipants) "FULL" else "${event.maxParticipants - event.currentParticipants} spots left",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.currentParticipants >= event.maxParticipants) Color.Red else PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 1f) Color.Red else PrimaryGreen,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Text("Details", fontSize = 12.sp)
                }

                if (isRegistered && registrationStatus == RegistrationStatus.APPROVED && onViewPass != null) {
                    Button(
                        onClick = onViewPass,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Entry Pass", fontSize = 12.sp)
                    }
                } else if (onRegister != null && !isRegistered && event.eventStatus == EventStatus.PUBLISHED) {
                    Button(
                        onClick = onRegister,
                        enabled = event.currentParticipants < event.maxParticipants,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                    ) {
                        Text(
                            text = if (event.currentParticipants >= event.maxParticipants) "Seats Full" else "Register Now",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: EventCategory) {
    val (bgColor, textColor) = when (category) {
        EventCategory.SCIENCE, EventCategory.TECHNOLOGY -> Pair(Color(0xFFE0F2FE), Color(0xFF0284C7))
        EventCategory.CODING, EventCategory.HACKATHON -> Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
        EventCategory.SPORTS -> Pair(Color(0xFFDCFCE7), Color(0xFF15803D))
        EventCategory.CULTURAL, EventCategory.MUSIC_AND_DANCE -> Pair(Color(0xFFFCE7F3), Color(0xFFBE185D))
        EventCategory.DEBATE, EventCategory.QUIZ -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        EventCategory.CELEBRATION -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = category.displayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun EventStatusBadge(status: EventStatus) {
    val (bgColor, textColor) = when (status) {
        EventStatus.PUBLISHED -> Pair(Color(0xFFDCFCE7), Color(0xFF168B35))
        EventStatus.ONGOING -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
        EventStatus.COMPLETED -> Pair(Color(0xFFE0E7FF), Color(0xFF3730A3))
        EventStatus.DRAFT -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
        EventStatus.CANCELLED -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RegistrationStatusBadge(status: RegistrationStatus) {
    val (bgColor, textColor) = when (status) {
        RegistrationStatus.APPROVED -> Pair(Color(0xFFDCFCE7), Color(0xFF168B35))
        RegistrationStatus.PENDING -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
        RegistrationStatus.REJECTED -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
        RegistrationStatus.CANCELLED -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
