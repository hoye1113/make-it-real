package com.eufymake.real.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.model.Announcement
import com.eufymake.real.model.Project
import com.eufymake.real.ui.components.ProjectCard
import com.eufymake.real.ui.theme.BrandGreen

@Composable
fun InspirationsScreen(
    announcements: List<Announcement>,
    projects: List<Project>,
    onLikeProject: (String) -> Unit,
    onMakeSame: (Project) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("全部") }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("全部", "3D纹理", "节日快乐", "个性化礼物", "艺术家日常")

    val filteredProjects = projects.filter { proj ->
        searchQuery.isEmpty() || proj.title.contains(searchQuery, ignoreCase = true) ||
                proj.description.contains(searchQuery, ignoreCase = true)
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
                        text = "灵感",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
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
                            if (searchQuery.isEmpty()) {
                                Text("搜索设计、基材、创作者...", fontSize = 13.sp, color = Color(0xFF8E8E93))
                            }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Color(0xFF1A1A1A)),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
            }
        }

        // Projects 2-column Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredProjects.chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowPair.forEach { proj ->
                            ProjectCard(
                                project = proj,
                                onLikeClick = { onLikeProject(proj.id) },
                                onClick = { selectedProjectId = proj.id },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Inspiration Detail Dialog (Frame 0015 & Frame 0020)
    val detail = projects.firstOrNull { it.id == selectedProjectId }
    if (detail != null) {
        Dialog(onDismissRequest = { selectedProjectId = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { selectedProjectId = null }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCBD5E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    detail.authorName.take(1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                detail.authorName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = BrandGreen, modifier = Modifier.size(16.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // "关注" button with green outline
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BrandGreen, RoundedCornerShape(12.dp))
                                    .clickable { }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("关注", fontSize = 12.sp, color = BrandGreen, fontWeight = FontWeight.SemiBold)
                            }

                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF1A1A1A))
                        }
                    }

                    // 2. Image Carousel with "3/8" counter pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(detail.coverGradientStart), Color(detail.coverGradientEnd)))
                            )
                    ) {
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

                        // Bottom-right 3/8 pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text("3/8", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 3. Information Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = detail.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        // Stats: Heart, View, Print | Date
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFF8E8E93), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${detail.likesCount}", fontSize = 11.sp, color = Color(0xFF8E8E93))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF8E8E93), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("744", fontSize = 11.sp, color = Color(0xFF8E8E93))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF8E8E93), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${detail.forkCount}", fontSize = 11.sp, color = Color(0xFF8E8E93))
                            }
                            Text("|  2026-09-24", fontSize = 11.sp, color = Color(0xFF8E8E93))
                        }

                        // Breadcrumb tags: 家居生活 > 冰箱贴
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF2F3F5))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("家居生活", fontSize = 11.sp, color = Color(0xFF475569))
                            }
                            Text(">", fontSize = 11.sp, color = Color(0xFF8E8E93), modifier = Modifier.align(Alignment.CenterVertically))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF2F3F5))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(detail.substrateName, fontSize = 11.sp, color = Color(0xFF475569))
                            }
                        }

                        // Hardware card (Frame 0015): 设备: eufyMake UV Printer E1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF7F8FA))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("设备:", fontSize = 12.sp, color = Color(0xFF8E8E93))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("eufyMake UV Printer E1", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                            }
                        }
                    }

                    // 4. Sticky Bottom Action Bar (Frame 0015)
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
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onLikeProject(detail.id) }) {
                                    Icon(
                                        imageVector = if (detail.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (detail.isLiked) Color(0xFFEF4444) else Color(0xFF1A1A1A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("${detail.likesCount}", fontSize = 10.sp, color = Color(0xFF8E8E93))
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                                    Text("0", fontSize = 10.sp, color = Color(0xFF8E8E93))
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.StarBorder, contentDescription = "Favorite", tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                                    Text("7", fontSize = 10.sp, color = Color(0xFF8E8E93))
                                }

                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
                            }

                            // Green CTA Button: "定制" (Customize)
                            Button(
                                onClick = {
                                    selectedProjectId = null
                                    onMakeSame(detail)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(22.dp),
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "定制",
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
}
