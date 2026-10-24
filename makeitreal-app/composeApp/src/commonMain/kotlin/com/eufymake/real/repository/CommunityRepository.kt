package com.eufymake.real.repository

import com.eufymake.real.api.*
import com.eufymake.real.model.Announcement
import com.eufymake.real.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CommunityRepository {
    val announcements: StateFlow<List<Announcement>>
    val projects: StateFlow<List<Project>>
    val comments: StateFlow<Map<String, List<CommentDto>>>
    val creators: StateFlow<Map<String, CreatorProfileDto>>
    val academyCourses: StateFlow<List<AcademyCourseDto>>
    val activities: StateFlow<List<ActivityDto>>
    val notifications: StateFlow<List<NotificationDto>>

    fun toggleLike(projectId: String)
    fun toggleBookmark(projectId: String)
    fun forkProject(projectId: String)
    fun publishProject(title: String, description: String, substrateId: String, substrateName: String): Project

    // Comments & Social
    fun addComment(projectId: String, content: String): CommentDto
    fun toggleLikeComment(projectId: String, commentId: String)
    fun toggleFollowCreator(creatorId: String)

    // Activities & Contests
    fun submitActivityEntry(activityId: String, projectId: String, craftNotes: String): Boolean

    // Notifications
    fun markNotificationRead(id: String)
    fun markAllNotificationsRead()
}

class MockCommunityRepository : CommunityRepository {

