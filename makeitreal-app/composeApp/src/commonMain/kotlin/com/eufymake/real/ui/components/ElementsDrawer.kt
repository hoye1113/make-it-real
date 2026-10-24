package com.eufymake.real.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType
import com.eufymake.real.ui.util.rememberAppImage

data class StickerVisualItem(
    val name: String,
    val imageName: String,
    val defaultReliefMm: Float = 2.0f
)

@Composable
fun ElementsDrawer(
    onDismiss: () -> Unit,
    onAddLayer: (CanvasLayer) -> Unit
) {
    val strings = LocalStrings.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = listOf(
        strings.canvas.elementsCatAll,
        strings.canvas.elementsCatHalloween,
        strings.canvas.elementsCatTrendy,
        strings.canvas.elementsCatRelief,
        strings.canvas.elementsCatShapes
    )

    val halloweenPack = listOf(
        StickerVisualItem(strings.canvas.stickerPumpkinCat, "Halloween Witch Cat Decor Skill.webp", 2.4f),
        StickerVisualItem(strings.canvas.stickerDarkSkull, "work_1.webp", 2.0f),
        StickerVisualItem(strings.canvas.stickerGlowCastle, "work_2.webp", 2.2f),
        StickerVisualItem(strings.canvas.stickerGhostBadge, "work_3.webp", 1.8f)
    )

    val trendyPack = listOf(
        StickerVisualItem(strings.canvas.stickerPopAvatar, "work_4.webp", 1.5f),
        StickerVisualItem(strings.canvas.stickerCyberLetters, "work_5.webp", 1.2f),
        StickerVisualItem(strings.canvas.stickerVintageGears, "work_6.webp", 2.5f),
        StickerVisualItem(strings.canvas.stickerShibaInu, "work_7.webp", 2.0f)
    )

    val reliefPack = listOf(
        StickerVisualItem(strings.canvas.stickerTactilePattern, "work_8.webp", 2.8f),
        StickerVisualItem(strings.canvas.stickerGoldEmblem, "work_1.webp", 2.0f),
        StickerVisualItem(strings.canvas.stickerAcrylicPrint, "work_2.webp", 1.5f)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 16.dp
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
                            placeholder = strings.canvas.searchElementsPlaceholder,
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

                val filteredHalloween = halloweenPack.filter { it.name.contains(searchQuery, ignoreCase = true) }
                val filteredTrendy = trendyPack.filter { it.name.contains(searchQuery, ignoreCase = true) }
                val filteredRelief = reliefPack.filter { it.name.contains(searchQuery, ignoreCase = true) }
                val shapeItems = listOf(
                    strings.canvas.shapeCircularBase to Color(0xFF455A64),
                    strings.canvas.shapeOctagonalFrame to Color(0xFF5D4037),
                    strings.canvas.shapeStarlightHalo to Color(0xFFFBC02D),
                    strings.canvas.shapeCrestShield to Color(0xFF00796B)
                ).filter { it.first.contains(searchQuery, ignoreCase = true) }

                val showHalloween = (selectedCategoryIndex == 0 || selectedCategoryIndex == 1) && filteredHalloween.isNotEmpty()
                val showTrendy = (selectedCategoryIndex == 0 || selectedCategoryIndex == 2) && filteredTrendy.isNotEmpty()
                val showRelief = (selectedCategoryIndex == 0 || selectedCategoryIndex == 3) && filteredRelief.isNotEmpty()
                val showShapes = (selectedCategoryIndex == 0 || selectedCategoryIndex == 4) && shapeItems.isNotEmpty()
                val hasAnyResults = showHalloween || showTrendy || showRelief || showShapes

                if (!hasAnyResults) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.canvas.elementsNoMatch(searchQuery),
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                } else {
                    // Content Lists
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        if (showHalloween) {
                            item {
                                StickerVisualRow(
                                    title = strings.canvas.elementsPackHalloween(filteredHalloween.size),
                                    items = filteredHalloween,
                                    onSelectItem = { item ->
                                        onAddLayer(
                                            CanvasLayer(
                                                id = "layer_sticker_${System.currentTimeMillis()}",
                                                name = item.name,
                                                type = LayerType.IMAGE,
                                                imageUrl = item.imageName,
                                                widthMm = 55f,
                                                heightMm = 55f,
                                                reliefDepthMm = item.defaultReliefMm,
                                                fillColor = 0xFF2E7D32L
                                            )
                                        )
                                        onDismiss()
                                    }
                                )
                            }
                        }

                        if (showTrendy) {
                            item {
                                StickerVisualRow(
                                    title = strings.canvas.elementsPackTrendy(filteredTrendy.size),
                                    items = filteredTrendy,
                                    onSelectItem = { item ->
                                        onAddLayer(
                                            CanvasLayer(
                                                id = "layer_sticker_${System.currentTimeMillis()}",
                                                name = item.name,
                                                type = LayerType.IMAGE,
                                                imageUrl = item.imageName,
                                                widthMm = 55f,
                                                heightMm = 55f,
                                                reliefDepthMm = item.defaultReliefMm,
                                                fillColor = 0xFF2E7D32L
                                            )
                                        )
                                        onDismiss()
                                    }
                                )
                            }
                        }

                        if (showRelief) {
                            item {
                                StickerVisualRow(
                                    title = strings.canvas.elementsPackRelief(filteredRelief.size),
                                    items = filteredRelief,
                                    onSelectItem = { item ->
                                        onAddLayer(
                                            CanvasLayer(
                                                id = "layer_sticker_${System.currentTimeMillis()}",
                                                name = item.name,
                                                type = LayerType.IMAGE,
                                                imageUrl = item.imageName,
                                                widthMm = 60f,
                                                heightMm = 60f,
                                                reliefDepthMm = item.defaultReliefMm,
                                                fillColor = 0xFF2E7D32L
                                            )
                                        )
                                        onDismiss()
                                    }
                                )
                            }
                        }

                        if (showShapes) {
                            item {
                                SectionRow(
                                    title = strings.canvas.elementsPackShapes,
                                    items = shapeItems,
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
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun StickerVisualRow(
    title: String,
    items: List<StickerVisualItem>,
    onSelectItem: (StickerVisualItem) -> Unit
) {
    val strings = LocalStrings.current
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
                text = strings.common.viewAll,
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                val bmp = rememberAppImage(item.imageName)
                Box(
                    modifier = Modifier
                        .size(width = 88.dp, height = 96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { onSelectItem(item) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            if (bmp != null) {
                                Image(
                                    bitmap = bmp,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            item.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = Color(0xFF334155)
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
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { (name, color) ->
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F3F5))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .clickable { onSelectItem(name, color) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 13.sp,
            color = Color(0xFF1A1A1A)
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E7D32)),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
