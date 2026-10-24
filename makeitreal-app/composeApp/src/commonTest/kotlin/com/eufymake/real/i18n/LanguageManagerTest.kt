package com.eufymake.real.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LanguageManagerTest {

    @Test
    fun testDefaultLanguageIsChinese() {
        LanguageManager.setLanguage(AppLanguage.ZH)
        assertEquals(AppLanguage.ZH, LanguageManager.currentLanguage)
        assertEquals("创作", LanguageManager.strings.nav.create)
        assertEquals("工艺设置", LanguageManager.strings.craft.craftTitle)
        assertEquals("墨水模式", LanguageManager.strings.craft.inkMode)
        assertEquals("实验: 3D浮雕", LanguageManager.strings.canvas.draftFromCourse("3D浮雕"))
    }

    @Test
    fun testSwitchToEnglish() {
        LanguageManager.setLanguage(AppLanguage.EN)
        assertEquals(AppLanguage.EN, LanguageManager.currentLanguage)
        assertEquals("Create", LanguageManager.strings.nav.create)
        assertEquals("Craft Settings", LanguageManager.strings.craft.craftTitle)
        assertEquals("Ink Mode", LanguageManager.strings.craft.inkMode)
        assertEquals("Layer 2 of 5", LanguageManager.strings.craft.layerIndex(2, 5))
        assertEquals("Lab: 3D Relief", LanguageManager.strings.canvas.draftFromCourse("3D Relief"))
        assertEquals("Remix: Mug Design", LanguageManager.strings.canvas.draftFromProject("Mug Design"))
    }

    @Test
    fun testToggleLanguage() {
        LanguageManager.setLanguage(AppLanguage.ZH)
        LanguageManager.toggleLanguage()
        assertEquals(AppLanguage.EN, LanguageManager.currentLanguage)
        LanguageManager.toggleLanguage()
        assertEquals(AppLanguage.ZH, LanguageManager.currentLanguage)
    }

    @Test
    fun testInsufficientPointsParameterization() {
        LanguageManager.setLanguage(AppLanguage.ZH)
        val zhMsg = LanguageManager.strings.craft.insufficientPoints(5, 2)
        assertTrue(zhMsg.contains("5") && zhMsg.contains("2"))

        LanguageManager.setLanguage(AppLanguage.EN)
        val enMsg = LanguageManager.strings.craft.insufficientPoints(5, 2)
        assertTrue(enMsg.contains("5") && enMsg.contains("2"))
        assertFalse(enMsg.contains("点数") || enMsg.contains("算力"))
    }

    @Test
    fun testEnStringsContainsNoChineseOrFullwidthPunctuation() {
        // Covers CJK Unified Ideographs + CJK Symbols and Punctuation + Halfwidth and Fullwidth Forms
        val cjkAndFullwidthRegex = Regex("[\\u4e00-\\u9fa5\\u3000-\\u303F\\uFF00-\\uFFEF]")

        val allEnSamples = listOf(
            // Nav
            EnStrings.nav.create,
            EnStrings.nav.inspirations,
            EnStrings.nav.devices,
            EnStrings.nav.profile,

            // Common
            EnStrings.common.cancel,
            EnStrings.common.confirm,
            EnStrings.common.save,
            EnStrings.common.delete,
            EnStrings.common.export,
            EnStrings.common.minutesAgo(5),
            EnStrings.common.hoursAgo(2),

            // Craft
            EnStrings.craft.craftTitle,
            EnStrings.craft.craftMode,
            EnStrings.craft.inkMode,
            EnStrings.craft.craftFlatTitle,
            EnStrings.craft.craftFlatSub,
            EnStrings.craft.craftPatternTitle,
            EnStrings.craft.craftPatternSub,
            EnStrings.craft.craftReliefTitle,
            EnStrings.craft.craftReliefSub,
            EnStrings.craft.craft3DEmbossTitle,
            EnStrings.craft.craft3DEmbossSub,
            EnStrings.craft.startProcessing,
            EnStrings.craft.insufficientPoints(5, 1),
            EnStrings.craft.layerIndex(1, 3),

            // Canvas
            EnStrings.canvas.canvasStudio,
            EnStrings.canvas.elementsCatAll,
            EnStrings.canvas.elementsCatHalloween,
            EnStrings.canvas.elementsCatTrendy,
            EnStrings.canvas.elementsCatRelief,
            EnStrings.canvas.elementsCatShapes,
            EnStrings.canvas.searchElementsPlaceholder,
            EnStrings.canvas.tabCommunityAssets,
            EnStrings.canvas.tabLocalAlbum,
            EnStrings.canvas.importFromComputer,
            EnStrings.canvas.draftFromCourse("Test"),
            EnStrings.canvas.draftFromProject("Test"),
            EnStrings.canvas.stickerPumpkinCat,
            EnStrings.canvas.shapeCircularBase,

            // Device
            EnStrings.device.devicesTitle,
            EnStrings.device.connectedDevice,
            EnStrings.device.statusIdle,
            EnStrings.device.statusBusy,
            EnStrings.device.timeFormat(12, 34),

            // Profile
            EnStrings.profile.profileTitle,
            EnStrings.profile.language,
            EnStrings.profile.langEn,
            EnStrings.profile.langZh,

            // Community
            EnStrings.community.inspirationsTitle,
            EnStrings.community.tabFeatured,
            EnStrings.community.comments,

            // Substrate
            EnStrings.substrate.substrateTitle,
            EnStrings.substrate.matAcrylic,
            EnStrings.substrate.matWood,

            // Dialog
            EnStrings.dialog.notifications,
            EnStrings.dialog.academyTitle,
            EnStrings.dialog.maintenanceCenterTitle,
            EnStrings.dialog.wasteInkLevel(24),
            EnStrings.dialog.skillTokyoNeon
        )

        for (s in allEnSamples) {
            assertFalse(
                cjkAndFullwidthRegex.containsMatchIn(s),
                "Found CJK or fullwidth character in EnStrings: \"$s\""
            )
        }
    }
}