    private val _announcements = MutableStateFlow<List<Announcement>>(
        listOf(
            Announcement(
                id = "contest_halloween",
                title = "Halloween Spooky UV Relief Challenge",
                subtitle = "Share glow-in-the-dark & multi-layer tactile designs to win $5000 in UV consumables!",
                bannerUrl = "",
                imageName = "banner_campaign.webp",
                tag = "CONTEST 2026",
                gradientStart = 0xFFFF512F,
                gradientEnd = 0xFFDD2476
            ),
            Announcement(
                id = "official_contest",
                title = "MakeItReal Global Creator Contest 2026",
                subtitle = "Official AnkerMake UV print design competition. Unleash your tactile imagination!",
                bannerUrl = "",
                imageName = "banner_final.webp",
                tag = "OFFICIAL EVENT",
                gradientStart = 0xFF1D2671,
                gradientEnd = 0xFFC33764
            ),
            Announcement(
                id = "studio_update",
                title = "MakeItReal 2.5D Studio v1.22 Update",
                subtitle = "New White Underbase Choke (0.2mm) & seamless cylinder rotary UV printing support.",
                bannerUrl = "",
                imageName = "banner_pc.webp",
                tag = "FIRMWARE & STUDIO",
                gradientStart = 0xFF11998E,
                gradientEnd = 0xFF38EF7D
            )
        )
    )
    override val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(
        listOf(
            Project(
                id = "proj_halloween_cat",
                title = "Halloween Witch Cat Decor",
                description = "2.4mm tactile embossed cat ornament with luminous spot varnish on witch hat.",
                authorName = "NeoMaker_99",
                authorHandle = "@neomaker99",
                creatorId = "creator_neo",
                imageName = "Halloween Witch Cat Decor Skill.webp",
                galleryImages = listOf("Halloween Witch Cat Decor Skill.webp", "work_3.webp", "work_1.webp"),
                likesCount = 912,
                forkCount = 184,
                substrateId = "sheet",
                substrateName = "亚克力板",
                coverGradientStart = 0xFF654ea3,
                coverGradientEnd = 0xFFeaafc8,
                tags = listOf("万圣节", "3D浮雕", "亚克力"),
                isStaffPick = true,
                category = "节日快乐"
            ),
            Project(
                id = "proj_moonlight_witch",
                title = "Moonlight Witch Ornament Skill",
                description = "Multi-pass UV relief with micro-textured crescent moon and metallic gold highlights.",
                authorName = "WhiskersDesign",
                authorHandle = "@whiskers",
                creatorId = "creator_whiskers",
                imageName = "Moonlight Witch Ornament Skill.webp",
                galleryImages = listOf("Moonlight Witch Ornament Skill.webp", "work_4.webp", "work_6.webp"),
                likesCount = 1205,
                forkCount = 312,
                substrateId = "coaster",
                substrateName = "吸水杯垫",
                coverGradientStart = 0xFF0F2027,
                coverGradientEnd = 0xFF2C5364,
                tags = listOf("杯垫", "女巫", "微浮雕"),
                isStaffPick = true,
                category = "3D纹理"
            ),
            Project(
                id = "proj_floral_skeleton",
                title = "Floral Halloween Skeleton Skill",
                description = "Botanical roses intertwined with bone relief structure, selective gloss finish.",
                authorName = "Katniss_e",
                authorHandle = "@katniss_e",
                creatorId = "creator_katniss",
                imageName = "Floral Halloween Skeleton Skill.webp",
                galleryImages = listOf("Floral Halloween Skeleton Skill.webp", "work_7.webp", "work_2.webp"),
                likesCount = 842,
                forkCount = 138,
                substrateId = "sheet",
                substrateName = "亚克力标牌",
                coverGradientStart = 0xFF134E5E,
                coverGradientEnd = 0xFF71B280,
                tags = listOf("骷髅", "浮雕", "艺术"),
                isStaffPick = true,
                category = "艺术家日常"
            ),
            Project(
                id = "proj_door_hanger",
                title = "Halloween Family Door Hanger",
                description = "Tactile welcome plate with 3D raised letters and spooky festive silhouettes.",
                authorName = "Jayleecustoms",
                authorHandle = "@jaylee",
                creatorId = "creator_jaylee",
                imageName = "Halloween Family Door Hanger Skill.webp",
                galleryImages = listOf("Halloween Family Door Hanger Skill.webp", "work_1.webp"),
                likesCount = 653,
                forkCount = 94,
                substrateId = "sheet",
                substrateName = "木质门牌",
                coverGradientStart = 0xFFB92B27,
                coverGradientEnd = 0xFF1565C0,
                tags = listOf("门牌", "家庭", "节日"),
                isStaffPick = false,
                category = "个性化礼物"
            ),
            Project(
                id = "proj_gothic_halloween",
                title = "Whimsical Gothic Halloween Art",
                description = "Impasto style oil paint simulation with 2.8mm layered UV white build-up.",
                authorName = "GothicArt",
                authorHandle = "@gothicart",
                creatorId = "creator_gothic",
                imageName = "Whimsical Gothic Halloween Skill.webp",
                galleryImages = listOf("Whimsical Gothic Halloween Skill.webp", "work_3.webp"),
                likesCount = 488,
                forkCount = 82,
                substrateId = "canvas",
                substrateName = "油画布",
                coverGradientStart = 0xFF4A4E69,
                coverGradientEnd = 0xFF22223B,
                tags = listOf("哥特", "油画感", "画布"),
                isStaffPick = true,
                category = "艺术家日常"
            ),
            Project(
                id = "proj_work_1",
                title = "Wedding Hand-drawn Commemorative",
                description = "Elegant celebratory portrait with crystal varnish gloss layer and delicate line relief.",
                authorName = "Sean",
                authorHandle = "@sean_crafts",
                creatorId = "creator_sean",
                imageName = "work_1.webp",
                galleryImages = listOf("work_1.webp", "work_2.webp"),
                likesCount = 1530,
                forkCount = 420,
                substrateId = "sheet",
                substrateName = "亚克力相框",
                coverGradientStart = 0xFFD4A373,
                coverGradientEnd = 0xFFFAEDCD,
                tags = listOf("婚礼", "纪念", "手绘"),
                isStaffPick = true,
                category = "个性化礼物"
            ),
            Project(
                id = "proj_work_2",
                title = "Bridesmaid Invitation Card",
                description = "Pearlescent white underbase with gilded lettering and tactile embossing.",
                authorName = "Katniss_e",
                authorHandle = "@katniss_e",
                creatorId = "creator_katniss",
                imageName = "work_2.webp",
                galleryImages = listOf("work_2.webp", "work_4.webp"),
                likesCount = 724,
                forkCount = 115,
                substrateId = "card",
                substrateName = "硬质贺卡",
                coverGradientStart = 0xFFDDA15E,
                coverGradientEnd = 0xFFBC6C25,
                tags = listOf("贺卡", "伴娘", "烫金"),
                isStaffPick = false,
                category = "个性化礼物"
            ),
            Project(
                id = "proj_work_3",
                title = "Horror Cons 6x8 Canvas",
                description = "High dynamic contrast UV art with ultra-black matte base and high-gloss blood splatter.",
                authorName = "Jayleecustoms",
                authorHandle = "@jaylee",
                creatorId = "creator_jaylee",
                imageName = "work_3.webp",
                galleryImages = listOf("work_3.webp", "work_7.webp"),
                likesCount = 1089,
                forkCount = 280,
                substrateId = "canvas",
                substrateName = "迷你油画布",
                coverGradientStart = 0xFFBC6C25,
                coverGradientEnd = 0xFF9B2226,
                tags = listOf("暗黑", "浮雕", "展览"),
                isStaffPick = true,
                category = "3D纹理"
            ),
            Project(
                id = "proj_work_4",
                title = "Dancer Wax Seal Tactile Stamp",
                description = "Fine-art relief depicting ballet dancer dynamic flow on circular wax substrate.",
                authorName = "Katniss_e",
                authorHandle = "@katniss_e",
                creatorId = "creator_katniss",
                imageName = "work_4.webp",
                galleryImages = listOf("work_4.webp", "work_6.webp"),
                likesCount = 412,
                forkCount = 56,
                substrateId = "coaster",
                substrateName = "火漆印章底座",
                coverGradientStart = 0xFF9B2226,
                coverGradientEnd = 0xFF6B2D5C,
                tags = listOf("印章", "芭蕾", "精细浮雕"),
                isStaffPick = false,
                category = "3D纹理"
            ),
            Project(
                id = "proj_work_5",
                title = "Octopus Deep Ocean 3D Mug",
                description = "Rotary 360 seamless print around cylinder mug with suction cup raised texture.",
                authorName = "AlexMaker",
                authorHandle = "@alexmaker",
                creatorId = "creator_alex",
                imageName = "work_5.webp",
                galleryImages = listOf("work_5.webp", "work_1.webp"),
                likesCount = 890,
                forkCount = 142,
                substrateId = "cup",
                substrateName = "圆柱马克杯",
                coverGradientStart = 0xFF6B2D5C,
                coverGradientEnd = 0xFF4A4E69,
                tags = listOf("杯子", "章鱼", "旋转打印"),
                isStaffPick = true,
                category = "3D纹理"
            ),
            Project(
                id = "proj_work_6",
                title = "Stained Glass Prism Magnet",
                description = "Translucent stained glass look with high-gloss clear varnish lines imitating lead came.",
                authorName = "GothicArt",
                authorHandle = "@gothicart",
                creatorId = "creator_gothic",
                imageName = "work_6.webp",
                galleryImages = listOf("work_6.webp", "work_7.webp"),
                likesCount = 610,
                forkCount = 95,
                substrateId = "magnet",
                substrateName = "磁吸贴片",
                coverGradientStart = 0xFF4A4E69,
                coverGradientEnd = 0xFF22223B,
                tags = listOf("彩窗", "磁铁", "光油"),
                isStaffPick = false,
                category = "艺术家日常"
            ),
            Project(
                id = "proj_work_7",
                title = "Cyber Skull Holographic Decal",
                description = "Weather-resistant UV cure over metallic vinyl film with precision contour kiss-cut.",
                authorName = "GhostPrints",
                authorHandle = "@ghostprints",
                creatorId = "creator_ghost",
                imageName = "work_7.webp",
                galleryImages = listOf("work_7.webp", "work_3.webp"),
                likesCount = 760,
                forkCount = 128,
                substrateId = "sticker",
                substrateName = "模切贴纸",
                coverGradientStart = 0xFFee0979,
                coverGradientEnd = 0xFFff6a00,
                tags = listOf("贴纸", "赛博朋克", "防水"),
                isStaffPick = true,
                category = "3D纹理"
            )
        )
    )
    override val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // 1. Comments Store
    private val _comments = MutableStateFlow<Map<String, List<CommentDto>>>(
        mapOf(
            "proj_halloween_cat" to listOf(
                CommentDto(
                    id = "c_1",
                    projectId = "proj_halloween_cat",
                    authorId = "user_101",
                    authorName = "CyberCraftsman",
                    content = "这个浮雕堆叠了多少层？边缘有毛刺吗？",
                    likesCount = 14,
                    createdAt = System.currentTimeMillis() - 3600000L
                ),
                CommentDto(
                    id = "c_2",
                    projectId = "proj_halloween_cat",
                    authorId = "creator_neo",
                    authorName = "NeoMaker_99",
                    content = "设置了 0.2mm 白墨内缩，总共走 18 个 Pass，边缘极其平滑！",
                    likesCount = 28,
                    createdAt = System.currentTimeMillis() - 1800000L
                ),
                CommentDto(
                    id = "c_3",
                    projectId = "proj_halloween_cat",
                    authorId = "user_204",
                    authorName = "Alex_Relief",
                    content = "太帅了，已经直接点击做同款载入打印板！",
                    likesCount = 9,
                    createdAt = System.currentTimeMillis() - 600000L
                )
            ),
            "proj_moonlight_witch" to listOf(
                CommentDto(
                    id = "c_4",
                    projectId = "proj_moonlight_witch",
                    authorId = "user_88",
                    authorName = "VarnishMaster",
                    content = "月亮那一部分用的是高光还是哑光？",
                    likesCount = 6,
                    createdAt = System.currentTimeMillis() - 7200000L
                ),
                CommentDto(
                    id = "c_5",
                    projectId = "proj_moonlight_witch",
                    authorId = "creator_whiskers",
                    authorName = "WhiskersDesign",
                    content = "月亮部分是 Spot Gloss 局部光油，夜光墨水打底，关灯后特别梦幻！",
                    likesCount = 19,
                    createdAt = System.currentTimeMillis() - 5400000L
                )
            )
        )
    )
    override val comments: StateFlow<Map<String, List<CommentDto>>> = _comments.asStateFlow()

