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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.eufymake.real.model.Substrate
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun SubstrateConfigSheet(
    substrate: Substrate,
    onDismiss: () -> Unit,
    onConfirmCreate: (Substrate) -> Unit
) {
    var width by remember { mutableStateOf(substrate.widthMm.toInt().toString()) }
    var height by remember { mutableStateOf(substrate.heightMm.toInt().toString()) }
    var thickness by remember { mutableStateOf(substrate.thicknessMm.toInt().toString()) }
    var selectedMaterial by remember { mutableStateOf(substrate.defaultMaterial) }
    var selectedPreset by remember { mutableStateOf(substrate.presets.firstOrNull() ?: "") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val materialOptions = listOf("亚克力", "木材", "金属", "PVC", "陶瓷", "玻璃")
    val colorSwatches = listOf(
        Color(0xFFF1F5F9), // Translucent white / clear
        Color(0xFFFFFFFF), // Solid white
        Color(0xFF1E293B), // Charcoal / Black
        Color(0xFFD4A373), // Natural wood
        Color(0xFFCBD5E1)  // Brushed aluminum
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                    Text(
                        text = substrate.nameZh,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(end = 36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Visual Preview Box
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF7F8FA))
                                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorSwatches.getOrElse(selectedColorIndex) { Color.White })
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            )
                        }
                    }

                    // 4-column Dimensions Input: Width, Length, Thickness, Unit
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DimensionInputBox(
                                label = "宽度 *",
                                value = width,
                                onValueChange = { width = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.weight(1f)
                            )
                            DimensionInputBox(
                                label = "长度 *",
                                value = height,
                                onValueChange = { height = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.weight(1f)
                            )
                            DimensionInputBox(
                                label = "厚度",
                                value = thickness,
                                onValueChange = { thickness = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.weight(1f)
                            )
                            DimensionInputBox(
                                label = "单位",
                                value = "mm",
                                onValueChange = {},
                                isReadOnly = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Material & Color Swatch Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Material Dropdown
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "材质",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF8E8E93)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF7F8FA))
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                        .clickable {
                                            val nextIdx = (materialOptions.indexOf(selectedMaterial) + 1) % materialOptions.size
                                            selectedMaterial = materialOptions[nextIdx]
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            selectedMaterial,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1A1A1A)
                                        )
                                        Icon(
                                            Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = Color(0xFF8E8E93)
                                        )
                                    }
                                }
                            }

                            // Color Swatches
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "颜色",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF8E8E93)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF7F8FA))
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    colorSwatches.forEachIndexed { idx, color ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (selectedColorIndex == idx) 2.dp else 1.dp,
                                                    color = if (selectedColorIndex == idx) BrandGreen else Color(0xFFCBD5E1),
                                                    shape = CircleShape
                                                )
                                                .clickable { selectedColorIndex = idx }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Presets List (if available)
                    if (substrate.presets.isNotEmpty()) {
                        item {
                            Text(
                                "预设规格",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                        }

                        items(substrate.presets) { preset ->
                            val isSelected = selectedPreset == preset
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF7F8FA))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) BrandGreen else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPreset = preset }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFF475569)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = BrandGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Button: "创作"
                Button(
                    onClick = {
                        val parsedW = width.toFloatOrNull() ?: substrate.widthMm
                        val parsedH = height.toFloatOrNull() ?: substrate.heightMm
                        val parsedThick = thickness.toFloatOrNull() ?: substrate.thicknessMm
                        val configuredSubstrate = substrate.copy(
                            widthMm = parsedW,
                            heightMm = parsedH,
                            thicknessMm = parsedThick,
                            defaultMaterial = selectedMaterial
                        )
                        onConfirmCreate(configuredSubstrate)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlack)
                ) {
                    Text(
                        "创作",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DimensionInputBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isReadOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8E8E93)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF7F8FA))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isReadOnly) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )
            } else {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
