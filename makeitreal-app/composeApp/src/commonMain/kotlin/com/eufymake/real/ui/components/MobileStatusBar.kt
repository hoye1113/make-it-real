package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Wifi
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

@Composable
fun MobileStatusBar(
    time: String = "09:41",
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val notchBg = if (isDark) Color(0xFF262626) else Color(0xFF141414)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Time
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )

        // Center: Dynamic Island camera capsule
        Box(
            modifier = Modifier
                .width(76.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(notchBg),
            contentAlignment = Alignment.CenterEnd
        ) {
            // Camera lens dot
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
            )
        }

        // Right: Signal, Wi-Fi, Battery
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NetworkCell,
                contentDescription = "Cellular",
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi",
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            
            // Battery icon: pill with positive terminal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .border(1.dp, contentColor.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(1.dp))
                            .background(contentColor)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp))
                        .background(contentColor.copy(alpha = 0.8f))
                )
            }
        }
    }
}

@Composable
fun MobileHomeIndicator(
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f))
        )
    }
}
