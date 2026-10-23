package com.eufymake.real.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eufymake.real.model.InkChannel

@Composable
fun InkBar(
    channel: InkChannel,
    modifier: Modifier = Modifier
) {
    val barColor = Color(channel.hexColor)
    val fraction = (channel.percentage / 100f).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Percentage text
        Text(
            text = "${channel.percentage.toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Ink tube container
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFF1F5F9))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Channel code badge (C, M, Y, K, W, G)
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = channel.code,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
