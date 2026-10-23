package com.eufymake.real.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.DeviceInfo
import com.eufymake.real.model.DeviceStatus
import com.eufymake.real.ui.components.InkBar
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.BrandPrimary

@Composable
fun DevicesScreen(
    deviceInfo: DeviceInfo,
    onToggleChamberLight: () -> Unit,
    onToggleAirFiltration: () -> Unit,
    onCancelJob: () -> Unit
) {
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
                    text = "设备",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF16A34A))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "已连接",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = deviceInfo.status.name,
                                fontSize = 10.sp,
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
                            Text(text = "Bed Temp", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "${deviceInfo.bedTemperatureC}°C",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        }
                        Column {
                            Text(text = "UV LED Lamp", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "${deviceInfo.uvLampTemperatureC}°C",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (deviceInfo.uvLampTemperatureC > 50f) Color(0xFFDC2626) else BrandPrimary
                            )
                        }
                        Column {
                            Text(text = "Rotary Fixture", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "Installed (Ready)",
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
                                    text = "ACTIVE UV JOB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandAccent,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = deviceInfo.currentJobName ?: "Printing tactile layers...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            }
                            IconButton(onClick = onCancelJob, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Cancel",
                                    tint = Color(0xFFEF4444)
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
                                text = "${deviceInfo.currentJobProgress.toInt()}% complete",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandAccent
                            )
                            Text(
                                text = "ETA: ${deviceInfo.timeRemainingSeconds}s remaining",
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
                            text = "6-Channel UV Ink Levels",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                        Text(
                            text = "CMYK + W + Gloss",
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

        // Hardware Controls (Chamber light, Air Filter)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Chamber Controls",
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
                                text = "Internal Chamber Light",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (deviceInfo.isChamberLightOn) "ON (4500K Neutral White)" else "OFF",
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
                                text = "HEPA & Carbon VOC Filter",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (deviceInfo.isAirFiltrationActive) "Active (99.8% VOC Absorption)" else "Standby",
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
}
