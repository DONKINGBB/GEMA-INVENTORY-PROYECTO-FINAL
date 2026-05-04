package com.example.gemainventory.ui.orders

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.components.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gemainventory.R
import com.example.gemainventory.model.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    clients: List<ClienteDto>,
    warehouses: List<AlmacenDto>,
    availableProducts: List<ProductoSeleccionDto>,
    onBack: () -> Unit,
    onWarehouseSelected: (Int) -> Unit,
    onSave: (PedidoDto) -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)

    var orderName by remember { mutableStateOf("") }
    var orderDate by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<ClienteDto?>(null) }
    var selectedWarehouse by remember { mutableStateOf<AlmacenDto?>(null) }
    val cartItems = remember { mutableStateListOf<DetallePedidoDto>() }

    var showProductDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val total = cartItems.sumOf { it.cantidad * it.precioUnitario }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GemaFormHeader(
                title = "Nuevo Pedido",
                onBack = onBack
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
                    .padding(20.dp)
            ) {
                // Section 1: General Info
                GemaSectionTitle("Información General", Icons.Default.Info, accentColor)
                
                GemaTextField(
                    value = orderName,
                    onValueChange = { orderName = it },
                    label = "Nombre del Pedido (Opcional)",
                    icon = Icons.Default.Edit
                )

                Spacer(modifier = Modifier.height(12.dp))

                GemaTextField(
                    value = orderDate,
                    onValueChange = { },
                    label = "Fecha Límite",
                    icon = Icons.Default.DateRange,
                    readOnly = true,
                    onClick = {
                        DatePickerDialog(context, { _, y, m, d ->
                            calendar.set(y, m, d)
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            orderDate = sdf.format(calendar.time)
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Entities
                GemaSectionTitle("Asociación", Icons.Default.Person, accentColor)

                GemaEntitySelector(
                    label = "Cliente",
                    selectedName = selectedClient?.nombre ?: "Seleccionar Cliente",
                    options = clients.map { it.nombre },
                    onOptionSelected = { name -> 
                        selectedClient = clients.find { it.nombre == name }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                GemaEntitySelector(
                    label = "Almacén de Origen",
                    selectedName = selectedWarehouse?.nombre ?: "Seleccionar Almacén",
                    options = warehouses.map { it.nombre },
                    onOptionSelected = { name -> 
                        selectedWarehouse = warehouses.find { it.nombre == name }
                        selectedWarehouse?.let { 
                            onWarehouseSelected(it.idAlmacen)
                            cartItems.clear()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Cart
                GemaSectionTitle("Productos", Icons.Default.ShoppingCart, accentColor)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { 
                            if (selectedWarehouse == null) {
                                android.widget.Toast.makeText(context, "Selecciona un almacén primero", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                showProductDialog = true 
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(glassColor)
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tu carrito está vacío",
                            color = Color.White.copy(alpha = 0.3f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    cartItems.forEach { item ->
                        CartItemRow(item, glassColor, accentColor) {
                            cartItems.remove(item)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Total Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total del Pedido", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                        Text(
                            currencyFormat.format(total),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                GemaButton(
                    text = "Guardar Pedido",
                    onClick = {
                        if (selectedClient == null || selectedWarehouse == null || cartItems.isEmpty()) {
                            android.widget.Toast.makeText(context, "Faltan datos requeridos", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val pedido = PedidoDto(
                                idCliente = selectedClient?.idCliente ?: "",
                                idAlmacenOrigen = selectedWarehouse?.idAlmacen ?: 0,
                                detalles = cartItems.toList(),
                                nombre = if (orderName.isEmpty()) null else orderName,
                                fechaLimite = if (orderDate.isEmpty()) null else orderDate
                            )
                            onSave(pedido)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    accentColor = accentColor
                )
                
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }

    if (showProductDialog) {
        ProductSelectionDialog(
            products = availableProducts,
            onDismiss = { showProductDialog = false },
            accentColor = accentColor,
            onProductAdded = { product, quantity ->
                val existing = cartItems.find { it.idProducto == product.idProducto }
                if (existing != null) {
                    val index = cartItems.indexOf(existing)
                    cartItems[index] = existing.copy(cantidad = existing.cantidad + quantity)
                } else {
                    cartItems.add(
                        DetallePedidoDto(
                            idProducto = product.idProducto,
                            nombreProducto = product.nombre,
                            cantidad = quantity,
                            precioUnitario = product.precioVenta
                        )
                    )
                }
                showProductDialog = false
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: DetallePedidoDto,
    glassColor: Color,
    accentColor: Color,
    onRemove: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(glassColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.nombreProducto ?: "Producto", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "${item.cantidad} x ${currencyFormat.format(item.precioUnitario)}",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Text(
            currencyFormat.format(item.subtotal),
            color = accentColor,
            fontWeight = FontWeight.ExtraBold
        )
        
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444).copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionDialog(
    products: List<ProductoSeleccionDto>,
    onDismiss: () -> Unit,
    accentColor: Color,
    onProductAdded: (ProductoSeleccionDto, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<ProductoSeleccionDto?>(null) }
    var quantity by remember { mutableStateOf("1") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Agregar Producto",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GemaEntitySelector(
                    label = "Seleccionar Producto",
                    selectedName = selectedProduct?.nombre ?: "Producto...",
                    options = products.map { it.nombre },
                    onOptionSelected = { name -> 
                        selectedProduct = products.find { it.nombre == name }
                    }
                )
                
                if (selectedProduct != null) {
                    Text(
                        "Stock disponible: ${selectedProduct!!.cantidad}",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GemaTextField(
                    value = quantity,
                    onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                    label = "Cantidad",
                    icon = Icons.Default.ShoppingCart
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    GemaButton(
                        text = "Agregar",
                        onClick = {
                            val q = quantity.toIntOrNull() ?: 0
                            if (selectedProduct != null && q > 0) {
                                if (q > selectedProduct!!.cantidad) {
                                    // Could show toast or error
                                } else {
                                    onProductAdded(selectedProduct!!, q)
                                }
                            }
                        },
                        modifier = Modifier.height(48.dp),
                        accentColor = accentColor
                    )
                }
            }
        }
    }
}
