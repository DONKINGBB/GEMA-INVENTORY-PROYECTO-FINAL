package com.example.gemainventory.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gemainventory.R
import com.example.gemainventory.ui.theme.GemaTheme

data class CatalogItem(
    val title: String,
    val iconRes: Int,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsManageScreen(
    darkTheme: Boolean,
    onBackClick: () -> Unit,
    onManageProducts: () -> Unit,
    onManageCategories: () -> Unit,
    onManageClients: () -> Unit,
    onManageWarehouses: () -> Unit,
    onManageSuppliers: () -> Unit
) {
    val primaryBlue = Color(0xFF0D2558)
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val contentColor = if (darkTheme) Color.White else Color.Black

    val catalogItems = listOf(
        CatalogItem("Gestionar Productos", R.drawable.ic_pedidos, onManageProducts),
        CatalogItem("Gestionar Categorías", R.drawable.ic_cat, onManageCategories),
        CatalogItem("Gestionar Clientes", R.drawable.ic_clientesyp, onManageClients),
        CatalogItem("Gestionar Almacenes", R.drawable.ic_almacenes, onManageWarehouses),
        CatalogItem("Gestionar Proveedores", R.drawable.ic_supplier, onManageSuppliers)
    )

    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Gestión de Catálogos", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                catalogItems.forEach { item ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        CatalogRow(
                            item = item,
                            contentColor = contentColor,
                            iconTint = if (darkTheme) Color(0xFF60A5FA) else primaryBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun CatalogRow(
    item: CatalogItem,
    contentColor: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.5f)
        )
    }
}
