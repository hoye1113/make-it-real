package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
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
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.repository.AIGenerationState
import com.eufymake.real.repository.MockAIRepository
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.BrandOrange
import com.eufymake.real.ui.theme.BrandPrimary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AIDialog(
    availableCredits: Int,
    onSpendCredits: (Int) -> Boolean,
    onAddGeneratedLayer: (CanvasLayer) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalStrings.current
    val coroutineScope = rememberCoroutineScope()
    val aiRepo = remember { MockAIRepository() }

    var aiPromptText by remember { mutableStateOf("Cyberpunk mechanical dragon with embossed scales") }
    var isAIGenerating by remember { mutableStateOf(false) }
    var aiProgressPercent by remember { mutableStateOf(0) }
    var aiStepDescription by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { if (!isAIGenerating) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI 3D Relief Studio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                    if (!isAIGenerating) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.canvas.aiSubtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = strings.canvas.aiCreditsBadge(availableCredits),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (availableCredits >= 2) BrandAccent else Color(0xFFEF4444)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = aiPromptText,
                    onValueChange = { aiPromptText = it; errorMessage = null },
                    enabled = !isAIGenerating,
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    placeholder = { Text(strings.canvas.aiPlaceholder) }
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, fontSize = 11.sp, color = Color(0xFFEF4444))
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isAIGenerating) {
                    Column {
                        LinearProgressIndicator(
                            progress = { aiProgressPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = BrandAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = aiStepDescription, fontSize = 11.sp, color = BrandAccent)
                            Text(text = "$aiProgressPercent%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (availableCredits < 2) {
                                errorMessage = strings.canvas.aiInsufficientCredits(2, availableCredits)
                                return@Button
                            }
                            isAIGenerating = true
                            coroutineScope.launch {
                                aiRepo.generate3DRelief(aiPromptText).collectLatest { state ->
                                    when (state) {
                                        is AIGenerationState.Progress -> {
                                            aiProgressPercent = state.percent
                                            aiStepDescription = state.stepDescription
                                        }
                                        is AIGenerationState.Success -> {
                                            // Deduct credits and add generated layer
                                            onSpendCredits(2)
                                            isAIGenerating = false
                                            onAddGeneratedLayer(state.generatedLayer)
                                            onDismiss()
                                        }
                                        is AIGenerationState.Error -> {
                                            isAIGenerating = false
                                            errorMessage = state.message
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.canvas.aiButtonGenerate,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
