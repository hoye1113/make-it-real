package com.eufymake.real.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType

@Composable
fun ElementsDialog(
    onAddElementLayer: (CanvasLayer) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Select Element Shape", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onAddElementLayer(
                                CanvasLayer(
                                    id = "layer_elem_${System.currentTimeMillis()}",
                                    name = "Circle Frame",
                                    type = LayerType.SHAPE,
                                    shapeType = "CIRCLE",
                                    widthMm = 45f,
                                    heightMm = 45f,
                                    reliefDepthMm = 0.8f
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Circle")
                    }

                    Button(
                        onClick = {
                            onAddElementLayer(
                                CanvasLayer(
                                    id = "layer_elem_${System.currentTimeMillis()}",
                                    name = "Rect Badge",
                                    type = LayerType.SHAPE,
                                    shapeType = "RECTANGLE",
                                    widthMm = 55f,
                                    heightMm = 35f,
                                    reliefDepthMm = 1.0f
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Rectangle")
                    }
                }
            }
        }
    }
}
