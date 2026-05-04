package com.example.gemainventory.ui.inventory

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemainventory.R
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailView(
    product: Product,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Prevent overlap with status bar
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .background(Color(0xFFEF4444).copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Image Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(surfaceColor.copy(alpha = 0.6f))
                        ) {
                            if (!product.imageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = getFullImageUrl(product.imageUrl),
                                    contentDescription = product.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Inventory2,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = Color.White.copy(alpha = 0.05f)
                                    )
                                }
                            }
                            
                            // Floating Stock Badge
                            StockStatusBadge(
                                quantity = product.quantity,
                                minStock = product.minStock,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Category and Name
                        Column {
                            Text(
                                text = product.category.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "SKU: ${product.sku}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Price and Quantity Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DetailCard(
                                modifier = Modifier.weight(1f),
                                title = "Precio Venta",
                                value = currencyFormat.format(product.salePrice),
                                icon = Icons.Default.Payments,
                                accentColor = Color(0xFF10B981),
                                surfaceColor = surfaceColor
                            )
                            DetailCard(
                                modifier = Modifier.weight(1f),
                                title = "En Stock",
                                value = "${product.quantity} uds",
                                icon = Icons.Default.Inventory,
                                accentColor = accentColor,
                                surfaceColor = surfaceColor
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Description Section
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = product.description ?: "Sin descripción disponible para este producto.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(20.dp),
                                lineHeight = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Technical Details
                        Text(
                            text = "Detalles Técnicos",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow(Icons.Default.Store, "Almacén", product.warehouseName ?: "No especificado")
                        DetailRow(Icons.Default.Category, "Categoría", product.category)
                        DetailRow(Icons.Default.TrendingDown, "Umbral de Alerta", "${product.minStock} unidades")
                        DetailRow(Icons.Default.Update, "Actualizado", formatTimestamp(product.updatedAt))

                        Spacer(modifier = Modifier.height(140.dp)) // Nav bar padding
                    }
                }
            }
        }
    }
}

@Composable
fun StockStatusBadge(quantity: Int, minStock: Int, modifier: Modifier = Modifier) {
    val (color, text) = when {
        quantity == 0 -> Color(0xFFEF4444) to "AGOTADO"
        quantity <= minStock -> Color(0xFFF59E0B) to "BAJO STOCK"
        else -> Color(0xFF10B981) to "DISPONIBLE"
    }

    Surface(
        modifier = modifier,
        color = Color(0xFF1E293B).copy(alpha = 0.9f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    surfaceColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = accentColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White.copy(alpha = 0.6f))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun formatTimestamp(timestamp: String?): String {
    if (timestamp.isNullOrBlank() || timestamp == "S/N" || timestamp == "Hace un momento") {
        return timestamp ?: "Hace un momento"
    }
    return try {
        // Formato ISO: 2026-05-02T22:15:03.422036
        // Handle both "T" and space separators
        val clean = timestamp.substringBefore(".")
        val separator = if (clean.contains("T")) "T" else " "
        val parts = clean.split(separator)
        if (parts.size == 2) {
            val dateParts = parts[0].split("-") // YYYY-MM-DD
            val timeParts = parts[1].split(":") // HH:mm:ss
            if (dateParts.size == 3 && timeParts.size >= 2) {
                "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
            } else timestamp
        } else timestamp
    } catch (e: Exception) {
        timestamp
    }
}
