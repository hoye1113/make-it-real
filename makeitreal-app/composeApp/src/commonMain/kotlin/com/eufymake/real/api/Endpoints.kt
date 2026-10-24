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
    const val COMMUNITY_FEED = "/api/v1/community/feed"
    const val COMMUNITY_PROJECTS = "/api/v1/community/projects"
    const val COMMUNITY_PROJECT_DETAIL = "/api/v1/community/projects/{projectId}"
    const val COMMUNITY_LIKE = "/api/v1/community/projects/{projectId}/like"
    const val COMMUNITY_BOOKMARK = "/api/v1/community/projects/{projectId}/bookmark"
    const val COMMUNITY_COMMENTS = "/api/v1/community/projects/{projectId}/comments"
    const val COMMUNITY_REMIX = "/api/v1/community/projects/{projectId}/remix"
    const val COMMUNITY_TAGS = "/api/v1/community/tags"
    const val COMMUNITY_FORK = "/api/v1/community/projects/{projectId}/fork"
    const val COMMUNITY_PUBLISH = "/api/v1/community/projects/publish"
    const val COMMUNITY_ANNOUNCEMENTS = "/api/v1/community/announcements"

    // 2. Creators & Social Graph
    const val CREATOR_PROFILE = "/api/v1/creators/{creatorId}"
    const val CREATOR_FOLLOW = "/api/v1/creators/{creatorId}/follow"
    const val CREATOR_PROJECTS = "/api/v1/creators/{creatorId}/projects"

    // 3. Drafts & Cloud Storage (.empf bundle sync)
    const val USER_DRAFTS = "/api/v1/user/drafts"
    const val USER_DRAFT_SYNC = "/api/v1/user/drafts/{draftId}"
    const val USER_DRAFT_DELETE = "/api/v1/user/drafts/{draftId}"
    const val USER_DRAFT_EXPORT_EMPF = "/api/v1/user/drafts/{draftId}/export"

    // 4. AI 3D Relief Generative Engine (SSE Streaming)
    const val AI_RELIEF_STREAM = "/api/v1/ai/relief/stream"
    const val AI_RELIEF_ESTIMATE = "/api/v1/ai/relief/estimate"

    // 5. Hardware IoT, Telemetry & Maintenance
    const val DEVICE_LIST = "/api/v1/devices"
    const val DEVICE_STATUS = "/api/v1/devices/{deviceId}/status"
    const val DEVICE_CONTROL = "/api/v1/devices/{deviceId}/control" // chamber light, air filtration
    const val DEVICE_MAINTENANCE = "/api/v1/devices/{deviceId}/maintenance" // nozzle clean, purge, test print
    const val DEVICE_PRINT_JOB = "/api/v1/devices/{deviceId}/jobs"
    const val DEVICE_WS_TELEMETRY = "$WS_URL/devices/{deviceId}/telemetry" // 6 ink levels, head/bed temp, progress

    // 6. MakeItReal Academy Courses
    const val ACADEMY_COURSES = "/api/v1/academy/courses"
    const val ACADEMY_COURSE_DETAIL = "/api/v1/academy/courses/{courseId}"
    const val ACADEMY_LOAD_SAMPLE = "/api/v1/academy/courses/{courseId}/sample"

    // 7. Contests & Brand Activities
    const val ACTIVITIES_LIST = "/api/v1/activities"
    const val ACTIVITY_DETAIL = "/api/v1/activities/{activityId}"
    const val ACTIVITY_SUBMIT_ENTRY = "/api/v1/activities/{activityId}/submit"

    // 8. Asset Stickers & Clipart
    const val ASSET_STICKER_PACKS = "/api/v1/assets/stickers/packs"
    const val ASSET_STICKERS = "/api/v1/assets/stickers"

    // 9. Notification Center
    const val NOTIFICATIONS_LIST = "/api/v1/user/notifications"
    const val NOTIFICATION_MARK_READ = "/api/v1/user/notifications/{id}/read"
    const val NOTIFICATION_MARK_ALL_READ = "/api/v1/user/notifications/read-all"

    // 10. User Points & Rewards Mall
    const val USER_POINTS_SUMMARY = "/api/v1/user/points"
    const val USER_CHECK_IN = "/api/v1/user/checkin"
    const val MALL_ITEMS = "/api/v1/mall/items"
    const val MALL_REDEEM = "/api/v1/mall/redeem"
}
