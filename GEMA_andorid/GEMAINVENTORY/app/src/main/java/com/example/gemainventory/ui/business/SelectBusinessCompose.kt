package com.example.gemainventory.ui.business

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import com.example.gemainventory.ui.theme.GemaTheme

@Composable
fun SelectBusinessScreen(
    onCrearNegocio: (String) -> Unit,
    onUnirNegocio: (String) -> Unit,
    onScanQr: () -> Unit,
    isLoading: Boolean = false,
    externalCode: String = ""
) {
    var nombreNegocio by remember { mutableStateOf("") }
    var codigoInvitacion by remember { mutableStateOf(externalCode) }
    
    val isDark = isSystemInDarkTheme()
    val accentColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF0D2558)
    
    // Sincronizar el código si viene del escáner
    LaunchedEffect(externalCode) {
        if (externalCode.isNotEmpty()) {
            codigoInvitacion = externalCode
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
        } else {
            listOf(Color(0xFFF8FAFC), Color(0xFFFFFFFF))
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo central con diseño minimalista
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logo_cuadrado_bb),
                    contentDescription = "Logo GEMA",
                    modifier = Modifier.size(70.dp),
                    tint = accentColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Bienvenido a GEMA!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Configura tu espacio de trabajo para comenzar",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tarjeta Crear Negocio
            BusinessActionCard(
                title = "Crear mi Negocio",
                description = "Soy el dueño y quiero registrar mi empresa.",
                icon = Icons.Default.Business,
                accentColor = accentColor,
                isDark = isDark,
                content = {
                    OutlinedTextField(
                        value = nombreNegocio,
                        onValueChange = { nombreNegocio = it },
                        label = { Text("Nombre de tu negocio") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = accentColor.copy(alpha = 0.2f),
                            focusedLabelColor = accentColor
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onCrearNegocio(nombreNegocio) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isLoading && nombreNegocio.isNotBlank(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Crear y Avanzar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Unirse a Equipo
            BusinessActionCard(
                title = "Unirme a un Equipo",
                description = "Tengo un código de invitación.",
                icon = Icons.Default.GroupAdd,
                accentColor = accentColor,
                isDark = isDark,
                content = {
                    OutlinedTextField(
                        value = codigoInvitacion,
                        onValueChange = { codigoInvitacion = it.uppercase() },
                        label = { Text("Código de invitación") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = accentColor.copy(alpha = 0.2f),
                            focusedLabelColor = accentColor
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onScanQr,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("QR", color = accentColor)
                        }
                        Button(
                            onClick = { onUnirNegocio(codigoInvitacion) },
                            modifier = Modifier.weight(2f).height(56.dp),
                            enabled = !isLoading && codigoInvitacion.isNotBlank(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Adelante", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun BusinessActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color.White
        ),
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            content()
        }
    }
}
