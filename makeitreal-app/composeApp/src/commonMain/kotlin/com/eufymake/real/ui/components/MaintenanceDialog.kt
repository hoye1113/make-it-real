package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
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
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.ui.theme.BrandGreen
import kotlinx.coroutines.delay

@Composable
fun MaintenanceDialog(
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    var isRunningMaintenance by remember { mutableStateOf(false) }
    var currentTaskName by remember { mutableStateOf("") }
    var progressPercent by remember { mutableStateOf(0f) }
    var secondsLeft by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isRunningMaintenance) {
        if (isRunningMaintenance) {
            progressPercent = 0f
            secondsLeft = 15
            for (i in 1..100) {
                delay(150)
                progressPercent = i.toFloat()
                secondsLeft = ((100 - i) * 0.15).toInt()
            }
            isRunningMaintenance = false
            isCompleted = true
        }
    }

    Dialog(onDismissRequest = { if (!isRunningMaintenance) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            strings.dialog.maintenanceCenterTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    if (!isRunningMaintenance) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }
                }

                // If currently running maintenance
                if (isRunningMaintenance) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0FDF4))
                            .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = { progressPercent / 100f },
                                modifier = Modifier.size(48.dp),
                                color = BrandGreen,
                                trackColor = Color(0xFFDCFCE7)
                            )

                            Text(
                                strings.dialog.executingTask(currentTaskName),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )

                            Text(
                                strings.dialog.maintenanceInProgress(secondsLeft),
                                fontSize = 11.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                } else if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(36.dp))
                            Text(
                                strings.dialog.maintenanceComplete,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                            Text(
                                strings.dialog.maintenanceCompleteDetail,
                                fontSize = 11.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }

                // Waste Ink Tank Status Bar
                val wasteInkProgress = 0.24f
                val wasteInkPercent = (wasteInkProgress * 100).toInt()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.dialog.wasteInkTank, fontSize = 12.sp, color = Color(0xFF475569))
                            Text(strings.dialog.wasteInkLevel(wasteInkPercent), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                        LinearProgressIndicator(
                            progress = { wasteInkProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF16A34A),
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }

                // Maintenance Operations
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            currentTaskName = strings.dialog.cleanPrintheadTask
                            isRunningMaintenance = true
                            isCompleted = false
                        },
                        enabled = !isRunningMaintenance,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text(strings.dialog.cleanPrintheadBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            currentTaskName = strings.dialog.flushInkTask
                            isRunningMaintenance = true
                            isCompleted = false
                        },
                        enabled = !isRunningMaintenance,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text(strings.dialog.flushInkBtn, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    }
                }
            }
        }
    }
}
