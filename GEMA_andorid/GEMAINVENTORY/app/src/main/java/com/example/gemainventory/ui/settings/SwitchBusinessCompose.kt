package com.example.gemainventory.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R

@Composable
fun SwitchBusinessScreen(
    darkTheme: Boolean,
    activeBusinessId: String,
    businesses: List<Map<String, String>>,
    onBusinessSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val surfaceColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (darkTheme) Color.White else Color(0xFF1E293B)
    val subTextColor = if (darkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(surfaceColor.copy(alpha = 0.5f))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Mis Negocios",
                    style = MaterialTheme.typography.headlineSmall,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(600, easing = EaseOutBack)
                )
            ) {
                Column {
                    Text(
                        text = "Selecciona la empresa con la que deseas trabajar ahora. Tus permisos y datos se actualizarán automáticamente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subTextColor,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(businesses) { index, business ->
                            val id = business["id"] ?: ""
                            val name = business["nombre"] ?: "Negocio"
                            val idRol = business["idRol"] ?: "2"
                            val isActive = id == activeBusinessId

                            val rolStr = when (idRol) {
                                "1", "1.0" -> "Dueño / Propietario"
                                else -> "Miembro del equipo"
                            }

                            BusinessItemCard(
                                name = name,
                                role = rolStr,
                                isActive = isActive,
                                index = index,
                                surfaceColor = surfaceColor,
                                textColor = textColor,
                                subTextColor = subTextColor,
                                onClick = { onBusinessSelected(id) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessItemCard(
    name: String,
    role: String,
    isActive: Boolean,
    index: Int,
    surfaceColor: Color,
    textColor: Color,
    subTextColor: Color,
    onClick: () -> Unit
) {
    var itemVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        itemVisible = true
    }

    AnimatedVisibility(
        visible = itemVisible,
        enter = fadeIn(animationSpec = tween(400, delayMillis = index * 50)) +
                slideInHorizontally(
                    initialOffsetX = { 50 },
                    animationSpec = tween(400, delayMillis = index * 50, easing = EaseOutBack)
                )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isActive) Modifier.background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        ) else Modifier
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minegocio),
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodySmall,
                            color = subTextColor
                        )
                    }

                    if (isActive) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_circle_24),
                            contentDescription = "Activo",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = subTextColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
