package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.Substrate
import com.eufymake.real.ui.theme.BrandAccent
import com.eufymake.real.ui.theme.TextPrimary
import com.eufymake.real.ui.theme.TextSecondary

@Composable
fun SubstrateCard(
    substrate: Substrate,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BrandAccent else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) Color(0xFFEFF6FF) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) BrandAccent.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (substrate.iconName) {
                    "palette" -> Icons.Default.Palette
                    "document_scanner" -> Icons.Default.DocumentScanner
                    "aspect_ratio" -> Icons.Default.AspectRatio
                    "layers" -> Icons.Default.Layers
                    "stars" -> Icons.Default.Stars
                    "circle" -> Icons.Default.Circle
                    "credit_card" -> Icons.Default.CreditCard
                    "crop_portrait" -> Icons.Default.CropPortrait
                    "local_cafe" -> Icons.Default.LocalCafe
                    else -> Icons.Default.Category
                }
                Icon(
                    imageVector = icon,
                    contentDescription = substrate.name,
                    tint = if (isSelected) BrandAccent else Color(0xFF475569),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = substrate.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (substrate.supportRotary) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEDD5))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ROTARY 360",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${substrate.widthMm.toInt()} × ${substrate.heightMm.toInt()} mm • ${substrate.description}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
