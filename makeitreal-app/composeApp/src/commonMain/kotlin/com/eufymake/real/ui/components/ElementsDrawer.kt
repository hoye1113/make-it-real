package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun ElementsDrawer(
    onDismiss: () -> Unit,
    onAddLayer: (CanvasLayer) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = listOf("模板", "图形", "图片", "艺术字体", "图案", "形状", "图案纹理")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top drag bar indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFD1D5DB))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(Color(0xFFF2F3F5))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        BasicTextFieldNoBorder(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "搜索",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Category Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { idx ->
                        val isSelected = selectedCategoryIndex == idx
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedCategoryIndex = idx }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = categories[idx],
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(2.5.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(Color(0xFF1A1A1A))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Lists
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        SectionRow(
                            title = "valentine retro design(11)",
                            items = listOf(
                                "Retro Heart Tag" to Color(0xFFFF6B6B),
                                "Cupid Arrow Emblem" to Color(0xFFFF8E72),
                                "Vintage Love Letter" to Color(0xFFF9CA24),
                                "Floral Ribbon" to Color(0xFF6AB04C)
                            ),
                            onSelectItem = { name, color ->
                                onAddLayer(
                                    CanvasLayer(
                                        id = "layer_elem_${System.currentTimeMillis()}",
                                        name = name,
                                        type = LayerType.SHAPE,
                                        shapeType = "RECTANGLE",
                                        widthMm = 45f,
                                        heightMm = 45f,
                                        reliefDepthMm = 1.2f,
                                        fillColor = color.value.toLong()
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }

                    item {
                        SectionRow(
                            title = "retro colorful birthday greeting cards(8)",
                            items = listOf(
                                "Retro Dino Clock" to Color(0xFF388E3C),
                                "Birthday Banner 3D" to Color(0xFF1976D2),
                                "Pop Gift Box" to Color(0xFF8E24AA),
                                "Cupcake Relief" to Color(0xFFF57C00)
                            ),
                            onSelectItem = { name, color ->
                                onAddLayer(
                                    CanvasLayer(
                                        id = "layer_elem_${System.currentTimeMillis()}",
                                        name = name,
                                        type = LayerType.IMAGE,
                                        widthMm = 55f,
                                        heightMm = 55f,
                                        reliefDepthMm = 2.0f,
                                        fillColor = color.value.toLong()
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }

                    item {
                        SectionRow(
                            title = "几何基础与装饰边框",
                            items = listOf(
                                "圆形徽章底座" to Color(0xFF455A64),
                                "八角形画框" to Color(0xFF5D4037),
                                "星芒光环" to Color(0xFFFBC02D),
                                "盾形立体浮雕" to Color(0xFF00796B)
                            ),
                            onSelectItem = { name, color ->
                                onAddLayer(
                                    CanvasLayer(
                                        id = "layer_elem_${System.currentTimeMillis()}",
                                        name = name,
                                        type = LayerType.SHAPE,
                                        shapeType = "CIRCLE",
                                        widthMm = 50f,
                                        heightMm = 50f,
                                        reliefDepthMm = 1.5f,
                                        fillColor = color.value.toLong()
                                    )
                                )
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionRow(
    title: String,
    items: List<Pair<String, Color>>,
    onSelectItem: (String, Color) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "查看全部",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { (name, color) ->
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F3F5))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .clickable { onSelectItem(name, color) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun BasicTextFieldNoBorder(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = Color(0xFF8E8E93)
            )
        }
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
                color = Color(0xFF1A1A1A)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
