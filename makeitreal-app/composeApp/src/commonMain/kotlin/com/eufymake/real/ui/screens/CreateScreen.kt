package com.eufymake.real.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.Substrate
import com.eufymake.real.ui.components.SubstrateConfigSheet
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.util.rememberAppImage

data class InspirationCardMeta(
    val title: String,
    val author: String,
    val likes: Int,
    val imageName: String,
    val substrateId: String,
    val category: String
)

@Composable
fun CreateScreen(
    points: Int,
    onOpenPointsMall: () -> Unit,
    onSelectSubstrate: (Substrate) -> Unit,
    onOpenDrafts: () -> Unit,
    onSelectInspirationItem: ((title: String, imageName: String, substrateId: String) -> Unit)? = null,
    deviceName: String = "E1 UV-50",
    isDeviceConnected: Boolean = true,
    onOpenDevices: () -> Unit = {}
) {
    var substrateToConfigure by remember { mutableStateOf<Substrate?>(null) }
    var selectedFilterIndex by remember { mutableStateOf(0) }

    val filterPills = listOf("全部", "3D纹理", "节日快乐", "个性化礼物", "艺术家日常")

    val allInspirationItems = listOf(
        InspirationCardMeta("Wedding Hand-drawn Commemorative", "Sean", 1530, "work_1.webp", "sheet", "个性化礼物"),
        InspirationCardMeta("Bridesmaid Invitation Card", "Katniss_e", 724, "work_2.webp", "card", "个性化礼物"),
        InspirationCardMeta("Horror Cons 6x8 Canvas", "Jayleecustoms", 1089, "work_3.webp", "canvas", "3D纹理"),
        InspirationCardMeta("Dancer Wax Seal Tactile Stamp", "Katniss_e", 412, "work_4.webp", "coaster", "3D纹理"),
        InspirationCardMeta("Octopus Deep Ocean 3D Mug", "AlexMaker", 890, "work_5.webp", "cup", "3D纹理"),
        InspirationCardMeta("Stained Glass Prism Magnet", "GothicArt", 610, "work_6.webp", "magnet", "艺术家日常"),
        InspirationCardMeta("Moonlight Witch Ornament Skill", "WhiskersDesign", 1205, "Moonlight Witch Ornament Skill.webp", "coaster", "节日快乐"),
        InspirationCardMeta("Floral Halloween Skeleton Relief", "Katniss_e", 842, "Floral Halloween Skeleton Skill.webp", "sheet", "艺术家日常")
    )

    val displayedItems = if (selectedFilterIndex == 0) {
        allInspirationItems
    } else {
        val selectedCat = filterPills[selectedFilterIndex]
        allInspirationItems.filter { it.category == selectedCat }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Row (Frame 0010)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "创作",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )

                    // Top-right device status pill: Real status + clickable to switch to Devices tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .clickable(onClick = onOpenDevices)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isDeviceConnected) BrandGreen else Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDeviceConnected) "$deviceName (就绪)" else "没有设备",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }
            }

            // 2. "自由创作" Hero Card (Frame 0010)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectSubstrate(Substrate.DEFAULT) },
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "自由创作",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "从打印板上开始自由起稿",
                                fontSize = 12.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }

                        // Perspective angled print bed canvas graphic (Frame 0010)
                        Box(
                            modifier = Modifier
                                .size(width = 65.dp, height = 75.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 48.dp, height = 58.dp)
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            // 3. "选择基材" 10-Item Grid Card (Frame 0010)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        // Card Header
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "选择基材",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = "查看全部",
                                    fontSize = 12.sp,
                                    color = BrandGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable { substrateToConfigure = Substrate.DEFAULT }
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "从选择一个基材开始，在基材上做设计",
                                fontSize = 11.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 2 Rows of 5 Substrates (Frame 0010 exact order)
                        val substrates = Substrate.ALL
                        val row1 = substrates.take(5)
                        val row2 = substrates.drop(5).take(5)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row1.forEach { sub ->
                                SubstrateIconItem(
                                    substrate = sub,
                                    onClick = { substrateToConfigure = sub }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row2.forEach { sub ->
                                SubstrateIconItem(
                                    substrate = sub,
                                    onClick = { substrateToConfigure = sub }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Horizontal Category Filter Pills (Frame 0010)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterPills.size) { idx ->
                        val isSelected = selectedFilterIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF1A1A1A) else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF1A1A1A) else Color(0xFFE5E7EB),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedFilterIndex = idx }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filterPills[idx],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1A1A1A)
                            )
                        }
                    }
                }
            }

            // 5. Community Inspirations Masonry Cards with Authentic Artwork
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    displayedItems.chunked(2).forEach { rowPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowPair.forEach { item ->
                                val cardImg = rememberAppImage(item.imageName)

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            onSelectInspirationItem?.invoke(item.title, item.imageName, item.substrateId) ?: run {
                                                val targetSub = Substrate.ALL.firstOrNull { it.id == item.substrateId } ?: Substrate.DEFAULT
                                                onSelectSubstrate(targetSub)
                                            }
                                        },
                                    color = Color.White,
                                    shadowElevation = 1.dp
                                ) {
                                    Column {
                                        // Card Image with authentic loaded bitmap & top-left layer badge
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(135.dp)
                                                .background(Color(0xFFE2E8F0))
                                        ) {
                                            if (cardImg != null) {
                                                Image(
                                                    bitmap = cardImg,
                                                    contentDescription = item.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            // Top-left layer badge
                                            Box(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .size(22.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color.Black.copy(alpha = 0.55f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Layers,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            // Bottom-right "做同款" pill
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(6.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color.Black.copy(alpha = 0.65f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "做同款",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        // Card Details
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = item.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1A1A1A),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFCBD5E1))
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = item.author,
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF8E8E93),
                                                        maxLines = 1
                                                    )
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.FavoriteBorder,
                                                        contentDescription = "Likes",
                                                        tint = Color(0xFF8E8E93),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = item.likes.toString(),
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF8E8E93)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Substrate Configuration Sheet (Frame 0140)
        substrateToConfigure?.let { sub ->
            SubstrateConfigSheet(
                substrate = sub,
                onDismiss = { substrateToConfigure = null },
                onConfirmCreate = { configuredSubstrate ->
                    substrateToConfigure = null
                    onSelectSubstrate(configuredSubstrate)
                }
            )
        }
    }
}

@Composable
private fun SubstrateIconItem(
    substrate: Substrate,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(58.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF7F8FA))
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val iconVector = when (substrate.iconName) {
                "document_scanner" -> Icons.Default.DocumentScanner
                "edit" -> Icons.Default.Edit
                "layers" -> Icons.Default.Layers
                "palette" -> Icons.Default.Palette
                "stars" -> Icons.Default.Stars
                "circle" -> Icons.Default.Circle
                "crop_portrait" -> Icons.Default.CropPortrait
                "credit_card" -> Icons.Default.CreditCard
                "local_cafe" -> Icons.Default.LocalCafe
                "smartphone" -> Icons.Default.PhoneIphone
                else -> Icons.Default.Category
            }

            Icon(
                imageVector = iconVector,
                contentDescription = substrate.nameZh,
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = substrate.nameZh,
            fontSize = 11.sp,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
