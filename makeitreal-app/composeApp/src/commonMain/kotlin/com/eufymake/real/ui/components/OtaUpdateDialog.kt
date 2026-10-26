package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.ui.window.DialogProperties
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.ota.OtaController
import com.eufymake.real.ota.OtaErrorStep
import com.eufymake.real.ota.OtaState

@Composable
fun OtaUpdateDialog() {
    val otaState by OtaController.state.collectAsState()
    val strings = LocalStrings.current

    val showDialog = when (otaState) {
        is OtaState.UpdateAvailable,
        is OtaState.Downloading,
        is OtaState.ReadyToInstall,
        is OtaState.UpToDate,
        is OtaState.Error -> true
        else -> false
    }

    if (!showDialog) return

    val isForce = when (val s = otaState) {
        is OtaState.UpdateAvailable -> s.manifest.updateStatus == 2
        is OtaState.Downloading -> s.manifest?.updateStatus == 2
        is OtaState.ReadyToInstall -> s.manifest.updateStatus == 2
        is OtaState.Error -> s.manifest?.updateStatus == 2
        else -> false
    }

    Dialog(
        onDismissRequest = {
            if (!isForce) {
                if (otaState is OtaState.Downloading) {
                    OtaController.cancelDownload()
                } else {
                    OtaController.dismiss()
                }
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isForce,
            dismissOnClickOutside = !isForce && otaState !is OtaState.Downloading
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF131D26)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                if (otaState is OtaState.UpToDate) {
                                    listOf(Color(0xFF00DC82), Color(0xFF10B981))
                                } else {
                                    listOf(Color(0xFF00DC82), Color(0xFF06B6D4))
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (otaState is OtaState.UpToDate) Icons.Default.CheckCircle else Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Version
                when (val s = otaState) {
                    is OtaState.UpdateAvailable -> {
                        Text(
                            text = strings.ota.newVersionFound,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "v${s.manifest.versionName}",
                            fontSize = 14.sp,
                            color = Color(0xFF00DC82),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    is OtaState.Downloading -> {
                        Text(
                            text = strings.ota.downloadingUpdate,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    is OtaState.ReadyToInstall -> {
                        Text(
                            text = strings.ota.downloadComplete,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.ota.readyToInstallDesc,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    is OtaState.UpToDate -> {
                        Text(
                            text = strings.ota.upToDateTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.ota.upToDateDesc,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    is OtaState.Error -> {
                        Text(
                            text = strings.ota.updateErrorTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content area
                when (val s = otaState) {
                    is OtaState.UpdateAvailable -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1B2834))
                                .padding(14.dp)
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                text = strings.ota.updateNotesTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = s.manifest.modifyContent.ifBlank { strings.ota.defaultNotes },
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        if (s.manifest.apkSize > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val mbSize = s.manifest.apkSize / (1024 * 1024)
                            Text(
                                text = strings.ota.packageSizeMb(mbSize),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    is OtaState.Downloading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (s.totalBytes > 0) {
                                LinearProgressIndicator(
                                    progress = { s.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF00DC82),
                                    trackColor = Color(0xFF1B2834)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                val pct = (s.progress * 100).toInt()
                                val curMb = s.currentBytes / (1024 * 1024)
                                val totMb = s.totalBytes / (1024 * 1024)
                                Text(
                                    text = "$pct% (${curMb}MB / ${totMb}MB)",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            } else {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF00DC82),
                                    trackColor = Color(0xFF1B2834)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                val curMb = s.currentBytes / (1024 * 1024)
                                Text(
                                    text = "${curMb}MB",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    is OtaState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A1B1E))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = s.message,
                                fontSize = 13.sp,
                                color = Color(0xFFFF6B6B)
                            )
                        }
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (otaState is OtaState.UpToDate) {
                        Button(
                            onClick = { OtaController.dismiss() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00DC82),
                                contentColor = Color.Black
                            )
                        ) {
                            Text(strings.ota.ok, fontWeight = FontWeight.Bold)
                        }
                    } else if (otaState is OtaState.Downloading) {
                        if (!isForce) {
                            OutlinedButton(
                                onClick = { OtaController.cancelDownload() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White.copy(alpha = 0.8f)
                                )
                            ) {
                                Text(strings.ota.cancelDownload)
                            }
                        }
                    } else {
                        if (!isForce) {
                            OutlinedButton(
                                onClick = { OtaController.dismiss() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White.copy(alpha = 0.8f)
                                )
                            ) {
                                Text(strings.ota.later)
                            }
                        }

                        when (val s = otaState) {
                            is OtaState.UpdateAvailable -> {
                                Button(
                                    onClick = { OtaController.downloadUpdate() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00DC82),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(strings.ota.updateNow, fontWeight = FontWeight.Bold)
                                }
                            }

                            is OtaState.ReadyToInstall -> {
                                Button(
                                    onClick = { OtaController.installUpdate() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00DC82),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(strings.ota.installNow, fontWeight = FontWeight.Bold)
                                }
                            }

                            is OtaState.Error -> {
                                Button(
                                    onClick = {
                                        when (s.step) {
                                            OtaErrorStep.CHECK -> OtaController.checkForUpdate(manual = true)
                                            OtaErrorStep.DOWNLOAD -> OtaController.downloadUpdate()
                                            OtaErrorStep.INSTALL -> OtaController.installUpdate()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00DC82),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(strings.ota.retry, fontWeight = FontWeight.Bold)
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
