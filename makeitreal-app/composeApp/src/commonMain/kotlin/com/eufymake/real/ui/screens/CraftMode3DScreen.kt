package com.eufymake.real.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.model.Draft
import com.eufymake.real.model.GlossMode
import com.eufymake.real.model.PrintMode
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandDarkCard
import com.eufymake.real.ui.theme.BrandGreen
import kotlinx.coroutines.delay

enum class CraftType {
    FLAT,
    FLAT_EMBOSS,
    PATTERN_TEXTURE,
    RELIEF_TEXTURE
}

@Composable
fun CraftMode3DScreen(
    draft: Draft,
    availableAICredits: Int,
    onBack: () -> Unit,
    onSpendCredits: (Int) -> Boolean,
    onReadyToPrint: (PrintMode, Float, GlossMode) -> Unit
) {
    var selectedCraft by remember { mutableStateOf(CraftType.FLAT_EMBOSS) }
    var maxHeightMm by remember { mutableStateOf(2.5f) }
    var smoothness by remember { mutableStateOf(7) }
    var inkSavingPercent by remember { mutableStateOf(0) }
    var selectedInkMode by remember { mutableStateOf("彩色堆高") }
    var showInkModeMenu by remember { mutableStateOf(false) }

    // AI confirmation and generation states
    var showAIPointsDialog by remember { mutableStateOf(false) }
    var isGeneratingTexture by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableStateOf(0f) }

    // Perspective angle
    var isPerspectiveTilted by remember { mutableStateOf(true) }

    // Trigger texture generation when relief mode confirmed
    LaunchedEffect(isGeneratingTexture) {
        if (isGeneratingTexture) {
            generationProgress = 0f
            while (generationProgress < 1f) {
                delay(60)
                generationProgress += 0.05f
            }
            isGeneratingTexture = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                    Text(
                        text = "工艺模式",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(end = 36.dp)
                    )
                }
            }

            // 2. 3D Perspective Workspace
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(BrandDarkCard),
                contentAlignment = Alignment.Center
            ) {
                // Top-left substrate dropdown pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = draft.substrate.nameZh,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Top-right reset view button
                IconButton(
                    onClick = { isPerspectiveTilted = !isPerspectiveTilted },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .size(38.dp)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Reset Camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isGeneratingTexture) {
                    // Texture generation progress indicator (Frame 0090)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "${(generationProgress * 100).toInt()}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { generationProgress },
                            color = BrandGreen,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .width(220.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "生成...",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    // 3D Angled Tilted Substrate Bed
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .graphicsLayer {
                                if (isPerspectiveTilted) {
                                    rotationX = 28f
                                    rotationZ = -14f
                                    cameraDistance = 14f
                                }
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Artworks on bed with tactile relief shading
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32))
                                    .border(
                                        width = if (selectedCraft != CraftType.FLAT) 3.dp else 1.dp,
                                        color = if (selectedCraft != CraftType.FLAT) BrandGreen else Color(0xFF1B5E20),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "3D TACTILE",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        "${draft.layers.size} Layers Active",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    if (selectedCraft == CraftType.RELIEF_TEXTURE) {
                                        Text(
                                            "Max Height: ${maxHeightMm}mm",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Bottom Craft Settings Sheet (Frames 0080 & 0100)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Center drag bar
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFD1D5DB))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header row: 工艺模式 (i)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "工艺模式",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Craft Mode Selection Cards (Frame 0080)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CraftCard(
                            label = "平面",
                            isSelected = selectedCraft == CraftType.FLAT,
                            onClick = { selectedCraft = CraftType.FLAT },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            label = "平面\n堆高",
                            isSelected = selectedCraft == CraftType.FLAT_EMBOSS,
                            onClick = { selectedCraft = CraftType.FLAT_EMBOSS },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            label = "图案\n纹理",
                            isSelected = selectedCraft == CraftType.PATTERN_TEXTURE,
                            onClick = { selectedCraft = CraftType.PATTERN_TEXTURE },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            label = "浮雕\n纹理",
                            badgeText = "🪙 5",
                            isSelected = selectedCraft == CraftType.RELIEF_TEXTURE,
                            onClick = {
                                if (selectedCraft != CraftType.RELIEF_TEXTURE) {
                                    showAIPointsDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Parameter Controls
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Ink Mode Selector Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF7F8FA))
                                .clickable { showInkModeMenu = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "墨水模式",
                                fontSize = 13.sp,
                                color = Color(0xFF8E8E93)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedInkMode,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF8E8E93),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Sliders for Emboss & Relief modes (Frame 0100)
                        if (selectedCraft != CraftType.FLAT) {
                            // Max Height Slider
                            SliderRow(
                                label = "最大高度",
                                valueText = "${((maxHeightMm * 10).toInt() / 10f)} mm",
                                value = maxHeightMm,
                                range = 0.2f..5.0f,
                                onValueChange = { maxHeightMm = it }
                            )

                            // Smoothness Slider
                            SliderRow(
                                label = "平滑度",
                                valueText = smoothness.toString(),
                                value = smoothness.toFloat(),
                                range = 0f..10f,
                                onValueChange = { smoothness = it.toInt() }
                            )

                            // Save Ink Slider
                            SliderRow(
                                label = "节省油墨",
                                valueText = "$inkSavingPercent% 节省油墨",
                                value = inkSavingPercent.toFloat(),
                                range = 0f..50f,
                                onValueChange = { inkSavingPercent = it.toInt() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Main CTA Button: "准备打印" (Prepare to Print)
                    Button(
                        onClick = {
                            val printMode = when (selectedCraft) {
                                CraftType.FLAT -> PrintMode.COLOR_ONLY
                                CraftType.FLAT_EMBOSS -> PrintMode.COLOR_AND_WHITE
                                CraftType.PATTERN_TEXTURE -> PrintMode.WHITE_ONLY
                                CraftType.RELIEF_TEXTURE -> PrintMode.RELIEF_3D
                            }
                            val glossMode = if (selectedInkMode.contains("光油")) GlossMode.FULL_GLOSS else GlossMode.NONE
                            onReadyToPrint(printMode, maxHeightMm, glossMode)
                        },
                        enabled = !isGeneratingTexture,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlack,
                            disabledContainerColor = Color(0xFFD1D5DB)
                        ),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "准备打印",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // AI Points Consumption Dialog (Frame 0082)
        if (showAIPointsDialog) {
            AIPointsConsumptionDialog(
                onConfirm = {
                    showAIPointsDialog = false
                    val ok = onSpendCredits(5)
                    if (ok) {
                        selectedCraft = CraftType.RELIEF_TEXTURE
                        isGeneratingTexture = true
                    }
                },
                onDismiss = { showAIPointsDialog = false }
            )
        }

        // Ink Mode Bottom Popup Sheet (Frame 0080)
        if (showInkModeMenu) {
            Dialog(onDismissRequest = { showInkModeMenu = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        listOf("彩色堆高", "光油（堆高）", "纯白底+彩色堆高").forEach { mode ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedInkMode = mode
                                        showInkModeMenu = false
                                    }
                                    .padding(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedInkMode == mode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedInkMode == mode) BrandGreen else Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CraftCard(
    label: String,
    badgeText: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF7F8FA))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) BrandGreen else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BrandGreen else Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}

@Composable
private fun SliderRow(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF2F3F5))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = valueText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = BrandGreen,
                inactiveTrackColor = Color(0xFFE5E7EB)
            ),
            modifier = Modifier.fillMaxWidth().height(28.dp)
        )
    }
}

@Composable
fun AIPointsConsumptionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var doNotRemindAgain by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "此功能将消耗5点。",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { doNotRemindAgain = !doNotRemindAgain }
                ) {
                    Checkbox(
                        checked = doNotRemindAgain,
                        onCheckedChange = { doNotRemindAgain = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "不再提醒",
                        fontSize = 13.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).height(44.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text("取消", fontSize = 14.sp, color = Color(0xFF1A1A1A))
                    }

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlack),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("确认", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
