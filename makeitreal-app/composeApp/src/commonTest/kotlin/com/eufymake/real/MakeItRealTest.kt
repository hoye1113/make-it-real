package com.eufymake.real

import com.eufymake.real.model.*
import com.eufymake.real.repository.*
import kotlin.test.*

class MakeItRealTest {

    @Test
    fun testSubstratesInitialization() {
        assertEquals(10, Substrate.ALL.size, "Should have 10 official substrates")
        val defaultSub = Substrate.DEFAULT
        assertEquals("sheet", defaultSub.id)
        assertEquals("薄片", defaultSub.nameZh)
        assertTrue(defaultSub.aspectRatio > 0f)

        val cup = Substrate.ALL.first { it.id == "cup" }
        assertEquals("杯子", cup.nameZh)
        assertTrue(cup.supportRotary, "Cup must support 360 rotary printing")
        assertEquals(SubstrateShape.CYLINDER, cup.shape)

        val tech = Substrate.ALL.first { it.id == "tech" }
        assertEquals("科技配件", tech.nameZh)
        assertEquals(SubstrateShape.PHONE_CASE, tech.shape)
        assertTrue(tech.presets.contains("iPhone 16"))
    }

    @Test
    fun testDraftRepositoryAndUndoRedo() {
        val repo = MockDraftRepository()
        val substrate = Substrate.ALL.first { it.id == "coaster" }
        val draft = repo.createNewDraft(substrate)

        assertEquals("Coaster Project", draft.title)
        assertEquals(0, draft.layers.size)
        assertFalse(repo.canUndo.value)
        assertFalse(repo.canRedo.value)

        // Add layer 1
        val layer1 = CanvasLayer(
            id = "layer_1",
            name = "Test Circle",
            type = LayerType.SHAPE,
            shapeType = "CIRCLE"
        )
        repo.addLayer(layer1)
        assertEquals(1, repo.currentDraft.value?.layers?.size)
        assertTrue(repo.canUndo.value)

        // Add layer 2
        val layer2 = CanvasLayer(
            id = "layer_2",
            name = "Test Text",
            type = LayerType.TEXT,
            textContent = "HELLO"
        )
        repo.addLayer(layer2)
        assertEquals(2, repo.currentDraft.value?.layers?.size)

        // Undo once
        repo.undo()
        assertEquals(1, repo.currentDraft.value?.layers?.size)
        assertTrue(repo.canRedo.value)

        // Redo
        repo.redo()
        assertEquals(2, repo.currentDraft.value?.layers?.size)

        // Remove layer
        repo.removeLayer("layer_1")
        assertEquals(1, repo.currentDraft.value?.layers?.size)
        assertEquals("layer_2", repo.currentDraft.value?.layers?.first()?.id)

        // Test transient drag gesture undo coalescing
        val initialX = repo.currentDraft.value!!.layers.first().xOffsetMm
        repo.recordHistorySnapshot()
        for (step in 1..10) {
            val currentL = repo.currentDraft.value!!.layers.first()
            repo.updateLayerTransient(currentL.copy(xOffsetMm = currentL.xOffsetMm + 5f))
        }
        repo.commitTransientUpdate()
        assertEquals(initialX + 50f, repo.currentDraft.value!!.layers.first().xOffsetMm)

        // Single undo should revert the entire drag gesture
        repo.undo()
        assertEquals(initialX, repo.currentDraft.value!!.layers.first().xOffsetMm)
    }

    @Test
    fun testCommunityRepository() {
        val repo = MockCommunityRepository()
        val initialProjects = repo.projects.value
        assertTrue(initialProjects.size >= 8, "Should have seeded community projects")

        val target = initialProjects.first()
        val initialLikes = target.likesCount
        val initialLiked = target.isLiked

        // Toggle like
        repo.toggleLike(target.id)
        val afterLike = repo.projects.value.first { it.id == target.id }
        assertEquals(!initialLiked, afterLike.isLiked)
        val expectedLikes = if (!initialLiked) initialLikes + 1 else initialLikes - 1
        assertEquals(expectedLikes, afterLike.likesCount)

        // Fork project
        val initialForks = target.forkCount
        repo.forkProject(target.id)
        val afterFork = repo.projects.value.first { it.id == target.id }
        assertEquals(initialForks + 1, afterFork.forkCount)

        // Publish new project
        val published = repo.publishProject("My New Craft", "Sample description", "canvas", "Art Canvas")
        assertEquals("My New Craft", published.title)
        assertEquals("You (Maker)", published.authorName)
        assertEquals(published.id, repo.projects.value.first().id)
    }

