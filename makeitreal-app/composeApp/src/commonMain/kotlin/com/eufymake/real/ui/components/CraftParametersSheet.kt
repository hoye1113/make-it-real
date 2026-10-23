package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
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
import com.eufymake.real.model.Draft
import com.eufymake.real.model.GlossMode
import com.eufymake.real.model.PrintMode
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.BrandPrimary

@Composable
fun CraftParametersSheet(
    draft: Draft,
    onDismiss: () -> Unit,
    onStartPrint: (PrintMode, Float, GlossMode) -> Unit,
    onPublishCommunity: (String, String) -> Unit
) {
    var selectedPrintMode by remember { mutableStateOf(PrintMode.RELIEF_3D) }
    var reliefDepth by remember { mutableStateOf(1.5f) }
    var selectedGlossMode by remember { mutableStateOf(GlossMode.SPOT_GLOSS) }
    var showPublishInput by remember { mutableStateOf(false) }
    var publishTitle by remember { mutableStateOf(draft.title) }
    var publishDescription by remember { mutableStateOf("Created with eufyMake UV relief craft engine.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "UV Craft Parameters",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                        Text(
                            text = "Substrate: ${draft.substrate.name} (${draft.substrate.widthMm.toInt()}×${draft.substrate.heightMm.toInt()}mm)",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Print Mode
                Text(
                    text = "PRINT MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrintMode.entries.forEach { mode ->
                        val isSelected = selectedPrintMode == mode
                        val label = when (mode) {
                            PrintMode.COLOR_ONLY -> "Color"
                            PrintMode.WHITE_ONLY -> "White"
                            PrintMode.COLOR_AND_WHITE -> "Color+W"
                            PrintMode.RELIEF_3D -> "3D Relief"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BrandPrimary else Color(0xFFF1F5F9))
                                .clickable { selectedPrintMode = mode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Relief Depth (active if RELIEF_3D)
                if (selectedPrintMode == PrintMode.RELIEF_3D) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RELIEF DEPTH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${(reliefDepth * 10).toInt() / 10f} mm (Stack: ${(reliefDepth * 12).toInt()} passes)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandAccent
                        )
                    }
                    Slider(
                        value = reliefDepth,
                        onValueChange = { reliefDepth = it },
                        valueRange = 0.2f..3.0f,
                        steps = 27,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandAccent,
                            activeTrackColor = BrandAccent,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // Section 3: Gloss Mode
                Text(
                    text = "GLOSS & VARNISH FINISH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlossMode.entries.forEach { mode ->
                        val isSelected = selectedGlossMode == mode
                        val label = when (mode) {
                            GlossMode.NONE -> "None"
                            GlossMode.SPOT_GLOSS -> "Spot Gloss"
                            GlossMode.FULL_GLOSS -> "Full Gloss"
                            GlossMode.MATTE_VARNISH -> "Matte"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF0284C7) else Color(0xFFF1F5F9))
                                .clickable { selectedGlossMode = mode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats preview card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "EST. TIME", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "3m 45s", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "WHITE INK", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "${(reliefDepth * 0.9).toInt() / 10f + 0.4f} ml", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LAYERS", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "${draft.layers.size} items", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (showPublishInput) {
                    OutlinedTextField(
                        value = publishTitle,
                        onValueChange = { publishTitle = it },
                        label = { Text("Project Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = publishDescription,
                        onValueChange = { publishDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onPublishCommunity(publishTitle, publishDescription)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Publish to Community")
                    }
                } else {
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPublishInput = true },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                onStartPrint(selectedPrintMode, reliefDepth, selectedGlossMode)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1.5f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print UV Job", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