    // 2. Creators Profile Store
    private val _creators = MutableStateFlow<Map<String, CreatorProfileDto>>(
        mapOf(
            "creator_neo" to CreatorProfileDto(
                creatorId = "creator_neo",
                name = "NeoMaker_99",
                handle = "@neomaker99",
                bio = "UV 浮雕极客 | 专注赛博朋克与异形切割 | AnkerMake 资深认证工程师",
                verifiedTitle = "官方特邀创作者",
                isVerified = true,
                followingCount = 42,
                followersCount = 3240,
                totalLikesCount = 12800,
                isFollowing = false
            ),
            "creator_whiskers" to CreatorProfileDto(
                creatorId = "creator_whiskers",
                name = "WhiskersDesign",
                handle = "@whiskers",
                bio = "插画师 & UV 软装艺术家 | 擅长奇幻女巫与手作复古杯垫设计",
                verifiedTitle = "签约艺术家",
                isVerified = true,
                followingCount = 28,
                followersCount = 1890,
                totalLikesCount = 8500,
                isFollowing = true
            ),
            "creator_katniss" to CreatorProfileDto(
                creatorId = "creator_katniss",
                name = "Katniss_e",
                handle = "@katniss_e",
                bio = "婚礼与派对定制纸品专家 | 烫金与触觉浮雕研究狂热者",
                verifiedTitle = "纸品工艺大师",
                isVerified = true,
                followingCount = 65,
                followersCount = 2410,
                totalLikesCount = 9600,
                isFollowing = false
            ),
            "creator_alex" to CreatorProfileDto(
                creatorId = "creator_alex",
                name = "AlexMaker",
                handle = "@alexmaker",
                bio = "圆柱无缝旋转打印狂热爱好者 | 搞定各类保温杯与玻璃马克杯",
                verifiedTitle = "旋印极客",
                isVerified = false,
                followingCount = 19,
                followersCount = 1520,
                totalLikesCount = 6100,
                isFollowing = false
            ),
            "creator_gothic" to CreatorProfileDto(
                creatorId = "creator_gothic",
                name = "GothicArt",
                handle = "@gothicart",
                bio = "暗黑哥特与古典教堂彩窗复刻 | 专注重度厚涂油画立体感",
                verifiedTitle = "先锋艺术家",
                isVerified = true,
                followingCount = 31,
                followersCount = 1120,
                totalLikesCount = 4300,
                isFollowing = false
            )
        )
    )
    override val creators: StateFlow<Map<String, CreatorProfileDto>> = _creators.asStateFlow()

