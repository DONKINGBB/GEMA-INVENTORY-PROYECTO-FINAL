package com.example.gemainventory.ui.orders

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import com.example.gemainventory.ui.components.GemaFloatingActionButton
import com.example.gemainventory.model.PedidoDto
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersView(
    orders: List<PedidoDto>,
    onAddOrderClick: () -> Unit,
    onDeliverOrder: (String) -> Unit,
    onDeleteOrder: (String) -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val glassColor = Color.White.copy(alpha = 0.05f)

    var visible by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<PedidoDto?>(null) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            GemaFloatingActionButton(
                onClick = onAddOrderClick,
                accentColor = accentColor,
                modifier = Modifier.padding(bottom = 100.dp)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                OrdersHeader(accentColor, glassColor)

                if (orders.isEmpty()) {
                    EmptyOrdersView(onAddOrderClick)
                } else {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(1000))
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 180.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderCard(order, { 
                                    selectedOrder = it
                                    showOptionsSheet = true
                                }, glassColor, accentColor)
                            }
                        }
                    }
                }
            }

            if (showOptionsSheet && selectedOrder != null) {
                ModalBottomSheet(
                    onDismissRequest = { showOptionsSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF1E293B),
                    scrimColor = Color.Black.copy(alpha = 0.6f),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
                ) {
                    OrderOptionsContent(
                        order = selectedOrder!!,
                        onDeliver = { 
                            selectedOrder?.id?.let { onDeliverOrder(it) }
                            showOptionsSheet = false
                        },
                        onDelete = { 
                            selectedOrder?.id?.let { onDeleteOrder(it) }
                            showOptionsSheet = false
                        },
                        onViewProducts = { 
                            showDetailsDialog = true
                            showOptionsSheet = false
                        },
                        accentColor = accentColor
                    )
                }
            }

            if (showDetailsDialog && selectedOrder != null) {
                ProductDetailsDialog(
                    order = selectedOrder!!,
                    onDismiss = { showDetailsDialog = false },
                    accentColor = accentColor
                )
            }
        }
    }
}

@Composable
fun ProductDetailsDialog(
    order: PedidoDto,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(24.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Detalles del Pedido",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(order.detalles ?: emptyList()) { detail ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(detail.nombreProducto ?: "Producto", color = Color.White, fontWeight = FontWeight.Medium)
                                Text(
                                    "${detail.cantidad} x ${currencyFormat.format(detail.precioUnitario)}",
                                    color = Color.White.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                currencyFormat.format(detail.subtotal),
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", color = Color.White.copy(alpha = 0.7f))
                    val total = order.detalles?.sumOf { it.subtotal } ?: 0.0
                    Text(
                        currencyFormat.format(total),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderOptionsContent(
    order: PedidoDto,
    onDeliver: () -> Unit,
    onDelete: () -> Unit,
    onViewProducts: () -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = order.nombre ?: "Pedido #${order.id?.take(8) ?: "S/N"}",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        OptionItem(
            icon = Icons.Default.List,
            title = "Ver Productos",
            subtitle = "Lista detallada de artículos",
            color = Color.White,
            onClick = onViewProducts
        )

        if (order.idEstado != 2) {
            Spacer(modifier = Modifier.height(16.dp))
            OptionItem(
                icon = Icons.Default.CheckCircle,
                title = "Marcar como Entregado",
                subtitle = "Actualizar estado y stock",
                color = Color(0xFF10B981),
                onClick = onDeliver
            )

            Spacer(modifier = Modifier.height(16.dp))
            OptionItem(
                icon = Icons.Default.Delete,
                title = "Eliminar Pedido",
                subtitle = "Esta acción no se puede deshacer",
                color = Color(0xFFEF4444),
                onClick = onDelete
            )
        }
    }
}

@Composable
fun OptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun OrdersHeader(accentColor: Color, glassColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Gestión de Ventas",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(glassColor)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: PedidoDto,
    onOrderClick: (PedidoDto) -> Unit,
    glassColor: Color,
    accentColor: Color
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val total = order.detalles?.sumOf { it.cantidad * it.precioUnitario } ?: 0.0
    val statusColor = when (order.idEstado) {
        1 -> Color(0xFFF59E0B) // Pending - Amber
        2 -> Color(0xFF10B981) // Delivered - Emerald
        else -> Color(0xFF6B7280) // Other - Gray
    }
    val statusText = when (order.idEstado) {
        1 -> "Pendiente"
        2 -> "Entregado"
        else -> "Desconocido"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick(order) }
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = accentColor.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_orders),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.nombre ?: "Pedido #${order.id?.take(6) ?: "---"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = order.fechaLimite ?: order.fechaPedido ?: "Sin fecha",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormat.format(total),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersView(onAddOrderClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White.copy(alpha = 0.03f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_orders),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White.copy(alpha = 0.2f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No hay pedidos",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Comienza creando tu primer pedido de venta.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddOrderClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(56.dp).fillMaxWidth(0.7f)
        ) {
            Text("Crear Pedido", fontWeight = FontWeight.Bold)
        }
    }
}
