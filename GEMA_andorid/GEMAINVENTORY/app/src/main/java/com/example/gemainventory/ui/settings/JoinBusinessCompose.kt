package com.example.gemainventory.ui.settings

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.theme.GemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinBusinessView(
    darkTheme: Boolean,
    onConfirm: (String) -> Unit,
    onScanQr: () -> Unit,
    onCancel: () -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // Animación de entrada suave
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    GemaTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(if (darkTheme) Color(0xFF0D1117) else Color.White)
                .padding(24.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, easing = EaseOutBack)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Handle de Bottom Sheet
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (darkTheme) Color.White.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.3f))
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🤝 Unirse a Negocio",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (darkTheme) Color.White else Color(0xFF0D2558),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ingresa el código de invitación o escanea el código QR de tu equipo.",
                        fontSize = 14.sp,
                        color = if (darkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = { if (it.length <= 12) inviteCode = it.uppercase() },
                        label = { Text("Código de invitación") },
                        placeholder = { Text("Ej: ABC12345") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = if (darkTheme) Color.White.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFF3B82F6)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón de Escaneo
                        OutlinedButton(
                            onClick = onScanQr,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3B82F6))
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("QR", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                        }

                        // Botón de Confirmar
                        Button(
                            onClick = {
                                if (inviteCode.isNotEmpty()) {
                                    onConfirm(inviteCode)
                                } else {
                                    Toast.makeText(context, "Por favor ingresa un código", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Text("Confirmar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(onClick = onCancel) {
                        Text("Cancelar", color = if (darkTheme) Color.White.copy(alpha = 0.5f) else Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}