    // 3. MakeItReal Academy Courses
    private val _academyCourses = MutableStateFlow<List<AcademyCourseDto>>(
        listOf(
            AcademyCourseDto(
                id = "course_1",
                title = "新手起航：如何为亚克力板制作第一款 3D 浮雕",
                subtitle = "掌握平面基材装夹、白墨铺底与局部浮雕高度设定核心步骤。",
                difficulty = "入门",
                durationMinutes = 6,
                coverImageName = "work_1.webp",
                tags = listOf("新手推荐", "亚克力", "基础排版"),
                sampleDraftSubstrateId = "sheet",
                keyParametersHint = "建议浮雕深度: 1.8mm, 白墨内缩: 0.2mm"
            ),
            AcademyCourseDto(
                id = "course_2",
                title = "深度调校：白墨内缩 (White Choke) 彻底杜绝露白毛边",
                subtitle = "原理解析：通过 0.2mm 像素收缩抑制高黏度白墨的物理流平扩散。",
                difficulty = "进阶",
                durationMinutes = 8,
                coverImageName = "Floral Halloween Skeleton Skill.webp",
                tags = listOf("工艺核心", "白墨调校", "边缘精修"),
                sampleDraftSubstrateId = "sheet",
                keyParametersHint = "白墨内缩: 0.2mm - 0.4mm, 固化灯功率: 100%"
            ),
            AcademyCourseDto(
                id = "course_3",
                title = "豪华质感：局部光油 (Spot Gloss) 与亚光清漆技巧",
                subtitle = "教你如何在暗黑画布上点缀水滴晶莹效果与镜面反光质感。",
                difficulty = "大师",
                durationMinutes = 10,
                coverImageName = "Moonlight Witch Ornament Skill.webp",
                tags = listOf("高光效果", "光油技巧", "奢华质感"),
                sampleDraftSubstrateId = "canvas",
                keyParametersHint = "光油模式: SPOT_GLOSS, 固化延时: 2秒"
            ),
            AcademyCourseDto(
                id = "course_4",
                title = "圆柱旋转打印：马克杯 360° 无缝贴合实战",
                subtitle = "利用把手避让区与旋转步进电机实现环绕立体图案印刷。",
                difficulty = "进阶",
                durationMinutes = 7,
                coverImageName = "work_5.webp",
                tags = listOf("圆柱体", "马克杯", "旋转治具"),
                sampleDraftSubstrateId = "cup",
                keyParametersHint = "周长展开计算: 直径×π, 左右留白: 各15mm"
            )
        )
    )
    override val academyCourses: StateFlow<List<AcademyCourseDto>> = _academyCourses.asStateFlow()

