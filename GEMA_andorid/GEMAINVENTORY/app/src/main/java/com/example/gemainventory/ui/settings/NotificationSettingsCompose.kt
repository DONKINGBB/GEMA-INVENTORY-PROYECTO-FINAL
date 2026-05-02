package com.example.gemainventory.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.theme.GemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    darkTheme: Boolean,
    lowStockEnabled: Boolean,
    onLowStockChange: (Boolean) -> Unit,
    newOrdersEnabled: Boolean,
    onNewOrdersChange: (Boolean) -> Unit,
    inventoryChangesEnabled: Boolean,
    onInventoryChangesChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean
) {
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val primaryBlue = Color(0xFF0D2558)
    val accentBlue = Color(0xFF3B82F6)

    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Alertas y Notificaciones", fontWeight = FontWeight.Bold, color = Color.White) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Personaliza tus alertas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (darkTheme) Color.White else primaryBlue
                )
                
                Text(
                    text = "Configura cómo quieres recibir avisos importantes sobre tu negocio para mantenerte siempre al día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                PremiumSettingSwitch(
                    title = "Stock Bajo",
                    subtitle = "Avisar cuando un producto llegue al mínimo establecido.",
                    iconRes = com.example.gemainventory.R.drawable.ic_notification_bell,
                    checked = lowStockEnabled,
                    onCheckedChange = onLowStockChange,
                    accentColor = accentBlue,
                    cardColor = cardColor,
                    darkTheme = darkTheme
                )

                PremiumSettingSwitch(
                    title = "Nuevos Pedidos",
                    subtitle = "Notificar inmediatamente cuando se registre una nueva orden.",
                    iconRes = com.example.gemainventory.R.drawable.pedidos,
                    checked = newOrdersEnabled,
                    onCheckedChange = onNewOrdersChange,
                    accentColor = Color(0xFF10B981), // Emerald
                    cardColor = cardColor,
                    darkTheme = darkTheme
                )

                PremiumSettingSwitch(
                    title = "Cambios en Inventario",
                    subtitle = "Alertas sobre modificaciones manuales de stock y precios.",
                    iconRes = com.example.gemainventory.R.drawable.inventario,
                    checked = inventoryChangesEnabled,
                    onCheckedChange = onInventoryChangesChange,
                    accentColor = Color(0xFFF59E0B), // Amber
                    cardColor = cardColor,
                    darkTheme = darkTheme
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("GUARDAR PREFERENCIAS", fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumSettingSwitch(
    title: String,
    subtitle: String,
    iconRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    cardColor: Color,
    darkTheme: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (darkTheme) Color.White else Color.Black
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.2f)
                )
            )
        }
    }
}
