package com.eufymake.real.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.api.ActivityDto
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.util.rememberAppImage

@Composable
fun ContestDialog(
    activity: ActivityDto,
    onSubmitEntry: (craftNotes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var showSubmissionForm by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }
    var submittedSuccessfully by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with campaign banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF512F), Color(0xFFDD2476))
                            )
                        )
                ) {
                    val img = if (activity.bannerImageName.isNotEmpty()) rememberAppImage(activity.bannerImageName) else null
                    if (img != null) {
                        Image(
                            bitmap = img,
                            contentDescription = activity.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Top close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Bottom tag & title overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                strings.dialog.officialContest(activity.prizePoolDesc),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            activity.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Scrollable content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Countdown & Status Bar
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEF2F2))
                                .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val now = System.currentTimeMillis()
                            val isExpired = activity.deadlineTimestamp <= now
                            val daysRemaining = if (isExpired) 0 else ((activity.deadlineTimestamp - now) / (1000L * 3600 * 24)).coerceAtLeast(1)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (isExpired) Color(0xFF64748B) else Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isExpired) strings.dialog.contestEnded else strings.dialog.daysRemaining(daysRemaining),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpired) Color(0xFF64748B) else Color(0xFF991B1B)
                                )
                            }
                            Text(
                                strings.dialog.submissionCount(activity.submissionCount),
                                fontSize = 11.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    // Description
                    item {
                        Text(
                            activity.description,
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }

                    // Prize Pool Card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFFBEB))
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.dialog.prizeRewards, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                }
                                Text(strings.dialog.prize1, fontSize = 11.sp, color = Color(0xFF78350F))
                                Text(strings.dialog.prize2, fontSize = 11.sp, color = Color(0xFF78350F))
                                Text(strings.dialog.prize3, fontSize = 11.sp, color = Color(0xFF78350F))
                            }
                        }
                    }

                    // Submission Form (if clicked)
                    if (showSubmissionForm && !submittedSuccessfully) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        strings.dialog.submissionNotesTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    OutlinedTextField(
                                        value = notesText,
                                        onValueChange = { notesText = it },
                                        placeholder = { Text(strings.dialog.submissionNotesPlaceholder, fontSize = 11.sp) },
                                        minLines = 3,
                                        maxLines = 4,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                    )
                                    Button(
                                        onClick = {
                                            onSubmitEntry(notesText.ifEmpty { "Contest Entry" })
                                            submittedSuccessfully = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Text(strings.dialog.confirmSubmitEntry, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    if (submittedSuccessfully) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(strings.dialog.submitSuccess, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(strings.dialog.submitSuccessDetail, fontSize = 11.sp, color = Color(0xFF166534))
                                }
                            }
                        }
                    }
                }

                // Bottom Action
                if (!showSubmissionForm && !submittedSuccessfully) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val isExpired = activity.deadlineTimestamp <= System.currentTimeMillis()
                            Button(
                                onClick = { if (!isExpired) showSubmissionForm = true },
                                enabled = !isExpired,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isExpired) Color(0xFFCBD5E1) else BrandGreen,
                                    disabledContainerColor = Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text(
                                    if (isExpired) strings.dialog.contestEnded else strings.community.contestJoinNow,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpired) Color(0xFF94A3B8) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
