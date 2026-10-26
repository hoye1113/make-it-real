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
import com.eufymake.real.api.AcademyCourseDto
import com.eufymake.real.i18n.LanguageManager
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.*
import com.eufymake.real.repository.*
import com.eufymake.real.ui.components.*
import com.eufymake.real.ui.screens.*
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.theme.MakeItRealTheme
import com.eufymake.real.ui.theme.StudioDarkWorkspace

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
    CompositionLocalProvider(LocalStrings provides LanguageManager.strings) {
        MakeItRealTheme {
            val strings = LocalStrings.current
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
        val comments by communityRepo.comments.collectAsState()
        val creators by communityRepo.creators.collectAsState()
        val academyCourses by communityRepo.academyCourses.collectAsState()
        val activities by communityRepo.activities.collectAsState()
        val notifications by communityRepo.notifications.collectAsState()

        val deviceInfo by deviceRepo.deviceInfo.collectAsState()
        val pointsState by pointsRepo.pointsState.collectAsState()

        var currentTab by remember { mutableStateOf(AppTab.CREATE) }
        var studioScreen by remember { mutableStateOf(StudioScreen.CANVAS) }
        var showPointsMall by remember { mutableStateOf(false) }
        var showProfileAcademyDrawer by remember { mutableStateOf(false) }
        var inspirationsInitialSection by remember { mutableStateOf<String?>(null) }

        val activeDraft = currentDraft
        val isStudioDark = activeDraft != null && studioScreen == StudioScreen.CRAFT_3D

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isStudioDark) StudioDarkWorkspace else Color(0xFFF7F8FA))
                .statusBarsPadding()
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                                    deviceRepo.startPrintJob(activeDraft, mode, depth, gloss)
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
                                val navItemColors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandGreen,
                                    selectedTextColor = BrandGreen,
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8),
                                    indicatorColor = Color.Transparent
                                )

                                // Tab 1: 创作
                                NavigationBarItem(
                                    selected = currentTab == AppTab.CREATE,
                                    onClick = { currentTab = AppTab.CREATE },
                                    colors = navItemColors,
                                    icon = {
                                        Icon(
                                            Icons.Default.AddBox,
                                            contentDescription = strings.nav.create,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            strings.nav.create,
                                            fontSize = 11.sp,
                                            fontWeight = if (currentTab == AppTab.CREATE) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )

                                // Tab 2: 设备
                                NavigationBarItem(
                                    selected = currentTab == AppTab.DEVICES,
                                    onClick = { currentTab = AppTab.DEVICES },
                                    colors = navItemColors,
                                    icon = {
                                        Icon(
                                            Icons.Default.Print,
                                            contentDescription = strings.nav.devices,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            strings.nav.devices,
                                            fontSize = 11.sp,
                                            fontWeight = if (currentTab == AppTab.DEVICES) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )

                                // Tab 3: 灵感
                                NavigationBarItem(
                                    selected = currentTab == AppTab.INSPIRATIONS,
                                    onClick = { currentTab = AppTab.INSPIRATIONS },
                                    colors = navItemColors,
                                    icon = {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = strings.nav.inspirations,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            strings.nav.inspirations,
                                            fontSize = 11.sp,
                                            fontWeight = if (currentTab == AppTab.INSPIRATIONS) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )

                                // Tab 4: 我的
                                NavigationBarItem(
                                    selected = currentTab == AppTab.ME,
                                    onClick = { currentTab = AppTab.ME },
                                    colors = navItemColors,
                                    icon = {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = strings.nav.profile,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            strings.nav.profile,
                                            fontSize = 11.sp,
                                            fontWeight = if (currentTab == AppTab.ME) FontWeight.Bold else FontWeight.Normal
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
                                            createStarterDraft(
                                                draftRepo = draftRepo,
                                                substrate = substrate,
                                                title = "${substrate.displayName} - Studio",
                                                imageUrl = "work_1.webp",
                                                widthMm = (substrate.widthMm * 0.7f).coerceAtMost(80f),
                                                heightMm = (substrate.heightMm * 0.7f).coerceAtMost(80f),
                                                reliefDepthMm = substrate.defaultReliefDepth
                                            )
                                        },
                                        onOpenDrafts = { currentTab = AppTab.ME },
                                        onSelectInspirationItem = { title, imageName, substrateId ->
                                            studioScreen = StudioScreen.CANVAS
                                            val sub = Substrate.ALL.firstOrNull { it.id == substrateId } ?: Substrate.DEFAULT
                                            createStarterDraft(
                                                draftRepo = draftRepo,
                                                substrate = sub,
                                                title = title,
                                                imageUrl = imageName,
                                                widthMm = 75f,
                                                heightMm = 75f,
                                                fillColor = 0xFF388E3CL
                                            )
                                        },
                                        deviceName = deviceInfo.modelName,
                                        isDeviceConnected = deviceInfo.isOnline,
                                        onOpenDevices = { currentTab = AppTab.DEVICES }
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
                                        commentsMap = comments,
                                        creatorsMap = creators,
                                        academyCourses = academyCourses,
                                        activities = activities,
                                        notifications = notifications,
                                        onLikeProject = { projectId ->
                                            communityRepo.toggleLike(projectId)
                                        },
                                        onToggleBookmark = { projectId ->
                                            communityRepo.toggleBookmark(projectId)
                                        },
                                        onToggleFollow = { creatorId ->
                                            communityRepo.toggleFollowCreator(creatorId)
                                        },
                                        onAddComment = { projectId, content ->
                                            communityRepo.addComment(projectId, content)
                                        },
                                        onToggleLikeComment = { projectId, commentId ->
                                            communityRepo.toggleLikeComment(projectId, commentId)
                                        },
                                        onSubmitActivityEntry = { activityId, projectId, notes ->
                                            communityRepo.submitActivityEntry(activityId, projectId, notes)
                                        },
                                        onMarkNotificationRead = { notifId ->
                                            communityRepo.markNotificationRead(notifId)
                                        },
                                        onMarkAllNotificationsRead = {
                                            communityRepo.markAllNotificationsRead()
                                        },
                                        onLoadCourseProject = { course ->
                                            val sub = Substrate.ALL.firstOrNull { it.id == course.sampleDraftSubstrateId } ?: Substrate.DEFAULT
                                            studioScreen = StudioScreen.CANVAS
                                            createStarterDraft(
                                                draftRepo = draftRepo,
                                                substrate = sub,
                                                title = strings.canvas.draftFromCourse(course.title),
                                                imageUrl = course.coverImageName
                                            )
                                        },
                                        onMakeSame = { project ->
                                            val substrate = Substrate.ALL.firstOrNull { it.id == project.substrateId } ?: Substrate.DEFAULT
                                            studioScreen = StudioScreen.CANVAS
                                            createStarterDraft(
                                                draftRepo = draftRepo,
                                                substrate = substrate,
                                                title = strings.canvas.draftFromProject(project.title),
                                                imageUrl = project.imageName,
                                                widthMm = 75f,
                                                heightMm = 75f
                                            )
                                            communityRepo.forkProject(project.id)
                                        },
                                        initialSection = inspirationsInitialSection,
                                        onSectionHandled = { inspirationsInitialSection = null }
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
                                        },
                                        onRenameDraft = { draftId, newTitle ->
                                            draftRepo.renameDraft(draftId, newTitle)
                                        },
                                        onDuplicateDraft = { draftId ->
                                            draftRepo.duplicateDraft(draftId)
                                        },
                                        onOpenAcademy = { showProfileAcademyDrawer = true },
                                        onOpenActivities = {
                                            inspirationsInitialSection = "ACTIVITIES"
                                            currentTab = AppTab.INSPIRATIONS
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
                        onReset = { pointsRepo.resetPoints() },
                        onDismiss = { showPointsMall = false }
                    )
                }

                // Academy Drawer from Profile
                if (showProfileAcademyDrawer) {
                    AcademyDrawer(
                        courses = academyCourses,
                        onLoadCourseProject = { course ->
                            showProfileAcademyDrawer = false
                            val sub = Substrate.ALL.firstOrNull { it.id == course.sampleDraftSubstrateId } ?: Substrate.DEFAULT
                            studioScreen = StudioScreen.CANVAS
                            createStarterDraft(
                                draftRepo = draftRepo,
                                substrate = sub,
                                title = strings.canvas.draftFromCourse(course.title),
                                imageUrl = course.coverImageName
                            )
                        },
                        onDismiss = { showProfileAcademyDrawer = false }
                    )
                }
            }
        }
    }
}
}

private fun createStarterDraft(
    draftRepo: MockDraftRepository,
    substrate: Substrate,
    title: String,
    imageUrl: String?,
    widthMm: Float = 70f,
    heightMm: Float = 70f,
    reliefDepthMm: Float = 2.4f,
    fillColor: Long = 0xFF2E7D32L
) {
    draftRepo.createNewDraft(substrate)
    draftRepo.addLayer(
        CanvasLayer(
            id = "layer_${System.currentTimeMillis()}_${(1000..9999).random()}",
            name = title,
            type = LayerType.IMAGE,
            imageUrl = imageUrl,
            widthMm = widthMm,
            heightMm = heightMm,
            reliefDepthMm = reliefDepthMm,
            fillColor = fillColor
        )
    )
}
