package com.example.gemainventory.ui.manual

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import com.example.gemainventory.ui.theme.GemaTheme

data class ManualSection(
    val title: String,
    val subtitle: String,
    val content: String,
    val iconRes: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManualScreen(
    darkTheme: Boolean,
    onBackClick: () -> Unit
) {
    val primaryBlue = Color(0xFF0D2558)
    val accentBlue = Color(0xFF3B82F6)
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White

    val sections = remember {
        listOf(
            ManualSection(
                title = "Gestión Multi-Negocio",
                subtitle = "Domina múltiples empresas.",
                content = "• Cambios de Contexto: Ve a Ajustes > Cambiar de Negocio. Selecciona la empresa deseada y la app refrescará tus permisos e inventarios al instante.\n• Roles: El sistema distingue si eres Dueño (control total) o Empleado (acceso limitado).\n• Aislamiento: Los clientes, inventarios y pedidos de cada negocio son totalmente privados y no se mezclan.",
                iconRes = R.drawable.inicio,
                color = Color(0xFF3B82F6)
            ),
            ManualSection(
                title = "Dashboard y Métricas",
                subtitle = "Tu visión 360° del negocio.",
                content = "• Ventas Totales: Ingresos brutos acumulados por tus ventas.\n• Productos Bajos: Alertas automáticas de ítems que necesitan resurtido.\n• Historial: Gráficos interactivos de ingresos vs egresos para analizar rentabilidad por mes.",
                iconRes = R.drawable.finanzas,
                color = Color(0xFF10B981)
            ),
            ManualSection(
                title = "Gestión de Inventario",
                subtitle = "Control total de mercancía.",
                content = "• Agregar Producto: Captura foto, precios y stock mínimo. El stock mínimo activa alertas en el inicio.\n• Escaneo QR: Localiza productos al instante apuntando la cámara.\n• Ubicaciones: Al editar un producto, puedes ver en qué almacén está guardado.",
                iconRes = R.drawable.inventario,
                color = Color(0xFFF59E0B)
            ),
            ManualSection(
                title = "Órdenes y Ventas",
                subtitle = "Entrada y salida de productos",
                content = "• Ventas: Selecciona clientes y añade productos. Al marcar como 'Pagado', afecta tus finanzas.\n• Compras: Registra abastecimiento a proveedores. El stock se incrementa al completar la orden.\n• Estatus de Pedido: Identifica visualmente pedidos Pendientes, Completados o Cancelados.",
                iconRes = R.drawable.pedidos,
                color = Color(0xFF6366F1)
            ),
            ManualSection(
                title = "Catálogos y Almacenes",
                subtitle = "Bases de datos auxiliares.",
                content = "• Directorio: Gestiona tus Clientes y Proveedores con datos de contacto detallados.\n• Sucursales: Configura múltiples almacenes si tu negocio opera en distintas ubicaciones físicas.",
                iconRes = R.drawable.ic_almacenes,
                color = Color(0xFFEC4899)
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredSections = sections.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Manual de Usuario", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = primaryBlue
                    )
                )
            },
            containerColor = backgroundColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header with Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        placeholder = { Text("Buscar ayuda...", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color.White
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "Base de Conocimientos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (darkTheme) Color.White else primaryBlue
                            )
                            Text(
                                "Guía interactiva v1.2.5",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    items(filteredSections) { section ->
                        PremiumManualCard(section, cardColor, darkTheme)
                    }

                    if (filteredSections.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text("No se encontraron resultados", color = Color.Gray)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumManualCard(section: ManualSection, cardColor: Color, darkTheme: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(section.color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = section.iconRes),
                        contentDescription = null,
                        tint = section.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (darkTheme) Color.White else Color.Black
                    )
                    Text(
                        text = section.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = section.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (darkTheme) Color.White.copy(alpha = 0.8f) else Color(0xFF334155),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}
