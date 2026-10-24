package com.eufymake.real.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.DeviceInfo
import com.eufymake.real.model.DeviceStatus
import com.eufymake.real.ui.components.InkBar
import com.eufymake.real.ui.components.MaintenanceDialog
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.theme.BrandPrimary
import com.eufymake.real.ui.util.tactilePress

@Composable
fun DevicesScreen(
    deviceInfo: DeviceInfo,
    onToggleChamberLight: () -> Unit,
    onToggleAirFiltration: () -> Unit,
    onCancelJob: () -> Unit
) {
    val strings = LocalStrings.current
    var showMaintenanceDialog by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.device.devicesTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimary
                    )

                    val isOnline = deviceInfo.status != DeviceStatus.OFFLINE
                    val pulseTransition = rememberInfiniteTransition(label = "wifiPulse")
                    val pulseAlpha by pulseTransition.animateFloat(
                        initialValue = 0.55f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isOnline) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                            .tactilePress()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .graphicsLayer { alpha = if (isOnline) pulseAlpha else 1f }
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF16A34A) else Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) strings.device.wifiConnected else strings.device.statusOffline,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) Color(0xFF15803D) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Hardware Machine Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = deviceInfo.modelName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary
                                    )
                                    Text(
                                        text = "S/N: ${deviceInfo.serialNumber} • ${deviceInfo.firmwareVersion}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            val statusColor = when (deviceInfo.status) {
                                DeviceStatus.PRINTING -> Color(0xFF2563EB)
                                DeviceStatus.UV_CURING -> Color(0xFF9333EA)
                                DeviceStatus.PREPARING -> Color(0xFFD97706)
                                DeviceStatus.IDLE -> Color(0xFF16A34A)
                                else -> Color(0xFF64748B)
                            }
                            val statusZh = when (deviceInfo.status) {
                                DeviceStatus.PRINTING -> strings.device.statusPrinting
                                DeviceStatus.UV_CURING -> strings.device.statusUvCuring
                                DeviceStatus.PREPARING -> strings.device.statusPreparing
                                DeviceStatus.IDLE -> strings.device.statusIdle
                                DeviceStatus.PAUSED -> strings.device.statusPaused
                                DeviceStatus.COMPLETED -> strings.device.statusCompleted
                                DeviceStatus.OFFLINE -> strings.device.statusOffline
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = statusZh,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Machine Telemetry Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = strings.device.bedTempLabel, fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(
                                    text = "${deviceInfo.bedTemperatureC}°C",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            }
                            Column {
                                Text(text = strings.device.uvLedLamp, fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(
                                    text = "${deviceInfo.uvLampTemperatureC}°C",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deviceInfo.uvLampTemperatureC > 50f) Color(0xFFDC2626) else BrandPrimary
                                )
                            }
                            Column {
                                Text(text = strings.device.rotaryModule, fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(
                                    text = strings.device.installedReady,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }
                    }
                }
            }

            // Active Print Job Card (if printing or preparing)
            if (deviceInfo.status != DeviceStatus.IDLE) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = strings.device.executingJob,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandAccent,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = deviceInfo.currentJobName ?: strings.device.defaultJobName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { showCancelConfirmDialog = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Cancel",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { deviceInfo.currentJobProgress / 100f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = BrandAccent,
                                trackColor = Color(0xFFDBEAFE)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.device.jobProgress(deviceInfo.currentJobProgress.toInt()),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandAccent
                                )
                                val remainingText = remember(deviceInfo.timeRemainingSeconds) {
                                    val totalSec = deviceInfo.timeRemainingSeconds
                                    val mins = totalSec / 60
                                    val secs = totalSec % 60
                                    if (mins > 0) strings.device.timeFormat(mins, secs) else strings.device.secsOnly(secs)
                                }
                                Text(
                                    text = strings.device.timeRemaining(remainingText),
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            // 6-Color UV Ink Monitoring Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.device.sixColorInk,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                            Text(
                                text = strings.device.inkSubtext,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            deviceInfo.inkLevels.asList.forEach { channel ->
                                InkBar(channel = channel)
                            }
                        }
                    }
                }
            }

            // Maintenance & Calibration Center Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE0E7FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(strings.dialog.maintenanceCenterTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                    Text(strings.device.maintenanceCenterSubtitle, fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            Button(
                                onClick = { showMaintenanceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(strings.device.enterMaintenance, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Waste ink status line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.device.wastePadUsage(24), fontSize = 11.sp, color = Color(0xFF475569))
                            Text(strings.device.wastePadStatusOk, fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Hardware Controls (Chamber light, Air Filter)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strings.device.chamberControl,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )

                    // Chamber Light Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = if (deviceInfo.isChamberLightOn) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = strings.device.alignmentLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (deviceInfo.isChamberLightOn) strings.device.alignmentLightOn else strings.device.alignmentLightOff,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = deviceInfo.isChamberLightOn,
                            onCheckedChange = { onToggleChamberLight() }
                        )
                    }

                    // Air Filtration Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = if (deviceInfo.isAirFiltrationActive) BrandAccent else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = strings.device.airFiltration,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (deviceInfo.isAirFiltrationActive) strings.device.airFiltrationActive else strings.device.airFiltrationIdle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = deviceInfo.isAirFiltrationActive,
                            onCheckedChange = { onToggleAirFiltration() }
                        )
                    }
                }
            }
        }

        // Cancel Print Job Confirmation Dialog
        if (showCancelConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showCancelConfirmDialog = false },
                title = {
                    Text(strings.device.stopJobTitle, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                },
                text = {
                    Text(
                        strings.device.stopJobDesc,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCancelConfirmDialog = false
                            onCancelJob()
                        }
                    ) {
                        Text(strings.device.confirmStop, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelConfirmDialog = false }) {
                        Text(strings.device.continueJob, color = Color(0xFF1E293B))
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }

        // Maintenance Dialog
        if (showMaintenanceDialog) {
            MaintenanceDialog(
                onDismiss = { showMaintenanceDialog = false }
            )
        }
    }
}
