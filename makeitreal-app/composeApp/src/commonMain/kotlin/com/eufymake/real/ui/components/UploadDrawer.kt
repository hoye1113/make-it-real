package com.eufymake.real.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType
import com.eufymake.real.ui.theme.BrandBlack
import com.eufymake.real.ui.theme.BrandGreen
import com.eufymake.real.ui.util.openImageFilePicker
import com.eufymake.real.ui.util.rememberAppImage

data class UploadSamplePhoto(
    val title: String,
    val imageName: String,
    val depth: Float,
    val fallbackColor: Color
)

@Composable
fun UploadDrawer(
    onDismiss: () -> Unit,
    onSelectImage: (CanvasLayer) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val samplePhotos = listOf(
        UploadSamplePhoto("婚礼手绘纪念画", "work_1.webp", 2.2f, Color(0xFFD4A373)),
        UploadSamplePhoto("伴娘邀请卡", "work_2.webp", 1.8f, Color(0xFFDDA15E)),
        UploadSamplePhoto("暗黑恐怖油画", "work_3.webp", 2.5f, Color(0xFFBC6C25)),
        UploadSamplePhoto("舞者火漆印章", "work_4.webp", 2.8f, Color(0xFF9B2226)),
        UploadSamplePhoto("深海章鱼马克杯", "work_5.webp", 2.0f, Color(0xFF6B2D5C)),
        UploadSamplePhoto("彩窗蝙蝠磁贴", "work_6.webp", 2.4f, Color(0xFF4A4E69)),
        UploadSamplePhoto("赛博骷髅全息贴纸", "work_7.webp", 1.5f, Color(0xFFee0979)),
        UploadSamplePhoto("万圣女巫猫饰品", "Halloween Witch Cat Decor Skill.webp", 2.6f, Color(0xFF2C3E50)),
        UploadSamplePhoto("月光女巫浮雕", "Moonlight Witch Ornament Skill.webp", 2.8f, Color(0xFF0F2027)),
        UploadSamplePhoto("植物骷髅艺术", "Floral Halloween Skeleton Skill.webp", 2.4f, Color(0xFF134E5E)),
        UploadSamplePhoto("哥特万圣节", "Whimsical Gothic Halloween Skill.webp", 2.2f, Color(0xFF4A4E69)),
        UploadSamplePhoto("万圣节家族门牌", "Halloween Family Door Hanger Skill.webp", 2.5f, Color(0xFFB92B27))
    )

    val localAlbumPhotos = listOf(
        UploadSamplePhoto("相册_20261023_01", "dinosaur_clock_relief.webp", 2.5f, Color(0xFF2C3E50)),
        UploadSamplePhoto("相册_夜光浮雕", "work_1.webp", 2.0f, Color(0xFFD4A373)),
        UploadSamplePhoto("截图_几何徽章", "work_2.webp", 1.8f, Color(0xFFDDA15E)),
        UploadSamplePhoto("下载_复古油画", "work_3.webp", 2.5f, Color(0xFFBC6C25)),
        UploadSamplePhoto("自拍_艺术转印", "work_4.webp", 2.8f, Color(0xFF9B2226)),
        UploadSamplePhoto("微距_纹理细节", "work_5.webp", 2.0f, Color(0xFF6B2D5C))
    )

    val displayPhotos = if (selectedTab == 0) samplePhotos else localAlbumPhotos

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 16.dp
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

                // Top Tabs: 素材库 vs 本地导入
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
                                text = "社区素材库",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedTab == 0) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
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
                                text = "本地相册",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color(0xFF1A1A1A) else Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedTab == 1) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color(0xFF1A1A1A))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action button: Import from Computer File System
                Button(
                    onClick = {
                        val path = openImageFilePicker()
                        if (path != null) {
                            val fileName = path.substringAfterLast('\\').substringAfterLast('/')
                            onSelectImage(
                                CanvasLayer(
                                    id = "layer_img_${System.currentTimeMillis()}",
                                    name = fileName,
                                    type = LayerType.IMAGE,
                                    imageUrl = path,
                                    widthMm = 70f,
                                    heightMm = 70f,
                                    reliefDepthMm = 2.0f
                                )
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = "Import",
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "从电脑导入图片文件 (PNG / JPG / WebP)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photos 3-column Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(displayPhotos) { photo ->
                        val thumbImg = rememberAppImage(photo.imageName)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(photo.fallbackColor)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                .clickable {
                                    onSelectImage(
                                        CanvasLayer(
                                            id = "layer_img_${System.currentTimeMillis()}",
                                            name = photo.title,
                                            type = LayerType.IMAGE,
                                            imageUrl = photo.imageName,
                                            widthMm = 65f,
                                            heightMm = 65f,
                                            reliefDepthMm = photo.depth,
                                            fillColor = photo.fallbackColor.value.toLong()
                                        )
                                    )
                                    onDismiss()
                                }
                        ) {
                            if (thumbImg != null) {
                                Image(
                                    bitmap = thumbImg,
                                    contentDescription = photo.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Dark scrim on bottom with title
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                        )
                                    )
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = photo.title,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
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
}
}

