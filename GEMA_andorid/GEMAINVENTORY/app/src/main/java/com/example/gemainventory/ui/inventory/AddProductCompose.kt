package com.example.gemainventory.ui.inventory

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemainventory.model.AlmacenDto
import com.example.gemainventory.model.CategoriaDto
import com.example.gemainventory.model.ProductoDto
import com.example.gemainventory.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    isEditing: Boolean,
    initialName: String = "",
    initialSku: String = "",
    initialQuantity: String = "0",
    initialCategory: String = "",
    initialWarehouseId: Int? = null,
    initialPriceBuy: String = "0.0",
    initialPriceSell: String = "0.0",
    initialDesc: String = "",
    initialImageUrl: String? = null,
    categories: List<CategoriaDto>,
    warehouses: List<AlmacenDto>,
    imageUri: Uri?,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onScan: (Int) -> Unit,
    onAddCategory: () -> Unit,
    onAddWarehouse: () -> Unit,
    onSave: (ProductoDto) -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)

    var name by remember { mutableStateOf(initialName) }
    var sku by remember { mutableStateOf(initialSku) }
    var quantity by remember { mutableStateOf(initialQuantity) }
    var priceBuy by remember { mutableStateOf(initialPriceBuy) }
    var priceSell by remember { mutableStateOf(initialPriceSell) }
    var desc by remember { mutableStateOf(initialDesc) }

    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // --- BUG FIX: Use a resolved warehouse ID that updates when warehouses list arrives ---
    var selectedWarehouseId by remember(initialWarehouseId) { mutableStateOf(initialWarehouseId) }
    var warehouseName by remember(selectedWarehouseId, warehouses) {
        mutableStateOf(
            warehouses.find { it.idAlmacen == selectedWarehouseId }?.nombre ?: "Selecciona un almacén"
        )
    }

    // Sync text fields when async data arrives
    LaunchedEffect(initialName) { if (initialName.isNotEmpty()) name = initialName }
    LaunchedEffect(initialSku) { if (initialSku.isNotEmpty()) sku = initialSku }
    LaunchedEffect(initialQuantity) { quantity = initialQuantity }
    LaunchedEffect(initialCategory) { if (initialCategory.isNotEmpty()) selectedCategory = initialCategory }
    LaunchedEffect(initialPriceBuy) { priceBuy = initialPriceBuy }
    LaunchedEffect(initialPriceSell) { priceSell = initialPriceSell }
    LaunchedEffect(initialDesc) { if (initialDesc.isNotEmpty()) desc = initialDesc }



    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GemaFormHeader(
                title = if (isEditing) "Editar Producto" else "Nuevo Producto",
                subtitle = if (isEditing) "Modifica los detalles del producto" else null,
                onBack = onBack,
                onDelete = if (isEditing) onDelete else null
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                    )
                )
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                Column {

                    // ── IMAGE SECTION ─────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(surfaceColor)
                            .clickable { onPickImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null || initialImageUrl != null) {
                            AsyncImage(
                                model = imageUri ?: initialImageUrl,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Remove image button
                            IconButton(
                                onClick = onRemoveImage,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(36.dp)
                                    .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Borrar imagen", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(accentColor.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Toca para subir imagen", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── SECTION: INFORMACIÓN GENERAL ──────────────────────
                    GemaSectionTitle(title = "Información General", icon = Icons.Default.Inventory2, color = accentColor)
                    
                    GemaTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del producto",
                        icon = Icons.Default.Inventory
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GemaTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = "SKU / Código de barras",
                        icon = Icons.Default.QrCode,
                        trailingContent = {
                            IconButton(onClick = { onScan(2) }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear SKU", tint = accentColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GemaTextField(
                            value = priceBuy,
                            onValueChange = { priceBuy = it },
                            label = "Precio Costo",
                            icon = Icons.Default.AttachMoney,
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        GemaTextField(
                            value = priceSell,
                            onValueChange = { priceSell = it },
                            label = "Precio Venta",
                            icon = Icons.Default.Payments,
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GemaTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = "Cantidad en Stock",
                        icon = Icons.Default.AddBox,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── SECTION: CLASIFICACIÓN ────────────────────────────
                    GemaSectionTitle(title = "Clasificación y Ubicación", icon = Icons.Default.Category, color = accentColor)

                    GemaEntitySelector(
                        label = "Categoría",
                        selectedName = selectedCategory.ifEmpty { "Selecciona una categoría" },
                        options = categories.map { it.nombre },
                        onOptionSelected = { selectedCategory = it },
                        onAddNew = onAddCategory,
                        icon = Icons.Default.Category
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GemaEntitySelector(
                        label = "Almacén",
                        selectedName = warehouseName,
                        options = warehouses.map { it.nombre },
                        onOptionSelected = { selectedName ->
                            val w = warehouses.find { it.nombre == selectedName }
                            if (w != null) {
                                selectedWarehouseId = w.idAlmacen
                                warehouseName = w.nombre
                            }
                        },
                        onAddNew = onAddWarehouse,
                        icon = Icons.Default.Warehouse
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── SECTION: DESCRIPCIÓN ─────────────────────────────
                    GemaSectionTitle(title = "Descripción", icon = Icons.Default.Description, color = accentColor)
                    
                    GemaTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = "Notas adicionales...",
                        icon = Icons.Default.Notes,
                        singleLine = false,
                        modifier = Modifier.heightIn(min = 120.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── SAVE BUTTON (al fondo del scroll) ──────────────────
                    GemaButton(
                        text = if (isEditing) "GUARDAR CAMBIOS" else "CREAR PRODUCTO",
                        onClick = {
                            val dto = ProductoDto(
                                nombre = name.trim(),
                                sku = sku.trim(),
                                cantidad = quantity.toIntOrNull() ?: 0,
                                categoria = selectedCategory,
                                precioCompra = priceBuy.toDoubleOrNull() ?: 0.0,
                                precioVenta = priceSell.toDoubleOrNull() ?: 0.0,
                                descripcion = desc.trim(),
                                stockMinimo = 5,
                                usuarioId = "",
                                idAlmacen = selectedWarehouseId
                            )
                            onSave(dto)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        accentColor = accentColor
                    )

                    // Espacio para que el botón no quede detrás del nav bar (~112dp)
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        } // end scrollable Column
        } // end outer Box
    }
}
