package com.eufymake.real.api

/**
 * eufyMake (MakeItReal) Backend REST & WebSocket Endpoints Definition.
 *
 * When transitioning from Mock to a real remote server, the Ktor HTTP/WS Client
 * connects to these predefined endpoints.
 */
object Endpoints {
    const val BASE_URL = "https://api.eufymake.com"
    const val WS_URL = "wss://api.eufymake.com/ws"

    // 1. Community & Feed
    const val COMMUNITY_PROJECTS = "/api/v1/community/projects"
    const val COMMUNITY_PROJECT_DETAIL = "/api/v1/community/projects/{projectId}"
    const val COMMUNITY_LIKE = "/api/v1/community/projects/{projectId}/like"
    const val COMMUNITY_FORK = "/api/v1/community/projects/{projectId}/fork"
    const val COMMUNITY_PUBLISH = "/api/v1/community/projects/publish"
    const val COMMUNITY_ANNOUNCEMENTS = "/api/v1/community/announcements"

    // 2. Drafts & Cloud Storage (.empf bundle sync)
    const val USER_DRAFTS = "/api/v1/user/drafts"
    const val USER_DRAFT_SYNC = "/api/v1/user/drafts/{draftId}"
    const val USER_DRAFT_DELETE = "/api/v1/user/drafts/{draftId}"

    // 3. AI 3D Relief Generative Engine (SSE Streaming)
    const val AI_RELIEF_STREAM = "/api/v1/ai/relief/stream"
    const val AI_RELIEF_ESTIMATE = "/api/v1/ai/relief/estimate"

    // 4. Hardware IoT & Telemetry
    const val DEVICE_LIST = "/api/v1/devices"
    const val DEVICE_STATUS = "/api/v1/devices/{deviceId}/status"
    const val DEVICE_CONTROL = "/api/v1/devices/{deviceId}/control" // chamber light, air filtration
    const val DEVICE_PRINT_JOB = "/api/v1/devices/{deviceId}/jobs"
    const val DEVICE_WS_TELEMETRY = "$WS_URL/devices/{deviceId}/telemetry" // 6 ink levels, head/bed temp, progress

    // 5. User Points & Rewards Mall
    const val USER_POINTS_SUMMARY = "/api/v1/user/points"
    const val USER_CHECK_IN = "/api/v1/user/checkin"
    const val MALL_ITEMS = "/api/v1/mall/items"
    const val MALL_REDEEM = "/api/v1/mall/redeem"
}
