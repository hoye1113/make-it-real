package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.MallItem
import com.eufymake.real.model.PointsState
import com.eufymake.real.model.RewardTask
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.BrandOrange
import com.eufymake.real.ui.util.tactileClick

@Composable
fun PointsMallDialog(
    pointsState: PointsState,
    onCheckIn: () -> Unit,
    onRedeem: (String) -> Unit,
    onReset: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Tasks, 1: Rewards Mall

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with points badge & Reset tool
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.points.pointsHubTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (onReset != null) {
                                    IconButton(
                                        onClick = onReset,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = strings.points.resetMockData,
                                            tint = Color.White.copy(alpha = 0.85f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = strings.common.close,
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = strings.points.availableBalance,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = "Points",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${pointsState.totalPoints}",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.points.pts,
                                        fontSize = 13.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Text(
                                    text = "${pointsState.aiCredits} ${strings.points.aiReliefCredits}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            // Daily check-in button
                            Button(
                                onClick = onCheckIn,
                                enabled = !pointsState.isCheckedInToday,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pointsState.isCheckedInToday) Color(0xFF334155) else BrandOrange,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (pointsState.isCheckedInToday) strings.points.claimed else strings.points.checkInReward,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Tab Switcher (Tasks vs Mall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) Color.White else Color.Transparent)
                            .tactileClick { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.points.tabDailyTasks,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) Color.White else Color.Transparent)
                            .tactileClick { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.points.tabRewardsMall,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }
                }

                // List content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 0) {
                        items(pointsState.tasks) { task ->
                            TaskRow(task = task, strings = strings)
                        }
                    } else {
                        items(pointsState.mallItems) { item ->
                            MallItemRow(
                                item = item,
                                canAfford = pointsState.totalPoints >= item.pointsCost,
                                onRedeem = { onRedeem(item.id) },
                                strings = strings
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: RewardTask, strings: com.eufymake.real.i18n.AppStrings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) Color(0xFFDCFCE7) else Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.Check else Icons.Default.Stars,
                    contentDescription = null,
                    tint = if (task.isCompleted) Color(0xFF16A34A) else BrandAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = task.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = strings.points.taskReward(task.pointsReward),
                    fontSize = 11.sp,
                    color = Color(0xFFD97706),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (task.isCompleted) Color(0xFFF1F5F9) else Color(0xFFDBEAFE))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (task.isCompleted) strings.points.completed else "${task.progress}/${task.maxProgress}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (task.isCompleted) Color(0xFF64748B) else BrandAccent
            )
        }
    }
}

@Composable
private fun MallItemRow(
    item: MallItem,
    canAfford: Boolean,
    onRedeem: () -> Unit,
    strings: com.eufymake.real.i18n.AppStrings
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.points.pointsCost(item.pointsCost),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandOrange
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Valued \$${item.originalPriceUsd}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Button(
            onClick = onRedeem,
            enabled = canAfford && item.stockCount > 0,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandAccent,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = if (item.stockCount <= 0) strings.points.soldOut else if (canAfford) strings.points.redeem else strings.points.lowPoints,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
