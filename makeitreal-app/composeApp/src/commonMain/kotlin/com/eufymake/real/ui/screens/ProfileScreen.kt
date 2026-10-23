package com.eufymake.real.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.Draft
import com.eufymake.real.model.PointsState
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun ProfileScreen(
    pointsState: PointsState,
    drafts: List<Draft>,
    onOpenPointsMall: () -> Unit,
    onResumeDraft: (Draft) -> Unit,
    onDeleteDraft: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var draftToDelete by remember { mutableStateOf<Draft?>(null) }
    val tabs = listOf("创建", "已发布", "点赞", "收藏")

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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(label = "粉丝", count = 0)
                        StatItem(label = "已关注", count = 0)
                        StatItem(label = "喜欢", count = 0)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Credits Banner: "AI积分 195" (Frame 0110)
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
                                    text = "AI积分",
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

                    // Quick Navigation: 学院 vs 活动 (Frame 0110)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { },
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
                                Text("学院", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { },
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
                                Text("活动", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
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
                            text = "暂无草稿，快去“创作”开启您的专属设计吧",
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

                                                // Top-right 3-dot menu (Safe confirmation trigger)
                                                IconButton(
                                                    onClick = { draftToDelete = draft },
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
                                                    text = "刚刚编辑",
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

        // Secondary Confirmation Dialog for Deletion (Prevents accidental data loss)
        draftToDelete?.let { targetDraft ->
            AlertDialog(
                onDismissRequest = { draftToDelete = null },
                title = {
                    Text(
                        text = "删除草稿",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                },
                text = {
                    Text(
                        text = "确定要删除草稿“${targetDraft.title}”吗？此操作无法撤销。",
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
                        Text("确认删除", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { draftToDelete = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("取消", color = Color(0xFF1A1A1A))
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
