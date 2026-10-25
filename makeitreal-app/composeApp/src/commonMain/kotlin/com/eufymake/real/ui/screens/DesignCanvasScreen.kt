package com.eufymake.real.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.*
import com.eufymake.real.ui.components.*
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.util.bounceClick
import com.eufymake.real.ui.util.rememberAppImage
import com.eufymake.real.ui.util.tactileClick
import com.eufymake.real.ui.util.tactilePress
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
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
    val strings = LocalStrings.current
    var selectedLayerId by remember { mutableStateOf<String?>(draft.layers.firstOrNull()?.id) }
    val selectedLayer = draft.layers.firstOrNull { it.id == selectedLayerId }

    var currentAlignmentMode by remember { mutableStateOf(AlignmentMode.FLATBED_CAMERA) }
    var isAlignmentDropdownExpanded by remember { mutableStateOf(false) }

    fun AlignmentMode.label(): String = when (this) {
        AlignmentMode.FLATBED_CAMERA -> strings.canvas.alignModeFlatbedCamera
        AlignmentMode.FLATBED_MINI -> strings.canvas.alignModeFlatbedMini
        AlignmentMode.ROTARY_JIG -> strings.canvas.alignModeRotaryJig
        AlignmentMode.ROLL_UV_DTF -> strings.canvas.alignModeRollUvDtf
    }

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

                    IconButton(
                        onClick = {
                            if (selectedLayer != null) {
                                selectedLayerId = null
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(

                            Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription = "Back",

                            tint = Color(0xFF1A1A1A)

                        )

                    }

                    // Center Dropdown Pill: 4 alignment modes
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF2F3F5))
                                .tactileClick { isAlignmentDropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentAlignmentMode.label(),
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

                        DropdownMenu(
                            expanded = isAlignmentDropdownExpanded,
                            onDismissRequest = { isAlignmentDropdownExpanded = false }
                        ) {
                            AlignmentMode.entries.forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = mode.label(),
                                            fontWeight = if (mode == currentAlignmentMode) FontWeight.Bold else FontWeight.Normal,
                                            color = if (mode == currentAlignmentMode) BrandGreen else Color(0xFF1A1A1A)
                                        )
                                    },
                                    onClick = {
                                        currentAlignmentMode = mode
                                        isAlignmentDropdownExpanded = false
                                        toastMessage = mode.label()
                                    }
                                )
                            }
                        }
                    }

                    // Right Viewfinder / Camera align icon [o] triggers dropdown
                    IconButton(
                        onClick = { isAlignmentDropdownExpanded = true },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.CropFree,
                            contentDescription = currentAlignmentMode.label(),
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                }

            }

            // 2. Interactive Canvas Stage

            Box(

                modifier = Modifier

                    .weight(1f)

                    .fillMaxWidth()

                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { selectedLayerId = null }, // tap outside to deselect

                contentAlignment = Alignment.Center

            ) {

                // Physical Substrate Bed

                if (draft.substrate.shape == SubstrateShape.CYLINDER || currentAlignmentMode == AlignmentMode.ROTARY_JIG) {

                    // Cylindrical Mug Unrolled Canvas (Frame 0145)

                    MugUnrolledBed(

                        layers = draft.layers,

                        selectedLayerId = selectedLayerId,

                        onSelectLayer = { selectedLayerId = it },

                        onDragStart = onDragStart,

                        onDragEnd = onDragEnd,

                        onUpdateLayerTransient = onUpdateLayerTransient,

                        onUpdateLayer = onUpdateLayer

                    )

                } else {

                    // Flat Standard Substrate Bed (Frame 0056 & 0070)

                    FlatSubstrateBed(

                        draft = draft,

                        selectedLayerId = selectedLayerId,

                        onSelectLayer = { selectedLayerId = it },

                        onDragStart = onDragStart,

                        onDragEnd = onDragEnd,

                        onUpdateLayerTransient = onUpdateLayerTransient,

                        onUpdateLayer = onUpdateLayer

                    )

                }

                // Empty Canvas Prompt Card (AnkerMake Quick Creation CTA)

                if (draft.layers.isEmpty()) {

                    Surface(

                        shape = RoundedCornerShape(18.dp),

                        color = Color.White.copy(alpha = 0.95f),

                        shadowElevation = 6.dp,

                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),

                        modifier = Modifier

                            .padding(20.dp)

                            .wrapContentSize()

                    ) {

                        Column(

                            modifier = Modifier.padding(18.dp),

                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Text(

                                text = strings.canvas.canvasReady,

                                fontSize = 15.sp,

                                fontWeight = FontWeight.Bold,

                                color = Color(0xFF1A1A1A)

                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(

                                text = strings.canvas.quickAddTip,

                                fontSize = 11.sp,

                                color = Color(0xFF8E8E93)

                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 2
                            ) {
                                Button(
                                    onClick = { showUploadDrawer = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.height(42.dp).tactilePress()
                                ) {
                                    Text(strings.canvas.uploadPhoto, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showTextDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F5)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.height(42.dp).tactilePress()
                                ) {
                                    Text(strings.canvas.addText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                                }

                                Button(
                                    onClick = { showElementsDrawer = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F5)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.height(42.dp).tactilePress()
                                ) {
                                    Text(strings.canvas.stickers, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                                }

                                Button(
                                    onClick = { showAIDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F5)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.height(42.dp).tactilePress()
                                ) {
                                    Text(strings.canvas.aiEmboss, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                                }
                            }

                        }

                    }

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
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    onRemoveLayer(selectedLayer.id)
                                    selectedLayerId = null
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = strings.common.delete,
                                    tint = Color(0xFF1A1A1A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(14.dp)
                                    .background(Color(0xFFE5E7EB))
                            )

                            IconButton(
                                onClick = { showLayersSheet = true },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = strings.canvas.layers,
                                    tint = Color(0xFF1A1A1A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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
                            .tactileClick { onNavigateToCraftMode() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(

                                text = strings.canvas.recipePill,

                                fontSize = 11.sp,

                                fontWeight = FontWeight.Bold,

                                color = Color(0xFF1A1A1A)

                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(

                                Icons.AutoMirrored.Filled.KeyboardArrowRight,

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

                        if (draft.substrate.shape == SubstrateShape.CYLINDER || currentAlignmentMode == AlignmentMode.ROTARY_JIG) {

                            Box(

                                modifier = Modifier

                                    .size(38.dp)

                                    .clip(CircleShape)

                                    .background(Color.White)

                                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)

                                    .clickable {
                                        toastMessage = strings.canvas.mugHint
                                    },

                                contentAlignment = Alignment.Center

                            ) {

                                Icon(

                                    Icons.AutoMirrored.Filled.HelpOutline,

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
                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Horizontally scrollable element tool items (Guarantees zero overflow on 380dp)
                        LazyRow(
                            modifier = Modifier.weight(1f).padding(end = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                DockItem(
                                    icon = Icons.Default.Crop,
                                    label = strings.canvas.crop,
                                    onClick = { toastMessage = "Crop mode" }
                                )
                            }
                            item {
                                DockItem(
                                    icon = Icons.Default.AutoAwesome,
                                    label = strings.canvas.skills,
                                    onClick = { showSkillsDrawer = true }
                                )
                            }
                            item {
                                DockItem(
                                    icon = Icons.Default.Image,
                                    label = strings.canvas.aiGenerate,
                                    onClick = { showAIDialog = true }
                                )
                            }
                            item {
                                DockItem(
                                    icon = Icons.Default.Face,
                                    label = strings.canvas.removeBg,
                                    onClick = {
                                        toastMessage = "Cutout complete"
                                        onUpdateLayer(selectedLayer.copy(name = "${selectedLayer.name} (Cutout)"))
                                    }
                                )
                            }
                            item {
                                DockItem(
                                    icon = Icons.Default.HighQuality,
                                    label = strings.canvas.upscale,
                                    onClick = { toastMessage = "UV 1440 DPI HD complete" }
                                )
                            }
                            item {
                                DockItem(
                                    icon = Icons.Default.Check,
                                    label = strings.common.done,
                                    onClick = { selectedLayerId = null }
                                )
                            }
                        }

                        // Right Precise Size Card: "W 298.7 / L 298.7"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF7F8FA))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = "W ${(selectedLayer.widthMm * 10).toInt() / 10f}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A),
                                    lineHeight = 12.sp
                                )
                                Text(
                                    text = "L ${(selectedLayer.heightMm * 10).toInt() / 10f}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A),
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    // STATE A: Canvas Global Mode (Frame 0070)
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DockItem(
                                icon = Icons.Default.CloudUpload,
                                label = strings.canvas.upload,
                                onClick = { showUploadDrawer = true }
                            )
                            DockItem(
                                icon = Icons.Default.AutoAwesome,
                                label = strings.canvas.skills,
                                onClick = { showSkillsDrawer = true }
                            )
                            DockItem(
                                icon = Icons.Default.Image,
                                label = strings.canvas.aiGenerate,
                                onClick = { showAIDialog = true }
                            )
                            DockItem(
                                icon = Icons.Default.Title,
                                label = strings.canvas.addText,
                                onClick = { showTextDialog = true }
                            )
                            DockItem(
                                icon = Icons.Default.Widgets,
                                label = strings.canvas.elements,
                                onClick = { showElementsDrawer = true }
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Right Main CTA: Black capsule "下一步" (Next Step)
                        Button(
                            onClick = onNavigateToCraftMode,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlack),
                            shape = RoundedCornerShape(22.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = strings.canvas.nextStep,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
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

                                id = "layer_skill_${System.currentTimeMillis()}_${(1000..9999).random()}",

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
    onUpdateLayerTransient: (CanvasLayer) -> Unit,
    onUpdateLayer: (CanvasLayer) -> Unit
) {
    val strings = LocalStrings.current
    var isSnappedX by remember { mutableStateOf(false) }
    var isSnappedY by remember { mutableStateOf(false) }

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
        // Dashed safe margin print boundary + Cyan magnetic snap guidelines
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

            // Magnetic Snap Guidelines (Cyan dashed lines)
            if (isSnappedX) {
                drawLine(
                    color = Color(0xFF00A0E9),
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
            if (isSnappedY) {
                drawLine(
                    color = Color(0xFF00A0E9),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
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
                onDragEnd = {
                    isSnappedX = false
                    isSnappedY = false
                    onDragEnd()
                },
                onSnapChange = { sx, sy ->
                    isSnappedX = sx
                    isSnappedY = sy
                },
                onUpdateLayerTransient = onUpdateLayerTransient,
                onUpdateLayer = onUpdateLayer
            )
        }

        // Floating Snap Alignment Badge
        if (isSnappedX || isSnappedY) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF00A0E9).copy(alpha = 0.92f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = when {
                        isSnappedX && isSnappedY -> strings.canvas.snapCenter
                        isSnappedX -> strings.canvas.snapH
                        else -> strings.canvas.snapV
                    },
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
    onUpdateLayerTransient: (CanvasLayer) -> Unit,
    onUpdateLayer: (CanvasLayer) -> Unit
) {
    val strings = LocalStrings.current
    var isSnappedX by remember { mutableStateOf(false) }
    var isSnappedY by remember { mutableStateOf(false) }

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
            // Hatched Non-Printable Zones on Left and Right (///) + Magnetic guidelines
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

                // Magnetic Snap Guidelines
                if (isSnappedX) {
                    drawLine(
                        color = Color(0xFF00A0E9),
                        start = Offset(size.width / 2f, 0f),
                        end = Offset(size.width / 2f, size.height),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
                if (isSnappedY) {
                    drawLine(
                        color = Color(0xFF00A0E9),
                        start = Offset(0f, size.height / 2f),
                        end = Offset(size.width, size.height / 2f),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
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
                        onDragEnd = {
                            isSnappedX = false
                            isSnappedY = false
                            onDragEnd()
                        },
                        onSnapChange = { sx, sy ->
                            isSnappedX = sx
                            isSnappedY = sy
                        },
                        onUpdateLayerTransient = onUpdateLayerTransient,
                        onUpdateLayer = onUpdateLayer
                    )
                }

                // Snap feedback badge
                if (isSnappedX || isSnappedY) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00A0E9).copy(alpha = 0.92f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when {
                                isSnappedX && isSnappedY -> strings.canvas.snapCenter
                                isSnappedX -> strings.canvas.snapH
                                else -> strings.canvas.snapV
                            },
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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

                Text(strings.canvas.mugHandleLeft, fontSize = 10.sp, color = Color(0xFF94A3B8))

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp))

                Text(strings.canvas.mugFront, fontSize = 10.sp, color = Color(0xFF94A3B8))

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Icon(Icons.Default.Coffee, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(24.dp).graphicsLayer { scaleX = -1f })

                Text(strings.canvas.mugHandleRight, fontSize = 10.sp, color = Color(0xFF94A3B8))

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
    onSnapChange: ((Boolean, Boolean) -> Unit)? = null,
    onUpdateLayerTransient: (CanvasLayer) -> Unit,
    onUpdateLayer: (CanvasLayer) -> Unit
) {
    val currentLayer by rememberUpdatedState(layer)

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (layer.xOffsetMm * 2f).dp.roundToPx(),
                    (layer.yOffsetMm * 2f).dp.roundToPx()
                )
            }
            .size(
                width = (layer.widthMm * 2f).coerceAtLeast(50f).dp,
                height = (layer.heightMm * 2f).coerceAtLeast(40f).dp
            )
            .graphicsLayer { rotationZ = layer.rotationDegrees }
            .pointerInput(layer.id) {
                var currentX = 0f
                var currentY = 0f
                val pxPerMm = 2.dp.toPx()

                detectDragGestures(
                    onDragStart = {
                        onSelect()
                        onDragStart()
                        currentX = currentLayer.xOffsetMm
                        currentY = currentLayer.yOffsetMm
                    },
                    onDragEnd = {
                        onSnapChange?.invoke(false, false)
                        onDragEnd()
                    },
                    onDragCancel = {
                        onSnapChange?.invoke(false, false)
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val deltaXMm = dragAmount.x / pxPerMm
                        val deltaYMm = dragAmount.y / pxPerMm
                        currentX += deltaXMm
                        currentY += deltaYMm

                        val isNearXCenter = kotlin.math.abs(currentX) <= 1.5f
                        val isNearYCenter = kotlin.math.abs(currentY) <= 1.5f
                        val snappedX = if (isNearXCenter) 0f else currentX
                        val snappedY = if (isNearYCenter) 0f else currentY

                        onSnapChange?.invoke(isNearXCenter, isNearYCenter)

                        val updated = currentLayer.copy(
                            xOffsetMm = snappedX,
                            yOffsetMm = snappedY
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

                val realImg = layer.imageUrl?.let { rememberAppImage(it) }

                if (realImg != null) {

                    Image(

                        bitmap = realImg,

                        contentDescription = layer.name,

                        contentScale = ContentScale.Crop,

                        modifier = Modifier

                            .fillMaxSize()

                            .clip(RoundedCornerShape(8.dp))

                    )

                } else {

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

            // 8 Scale Handle Tabs (4 interactive corner scale tabs + 4 edge centers)
            HandleTab(
                Alignment.TopStart,
                offset = Offset(-4f, -4f),
                onScaleStart = onDragStart,
                onScaleDelta = { deltaX, deltaY ->
                    val newW = (layer.widthMm - deltaX).coerceIn(20f, 300f)
                    val newH = (layer.heightMm - deltaY).coerceIn(20f, 300f)
                    onUpdateLayerTransient(layer.copy(widthMm = newW, heightMm = newH))
                },
                onScaleEnd = onDragEnd
            )

            HandleTab(Alignment.TopCenter, offset = Offset(0f, -4f))

            HandleTab(
                Alignment.TopEnd,
                offset = Offset(4f, -4f),
                onScaleStart = onDragStart,
                onScaleDelta = { deltaX, deltaY ->
                    val newW = (layer.widthMm + deltaX).coerceIn(20f, 300f)
                    val newH = (layer.heightMm - deltaY).coerceIn(20f, 300f)
                    onUpdateLayerTransient(layer.copy(widthMm = newW, heightMm = newH))
                },
                onScaleEnd = onDragEnd
            )

            HandleTab(Alignment.CenterStart, offset = Offset(-4f, 0f))

            HandleTab(Alignment.CenterEnd, offset = Offset(4f, 0f))

            HandleTab(
                Alignment.BottomStart,
                offset = Offset(-4f, 4f),
                onScaleStart = onDragStart,
                onScaleDelta = { deltaX, deltaY ->
                    val newW = (layer.widthMm - deltaX).coerceIn(20f, 300f)
                    val newH = (layer.heightMm + deltaY).coerceIn(20f, 300f)
                    onUpdateLayerTransient(layer.copy(widthMm = newW, heightMm = newH))
                },
                onScaleEnd = onDragEnd
            )

            HandleTab(Alignment.BottomCenter, offset = Offset(0f, 4f))

            HandleTab(
                Alignment.BottomEnd,
                offset = Offset(4f, 4f),
                onScaleStart = onDragStart,
                onScaleDelta = { deltaX, deltaY ->
                    val newW = (layer.widthMm + deltaX).coerceIn(20f, 300f)
                    val newH = (layer.heightMm + deltaY).coerceIn(20f, 300f)
                    onUpdateLayerTransient(layer.copy(widthMm = newW, heightMm = newH))
                },
                onScaleEnd = onDragEnd
            )

            // Bottom-Center Independent Rotation Handle `↻` (Frame 0056)

            Box(

                modifier = Modifier

                    .align(Alignment.BottomCenter)

                    .offset(y = 22.dp)

                    .size(24.dp)

                    .clip(CircleShape)

                    .background(Color.White)

                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)

                    .clickable {

                        val nextRotation = (layer.rotationDegrees + 90f) % 360f

                        onUpdateLayer(layer.copy(rotationDegrees = nextRotation))

                    },

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

private fun BoxScope.HandleTab(
    alignment: Alignment,
    offset: Offset,
    onScaleStart: (() -> Unit)? = null,
    onScaleDelta: ((Float, Float) -> Unit)? = null,
    onScaleEnd: (() -> Unit)? = null
) {
    val currentScaleStart by rememberUpdatedState(onScaleStart)
    val currentScaleDelta by rememberUpdatedState(onScaleDelta)
    val currentScaleEnd by rememberUpdatedState(onScaleEnd)

    val handleModifier = Modifier
        .align(alignment)
        .offset(x = offset.x.dp, y = offset.y.dp)
        .size(8.dp)
        .background(Color.White)
        .border(1.dp, BrandGreen)
        .then(
            if (onScaleDelta != null) {
                Modifier.pointerInput(alignment) {
                    val pxPerMm = 2.dp.toPx()
                    detectDragGestures(
                        onDragStart = { currentScaleStart?.invoke() },
                        onDragEnd = { currentScaleEnd?.invoke() },
                        onDragCancel = { currentScaleEnd?.invoke() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val deltaXMm = dragAmount.x / pxPerMm
                            val deltaYMm = dragAmount.y / pxPerMm
                            currentScaleDelta?.invoke(deltaXMm, deltaYMm)
                        }
                    )
                }
            } else Modifier
        )

    Box(modifier = handleModifier)
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

