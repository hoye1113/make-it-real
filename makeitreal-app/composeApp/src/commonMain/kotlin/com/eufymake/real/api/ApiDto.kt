package com.eufymake.real.api

import com.eufymake.real.model.*

/**
 * Standard API Envelope for backend JSON responses.
 */
data class ApiResponse<T>(
    val code: Int = 200,
    val message: String = "SUCCESS",
    val data: T? = null,
    val timestamp: Long = 0L
)

// Request & Response DTOs

data class PublishProjectRequest(
    val title: String,
    val description: String,
    val substrateId: String,
    val substrateName: String,
    val tags: List<String> = emptyList(),
    val layers: List<CanvasLayer> = emptyList()
)

data class AIReliefGenerateRequest(
    val prompt: String,
    val style: String = "Realistic Relief",
    val substrateId: String,
    val maxDepthMm: Float = 2.0f
)

data class AIProgressStreamDto(
    val percentage: Int,
    val stepDescription: String,
    val previewHeightmapUrl: String? = null
)

data class DeviceControlRequest(
    val deviceId: String,
    val chamberLightOn: Boolean? = null,
    val airFiltrationActive: Boolean? = null
)

data class StartPrintJobRequest(
    val deviceId: String,
    val draftId: String,
    val printMode: PrintMode,
    val reliefDepthMm: Float,
    val glossMode: GlossMode
)

data class RedeemMallItemRequest(
    val itemId: String,
    val shippingAddressId: String? = null
)

data class CheckInResponse(
    val totalPoints: Int,
    val consecutiveDays: Int,
    val pointsAwarded: Int = 15
)

// 1. 评论模型
data class CommentDto(
    val id: String,
    val projectId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val content: String,
    val createdAt: Long = 0L,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class AddCommentRequest(val content: String)

// 2. 创作者名片模型
data class CreatorProfileDto(
    val creatorId: String,
    val name: String,
    val handle: String,
    val bio: String,
    val avatarUrl: String = "",
    val bannerUrl: String = "",
    val isVerified: Boolean = false,
    val verifiedTitle: String = "官方认证创作者",
    val followingCount: Int = 0,
    val followersCount: Int = 0,
    val totalLikesCount: Int = 0,
    val isFollowing: Boolean = false
)

data class ToggleFollowResponse(
    val creatorId: String,
    val isFollowing: Boolean,
    val newFollowerCount: Int
)

// 3. 工艺学院教程模型
data class AcademyCourseDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val difficulty: String = "入门",
    val durationMinutes: Int = 5,
    val coverImageName: String = "",
    val tags: List<String> = emptyList(),
    val sampleDraftSubstrateId: String = "sheet",
    val keyParametersHint: String = "白墨内缩: 0.2mm, 浮雕高度: 2.2mm"
)

// 4. 赛事活动模型
data class ActivityDto(
    val id: String,
    val title: String,
    val description: String,
    val bannerImageName: String,
    val prizePoolDesc: String,
    val deadlineTimestamp: Long,
    val submissionCount: Int,
    val tags: List<String> = emptyList()
)

data class SubmitActivityEntryRequest(
    val activityId: String,
    val projectId: String,
    val craftNotes: String
)

// 5. 贴纸分类与资产模型
data class StickerPackDto(
    val packId: String,
    val title: String,
    val iconName: String,
    val stickersCount: Int
)

data class StickerItemDto(
    val id: String,
    val name: String,
    val packId: String,
    val assetUrl: String,
    val defaultReliefDepth: Float = 1.8f
)

// 6. 硬件设备耗材与维护模型
data class InkLevelsDto(
    val cyanPercent: Int = 92,
    val magentaPercent: Int = 85,
    val yellowPercent: Int = 78,
    val blackPercent: Int = 88,
    val whitePercent: Int = 64,       // 白墨关键耗材
    val varnishPercent: Int = 70,     // 光油耗材
    val wasteTankPercent: Int = 18    // 废墨箱容量
)

data class MaintenanceRequest(
    val actionType: String // "NOZZLE_CLEAN", "INK_PURGE", "TEST_PRINT"
)

// 7. 消息通知模型
data class NotificationDto(
    val id: String,
    val type: String, // "LIKE", "COMMENT", "SYSTEM", "CONTEST"
    val title: String,
    val content: String,
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val jumpRoute: String = ""
)
