package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType

@Composable
fun UploadDrawer(
    onDismiss: () -> Unit,
    onSelectImage: (CanvasLayer) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val samplePhotos = listOf(
        Triple("油泼拉面与牛肉", Color(0xFFE67E22), 2.0f),
        Triple("霸王龙立体时钟", Color(0xFF27AE60), 2.5f),
        Triple("万圣节南瓜派对", Color(0xFFD35400), 1.8f),
        Triple("猫咪头像刺绣", Color(0xFF8E44AD), 1.5f),
        Triple("赛博朋克机械臂", Color(0xFF2980B9), 2.2f),
        Triple("海浪与富士山浮雕", Color(0xFF16A085), 2.4f),
        Triple("复古烫金火漆印章", Color(0xFFC0392B), 3.0f),
        Triple("星空漫游宇航员", Color(0xFF2C3E50), 1.6f),
        Triple("手绘新娘伴娘卡", Color(0xFFE84393), 1.2f)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top drag bar indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFD1D5DB))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Top Tabs: 相机 vs 历史记录
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = 0 }
                        ) {
                            Text(
                                text = "相机",
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedTab == 0) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color(0xFF1A1A1A))
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedTab = 1 }
                        ) {
                            Text(
                                text = "历史记录",
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedTab == 1) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color(0xFF1A1A1A))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Album Dropdown row: "Recent >"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Photos 3-column Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(samplePhotos) { (name, color, depth) ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .clickable {
                                    onSelectImage(
                                        CanvasLayer(
                                            id = "layer_img_${System.currentTimeMillis()}",
                                            name = name,
                                            type = LayerType.IMAGE,
                                            widthMm = 65f,
                                            heightMm = 65f,
                                            reliefDepthMm = depth,
                                            fillColor = color.value.toLong()
                                        )
                                    )
                                    onDismiss()
                                }
                                .padding(6.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
