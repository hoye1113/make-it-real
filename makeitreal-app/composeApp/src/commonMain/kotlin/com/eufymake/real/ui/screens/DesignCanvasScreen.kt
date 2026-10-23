package com.eufymake.real.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.*
import com.eufymake.real.ui.components.*
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen
import kotlin.math.roundToInt

@Composable
fun DesignCanvasScreen(
    draft: Draft,
    canUndo: Boolean,
    canRedo: Boolean,
    availableAICredits: Int,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onBack: () -> Unit,
    onAddLayer: (CanvasLayer) -> Unit,
    onDragStart: () -> Unit,
    onUpdateLayerTransient: (CanvasLayer) -> Unit,
    onDragEnd: () -> Unit,
    onUpdateLayer: (CanvasLayer) -> Unit,
    onRemoveLayer: (String) -> Unit,
    onSpendAICredits: (Int) -> Boolean,
    onNavigateToCraftMode: () -> Unit
) {
    var selectedLayerId by remember { mutableStateOf<String?>(draft.layers.firstOrNull()?.id) }
    val selectedLayer = draft.layers.firstOrNull { it.id == selectedLayerId }

    // Drawer visibility states
    var showUploadDrawer by remember { mutableStateOf(false) }
    var showSkillsDrawer by remember { mutableStateOf(false) }
    var showAIDialog by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }
    var showElementsDrawer by remember { mutableStateOf(false) }
    var showLayersSheet by remember { mutableStateOf(false) }

    // Toast message for actions (e.g. AI background removed)
    var toastMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            kotlinx.coroutines.delay(2000)
            toastMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Header Bar (Frame 0056 & 0070)
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }

                    // Center Dropdown Pill: "标准 摄像头对齐 v"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF2F3F5))
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "标准  摄像头对齐",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Right Viewfinder / Camera align icon [o]
                    IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.CropFree,
                            contentDescription = "Viewfinder Align",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                }
            }

            // 2. Interactive Canvas Stage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { selectedLayerId = null }, // tap outside to deselect
                contentAlignment = Alignment.Center
            ) {
                // Physical Substrate Bed
                if (draft.substrate.shape == SubstrateShape.CYLINDER) {
                    // Cylindrical Mug Unrolled Canvas (Frame 0145)
                    MugUnrolledBed(
                        layers = draft.layers,
                        selectedLayerId = selectedLayerId,
                        onSelectLayer = { selectedLayerId = it },
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onUpdateLayerTransient = onUpdateLayerTransient
                    )
                } else {
                    // Flat Standard Substrate Bed (Frame 0056 & 0070)
                    FlatSubstrateBed(
                        draft = draft,
                        selectedLayerId = selectedLayerId,
                        onSelectLayer = { selectedLayerId = it },
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onUpdateLayerTransient = onUpdateLayerTransient
                    )
                }

                // Floating Action Bar on top of selected object: [🗑 删除 | ⋮ 更多] (Frame 0056)
                if (selectedLayer != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        onRemoveLayer(selectedLayer.id)
                                        selectedLayerId = null
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(14.dp)
                                    .background(Color(0xFFE5E7EB))
                            )
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { showLayersSheet = true }
                            )
                        }
                    }

                    // Floating Craft Recipe Pill on Canvas bottom-right (Frame 0056): "平面 白墨->CMYK->光油 >"
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .clickable { onNavigateToCraftMode() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "平面  白墨->CMYK->光油",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Floating Bottom-Left Undo / Redo Double Pill (Frame 0070)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) Color(0xFF1A1A1A) else Color(0xFFD1D5DB),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(enabled = canUndo, onClick = onUndo)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) Color(0xFF1A1A1A) else Color(0xFFD1D5DB),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(enabled = canRedo, onClick = onRedo)
                        )
                    }
                }

                // Floating Bottom-Right Layers Sheet Button (if no layer selected, Frame 0070)
                if (selectedLayer == null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (draft.substrate.shape == SubstrateShape.CYLINDER) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                                    .clickable { toastMessage = "圆柱杯体展开：左右斜线区为把手避让区" },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.HelpOutline,
                                    contentDescription = "Help",
                                    tint = Color(0xFF1A1A1A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                            .clickable { showLayersSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = "Layers",
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 3. Dynamic Two-State Bottom Dock (THE CORE TOOL LINKAGE!)
            Surface(
                modifier = Modifier.fillMaxWidth().height(68.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                if (selectedLayer != null) {
                    // STATE B: Object Selected Mode (Frame 0056)
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Element Specialized Tool Items
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DockItem(
                                icon = Icons.Default.Crop,
                                label = "裁切",
                                onClick = { toastMessage = "裁剪功能已激活" }
                            )
                            DockItem(
                                icon = Icons.Default.AutoAwesome,
                                label = "技能",
                                onClick = { showSkillsDrawer = true }
                            )
                            DockItem(
                                icon = Icons.Default.Image,
                                label = "图像AI",
                                onClick = { showAIDialog = true }
                            )
                            DockItem(
                                icon = Icons.Default.Face,
                                label = "AI背景移除器",
                                onClick = {
                                    toastMessage = "AI一键扣图成功"
                                    onUpdateLayer(selectedLayer.copy(name = "${selectedLayer.name} (已扣图)"))
                                }
                            )
                            DockItem(
                                icon = Icons.Default.HighQuality,
                                label = "画质提升",
                                onClick = { toastMessage = "画质提升：UV 1440 DPI超清超分完成" }
                            )
                        }

                        // Right Precise Size Card: "W 298.7 / L 298.7"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF7F8FA))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "W ${(selectedLayer.widthMm * 10).toInt() / 10f}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = "L ${(selectedLayer.heightMm * 10).toInt() / 10f}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                } else {
                    // STATE A: Canvas Global Mode (Frame 0070)
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DockItem(
                                icon = Icons.Default.CloudUpload,
                                label = "上传",
                                onClick = { showUploadDrawer = true }
                            )
                            DockItem(
                                icon = Icons.Default.AutoAwesome,
                                label = "技能",
                                onClick = { showSkillsDrawer = true }
                            )
                            DockItem(
                                icon = Icons.Default.Image,
                                label = "图像AI",
                                onClick = { showAIDialog = true }
                            )
                            DockItem(
                                icon = Icons.Default.Title,
                                label = "文字",
                                onClick = { showTextDialog = true }
                            )
                            DockItem(
                                icon = Icons.Default.Widgets,
                                label = "元素",
                                onClick = { showElementsDrawer = true }
                            )
                        }

                        // Right Main CTA: Black capsule "下一步" (Next Step)
                        Button(
                            onClick = onNavigateToCraftMode,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlack),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(
                                text = "下一步",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Action feedback toast
        if (toastMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = toastMessage ?: "",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Drawers & Modals
        if (showUploadDrawer) {
            UploadDrawer(
                onDismiss = { showUploadDrawer = false },
                onSelectImage = { layer ->
                    onAddLayer(layer)
                    selectedLayerId = layer.id
                }
            )
        }

        if (showSkillsDrawer) {
            SkillsDrawer(
                onDismiss = { showSkillsDrawer = false },
                onApplySkill = { skillTitle, depth ->
                    if (selectedLayer != null) {
                        onUpdateLayer(selectedLayer.copy(name = "${selectedLayer.name} ($skillTitle)", reliefDepthMm = depth))
                    } else {
                        onAddLayer(
                            CanvasLayer(
                                id = "layer_skill_${System.currentTimeMillis()}",
                                name = skillTitle,
                                type = LayerType.IMAGE,
                                widthMm = 60f,
                                heightMm = 60f,
                                reliefDepthMm = depth,
                                fillColor = 0xFF2E7D32L
                            )
                        )
                    }
                }
            )
        }

        if (showAIDialog) {
            AIDialog(
                availableCredits = availableAICredits,
                onSpendCredits = onSpendAICredits,
                onAddGeneratedLayer = { layer ->
                    onAddLayer(layer)
                    selectedLayerId = layer.id
                },
                onDismiss = { showAIDialog = false }
            )
        }

        if (showTextDialog) {
            TypographyDialog(
                onAddTextLayer = { layer ->
                    onAddLayer(layer)
                    selectedLayerId = layer.id
                },
                onDismiss = { showTextDialog = false }
            )
        }

        if (showElementsDrawer) {
            ElementsDrawer(
                onDismiss = { showElementsDrawer = false },
                onAddLayer = { layer ->
                    onAddLayer(layer)
                    selectedLayerId = layer.id
                }
            )
        }

        if (showLayersSheet) {
            LayersSheet(
                layers = draft.layers,
                selectedLayerId = selectedLayerId,
                onSelectLayer = { selectedLayerId = it },
                onToggleVisibility = { layer -> onUpdateLayer(layer.copy(isVisible = !layer.isVisible)) },
                onRemoveLayer = onRemoveLayer,
                onDismiss = { showLayersSheet = false }
            )
        }
    }
}

@Composable
private fun FlatSubstrateBed(
    draft: Draft,
    selectedLayerId: String?,
    onSelectLayer: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onUpdateLayerTransient: (CanvasLayer) -> Unit
) {
    Box(
        modifier = Modifier
            .size(310.dp)
            .clip(
                if (draft.substrate.shape == SubstrateShape.CIRCLE) CircleShape
                else RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .border(
                1.dp,
                Color(0xFFE5E7EB),
                if (draft.substrate.shape == SubstrateShape.CIRCLE) CircleShape
                else RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Dashed safe margin print boundary
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val stroke = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
            if (draft.substrate.shape == SubstrateShape.CIRCLE) {
                drawCircle(color = Color(0xFFCBD5E1), style = stroke)
            } else {
                drawRoundRect(
                    color = Color(0xFFCBD5E1),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                    style = stroke
                )
            }
        }

        // Render Canvas Layers
        draft.layers.filter { it.isVisible }.forEach { layer ->
            val isSelected = layer.id == selectedLayerId
            GizmoLayerItem(
                layer = layer,
                isSelected = isSelected,
                onSelect = { onSelectLayer(layer.id) },
                onDragStart = onDragStart,
                onDragEnd = onDragEnd,
                onUpdateLayerTransient = onUpdateLayerTransient
            )
        }
    }
}

@Composable
private fun MugUnrolledBed(
    layers: List<CanvasLayer>,
    selectedLayerId: String?,
    onSelectLayer: (String) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onUpdateLayerTransient: (CanvasLayer) -> Unit
) {
    // Unrolled cylinder surface with hatched margin areas (Frame 0145)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
        ) {
            // Hatched Non-Printable Zones on Left and Right (///)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val marginWidthPx = size.width * 0.16f
                val stroke = Stroke(width = 1.dp.toPx())

                // Left hatched margin
                var y = 0f
                while (y < size.height + marginWidthPx) {
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(0f, y),
                        end = Offset(marginWidthPx, y - marginWidthPx),
                        strokeWidth = 1f
                    )
                    y += 12f
                }

                // Right hatched margin
                y = 0f
                val rightStart = size.width - marginWidthPx
                while (y < size.height + marginWidthPx) {
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(rightStart, y),
                        end = Offset(size.width, y - marginWidthPx),
                        strokeWidth = 1f
                    )
                    y += 12f
                }

                // Dotted Guide Lines
                val dashedStroke = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(marginWidthPx + 30f, 0f),
                    end = Offset(marginWidthPx + 30f, size.height),
                    pathEffect = dashedStroke.pathEffect
                )
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    pathEffect = dashedStroke.pathEffect
                )
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(size.width - marginWidthPx - 30f, 0f),
                    end = Offset(size.width - marginWidthPx - 30f, size.height),
                    pathEffect = dashedStroke.pathEffect
                )
            }

            // Render Layers in printable center
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                layers.filter { it.isVisible }.forEach { layer ->
                    val isSelected = layer.id == selectedLayerId
                    GizmoLayerItem(
                        layer = layer,
                        isSelected = isSelected,
                        onSelect = { onSelectLayer(layer.id) },
                        onDragStart = onDragStart,
                        onDragEnd = onDragEnd,
                        onUpdateLayerTransient = onUpdateLayerTransient
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Three Cup Orientation Icons below guidelines (Frame 0145)
        Row(
            modifier = Modifier.width(340.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Coffee, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp))
                Text("把手左侧", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp))
                Text("杯身正面", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Coffee, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp).graphicsLayer { scaleX = -1f })
                Text("把手右侧", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun GizmoLayerItem(
    layer: CanvasLayer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onUpdateLayerTransient: (CanvasLayer) -> Unit
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    layer.xOffsetMm.roundToInt() * 2,
                    layer.yOffsetMm.roundToInt() * 2
                )
            }
            .size(
                width = (layer.widthMm * 2f).coerceAtLeast(50f).dp,
                height = (layer.heightMm * 2f).coerceAtLeast(40f).dp
            )
            .pointerInput(layer.id) {
                detectDragGestures(
                    onDragStart = {
                        onSelect()
                        onDragStart()
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val updated = layer.copy(
                            xOffsetMm = layer.xOffsetMm + dragAmount.x / 2f,
                            yOffsetMm = layer.yOffsetMm + dragAmount.y / 2f
                        )
                        onUpdateLayerTransient(updated)
                    }
                )
            }
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        // Inner Content Render
        when (layer.type) {
            LayerType.IMAGE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(layer.fillColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = layer.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
            LayerType.TEXT -> {
                Text(
                    text = layer.textContent,
                    fontSize = (layer.fontSizeSp * 0.7f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(layer.textColor)
                )
            }
            LayerType.SHAPE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            if (layer.shapeType == "CIRCLE") CircleShape
                            else RoundedCornerShape(8.dp)
                        )
                        .background(Color(layer.fillColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (layer.shapeType == "CIRCLE") Icons.Default.Circle else Icons.Default.CropSquare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bounding Box & 8 Handles & Rotation Gizmo (Frame 0056)
        if (isSelected) {
            // Cyan/Green Hairline Border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.5.dp, BrandGreen, RoundedCornerShape(4.dp))
            )

            // Top-left Cyan Rotation Ring Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFF00A0E9), CircleShape)
            )

            // 8 Scale Handle Tabs (4 corners + 4 edge centers)
            HandleTab(Alignment.TopStart, offset = Offset(-4f, -4f))
            HandleTab(Alignment.TopCenter, offset = Offset(0f, -4f))
            HandleTab(Alignment.TopEnd, offset = Offset(4f, -4f))
            HandleTab(Alignment.CenterStart, offset = Offset(-4f, 0f))
            HandleTab(Alignment.CenterEnd, offset = Offset(4f, 0f))
            HandleTab(Alignment.BottomStart, offset = Offset(-4f, 4f))
            HandleTab(Alignment.BottomCenter, offset = Offset(0f, 4f))
            HandleTab(Alignment.BottomEnd, offset = Offset(4f, 4f))

            // Bottom-Center Independent Rotation Handle `↻` (Frame 0056)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 22.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Rotate",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.HandleTab(alignment: Alignment, offset: Offset) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = offset.x.dp, y = offset.y.dp)
            .size(7.dp)
            .background(Color.White)
            .border(1.dp, BrandGreen)
    )
}

@Composable
private fun DockItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF1A1A1A),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
    }
}
