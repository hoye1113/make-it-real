package com.eufymake.real.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val forkCount: Int = 0,
    val viewCount: Int = 0,
    val substrateId: String = "canvas",
    val substrateName: String = "Art Canvas",
    val layersCount: Int = 2,
    val coverGradientStart: Long = 0xFF4A90E2,
    val coverGradientEnd: Long = 0xFF50E3C2,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 1729500000000L,
    val isStaffPick: Boolean = false,
    val isContestEntry: Boolean = false,
    val category: String = "Creative" // "Creative", "Tech", "Art", "Home", "Holiday"
)

@Serializable
data class Draft(
    val id: String,
    val title: String,
    val substrate: Substrate,
    val layers: List<CanvasLayer> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDirty: Boolean = false
)

@Serializable
data class Announcement(
    val id: String,
    val title: String,
    val subtitle: String,
    val bannerUrl: String = "",
    val tag: String = "HOT EVENT",
    val linkUrl: String = "",
    val gradientStart: Long = 0xFFFF5E36,
    val gradientEnd: Long = 0xFFFFAE00
)
