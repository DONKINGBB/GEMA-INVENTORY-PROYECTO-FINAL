package com.example.gemainventory.ui.inventory

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemainventory.R
import com.example.gemainventory.ui.components.GemaFloatingActionButton
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryView(
    products: List<Product>,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onFilterClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddProductClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            GemaFloatingActionButton(
                onClick = onAddProductClick,
                accentColor = accentColor,
                modifier = Modifier.padding(bottom = 120.dp)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                InventoryHeader(
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchToggle = onSearchToggle,
                    onFilterClick = onFilterClick,
                    accentColor = accentColor,
                    glassColor = glassColor
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Stock Alert Logic: 0 = Red, <= 5 = Yellow, >= 6 = Hidden
                        val criticalProducts = products.filter { it.quantity <= 5 }
                        
                        if (criticalProducts.isNotEmpty() && !isSearchActive) {
                            item {
                                ReorderAssistantCard(
                                    criticalProducts = criticalProducts,
                                    surfaceColor = surfaceColor
                                )
                            }
                        }

                        if (products.isEmpty()) {
                            item {
                                EmptyInventoryView(onAddProductClick)
                            }
                        } else {
                            items(products) { product ->
                                ProductItem(
                                    product = product,
                                    surfaceColor = surfaceColor,
                                    accentColor = accentColor,
                                    onClick = { onProductClick(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryHeader(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onFilterClick: () -> Unit,
    accentColor: Color,
    glassColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 24.dp)
    ) {
        AnimatedContent(
            targetState = isSearchActive,
            transitionSpec = {
                fadeIn(tween(400)) togetherWith fadeOut(tween(400))
            },
            label = "HeaderAnimation"
        ) { active ->
            if (active) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("¿Qué producto buscas?", color = Color.White.copy(alpha = 0.3f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accentColor) },
                    trailingIcon = {
                        IconButton(onClick = { onSearchToggle(false) }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White.copy(alpha = 0.3f))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.9f),
                        unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.7f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mi Almacén",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Inventario",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HeaderIcon(icon = Icons.Default.Search, onClick = { onSearchToggle(true) }, glassColor = glassColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        HeaderIcon(icon = Icons.Default.Tune, onClick = onFilterClick, glassColor = glassColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, glassColor: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun ReorderAssistantCard(
    criticalProducts: List<Product>,
    surfaceColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)) // Más transparente
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Alerta de Stock",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${criticalProducts.size} Críticos",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            criticalProducts.take(3).forEach { product ->
                val (color, statusText) = if (product.quantity == 0) {
                    Color(0xFFEF4444) to "AGOTADO"
                } else {
                    Color(0xFFF59E0B) to "BAJO STOCK"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        "${product.quantity} unidades",
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            if (criticalProducts.size > 3) {
                Text(
                    "Ver todos los ${criticalProducts.size} productos en riesgo",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { /* Ver todos logic */ }
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    surfaceColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)) // Glassy look
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with glass effect
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = getFullImageUrl(product.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.sku.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currencyFormat.format(product.salePrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            StockBadge(product)
        }
    }
}

@Composable
fun StockBadge(product: Product) {
    val (color, text) = when {
        product.quantity == 0 -> Color(0xFFEF4444) to "AGOTADO"
        product.quantity <= product.minStock -> Color(0xFFF59E0B) to "BAJO"
        else -> Color(0xFF10B981) to "OK"
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = product.quantity.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
        Surface(
            color = color.copy(alpha = 0.1f), // Más sutil
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EmptyInventoryView(onAddProductClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .blur(40.dp)
            )
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Sin existencias",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Tu inventario digital está listo.\nAgrega un producto para comenzar.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddProductClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 32.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Añadir Producto", fontWeight = FontWeight.Bold)
        }
    }
}

