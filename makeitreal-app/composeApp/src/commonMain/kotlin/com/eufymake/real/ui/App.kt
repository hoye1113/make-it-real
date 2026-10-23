package com.eufymake.real.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.*
import com.eufymake.real.repository.*
import com.eufymake.real.ui.components.PointsMallDialog
import com.eufymake.real.ui.screens.*
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.theme.BrandPrimary
import com.eufymake.real.ui.theme.MakeItRealTheme

enum class AppTab {
    CREATE,
    DEVICES,
    INSPIRATIONS,
    ME
}

enum class StudioScreen {
    CANVAS,
    CRAFT_3D
}

@Composable
fun App() {
    MakeItRealTheme {
        // Singletons / Repositories
        val draftRepo = remember { MockDraftRepository() }
        val communityRepo = remember { MockCommunityRepository() }
        val deviceRepo = remember { MockDeviceRepository() }
        val pointsRepo = remember { MockPointsRepository() }

        // Reactive states
        val currentDraft by draftRepo.currentDraft.collectAsState()
        val drafts by draftRepo.drafts.collectAsState()
        val canUndo by draftRepo.canUndo.collectAsState()
        val canRedo by draftRepo.canRedo.collectAsState()

        val projects by communityRepo.projects.collectAsState()
        val announcements by communityRepo.announcements.collectAsState()

        val deviceInfo by deviceRepo.deviceInfo.collectAsState()
        val pointsState by pointsRepo.pointsState.collectAsState()

        var currentTab by remember { mutableStateOf(AppTab.CREATE) }
        var studioScreen by remember { mutableStateOf(StudioScreen.CANVAS) }
        var showPointsMall by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
            val activeDraft = currentDraft
            if (activeDraft != null) {
                // In Studio Mode
                when (studioScreen) {
                    StudioScreen.CANVAS -> {
                        DesignCanvasScreen(
                            draft = activeDraft,
                            canUndo = canUndo,
                            canRedo = canRedo,
                            availableAICredits = pointsState.aiCredits,
                            onUndo = { draftRepo.undo() },
                            onRedo = { draftRepo.redo() },
                            onBack = {
                                studioScreen = StudioScreen.CANVAS
                                draftRepo.setCurrentDraft(null)
                            },
                            onAddLayer = { layer -> draftRepo.addLayer(layer) },
                            onDragStart = { draftRepo.recordHistorySnapshot() },
                            onUpdateLayerTransient = { layer -> draftRepo.updateLayerTransient(layer) },
                            onDragEnd = { draftRepo.commitTransientUpdate() },
                            onUpdateLayer = { layer -> draftRepo.updateLayer(layer) },
                            onRemoveLayer = { layerId -> draftRepo.removeLayer(layerId) },
                            onSpendAICredits = { amount -> pointsRepo.spendAICredits(amount) },
                            onNavigateToCraftMode = { studioScreen = StudioScreen.CRAFT_3D }
                        )
                    }
                    StudioScreen.CRAFT_3D -> {
                        CraftMode3DScreen(
                            draft = activeDraft,
                            availableAICredits = pointsState.aiCredits,
                            onBack = { studioScreen = StudioScreen.CANVAS },
                            onSpendCredits = { amount -> pointsRepo.spendAICredits(amount) },
                            onReadyToPrint = { mode, depth, gloss ->
                                deviceRepo.startPrintJob(activeDraft)
                                studioScreen = StudioScreen.CANVAS
                                draftRepo.setCurrentDraft(null)
                                currentTab = AppTab.DEVICES
                            }
                        )
                    }
                }
            } else {
                // Main Shell with 4 Bottom Tabs (Frame 0001 & 0010)
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 4.dp,
                            modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        ) {
                            // Tab 1: 创作
                            NavigationBarItem(
                                selected = currentTab == AppTab.CREATE,
                                onClick = { currentTab = AppTab.CREATE },
                                icon = {
                                    Icon(
                                        Icons.Default.AddBox,
                                        contentDescription = "创作",
                                        tint = if (currentTab == AppTab.CREATE) BrandGreen else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        "创作",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentTab == AppTab.CREATE) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentTab == AppTab.CREATE) BrandGreen else Color(0xFF94A3B8)
                                    )
                                }
                            )

                            // Tab 2: 设备
                            NavigationBarItem(
                                selected = currentTab == AppTab.DEVICES,
                                onClick = { currentTab = AppTab.DEVICES },
                                icon = {
                                    Icon(
                                        Icons.Default.Print,
                                        contentDescription = "设备",
                                        tint = if (currentTab == AppTab.DEVICES) BrandGreen else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        "设备",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentTab == AppTab.DEVICES) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentTab == AppTab.DEVICES) BrandGreen else Color(0xFF94A3B8)
                                    )
                                }
                            )

                            // Tab 3: 灵感
                            NavigationBarItem(
                                selected = currentTab == AppTab.INSPIRATIONS,
                                onClick = { currentTab = AppTab.INSPIRATIONS },
                                icon = {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = "灵感",
                                        tint = if (currentTab == AppTab.INSPIRATIONS) BrandGreen else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        "灵感",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentTab == AppTab.INSPIRATIONS) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentTab == AppTab.INSPIRATIONS) BrandGreen else Color(0xFF94A3B8)
                                    )
                                }
                            )

                            // Tab 4: 我的
                            NavigationBarItem(
                                selected = currentTab == AppTab.ME,
                                onClick = { currentTab = AppTab.ME },
                                icon = {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "我的",
                                        tint = if (currentTab == AppTab.ME) BrandGreen else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        "我的",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentTab == AppTab.ME) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentTab == AppTab.ME) BrandGreen else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (currentTab) {
                            AppTab.CREATE -> {
                                CreateScreen(
                                    points = pointsState.totalPoints,
                                    onOpenPointsMall = { showPointsMall = true },
                                    onSelectSubstrate = { substrate ->
                                        studioScreen = StudioScreen.CANVAS
                                        val newDraft = draftRepo.createNewDraft(substrate)
                                        // Add a starter layer matching AnkerMake demo
                                        draftRepo.addLayer(
                                            CanvasLayer(
                                                id = "layer_base_${System.currentTimeMillis()}",
                                                name = "${substrate.nameZh}设计主体",
                                                type = LayerType.IMAGE,
                                                widthMm = (substrate.widthMm * 0.7f).coerceAtMost(80f),
                                                heightMm = (substrate.heightMm * 0.7f).coerceAtMost(80f),
                                                reliefDepthMm = substrate.defaultReliefDepth,
                                                fillColor = 0xFF2E7D32L
                                            )
                                        )
                                    },
                                    onOpenDrafts = { currentTab = AppTab.ME },
                                    onSelectInspirationItem = { title ->
                                        studioScreen = StudioScreen.CANVAS
                                        val sub = Substrate.DEFAULT
                                        draftRepo.createNewDraft(sub)
                                        draftRepo.addLayer(
                                            CanvasLayer(
                                                id = "layer_insp_${System.currentTimeMillis()}",
                                                name = title,
                                                type = LayerType.IMAGE,
                                                widthMm = 75f,
                                                heightMm = 75f,
                                                reliefDepthMm = 2.0f,
                                                fillColor = 0xFF388E3CL
                                            )
                                        )
                                    }
                                )
                            }
                            AppTab.DEVICES -> {
                                DevicesScreen(
                                    deviceInfo = deviceInfo,
                                    onToggleChamberLight = { deviceRepo.toggleChamberLight() },
                                    onToggleAirFiltration = { deviceRepo.toggleAirFiltration() },
                                    onCancelJob = { deviceRepo.cancelPrintJob() }
                                )
                            }
                            AppTab.INSPIRATIONS -> {
                                InspirationsScreen(
                                    announcements = announcements,
                                    projects = projects,
                                    onLikeProject = { projectId ->
                                        communityRepo.toggleLike(projectId)
                                    },
                                    onMakeSame = { project ->
                                        val substrate = Substrate.ALL.firstOrNull { it.id == project.substrateId } ?: Substrate.DEFAULT
                                        studioScreen = StudioScreen.CANVAS
                                        val newDraft = draftRepo.createNewDraft(substrate)
                                        draftRepo.addLayer(
                                            CanvasLayer(
                                                id = "layer_fork_${System.currentTimeMillis()}",
                                                name = "同款: ${project.title}",
                                                type = LayerType.IMAGE,
                                                widthMm = 75f,
                                                heightMm = 75f,
                                                reliefDepthMm = 2.0f,
                                                fillColor = 0xFF2E7D32L
                                            )
                                        )
                                        communityRepo.forkProject(project.id)
                                    }
                                )
                            }
                            AppTab.ME -> {
                                ProfileScreen(
                                    pointsState = pointsState,
                                    drafts = drafts,
                                    onOpenPointsMall = { showPointsMall = true },
                                    onResumeDraft = { draft ->
                                        studioScreen = StudioScreen.CANVAS
                                        draftRepo.setCurrentDraft(draft)
                                    },
                                    onDeleteDraft = { draftId ->
                                        draftRepo.deleteDraft(draftId)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Points & AI Mall Dialog
            if (showPointsMall) {
                PointsMallDialog(
                    pointsState = pointsState,
                    onCheckIn = { pointsRepo.checkIn() },
                    onRedeem = { itemId -> pointsRepo.redeemMallItem(itemId) },
                    onDismiss = { showPointsMall = false }
                )
            }
        }
    }
}
