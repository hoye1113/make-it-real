package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.api.NotificationDto
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun NotificationsDialog(
    notifications: List<NotificationDto>,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var selectedFilterIndex by remember { mutableStateOf(0) }

    val filteredList = notifications.filter {
        when (selectedFilterIndex) {
            0 -> true
            1 -> it.type == "LIKE"
            2 -> it.type == "COMMENT"
            3 -> it.type == "SYSTEM" || it.type == "CONTEST"
            else -> true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        strings.dialog.notifications,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            strings.dialog.markAllRead,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandGreen,
                            modifier = Modifier.clickable { onMarkAllRead() }
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }
                }

                // Filter Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(strings.dialog.notifAll, strings.dialog.notifLikes, strings.dialog.notifComments, strings.dialog.notifSystem)
                    tabs.forEachIndexed { idx, tab ->
                        val isSelected = selectedFilterIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                                .clickable { selectedFilterIndex = idx }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Notifications List
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(strings.dialog.noNotifications, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            val iconVector = when (item.type) {
                                "LIKE" -> Icons.Default.Favorite
                                "COMMENT" -> Icons.Default.ChatBubble
                                "CONTEST" -> Icons.Default.EmojiEvents
                                else -> Icons.Default.Notifications
                            }
                            val iconColor = when (item.type) {
                                "LIKE" -> Color(0xFFEF4444)
                                "COMMENT" -> Color(0xFF3B82F6)
                                "CONTEST" -> Color(0xFFF59E0B)
                                else -> BrandGreen
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (!item.isRead) Color(0xFFF0FDF4) else Color(0xFFF8FAFC))
                                    .clickable { onMarkRead(item.id) }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(iconColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            iconVector,
                                            contentDescription = null,
                                            tint = iconColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                item.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                            if (!item.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFEF4444))
                                                )
                                            }
                                        }

                                        Text(
                                            item.content,
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569),
                                            lineHeight = 15.sp
                                        )

                                        val notifTimeAgo = remember(item.timestamp) {
                                            val mins = ((System.currentTimeMillis() - item.timestamp) / 60000).coerceAtLeast(1)
                                            if (mins < 60) strings.common.minutesAgo(mins) else strings.common.hoursAgo(mins / 60)
                                        }
                                        Text(
                                            notifTimeAgo,
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
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
