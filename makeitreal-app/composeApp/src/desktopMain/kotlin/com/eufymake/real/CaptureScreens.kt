package com.eufymake.real

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eufymake.real.model.*
import com.eufymake.real.repository.*
import com.eufymake.real.ui.App
import com.eufymake.real.ui.components.*
import com.eufymake.real.ui.screens.*
import com.eufymake.real.ui.theme.MakeItRealTheme
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

fun saveScene(name: String, width: Int = 380, height: Int = 760, content: @Composable () -> Unit) {
    try {
        println("Rendering $name (${width}x${height})...")
        val scene = ImageComposeScene(width, height)
        scene.setContent {
            MakeItRealTheme {
                Box(modifier = Modifier.size(width.dp, height.dp).background(Color(0xFFF7F8FA))) {
                    content()
                }
            }
        }
        val image = scene.render()
        val data = image.encodeToData(EncodedImageFormat.PNG)
        if (data != null) {
            val customOut = System.getenv("PREVIEW_OUTPUT_DIR")?.let { File(it) }
            val defaultBrain = File("C:/Users/38788/.gemini/antigravity/brain/5ec1d1d5-6de8-4d10-8aae-da4c0d8653f0")
            val fallbackDir = File("build/reports/previews")
            val outDir = when {
                customOut != null && customOut.exists() -> customOut
                defaultBrain.exists() -> defaultBrain
                else -> fallbackDir.apply { mkdirs() }
            }
            val outFile = File(outDir, "$name.png")
            outFile.writeBytes(data.bytes)
            println("  [OK] Saved to: ${outFile.name} (${data.bytes.size} bytes)")
        } else {
            println("  [FAIL] Failed to encode $name")
        }
    } catch (e: Throwable) {
        println("  [ERROR] Error rendering $name: ${e.message}")
        e.printStackTrace()
    }
}

fun main() {
    println("==================================================")
    println(" Starting eufyMake Automated Visual Inspection ")
    println("==================================================")

    val commRepo = MockCommunityRepository()
    val draftRepo = MockDraftRepository()
    val deviceRepo = MockDeviceRepository()
    val pointsRepo = MockPointsRepository()

    val projects = commRepo.projects.value
    val announcements = commRepo.announcements.value
    val comments = commRepo.comments.value
    val creators = commRepo.creators.value
    val academy = commRepo.academyCourses.value
    val activities = commRepo.activities.value
    val notifs = commRepo.notifications.value
    val devInfo = deviceRepo.deviceInfo.value
    val points = pointsRepo.pointsState.value

    // Screen 1: App Main Shell (Create Tab)
    saveScene("preview_01_create_tab") {
        App()
    }

    // Screen 2: Inspirations Feed Screen
    saveScene("preview_02_inspirations_feed") {
        InspirationsScreen(
            announcements = announcements,
            projects = projects,
            commentsMap = comments,
            creatorsMap = creators,
            academyCourses = academy,
            activities = activities,
            notifications = notifs,
            onLikeProject = {},
            onToggleBookmark = {},
            onToggleFollow = {},
            onAddComment = { _, _ -> },
            onToggleLikeComment = { _, _ -> },
            onSubmitActivityEntry = { _, _, _ -> true },
            onMarkNotificationRead = {},
            onMarkAllNotificationsRead = {},
            onLoadCourseProject = {},
            onMakeSame = {}
        )
    }

    // Screen 3: Devices Console & Ink Telemetry
    saveScene("preview_03_devices_console") {
        DevicesScreen(
            deviceInfo = devInfo,
            onToggleChamberLight = {},
            onToggleAirFiltration = {},
            onCancelJob = {}
        )
    }

    // Screen 4: Creator Profile Modal
    val sampleCreator = creators.values.firstOrNull()
    if (sampleCreator != null) {
        saveScene("preview_04_creator_profile") {
            CreatorProfileDialog(
                creator = sampleCreator,
                creatorProjects = projects.filter { it.creatorId == sampleCreator.creatorId },
                onToggleFollow = {},
                onSelectProject = {},
                onDismiss = {}
            )
        }
    }

    // Screen 5: Comments Sheet
    val sampleComments = comments.values.firstOrNull() ?: emptyList()
    saveScene("preview_05_comments_sheet") {
        CommentsSheet(
            comments = sampleComments,
            onAddComment = {},
            onToggleLikeComment = {},
            onDismiss = {}
        )
    }

    // Screen 6: Academy Tutorials Drawer
    saveScene("preview_06_academy_drawer") {
        AcademyDrawer(
            courses = academy,
            onLoadCourseProject = {},
            onDismiss = {}
        )
    }

    // Screen 7: Contest & Campaign Modal
    val sampleActivity = activities.firstOrNull()
    if (sampleActivity != null) {
        saveScene("preview_07_contest_modal") {
            ContestDialog(
                activity = sampleActivity,
                onSubmitEntry = {},
                onDismiss = {}
            )
        }
    }

    // Screen 8: Maintenance Center Dialog
    saveScene("preview_08_maintenance_dialog") {
        MaintenanceDialog(
            onDismiss = {}
        )
    }

    // Screen 9: Canvas Studio Mode
    val starterDraft = draftRepo.createNewDraft(Substrate.DEFAULT).copy(
        layers = listOf(
            CanvasLayer(
                id = "layer_clock",
                name = "恐龙地质岩石浮雕时钟",
                type = LayerType.IMAGE,
                imageUrl = "dinosaur_clock_relief.webp",
                widthMm = 65f,
                heightMm = 65f,
                reliefDepthMm = 2.5f,
                fillColor = 0xFF2E7D32L
            )
        )
    )
    saveScene("preview_09_canvas_studio") {
        DesignCanvasScreen(
            draft = starterDraft,
            canUndo = true,
            canRedo = false,
            availableAICredits = 120,
            onUndo = {},
            onRedo = {},
            onBack = {},
            onAddLayer = {},
            onDragStart = {},
            onUpdateLayerTransient = {},
            onDragEnd = {},
            onUpdateLayer = {},
            onRemoveLayer = {},
            onSpendAICredits = { true },
            onNavigateToCraftMode = {}
        )
    }

    // Screen 10: 3D Craft Mode & White Underbase Choke
    saveScene("preview_10_craft_3d_mode") {
        CraftMode3DScreen(
            draft = starterDraft,
            availableAICredits = 120,
            onBack = {},
            onSpendCredits = { true },
            onReadyToPrint = { _, _, _ -> }
        )
    }

    // Screen 11: Me Tab & Draft Management
    saveScene("preview_11_profile_me_tab") {
        ProfileScreen(
            pointsState = points,
            drafts = draftRepo.drafts.value,
            onOpenPointsMall = {},
            onResumeDraft = {},
            onDeleteDraft = {},
            onRenameDraft = { _, _ -> },
            onDuplicateDraft = {}
        )
    }

    println("==================================================")
    println(" All visual inspections rendered successfully! ")
    println("==================================================")
}
