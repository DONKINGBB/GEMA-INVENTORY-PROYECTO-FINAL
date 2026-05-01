package com.example.gemainventory.ui.manual

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gemainventory.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.theme.GemaTheme


data class ManualSection(
    val title: String,
    val subtitle: String,
    val content: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManualScreen(
    darkTheme: Boolean = false,
    onBackClick: () -> Unit
) {
    val sections = remember {
        listOf(
            ManualSection(
                title = "Gestión Multi-Negocio",
                subtitle = "Domina múltiples empresas.",
                content = "• Cambios de Contexto: Ve a Ajustes > Cambiar de Negocio. Selecciona la empresa deseada y la app refrescará tus permisos e inventarios al instante.\n• Roles: El sistema distingue si eres Dueño (control total) o Empleado (acceso limitado).\n• Aislamiento: Los clientes, inventarios y pedidos de cada negocio son totalmente privados y no se mezclan.",
                iconRes = R.drawable.inicio
            ),
            ManualSection(
                title = "Dashboard y Métricas",
                subtitle = "Tu visión 360° del negocio.",
                content = "• Ventas Totales: Ingresos brutos acumulados por tus ventas.\n• Productos Bajos: Alertas automáticas de ítems que necesitan resurtido.\n• Historial: Gráficos interactivos de ingresos vs egresos para analizar rentabilidad por mes.",
                iconRes = R.drawable.finanzas
            ),
            ManualSection(
                title = "Gestión de Inventario",
                subtitle = "Control total de mercancía.",
                content = "• Agregar Producto: Captura foto, precios y stock mínimo. El stock mínimo activa alertas en el inicio.\n• Escaneo QR: Localiza productos al instante apuntando la cámara.\n• Ubicaciones: Al editar un producto, puedes ver en qué almacén está guardado.",
                iconRes = R.drawable.inventario
            ),
            ManualSection(
                title = "Órdenes y Ventas",
                subtitle = "Entrada y salida de productos",
                content = "• Ventas: Selecciona clientes y añade productos. Al marcar como 'Pagado', afecta tus finanzas.\n• Compras: Registra abastecimiento a proveedores. El stock se incrementa al completar la orden.\n• Estatus de Pedido: Identifica visualmente pedidos Pendientes, Completados o Cancelados.",
                iconRes = R.drawable.pedidos
            ),
            ManualSection(
                title = "Catálogos y Almacenes",
                subtitle = "Bases de datos auxiliares.",
                content = "• Directorio: Gestiona tus Clientes y Proveedores con datos de contacto detallados.\n• Sucursales: Configura múltiples almacenes si tu negocio opera en distintas ubicaciones físicas.",
                iconRes = R.drawable.ic_almacenes
            )
        )
    }

    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Manual de Usuario",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "GEMA Inventory v1.2 - Base de Conocimientos",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Guía integral para la gestión profesional de tu empresa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(sections) { section ->
                    ManualCard(section)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "GEMA Inventory v1.2",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ManualCard(section: ManualSection) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = section.iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = section.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = section.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