    // 4. Activities & Contests
    private val _activities = MutableStateFlow<List<ActivityDto>>(
        listOf(
            ActivityDto(
                id = "act_halloween_2026",
                title = "2026 万圣节 3D 浮雕创意挑战赛",
                description = "释放你的触觉想象力！使用 eufyMake UV 打印机制作夜光或多层触觉浮雕万圣节作品，赢取万元原装耗材与全球官方展出机会。",
                bannerImageName = "banner_campaign.webp",
                prizePoolDesc = "$5,000 原装耗材礼包 + 1年份免费墨水 + 官方认证奖杯",
                deadlineTimestamp = System.currentTimeMillis() + 7 * 86400000L + 14 * 3600000L,
                submissionCount = 142,
                tags = listOf("万圣节", "官方大赛", "丰厚奖品")
            ),
            ActivityDto(
                id = "act_rotary_cup",
                title = "旋转制造大师：360° 杯具体系二创季",
                description = "探索不锈钢保温杯、陶瓷马克杯与玻璃酒杯的极限浮雕触感。",
                bannerImageName = "banner_final.webp",
                prizePoolDesc = "高精度旋转轴治具配件 + 豪华亚克力耗材箱",
                deadlineTimestamp = System.currentTimeMillis() + 18 * 86400000L,
                submissionCount = 89,
                tags = listOf("旋转打印", "杯具", "创客挑战")
            )
        )
    )
    override val activities: StateFlow<List<ActivityDto>> = _activities.asStateFlow()