    @Test
    fun testPointsAndRewardsRepository() {
        val repo = MockPointsRepository()
        val initialPoints = repo.pointsState.value.totalPoints
        val initialCredits = repo.pointsState.value.aiCredits

        // Check in
        val checkInSuccess = repo.checkIn()
        assertTrue(checkInSuccess)
        assertEquals(initialPoints + 15, repo.pointsState.value.totalPoints)
        assertTrue(repo.pointsState.value.isCheckedInToday)

        // Second check-in today should fail
        assertFalse(repo.checkIn())

        // Spend AI credits
        val spendSuccess = repo.spendAICredits(2)
        assertTrue(spendSuccess)
        assertEquals(initialCredits - 2, repo.pointsState.value.aiCredits)

        // Redeem mall item
        val item = repo.pointsState.value.mallItems.first { it.id == "ai_credit_bundle" }
        val pointsBeforeRedeem = repo.pointsState.value.totalPoints
        val creditsBeforeRedeem = repo.pointsState.value.aiCredits
        val redeemSuccess = repo.redeemMallItem(item.id)
        assertTrue(redeemSuccess)
        assertEquals(pointsBeforeRedeem - item.pointsCost, repo.pointsState.value.totalPoints)
        assertEquals(creditsBeforeRedeem + 50, repo.pointsState.value.aiCredits)

        // Reset points test
        repo.resetPoints()
        assertEquals(initialPoints, repo.pointsState.value.totalPoints)
        assertEquals(initialCredits, repo.pointsState.value.aiCredits)
        assertFalse(repo.pointsState.value.isCheckedInToday)
    }

    @Test
    fun testMaterialTypeAndAlignmentModes() {
        assertEquals(MaterialType.ACRYLIC, MaterialType.fromString("亚克力"))
        assertEquals(MaterialType.LEATHER, MaterialType.fromString("皮革"))
        assertEquals(MaterialType.WOOD, MaterialType.fromString("木材"))
        assertEquals(MaterialType.METAL, MaterialType.fromString("金属"))
        assertEquals(MaterialType.PVC, MaterialType.fromString("PVC"))
        assertEquals(MaterialType.CERAMIC, MaterialType.fromString("陶瓷"))
        assertEquals(MaterialType.GLASS, MaterialType.fromString("玻璃"))

        assertEquals(4, AlignmentMode.entries.size)
        assertTrue(AlignmentMode.entries.contains(AlignmentMode.FLATBED_CAMERA))
        assertTrue(AlignmentMode.entries.contains(AlignmentMode.FLATBED_MINI))
        assertTrue(AlignmentMode.entries.contains(AlignmentMode.ROTARY_JIG))
        assertTrue(AlignmentMode.entries.contains(AlignmentMode.ROLL_UV_DTF))
    }

    @Test
    fun testDeviceRepository() {
        val repo = MockDeviceRepository()
        assertEquals(DeviceStatus.IDLE, repo.deviceInfo.value.status)

        // Toggle chamber light
        val lightBefore = repo.deviceInfo.value.isChamberLightOn
        repo.toggleChamberLight()
        assertEquals(!lightBefore, repo.deviceInfo.value.isChamberLightOn)

        // Toggle air filtration
        val filterBefore = repo.deviceInfo.value.isAirFiltrationActive
        repo.toggleAirFiltration()
        assertEquals(!filterBefore, repo.deviceInfo.value.isAirFiltrationActive)

        // Ink channels verify
        val inks = repo.deviceInfo.value.inkLevels.asList
        assertEquals(6, inks.size, "Must have 6 UV channels (C, M, Y, K, W, G)")
        assertEquals(listOf("C", "M", "Y", "K", "W", "G"), inks.map { it.code })
    }
}
