package com.eufymake.real.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.i18n.AppStrings
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.Draft
import com.eufymake.real.model.SubstrateShape
import com.eufymake.real.model.GlossMode
import com.eufymake.real.model.PrintMode
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.theme.StudioDarkStatusBar
import com.eufymake.real.ui.theme.StudioDarkTopBar
import com.eufymake.real.ui.theme.StudioDarkWorkspace
import com.eufymake.real.ui.util.SubstrateMaterials
import com.eufymake.real.ui.util.bounceClick
import com.eufymake.real.ui.util.rememberAppImage
import com.eufymake.real.ui.util.tactilePress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class CraftType {
    FLAT,
    FLAT_EMBOSS,
    PATTERN_TEXTURE,
    RELIEF_TEXTURE
}

enum class InkMode(val isGloss: Boolean) {
    COLOR_EMBOSS(false),
    GLOSS_EMBOSS(true),
    WHITE_AND_COLOR(false);

    fun localizedTitle(strings: AppStrings): String = when (this) {
        COLOR_EMBOSS -> strings.craft.inkColorEmboss
        GLOSS_EMBOSS -> strings.craft.inkGlossEmboss
        WHITE_AND_COLOR -> strings.craft.inkWhiteAndColor
    }
}

@Composable
fun CraftMode3DScreen(
    draft: Draft,
    availableAICredits: Int,
    onBack: () -> Unit,
    onSpendCredits: (Int) -> Boolean,
    onReadyToPrint: (PrintMode, Float, GlossMode) -> Unit
) {
    val strings = LocalStrings.current
    var selectedCraft by remember { mutableStateOf(CraftType.FLAT_EMBOSS) }
    var maxHeightMm by remember { mutableStateOf(2.5f) }
    var smoothness by remember { mutableStateOf(7) }
    var inkSavingPercent by remember { mutableStateOf(0) }
    var whiteUnderbaseChoke by remember { mutableStateOf(0.2f) }
    var selectedInkMode by remember { mutableStateOf(InkMode.COLOR_EMBOSS) }
    var showInkModeMenu by remember { mutableStateOf(false) }

    // Wireframe overlay toggle (from AnkerMake Frame 0080)
    var showWireframe by remember { mutableStateOf(false) }

    // Perspective angle
    var isPerspectiveTilted by remember { mutableStateOf(true) }
    var dragPitch by remember { mutableStateOf(0f) }
    var dragYaw by remember { mutableStateOf(0f) }
    val animBaseX by animateFloatAsState(targetValue = if (isPerspectiveTilted) 26f else 0f)
    val animBaseZ by animateFloatAsState(targetValue = if (isPerspectiveTilted) -12f else 0f)
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showSubstrateInfoDialog by remember { mutableStateOf(false) }
    var skipAICreditDialog by remember { mutableStateOf(false) }

    // AI confirmation and generation states
    var showAIPointsDialog by remember { mutableStateOf(false) }
    var isGeneratingTexture by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableStateOf(0f) }
    var showChokeHelpDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var resetJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Trigger texture generation when relief mode confirmed (smooth cinematic tween)
    LaunchedEffect(isGeneratingTexture) {
        if (isGeneratingTexture) {
            generationProgress = 0f
            androidx.compose.animation.core.animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            ) { value, _ ->
                generationProgress = value
            }
            isGeneratingTexture = false
        }
    }

    // Get primary layer artwork image name (or fallback to demo artwork)
    val activeImageName = draft.layers.firstOrNull { !it.imageUrl.isNullOrEmpty() }?.imageUrl
        ?: "dinosaur_clock_relief.webp"
    val artworkBitmap = rememberAppImage(activeImageName)

    Box(modifier = Modifier.fillMaxSize().background(StudioDarkWorkspace)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: 56dp Height, Dark Immersive Surface matching StudioDarkTopBar
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                color = StudioDarkTopBar,
                shadowElevation = 0.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.common.back,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = strings.craft.craftMode,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // 2. 3D Perspective Workspace with Touch/Drag Orbiting
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(StudioDarkWorkspace)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { resetJob?.cancel() }
                        ) { change, dragAmount ->
                            change.consume()
                            dragYaw = (dragYaw + dragAmount.x * 0.35f).coerceIn(-50f, 50f)
                            dragPitch = (dragPitch - dragAmount.y * 0.35f).coerceIn(-25f, 25f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Top-left substrate dropdown pill & Wireframe toggle (Frame 0080)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp, top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .tactilePress()
                            .clickable {
                                showSubstrateInfoDialog = true
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = draft.substrate.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFFE2E8F0),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Wireframe Toggle Icon (44dp touch target with 32dp visual capsule)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .tactilePress()
                            .clickable { showWireframe = !showWireframe },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (showWireframe) BrandGreen else Color.White.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.GridOn,
                                contentDescription = strings.craft.wireframe,
                                tint = if (showWireframe) Color.White else Color(0xFF1E293B),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // Bottom-right 3D/2D perspective toggle & reticle button (Frame 0080) with smooth spring reset
                IconButton(
                    onClick = {
                        if (dragPitch != 0f || dragYaw != 0f) {
                            resetJob?.cancel()
                            resetJob = coroutineScope.launch {
                                val startPitch = dragPitch
                                val startYaw = dragYaw
                                androidx.compose.animation.core.animate(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) { frac, _ ->
                                    dragPitch = startPitch * (1f - frac)
                                    dragYaw = startYaw * (1f - frac)
                                }
                                dragPitch = 0f
                                dragYaw = 0f
                            }
                            isPerspectiveTilted = true
                        } else {
                            resetJob?.cancel()
                            isPerspectiveTilted = !isPerspectiveTilted
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 14.dp, bottom = 14.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        if (isPerspectiveTilted) Icons.Default.Refresh else Icons.Default.CropFree,
                        contentDescription = if (isPerspectiveTilted) strings.craft.view2D else strings.craft.view3D,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isGeneratingTexture) {
                    // Texture generation progress indicator (Frame 0090)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "${(generationProgress * 100).toInt()}%",
                            fontSize = 34.sp,
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
                            text = strings.craft.calculatingRelief,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    // 3D Angled Tilted Substrate Bed with Decoupled Ground Shadow (Frame 0070 & 0080)
                    Box(
                        modifier = Modifier.offset(y = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Reactive ambient table drop shadow (moves slightly with turntable yaw)
                        Box(
                            modifier = Modifier
                                .size(210.dp, 22.dp)
                                .offset { IntOffset(x = (-dragYaw * 1.5f).roundToInt(), y = 115.dp.roundToPx()) }
                                .graphicsLayer { alpha = 0.45f - kotlin.math.abs(dragPitch) * 0.005f }
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)
                                    )
                                )
                        )

                        // 3D Rotating Bed Container with Physical Slab Depth & Authentic Substrate Material
                        Box(
                            modifier = Modifier
                                .size(230.dp)
                                .graphicsLayer {
                                    this.rotationX = animBaseX + dragPitch
                                    this.rotationY = dragYaw
                                    this.rotationZ = animBaseZ
                                    this.cameraDistance = 10f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Bottom slab physical depth (3mm thickness)
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .offset(y = 6.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SubstrateMaterials.getSlabDepthColor(draft.substrate))
                            )

                            // Top bed surface with authentic material texture
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SubstrateMaterials.getBrush(draft.substrate))
                                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Real Artwork on 3D Bed with Embossed Lighting Treatment
                                if (artworkBitmap != null) {
                                    val isCircular = draft.substrate.shape == SubstrateShape.CIRCLE ||
                                                    draft.substrate.shape == SubstrateShape.CYLINDER
                                    val artShape = if (isCircular) CircleShape else RoundedCornerShape(8.dp)

                                    Box(
                                        modifier = Modifier
                                            .size(190.dp)
                                            .shadow(elevation = (maxHeightMm * 2.5f).dp, shape = artShape)
                                            .clip(artShape)
                                            .border(
                                                width = if (selectedCraft != CraftType.FLAT) (maxHeightMm * 0.8f).coerceAtLeast(1.5f).dp else 0.5.dp,
                                                brush = Brush.linearGradient(
                                                    listOf(
                                                        Color.White.copy(alpha = 0.85f),
                                                        Color.Black.copy(alpha = 0.55f)
                                                    )
                                                ),
                                                shape = artShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = artworkBitmap,
                                            contentDescription = "3D Artwork",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Dynamic 3D lighting reflection & relief bevel shading
                                        Canvas(modifier = Modifier.matchParentSize().clip(artShape)) {
                                            val gloss = selectedInkMode.isGloss
                                            val reliefFactor = (maxHeightMm / 4f).coerceIn(0.2f, 1f)

                                            // 1. Virtual light source fixed at upper-left; as dragYaw rotates, reflection moves across surface
                                            val lightCenterX = size.width * (0.42f - dragYaw / 110f)
                                            val lightCenterY = size.height * (0.35f - dragPitch / 110f)

                                            drawRect(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color.White.copy(alpha = if (gloss) 0.38f else 0.16f),
                                                        Color.Transparent
                                                    ),
                                                    center = Offset(lightCenterX, lightCenterY),
                                                    radius = size.minDimension * 0.72f
                                                ),
                                                blendMode = BlendMode.Screen
                                            )

                                            // 2. Bevel shading (Upper edge highlight, lower edge ambient occlusion)
                                            drawRect(
                                                brush = Brush.verticalGradient(
                                                    0.00f to Color.White.copy(alpha = 0.22f * reliefFactor),
                                                    0.12f to Color.Transparent,
                                                    0.85f to Color.Transparent,
                                                    1.00f to Color.Black.copy(alpha = 0.26f * reliefFactor)
                                                ),
                                                blendMode = BlendMode.Overlay
                                            )
                                        }

                                        // Micro-relief texture overlay if RELIEF_TEXTURE active
                                        if (selectedCraft == CraftType.RELIEF_TEXTURE) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.radialGradient(
                                                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                                                        )
                                                    )
                                            )
                                        }

                                        // Specular gloss sheen overlay with dynamic light sweep shimmer
                                        if (selectedInkMode.isGloss) {
                                            val infiniteTransition = rememberInfiniteTransition(label = "glossSweep")
                                            val sweepOffset by infiniteTransition.animateFloat(
                                                initialValue = -250f,
                                                targetValue = 650f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(2800, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Restart
                                                ),
                                                label = "sweepOffset"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                Color.White.copy(alpha = 0.05f),
                                                                Color.White.copy(alpha = 0.45f),
                                                                Color.White.copy(alpha = 0.05f)
                                                            ),
                                                            start = Offset(sweepOffset, sweepOffset),
                                                            end = Offset(sweepOffset + 140f, sweepOffset + 140f)
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                } else {
                                    // Fallback wireframe circular plate
                                    Box(
                                        modifier = Modifier
                                            .size(190.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("3D TACTILE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                // Wireframe overlay: Authentic Toolpath/Slice N×N Grid
                                if (showWireframe) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val stepPx = size.width / 10f
                                        val gridColor = Color(0xFF00C37A).copy(alpha = 0.45f)
                                        for (i in 1 until 10) {
                                            val pos = i * stepPx
                                            drawLine(gridColor, Offset(pos, 0f), Offset(pos, size.height), strokeWidth = 1f)
                                            drawLine(gridColor, Offset(0f, pos), Offset(size.width, pos), strokeWidth = 1f)
                                        }
                                        drawLine(gridColor.copy(alpha = 0.25f), Offset(0f, 0f), Offset(size.width, size.height), strokeWidth = 1f)
                                        drawLine(gridColor.copy(alpha = 0.25f), Offset(size.width, 0f), Offset(0f, size.height), strokeWidth = 1f)
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
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Center drag bar indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header row: 工艺模式 (i)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.craft.craftMode,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Craft Mode Selection Cards (Frames 0070 & 0080)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CraftCard(
                            title = strings.craft.craftFlatTitle,
                            subtitle = strings.craft.craftFlatSub,
                            badgeText = null,
                            isSelected = selectedCraft == CraftType.FLAT,
                            onClick = { selectedCraft = CraftType.FLAT },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            title = strings.craft.craftPatternTitle,
                            subtitle = strings.craft.craftPatternSub,
                            badgeText = null,
                            isSelected = selectedCraft == CraftType.FLAT_EMBOSS,
                            onClick = { selectedCraft = CraftType.FLAT_EMBOSS },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            title = strings.craft.craftReliefTitle,
                            subtitle = strings.craft.craftReliefSub,
                            badgeText = null,
                            isSelected = selectedCraft == CraftType.PATTERN_TEXTURE,
                            onClick = { selectedCraft = CraftType.PATTERN_TEXTURE },
                            modifier = Modifier.weight(1f)
                        )
                        CraftCard(
                            title = strings.craft.craft3DEmbossTitle,
                            subtitle = strings.craft.craft3DEmbossSub,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Parameter Controls Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 1. Ink Mode Selector
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strings.craft.inkMode,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .clickable { showInkModeMenu = true }
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedInkMode.localizedTitle(strings),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Sliders for Emboss & Relief modes (Frame 0100)
                        if (selectedCraft != CraftType.FLAT) {
                            // 2. 凸起高度 (Max Height)
                            AppSliderRow(
                                label = strings.craft.maxHeight,
                                valueText = "${((maxHeightMm * 10).toInt() / 10f)} mm",
                                isPill = true,
                                value = maxHeightMm,
                                min = 0.2f,
                                max = 5.0f,
                                onValueChange = { maxHeightMm = it }
                            )

                            // 3. 平滑度 (Smoothness)
                            AppSliderRow(
                                label = strings.craft.smoothness,
                                valueText = smoothness.toString(),
                                isPill = true,
                                value = smoothness.toFloat(),
                                min = 0f,
                                max = 10f,
                                onValueChange = { smoothness = it.roundToInt() }
                            )

                            // 4. 节省油墨 (Ink Saving)
                            AppSliderRow(
                                label = strings.craft.inkSaving,
                                hasHelpIcon = true,
                                valueText = "$inkSavingPercent%",
                                isPill = false,
                                value = inkSavingPercent.toFloat(),
                                min = 0f,
                                max = 50f,
                                onValueChange = { inkSavingPercent = it.roundToInt() }
                            )

                            // 5. 白墨内缩 (White Underbase Choke) - Essential UV Setting!
                            AppSliderRow(
                                label = strings.craft.whiteChoke,
                                hasHelpIcon = true,
                                onHelpClick = { showChokeHelpDialog = true },
                                valueText = "${((whiteUnderbaseChoke * 100).toInt() / 100f)} mm",
                                isPill = true,
                                value = whiteUnderbaseChoke,
                                min = 0.0f,
                                max = 0.5f,
                                onValueChange = { whiteUnderbaseChoke = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Main CTA Button: "准备打印" (Frame 0080)
                    Button(
                        onClick = {
                            val printMode = when (selectedCraft) {
                                CraftType.FLAT -> PrintMode.COLOR_ONLY
                                CraftType.FLAT_EMBOSS -> PrintMode.COLOR_AND_WHITE
                                CraftType.PATTERN_TEXTURE -> PrintMode.WHITE_ONLY
                                CraftType.RELIEF_TEXTURE -> PrintMode.RELIEF_3D
                            }
                            val glossMode = if (selectedInkMode.isGloss) GlossMode.FULL_GLOSS else GlossMode.NONE
                            onReadyToPrint(printMode, maxHeightMm, glossMode)
                        },
                        enabled = !isGeneratingTexture,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlack,
                            disabledContainerColor = Color(0xFFD1D5DB)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.craft.readyToPrint,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // AI Points Consumption Dialog (Frame 0082)
        if (showAIPointsDialog) {
            AIPointsConsumptionDialog(
                availableCredits = availableAICredits,
                onConfirm = {
                    showAIPointsDialog = false
                    val ok = onSpendCredits(5)
                    if (ok) {
                        selectedCraft = CraftType.RELIEF_TEXTURE
                        isGeneratingTexture = true
                    } else {
                        toastMessage = strings.craft.insufficientPoints(5, availableAICredits)
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
                        InkMode.values().forEach { mode ->
                            val isSelected = selectedInkMode == mode
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedInkMode = mode
                                        showInkModeMenu = false
                                    }
                                    .padding(horizontal = 24.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mode.localizedTitle(strings),
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandGreen else Color(0xFF1E293B)
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // White Choke Help Dialog
        if (showChokeHelpDialog) {
            AlertDialog(
                onDismissRequest = { showChokeHelpDialog = false },
                title = { Text(strings.craft.whiteChoke, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = strings.craft.whiteChokeHelp,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showChokeHelpDialog = false }) {
                        Text(strings.common.done, color = BrandGreen, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Lightweight Toast Feedback Banner
        if (toastMessage != null) {
            LaunchedEffect(toastMessage) {
                delay(2500)
                toastMessage = null
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp, start = 20.dp, end = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = toastMessage ?: "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }

        // Substrate Spec Detail Dialog (Fix #8 & #10)
        if (showSubstrateInfoDialog) {
            Dialog(onDismissRequest = { showSubstrateInfoDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.craft.substrateSpec,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            IconButton(onClick = { showSubstrateInfoDialog = false }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = strings.common.close, tint = Color(0xFF64748B))
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        listOf(
                            strings.substrate.material to draft.substrate.displayName,
                            strings.device.workArea to "${draft.substrate.widthMm.toInt()} × ${draft.substrate.heightMm.toInt()} mm",
                            strings.craft.layerMode to if (draft.substrate.shape == SubstrateShape.CIRCLE) "Circle" else "Rectangle",
                            strings.craft.maxHeight to "${draft.substrate.defaultReliefDepth} mm",
                            strings.craft.focus to "2.5 mm",
                            strings.craft.power to "UV LED 12W 100%"
                        ).forEach { (k, v) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(k, fontSize = 13.sp, color = Color(0xFF64748B))
                                Text(v, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showSubstrateInfoDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(strings.common.confirm, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4 Craft Cards with pixel-perfect vertical centering, text baseline consistency, and micro-bounce feedback.
 */
@Composable
private fun CraftCard(
    title: String,
    subtitle: String?,
    badgeText: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.025f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) BrandGreen else Color(0xFFE2E8F0),
        animationSpec = tween(durationMillis = 200)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BrandGreen else Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) BrandGreen else Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
            }
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BrandGreen else Color(0xFFD97706)
                    )
                }
            }
        }
    }
}

/**
 * Pixel-perfect Slider row with precise label, value indicator, and clean custom slider.
 */
@Composable
private fun AppSliderRow(
    label: String,
    hasHelpIcon: Boolean = false,
    onHelpClick: (() -> Unit)? = null,
    valueText: String,
    isPill: Boolean = true,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    val strings = LocalStrings.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                if (hasHelpIcon) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .tactilePress()
                            .clickable { onHelpClick?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = strings.craft.whiteChoke,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            if (isPill) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = valueText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            } else {
                Text(
                    text = valueText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (value > 0f) BrandGreen else Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Custom Pixel-Perfect Slider (Matches AnkerMake App Frame 0100)
        CustomSleekSlider(
            value = value,
            min = min,
            max = max,
            valueText = valueText,
            onValueChange = onValueChange
        )
    }
}

/**
 * 1:1 reproduction of AnkerMake sleek slider:
 * 4dp rounded track (active green, inactive light grey)
 * 16dp white circular thumb with 2.5dp green ring border
 * Track inset precisely to thumb center, with floating bubble indicator during drag.
 */
@Composable
private fun CustomSleekSlider(
    value: Float,
    min: Float,
    max: Float,
    valueText: String = "",
    onValueChange: (Float) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidth = maxWidth
        val fraction = ((value - min) / (max - min)).coerceIn(0f, 1f)
        val thumbDiameter = 16.dp
        val usableTrackWidth = (totalWidth - thumbDiameter).coerceAtLeast(0.dp)
        val thumbOffset = usableTrackWidth * fraction
        val activeTrackWidth = (thumbOffset + (thumbDiameter / 2)).coerceAtLeast(0.dp)

        val totalWidthPx = constraints.maxWidth.toFloat()
        val thumbDiameterPx = with(androidx.compose.ui.platform.LocalDensity.current) { thumbDiameter.toPx() }
        val usableTrackWidthPx = (totalWidthPx - thumbDiameterPx).coerceAtLeast(1f)

        fun updateFromOffset(x: Float) {
            val adjustedX = (x - thumbDiameterPx / 2f).coerceIn(0f, usableTrackWidthPx)
            val f = adjustedX / usableTrackWidthPx
            val newValue = min + f * (max - min)
            currentOnValueChange(newValue)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(min, max) {
                    detectTapGestures(
                        onPress = { offset ->
                            isDragging = true
                            updateFromOffset(offset.x)
                            tryAwaitRelease()
                            isDragging = false
                        }
                    )
                }
                .pointerInput(min, max) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            updateFromOffset(offset.x)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, _ ->
                            change.consume()
                            updateFromOffset(change.position.x)
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Inactive Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0))
            )

            // Active Track (stops cleanly at thumb center)
            Box(
                modifier = Modifier
                    .width(activeTrackWidth)
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandGreen)
            )

            // Floating value bubble during drag
            if (isDragging && valueText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (thumbOffset - 16.dp).coerceAtLeast(0.dp),
                            y = (-20).dp
                        )
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = valueText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(thumbDiameter)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.5.dp, BrandGreen, CircleShape)
            )
        }
    }
}

/**
 * 1:1 reproduction of AnkerMake AI Points Dialog (Frame 0082)
 */
@Composable
private fun AIPointsConsumptionDialog(
    availableCredits: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var dontRemindAgain by remember { mutableStateOf(false) }
    val hasEnoughCredits = availableCredits >= 5

    val strings = LocalStrings.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(310.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = strings.craft.aiPointsCost,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${strings.profile.cloudSync}: $availableCredits",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (hasEnoughCredits) Color(0xFF15803D) else Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { dontRemindAgain = !dontRemindAgain }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (dontRemindAgain) BrandGreen else Color.Transparent)
                            .border(1.5.dp, if (dontRemindAgain) BrandGreen else Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dontRemindAgain) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.craft.doNotRemind,
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E293B))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(strings.common.cancel, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = hasEnoughCredits,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlack,
                            disabledContainerColor = Color(0xFFE2E8F0)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                if (hasEnoughCredits) strings.common.confirm else strings.craft.insufficientPointsBtn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasEnoughCredits) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}