    // 5. Notifications Store
    private val _notifications = MutableStateFlow<List<NotificationDto>>(
        listOf(
            NotificationDto(
                id = "notif_1",
                type = "CONTEST",
                title = "万圣节创意挑战赛现已正式开赛！",
                content = "您的作品《万圣女巫猫》已获得参赛资格，目前获得 142 票！",
                timestamp = System.currentTimeMillis() - 7200000L,
                isRead = false,
                jumpRoute = "contest"
            ),
            NotificationDto(
                id = "notif_2",
                type = "LIKE",
                title = "创作者 NeoMaker_99 赞了您的设计",
                content = "您的工程《亚克力多层浮雕》被收录至“官方每日精选”！",
                timestamp = System.currentTimeMillis() - 18000000L,
                isRead = false,
                jumpRoute = "feed"
            ),
            NotificationDto(
                id = "notif_3",
                type = "SYSTEM",
                title = "固件 v1.4.12 升级已推送",
                content = "优化了圆柱把手安全避让区的位移算法，打印更稳定，建议及时更新。",
                timestamp = System.currentTimeMillis() - 86400000L,
                isRead = true,
                jumpRoute = "device"
            )
        )
    )
    override val notifications: StateFlow<List<NotificationDto>> = _notifications.asStateFlow()

    override fun toggleLike(projectId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val newLiked = !proj.isLiked
                val delta = if (newLiked) 1 else -1
                proj.copy(isLiked = newLiked, likesCount = (proj.likesCount + delta).coerceAtLeast(0))
            } else {
                proj
            }
        }
    }

    override fun toggleBookmark(projectId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(isBookmarked = !proj.isBookmarked)
            } else {
                proj
            }
        }
    }

    override fun forkProject(projectId: String) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(forkCount = proj.forkCount + 1)
            } else {
                proj
            }
        }
    }

    override fun publishProject(title: String, description: String, substrateId: String, substrateName: String): Project {
        val newProj = Project(
            id = "proj_${System.currentTimeMillis()}",
            title = title,
            description = description,
            authorName = "You (Maker)",
            authorHandle = "@you_maker",
            creatorId = "creator_you",
            likesCount = 1,
            isLiked = true,
            isBookmarked = false,
            substrateId = substrateId,
            substrateName = substrateName,
            coverGradientStart = 0xFF2193b0,
            coverGradientEnd = 0xFF6dd5ed,
            tags = listOf("原创", substrateName),
            createdAt = System.currentTimeMillis()
        )
        _projects.value = listOf(newProj) + _projects.value
        return newProj
    }

    override fun addComment(projectId: String, content: String): CommentDto {
        val newComment = CommentDto(
            id = "c_${System.currentTimeMillis()}",
            projectId = projectId,
            authorId = "user_me",
            authorName = "You (Maker)",
            content = content,
            createdAt = System.currentTimeMillis(),
            likesCount = 0,
            isLiked = false
        )
        val currentList = _comments.value[projectId] ?: emptyList()
        val updatedMap = _comments.value.toMutableMap()
        updatedMap[projectId] = listOf(newComment) + currentList
        _comments.value = updatedMap
        return newComment
    }

    override fun toggleLikeComment(projectId: String, commentId: String) {
        val currentList = _comments.value[projectId] ?: return
        val updatedList = currentList.map { c ->
            if (c.id == commentId) {
                val newLiked = !c.isLiked
                val delta = if (newLiked) 1 else -1
                c.copy(isLiked = newLiked, likesCount = (c.likesCount + delta).coerceAtLeast(0))
            } else c
        }
        val updatedMap = _comments.value.toMutableMap()
        updatedMap[projectId] = updatedList
        _comments.value = updatedMap
    }

    override fun toggleFollowCreator(creatorId: String) {
        val currentMap = _creators.value.toMutableMap()
        val profile = currentMap[creatorId] ?: return
        val newFollowing = !profile.isFollowing
        val delta = if (newFollowing) 1 else -1
        currentMap[creatorId] = profile.copy(
            isFollowing = newFollowing,
            followersCount = (profile.followersCount + delta).coerceAtLeast(0)
        )
        _creators.value = currentMap
    }

    override fun submitActivityEntry(activityId: String, projectId: String, craftNotes: String): Boolean {
        // Tag the project as contest entry
        _projects.value = _projects.value.map { p ->
            if (p.id == projectId) p.copy(isContestEntry = true, tags = p.tags + listOf("参赛作品"))
            else p
        }
        // Increment activity submission count
        _activities.value = _activities.value.map { act ->
            if (act.id == activityId) act.copy(submissionCount = act.submissionCount + 1)
            else act
        }
        return true
    }

    override fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { n ->
            if (n.id == id) n.copy(isRead = true) else n
        }
    }

    override fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
