package com.eufymake.real.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.i18n.AppLanguage
import com.eufymake.real.i18n.LanguageManager
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.Draft
import com.eufymake.real.model.PointsState
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun ProfileScreen(
    pointsState: PointsState,
    drafts: List<Draft>,
    onOpenPointsMall: () -> Unit,
    onResumeDraft: (Draft) -> Unit,
    onDeleteDraft: (String) -> Unit,
    onRenameDraft: (String, String) -> Unit = { _, _ -> },
    onDuplicateDraft: (String) -> Unit = {},
    onOpenAcademy: () -> Unit = {},
    onOpenActivities: () -> Unit = {}
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDraftForOptions by remember { mutableStateOf<Draft?>(null) }
    var draftToRename by remember { mutableStateOf<Draft?>(null) }
    var renameInputText by remember { mutableStateOf("") }
    var draftToDelete by remember { mutableStateOf<Draft?>(null) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val tabs = listOf(strings.canvas.newCanvas, strings.profile.myCreations, strings.community.tabFeatured, strings.profile.drafts)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Header & Stats (Frame 0110)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Bar with User Info & Language Selector
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(26.dp))
                            }
                            Column {
                                Text(strings.profile.userNick, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                                Text(strings.profile.userRole, fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Version / OTA Update Check Button
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable {
                                    com.eufymake.real.ota.OtaController.checkForUpdate(manual = true)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = "OTA Update", tint = BrandGreen, modifier = Modifier.size(15.dp))
                                    Text("v${com.eufymake.real.ota.OtaController.getCurrentVersionName()}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                                }
                            }

                            // Language Switch Button
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable { showLanguageDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = strings.profile.language, tint = BrandGreen, modifier = Modifier.size(16.dp))
                                    Text(strings.profile.currentLangName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(label = strings.community.follow, count = 0)
                        StatItem(label = strings.community.following, count = 0)
                        StatItem(label = strings.profile.favorites, count = 0)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Credits Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenPointsMall),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF475569)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.profile.cloudSync,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                            }

                            Text(
                                text = "${pointsState.aiCredits}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Navigation: 学院 vs 活动
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenAcademy),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Color(0xFF1A1A1A),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.dialog.academy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenActivities),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.community.tabContest, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            }
                        }
                    }
                }
            }

            // 2. Tabs: 创建 / 已发布 / 点赞 / 收藏 (Frame 0110)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    tabs.forEachIndexed { idx, tabName ->
                        val isSelected = selectedTab == idx
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = idx }
                        ) {
                            Text(
                                text = tabName,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color(0xFF1A1A1A))
                                )
                            }
                        }
                    }
                }
            }

            // 3. Project Cards Grid (Frame 0110)
            item {
                if (drafts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.profile.emptyDraftsHint,
                            fontSize = 13.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        drafts.chunked(2).forEach { rowDrafts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowDrafts.forEach { draft ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { onResumeDraft(draft) },
                                        color = Color.White,
                                        shadowElevation = 1.dp
                                    ) {
                                        Column {
                                            // Card preview box
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .background(Color(0xFFF1F5F9))
                                            ) {
                                                // Top-left 3D layer badge
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopStart)
                                                        .padding(8.dp)
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.Black.copy(alpha = 0.4f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.Layers,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }

                                                // Top-right 3-dot menu (Draft Management Trigger)
                                                IconButton(
                                                    onClick = { selectedDraftForOptions = draft },
                                                    modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.MoreVert,
                                                        contentDescription = "Options",
                                                        tint = Color(0xFF1A1A1A),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }

                                            // Card Details
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = draft.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1A1A1A),
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = strings.profile.justEdited,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF8E8E93)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowDrafts.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Draft Options Bottom Sheet Dialog
        selectedDraftForOptions?.let { draft ->
            Dialog(
                onDismissRequest = { selectedDraftForOptions = null },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        color = Color.White,
                        shadowElevation = 16.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            // Top drag handle pill
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .size(width = 36.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFCBD5E1))
                                    .align(Alignment.CenterHorizontally)
                            )

                            Text(
                                text = draft.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                maxLines = 1,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Option 1: Rename
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        draftToRename = draft
                                        renameInputText = draft.title
                                        selectedDraftForOptions = null
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(strings.common.edit, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                            }

                            // Option 2: Duplicate
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onDuplicateDraft(draft.id)
                                        selectedDraftForOptions = null
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(strings.common.copy, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                            }

                            // Option 3: Delete
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        draftToDelete = draft
                                        selectedDraftForOptions = null
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(strings.common.delete, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFFDC2626))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Cancel Button
                            Button(
                                onClick = { selectedDraftForOptions = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text(strings.common.cancel, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Rename Draft Dialog
        draftToRename?.let { targetDraft ->
            AlertDialog(
                onDismissRequest = { draftToRename = null },
                title = {
                    Text(
                        text = strings.common.edit,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = strings.canvas.blankCanvas,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF7F8FA))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = renameInputText,
                                onValueChange = { renameInputText = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1A1A1A)
                                ),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(BrandGreen),
                                decorationBox = { innerTextField ->
                                    if (renameInputText.isEmpty()) {
                                        Text(strings.common.edit, color = Color(0xFF94A3B8), fontSize = 14.sp)
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (renameInputText.isNotBlank()) {
                                onRenameDraft(targetDraft.id, renameInputText.trim())
                            }
                            draftToRename = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.common.save, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { draftToRename = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.common.cancel, color = Color(0xFF1A1A1A))
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }

        // Secondary Confirmation Dialog for Deletion (Prevents accidental data loss)
        draftToDelete?.let { targetDraft ->
            AlertDialog(
                onDismissRequest = { draftToDelete = null },
                title = {
                    Text(
                        text = strings.common.delete,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                },
                text = {
                    Text(
                        text = strings.canvas.deleteDraftConfirm,
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteDraft(targetDraft.id)
                            draftToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.common.delete, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { draftToDelete = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.common.cancel, color = Color(0xFF1A1A1A))
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        text = strings.profile.selectLanguage,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Chinese Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (LanguageManager.currentLanguage == AppLanguage.ZH) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
                            border = if (LanguageManager.currentLanguage == AppLanguage.ZH) androidx.compose.foundation.BorderStroke(1.5.dp, BrandGreen) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    LanguageManager.setLanguage(AppLanguage.ZH)
                                    showLanguageDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.profile.langZh,
                                    fontSize = 15.sp,
                                    fontWeight = if (LanguageManager.currentLanguage == AppLanguage.ZH) FontWeight.Bold else FontWeight.Medium,
                                    color = if (LanguageManager.currentLanguage == AppLanguage.ZH) BrandGreen else Color(0xFF1A1A1A)
                                )
                                if (LanguageManager.currentLanguage == AppLanguage.ZH) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        // English Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (LanguageManager.currentLanguage == AppLanguage.EN) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
                            border = if (LanguageManager.currentLanguage == AppLanguage.EN) androidx.compose.foundation.BorderStroke(1.5.dp, BrandGreen) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    LanguageManager.setLanguage(AppLanguage.EN)
                                    showLanguageDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.profile.langEn,
                                    fontSize = 15.sp,
                                    fontWeight = if (LanguageManager.currentLanguage == AppLanguage.EN) FontWeight.Bold else FontWeight.Medium,
                                    color = if (LanguageManager.currentLanguage == AppLanguage.EN) BrandGreen else Color(0xFF1A1A1A)
                                )
                                if (LanguageManager.currentLanguage == AppLanguage.EN) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text(strings.common.done, color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label ",
            fontSize = 14.sp,
            color = Color(0xFF8E8E93)
        )
        Text(
            text = count.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}
