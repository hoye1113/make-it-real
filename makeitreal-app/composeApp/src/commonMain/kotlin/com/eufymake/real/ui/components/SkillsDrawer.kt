package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eufymake.real.i18n.LocalStrings
import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.LayerType
import com.eufymake.real.ui.theme.BrandGreen

data class SkillItemMeta(
    val title: String,
    val subtitle: String,
    val depth: Float,
    val colors: List<Color>
)

@Composable
fun SkillsDrawer(
    onDismiss: () -> Unit,
    onApplySkill: (String, Float) -> Unit
) {
    val strings = LocalStrings.current

    val skills = listOf(
        SkillItemMeta(strings.dialog.skillTokyoNeon, "3D Tokyo Neon Typography", 2.5f, listOf(Color(0xFFE11D48), Color(0xFFFB7185))),
        SkillItemMeta(strings.dialog.skillVintageComic, "American Comic Book Style", 1.8f, listOf(Color(0xFFEA580C), Color(0xFFFBBF24))),
        SkillItemMeta(strings.dialog.skillWoodCarving, "Tactile Wooden Relief Texture", 3.0f, listOf(Color(0xFF78350F), Color(0xFFB45309))),
        SkillItemMeta(strings.dialog.skillStainedGlass, "Stained Glass 3D Varnish", 2.0f, listOf(Color(0xFF0284C7), Color(0xFF38BDF8))),
        SkillItemMeta(strings.dialog.skillClayMiniature, "Plasticine Clay 3D Miniature", 2.2f, listOf(Color(0xFF059669), Color(0xFF34D399)))
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Close and Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.dialog.skillsTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Horizontal Carousel of Big Style Preset Cards
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(skills) { skill ->
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.verticalGradient(skill.colors))
                                .clickable {
                                    onApplySkill(skill.title, skill.depth)
                                    onDismiss()
                                }
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Depth badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = strings.dialog.skillDepth(skill.depth),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = skill.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = skill.subtitle,
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 2
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
