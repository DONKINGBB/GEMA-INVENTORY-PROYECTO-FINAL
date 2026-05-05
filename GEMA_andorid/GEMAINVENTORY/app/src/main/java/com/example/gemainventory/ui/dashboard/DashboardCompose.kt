package com.example.gemainventory.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import com.example.gemainventory.model.NotificationItem
import com.example.gemainventory.model.NotificationType
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardView(
    inventoryValue: Double,
    pendingOrders: Int,
    lowStockCount: Int,
    monthProfit: Double,
    stockAlerts: List<StockAlert>,
    recentActivity: List<NotificationItem>,
    onNotificationsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onActivityItemClick: (NotificationItem) -> Unit,
    userName: String = "",
    userRol: Int = 1
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                DashboardTopBar(onNotificationsClick, glassColor, userName)
                Spacer(modifier = Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Column {
                        DashboardSectionHeader("Resumen Rápido", accentColor)

                        // --- Lógica de visibilidad de tarjetas por rol ---
                        val showInventoryValue = userRol in listOf(1, 2, 3, 6)
                        val showOrders = userRol in listOf(1, 2, 3, 4, 5)
                        val showLowStock = userRol in listOf(1, 2, 3, 4, 6) // Todos menos repartidor suelen ver stock
                        val showProfit = userRol in listOf(1, 2)

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Fila 1
                            if (showInventoryValue || showOrders) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (showInventoryValue) {
                                        SummaryCard(
                                            title = "Valor Inventario",
                                            value = formatCurrency(inventoryValue),
                                            icon = Icons.Default.AccountBalanceWallet,
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFF10B981),
                                            glassColor = glassColor,
                                            index = 0
                                        )
                                    } else if (showOrders && !showLowStock) { // Solo si no hay fila 2
                                         Spacer(modifier = Modifier.weight(1f))
                                    }
                                    
                                    if (showOrders) {
                                        SummaryCard(
                                            title = "Pedidos",
                                            value = pendingOrders.toString(),
                                            icon = Icons.Default.ShoppingCart,
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFF3B82F6),
                                            glassColor = glassColor,
                                            index = 1
                                        )
                                    } else if (showInventoryValue) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            // Fila 2
                            if (showLowStock || showProfit) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (showLowStock) {
                                        SummaryCard(
                                            title = "Bajo Stock",
                                            value = lowStockCount.toString(),
                                            icon = Icons.Default.Warning,
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFFF59E0B),
                                            glassColor = glassColor,
                                            index = 2
                                        )
                                    } else if (showProfit) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }

                                    if (showProfit) {
                                        SummaryCard(
                                            title = "Beneficio (Mes)",
                                            value = formatCurrency(monthProfit),
                                            icon = Icons.Default.TrendingUp,
                                            modifier = Modifier.weight(1f),
                                            accentColor = if (monthProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                                            valueColor = when {
                                                monthProfit > 0 -> Color(0xFF10B981)
                                                monthProfit < 0 -> Color(0xFFEF4444)
                                                else -> Color.White
                                            },
                                            glassColor = glassColor,
                                            index = 3
                                        )
                                    } else if (showLowStock) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (stockAlerts.isNotEmpty()) {
                            StockAlertCard(stockAlerts, onInventoryClick, surfaceColor)
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        DashboardSectionHeader("Actividad Reciente", accentColor)

                        if (recentActivity.isEmpty()) {
                            EmptyActivityCard(surfaceColor)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                recentActivity.forEachIndexed { index, item ->
                                    ActivityItem(item, surfaceColor, index, onActivityItemClick)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(130.dp)) // Extra padding for Nav Bar
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar(onNotificationsClick: () -> Unit, glassColor: Color, userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Bienvenido de nuevo,",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(glassColor)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentColor: Color,
    valueColor: Color = Color.White,
    glassColor: Color,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + scaleIn(tween(500), initialScale = 0.9f),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StockAlertCard(
    alerts: List<StockAlert>,
    onInventoryClick: () -> Unit,
    surfaceColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Alerta de Stock",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            alerts.take(3).forEach { alert ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = alert.productName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Quedan ${alert.quantity} unidades",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                    
                    val statusColor = if (alert.isOutOfStock) Color(0xFFEF4444) else Color(0xFFF59E0B)
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (alert.isOutOfStock) "AGOTADO" else "BAJO",
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onInventoryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.2f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Revisar Inventario", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ActivityItem(
    item: NotificationItem,
    surfaceColor: Color,
    index: Int,
    onClick: (NotificationItem) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        visible = true
    }

    val accentColor = if (item.type == NotificationType.ORDER) Color(0xFF10B981) else Color(0xFF3B82F6)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInHorizontally(tween(500), initialOffsetX = { 50 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(item) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f),
                        maxLines = 1
                    )
                }
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.2f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DashboardSectionHeader(title: String, color: Color) {
    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 16.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyActivityCard(surfaceColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Text(
            "Sin actividad reciente",
            modifier = Modifier.padding(24.dp),
            color = Color.White.copy(alpha = 0.3f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(value)
}

data class StockAlert(
    val productName: String,
    val quantity: Int,
    val isOutOfStock: Boolean
)
