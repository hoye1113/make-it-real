package com.eufymake.real.model

import kotlinx.serialization.Serializable

@Serializable
data class RewardTask(
    val id: String,
    val title: String,
    val pointsReward: Int,
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val icon: String = "stars"
)

@Serializable
data class MallItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val pointsCost: Int,
    val originalPriceUsd: Double,
    val stockCount: Int,
    val badge: String = "POPULAR"
)

@Serializable
data class PointsState(
    val totalPoints: Int = 1450,
    val aiCredits: Int = 36,
    val consecutiveDays: Int = 5,
    val isCheckedInToday: Boolean = false,
    val tasks: List<RewardTask> = listOf(
        RewardTask("check_in", "Daily Check-in & Sign-in", 10, false, 0, 1, "calendar_today"),
        RewardTask("ai_gen", "Generate AI 3D Relief Artwork", 25, false, 0, 1, "auto_awesome"),
        RewardTask("new_draft", "Create & Save a Custom Substrate", 15, false, 0, 1, "edit_square"),
        RewardTask("publish", "Publish 1 Work to Community Feed", 50, false, 0, 1, "share"),
        RewardTask("get_likes", "Receive 5 likes from makers", 20, false, 2, 5, "favorite")
    ),
    val mallItems: List<MallItem> = listOf(
        MallItem("uv_white_coupon", "$10 Off White UV Ink Cartridge", "Compatible with eufyMake E1 series", 400, 10.0, 48),
        MallItem("gloss_varnish_pack", "UV Gloss Varnish Bottle (250ml)", "Mirror-like clear gloss finish", 750, 18.0, 19),
        MallItem("ai_credit_bundle", "50 Extra AI Relief Heightmap Credits", "Fast generation priority queue", 300, 8.0, 999),
        MallItem("acrylic_pack_10", "A5 Frosted Acrylic Sheets (10-pack)", "Optimal adhesion for UV relief", 1100, 24.0, 12)
    )
)
