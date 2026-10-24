package com.eufymake.real.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.api.AcademyCourseDto
import com.eufymake.real.api.ActivityDto
import com.eufymake.real.api.CommentDto
import com.eufymake.real.api.CreatorProfileDto
import com.eufymake.real.api.NotificationDto
import com.eufymake.real.model.Announcement
import com.eufymake.real.model.Project
import com.eufymake.real.ui.components.*
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.util.rememberAppImage

@Composable
fun InspirationsScreen(
    announcements: List<Announcement>,
    projects: List<Project>,
    commentsMap: Map<String, List<CommentDto>>,
    creatorsMap: Map<String, CreatorProfileDto>,
    academyCourses: List<AcademyCourseDto>,
    activities: List<ActivityDto>,
    notifications: List<NotificationDto>,
    onLikeProject: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onToggleLikeComment: (String, String) -> Unit,
    onSubmitActivityEntry: (String, String, String) -> Boolean,
    onMarkNotificationRead: (String) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    onLoadCourseProject: (AcademyCourseDto) -> Unit,
    onMakeSame: (Project) -> Unit,
    initialSection: String? = null,
    onSectionHandled: (() -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf("全部") }
    var selectedHashtag by remember { mutableStateOf<String?>(null) }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog & Drawer visible states
    var showAcademyDrawer by remember { mutableStateOf(false) }
    var showContestDialog by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<ActivityDto?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var viewingCreatorId by remember { mutableStateOf<String?>(null) }

    // Deep link handling for section navigation from other tabs (Fix #11)
    LaunchedEffect(initialSection) {
        if (initialSection == "ACTIVITIES") {
            selectedActivity = activities.firstOrNull()
            showContestDialog = true
            onSectionHandled?.invoke()
        } else if (initialSection == "ACADEMY") {
            showAcademyDrawer = true
            onSectionHandled?.invoke()
        }
    }

    // Multi-image gallery index for detail dialog
    var currentGalleryIndex by remember { mutableStateOf(0) }

    val categories = listOf("全部", "3D纹理", "节日快乐", "个性化礼物", "艺术家日常")
    val hashtags = listOf("全部", "#万圣节", "#3D浮雕", "#亚克力", "#圆柱杯", "#局部光油")

    val unreadNotificationsCount = notifications.count { !it.isRead }

    val filteredProjects = projects.filter { proj ->
        val matchesCategory = selectedCategory == "全部" || proj.category == selectedCategory
        val matchesHashtag = selectedHashtag == null || selectedHashtag == "全部" || proj.tags.any { "#$it" == selectedHashtag || it == selectedHashtag }
        val matchesSearch = searchQuery.isEmpty() || proj.title.contains(searchQuery, ignoreCase = true) ||
                proj.description.contains(searchQuery, ignoreCase = true) ||
                proj.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesHashtag && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "灵感社区",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )

                    // Academy & Notifications Buttons
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Academy pill button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFEF3C7))
                                .clickable { showAcademyDrawer = true }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎓", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("学院", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }

                        // Notification bell icon with badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable { showNotificationsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(18.dp)
                            )
                            if (unreadNotificationsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(Color(0xFFF2F3F5))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF1A1A1A)
                                ),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(BrandGreen),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "搜索设计、基材、工艺、创作者...",
                                                fontSize = 13.sp,
                                                color = Color(0xFF8E8E93)
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official Campaign Hero Banner Carousel
                if (announcements.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().height(135.dp)
                    ) {
                        items(announcements) { ann ->
                            val bannerBmp = if (ann.imageName.isNotEmpty()) rememberAppImage(ann.imageName) else null
                            Box(
                                modifier = Modifier
                                    .width(320.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(ann.gradientStart), Color(ann.gradientEnd))
                                        )
                                    )
                                    .clickable {
                                        if (ann.id.contains("halloween") || ann.id.contains("contest") || ann.id.contains("cup") || ann.id.contains("rotary")) {
                                            val targetAct = when {
                                                ann.id.contains("halloween") -> activities.firstOrNull { it.id.contains("halloween") }
                                                ann.id.contains("cup") || ann.id.contains("rotary") -> activities.firstOrNull { it.id.contains("cup") || it.id.contains("rotary") }
                                                else -> activities.firstOrNull { it.id == ann.id }
                                            } ?: activities.firstOrNull()
                                            selectedActivity = targetAct
                                            showContestDialog = true
                                        } else {
                                            showAcademyDrawer = true
                                        }
                                    }
                            ) {
                                if (bannerBmp != null) {
                                    Image(
                                        bitmap = bannerBmp,
                                        contentDescription = ann.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Dark gradient protection
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                            )
                                        )
                                    )

                                // Text contents
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = ann.tag,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ann.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = ann.subtitle,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Category Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { idx ->
                        val isSelected = selectedCategory == categories[idx]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF1A1A1A) else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF1A1A1A) else Color(0xFFE5E7EB),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedCategory = categories[idx] }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = categories[idx],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1A1A1A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hashtag Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(hashtags.size) { idx ->
                        val isSelected = selectedHashtag == hashtags[idx] || (selectedHashtag == null && hashtags[idx] == "全部")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                                .clickable { selectedHashtag = if (hashtags[idx] == "全部") null else hashtags[idx] }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = hashtags[idx],
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF15803D) else Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        }

        // Projects 2-column Lazy Grid (Row recycled)
        items(filteredProjects.chunked(2)) { rowPair ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPair.forEach { proj ->
                    ProjectCard(
                        project = proj,
                        onLikeClick = { onLikeProject(proj.id) },
                        onClick = {
                            selectedProjectId = proj.id
                            currentGalleryIndex = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Inspiration Detail Dialog
    val detail = projects.firstOrNull { it.id == selectedProjectId }
    if (detail != null) {
        val creator = creatorsMap[detail.creatorId]
        val projectComments = commentsMap[detail.id] ?: emptyList()
        val gallery = if (detail.galleryImages.isNotEmpty()) detail.galleryImages else listOf(detail.imageName)

        Dialog(onDismissRequest = { selectedProjectId = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Top bar: Back arrow, Creator profile, Follow button, More options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewingCreatorId = detail.creatorId }
                        ) {
                            IconButton(onClick = { selectedProjectId = null }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCBD5E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    detail.authorName.take(1),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        detail.authorName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = BrandGreen, modifier = Modifier.size(14.dp))
                                }
                                Text(detail.authorHandle, fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // "关注 / 已关注" button
                            val isFollowing = creator?.isFollowing ?: false
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isFollowing) Color(0xFFCBD5E1) else BrandGreen, RoundedCornerShape(12.dp))
                                    .background(if (isFollowing) Color(0xFFF1F5F9) else Color.White)
                                    .clickable { onToggleFollow(detail.creatorId) }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (isFollowing) "已关注" else "关注",
                                    fontSize = 12.sp,
                                    color = if (isFollowing) Color(0xFF64748B) else BrandGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // 2. Interactive Multi-Image Gallery with Swipe Navigation
                    val activeImageName = gallery.getOrElse(currentGalleryIndex) { detail.imageName }
                    val activeBmp = if (activeImageName.isNotEmpty()) rememberAppImage(activeImageName) else null

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(detail.coverGradientStart), Color(detail.coverGradientEnd)))
                            )
                    ) {
                        if (activeBmp != null) {
                            Image(
                                bitmap = activeBmp,
                                contentDescription = detail.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Left Arrow (if > 1 image and index > 0)
                        if (currentGalleryIndex > 0) {
                            IconButton(
                                onClick = { currentGalleryIndex-- },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", tint = Color.White)
                            }
                        }

                        // Right Arrow (if > 1 image and index < gallery.lastIndex)
                        if (currentGalleryIndex < gallery.lastIndex) {
                            IconButton(
                                onClick = { currentGalleryIndex++ },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
                            }
                        }

                        // Top-left 3D layer badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }

                        // Bottom-right gallery index pill: e.g. "1/3"
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "${currentGalleryIndex + 1}/${gallery.size}",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 3. Information Body
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = detail.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                        }

                        item {
                            Text(
                                text = detail.description,
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                lineHeight = 17.sp
                            )
                        }

                        // Stats: Heart, View, Print | Date
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${detail.likesCount}", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${detail.forkCount}次同款", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${projectComments.size}条评论", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }
                        }

                        // Hashtag Chips
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                detail.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .clickable {
                                                selectedHashtag = "#$tag"
                                                selectedProjectId = null
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("#$tag", fontSize = 11.sp, color = BrandGreen, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Hardware card: eufyMake UV Printer E1
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("适配基材: ${detail.substrateName}", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text("推荐设备: eufyMake UV Printer E1", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    }
                                }
                            }
                        }
                    }

                    val detailHeartScale = remember { Animatable(1f) }
                    LaunchedEffect(detail.isLiked) {
                        if (detail.isLiked) {
                            detailHeartScale.animateTo(1.35f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                            detailHeartScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        } else {
                            detailHeartScale.snapTo(1f)
                        }
                    }
                    val detailStarScale = remember { Animatable(1f) }
                    LaunchedEffect(detail.isBookmarked) {
                        if (detail.isBookmarked) {
                            detailStarScale.animateTo(1.35f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                            detailStarScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        } else {
                            detailStarScale.snapTo(1f)
                        }
                    }

                    // 4. Sticky Bottom Action Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(68.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                                // Like action with bounce & animated counter
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { onLikeProject(detail.id) }
                                ) {
                                    Icon(
                                        imageVector = if (detail.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (detail.isLiked) Color(0xFFEF4444) else Color(0xFF1A1A1A),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer {
                                                scaleX = detailHeartScale.value
                                                scaleY = detailHeartScale.value
                                            }
                                    )
                                    AnimatedContent(
                                        targetState = detail.likesCount,
                                        transitionSpec = {
                                            if (targetState > initialState) {
                                                slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                                            } else {
                                                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                                            }
                                        }
                                    ) { count ->
                                        Text(
                                            text = "$count",
                                            fontSize = 10.sp,
                                            color = if (detail.isLiked) Color(0xFFEF4444) else Color(0xFF8E8E93)
                                        )
                                    }
                                }

                                // Comment action
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { showCommentsSheet = true }
                                ) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                                    Text("${projectComments.size}", fontSize = 10.sp, color = Color(0xFF8E8E93))
                                }

                                // Bookmark / Favorite action with bounce
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { onToggleBookmark(detail.id) }
                                ) {
                                    Icon(
                                        imageVector = if (detail.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (detail.isBookmarked) Color(0xFFF59E0B) else Color(0xFF1A1A1A),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer {
                                                scaleX = detailStarScale.value
                                                scaleY = detailStarScale.value
                                            }
                                    )
                                    Text(if (detail.isBookmarked) "已收藏" else "收藏", fontSize = 10.sp, color = Color(0xFF8E8E93))
                                }
                            }

                            // Green CTA Button: "定制同款" (Make Same)
                            Button(
                                onClick = {
                                    selectedProjectId = null
                                    onMakeSame(detail)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(22.dp),
                                modifier = Modifier
                                    .width(135.dp)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "做同款",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Creator Profile Dialog
    if (viewingCreatorId != null) {
        val creator = creatorsMap[viewingCreatorId]
        if (creator != null) {
            val creatorProjects = projects.filter { it.creatorId == creator.creatorId }
            CreatorProfileDialog(
                creator = creator,
                creatorProjects = creatorProjects,
                onToggleFollow = { onToggleFollow(creator.creatorId) },
                onSelectProject = { p ->
                    viewingCreatorId = null
                    selectedProjectId = p.id
                    currentGalleryIndex = 0
                },
                onDismiss = { viewingCreatorId = null }
            )
        }
    }

    // Comments Sheet
    if (showCommentsSheet && detail != null) {
        val comments = commentsMap[detail.id] ?: emptyList()
        CommentsSheet(
            comments = comments,
            onAddComment = { content -> onAddComment(detail.id, content) },
            onToggleLikeComment = { commentId -> onToggleLikeComment(detail.id, commentId) },
            onDismiss = { showCommentsSheet = false }
        )
    }

    // Academy Drawer
    if (showAcademyDrawer) {
        AcademyDrawer(
            courses = academyCourses,
            onLoadCourseProject = { course ->
                showAcademyDrawer = false
                onLoadCourseProject(course)
            },
            onDismiss = { showAcademyDrawer = false }
        )
    }

    // Contest Modal
    if (showContestDialog) {
        val act = selectedActivity ?: activities.firstOrNull()
        if (act != null) {
            ContestDialog(
                activity = act,
                onSubmitEntry = { notes ->
                    onSubmitActivityEntry(act.id, detail?.id ?: "proj_halloween_cat", notes)
                },
                onDismiss = { showContestDialog = false }
            )
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onMarkRead = onMarkNotificationRead,
            onMarkAllRead = onMarkAllNotificationsRead,
            onDismiss = { showNotificationsDialog = false }
        )
    }
}
