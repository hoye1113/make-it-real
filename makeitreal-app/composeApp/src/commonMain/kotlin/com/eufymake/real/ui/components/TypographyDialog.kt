package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType
import com.eufymake.real.ui.theme.BrandPrimary

@Composable
fun TypographyDialog(
    onAddTextLayer: (CanvasLayer) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember { mutableStateOf("eufyMake UV") }
    var textColorHex by remember { mutableStateOf(0xFF1D2024L) }

    val presetColors = listOf(
        0xFF1D2024L, // Black
        0xFFFF5E36L, // Brand Orange
        0xFF2563EBL, // Brand Blue
        0xFF059669L, // Emerald
        0xFFD97706L, // Gold
        0xFF9333EAL  // Purple
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add Typography Layer", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Text Content") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Ink Color", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presetColors.forEach { colorVal ->
                        val isSelected = textColorHex == colorVal
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable { textColorHex = colorVal }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onAddTextLayer(
                                CanvasLayer(
                                    id = "layer_txt_${System.currentTimeMillis()}",
                                    name = "Text: $textInput",
                                    type = LayerType.TEXT,
                                    textContent = textInput,
                                    textColor = textColorHex,
                                    widthMm = 60f,
                                    heightMm = 25f,
                                    reliefDepthMm = 1.6f
                                )
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add to Canvas", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
