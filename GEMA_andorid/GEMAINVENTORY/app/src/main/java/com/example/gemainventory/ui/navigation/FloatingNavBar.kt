package com.example.gemainventory.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R

data class NavItem(
    val id: Int,
    val title: String,
    val iconRes: Int
)

@Composable
fun FloatingNavBar(
    selectedId: Int,
    visibleItems: List<NavItem>,
    onItemClick: (Int) -> Unit
) {
    val backgroundColor = Color(0xFF1E293B).copy(alpha = 0.95f)
    val accentColor = Color(0xFF3B82F6)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            visibleItems.forEach { item ->
                val isSelected = item.id == selectedId
                
                NavBarItem(
                    item = item,
                    isSelected = isSelected,
                    accentColor = accentColor,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val color by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.4f),
        label = "color"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent,
        label = "bgColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.title,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
