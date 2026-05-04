package com.example.gemainventory.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.gemainventory.R

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    userName: String,
    userEmail: String,
    userPhotoUrl: String?,
    isBiometricEnabled: Boolean,
    showManageUsers: Boolean,
    onProfileClick: () -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onCatalogClick: () -> Unit,
    onBusinessClick: () -> Unit,
    onJoinCreateClick: () -> Unit,
    onSwitchBusinessClick: () -> Unit,
    onManageUsersClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onManualClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)
    val textColor = Color.White
    val subTextColor = Color(0xFF94A3B8)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Consistent Top Bar Alignment
                Column {
                    Text(
                        text = "Preferencias",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.headlineLarge,
                        color = textColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Column {
                        // Profile Header with Spinning Ring
                        ProfileHeader(
                            name = userName,
                            email = userEmail,
                            photoUrl = userPhotoUrl,
                            accentColor = accentColor,
                            onClick = onProfileClick
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        SettingsSectionTitle("Seguridad", accentColor)
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)), // Más transparente
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column {
                                SettingsSwitchItem(
                                    icon = R.drawable.ic_moon,
                                    title = "Modo Oscuro",
                                    checked = darkTheme,
                                    onCheckedChange = onDarkModeToggle,
                                    textColor = textColor,
                                    accentColor = accentColor
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                SettingsSwitchItem(
                                    icon = R.drawable.proteccionbiometrica,
                                    title = "Protección Biométrica",
                                    checked = isBiometricEnabled,
                                    onCheckedChange = onBiometricToggle,
                                    textColor = textColor,
                                    accentColor = accentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        SettingsSectionTitle("Administración", accentColor)
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)), // Más transparente
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column {
                                SettingsItem(R.drawable.ic_database, "Gestión de Catálogos", textColor, accentColor, onCatalogClick)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                SettingsItem(R.drawable.minegocio, "Mi Negocio / Invitar", textColor, accentColor, onBusinessClick)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                SettingsItem(R.drawable.ic_plus, "Unirse o Crear Negocio", textColor, accentColor, onJoinCreateClick)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                SettingsItem(R.drawable.cambiardenegocio, "Cambiar de Negocio", textColor, accentColor, onSwitchBusinessClick)
                                
                                if (showManageUsers) {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                    SettingsItem(R.drawable.gestiondeequipo, "Gestión de Equipo", textColor, accentColor, onManageUsersClick)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        SettingsSectionTitle("Soporte", accentColor)
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)), // Más transparente
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                SettingsItem(R.drawable.ic_notification_bell, "Notificaciones", textColor, accentColor, onNotificationsClick)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.White.copy(alpha = 0.05f))
                                SettingsItem(R.drawable.ic_user_manual, "Manual de Usuario", textColor, accentColor, onManualClick)
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Logout Button (Sin shadow, con borde rojo y fondo sutil)
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                        ) {
                            Text(
                                "Cerrar Sesión",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(130.dp)) // Extra padding for Nav Bar
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    photoUrl: String?,
    accentColor: Color,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                // Spinning Ring
                Canvas(modifier = Modifier.size(80.dp).rotate(rotation)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.1f),
                                accentColor,
                                accentColor.copy(alpha = 0.1f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!photoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_account_circle),
                            error = painterResource(id = R.drawable.ic_account_circle)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account_circle),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.ifEmpty { "Usuario GEMA" },
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                )
                Text(
                    text = email.ifEmpty { "Configura tu perfil" },
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(text: String, color: Color) {
    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 16.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    textColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = textColor.copy(alpha = 0.2f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textColor: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                uncheckedTrackColor = textColor.copy(alpha = 0.1f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun CreateBusinessView(
    darkTheme: Boolean,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val backgroundColor = Color(0xFF1E293B)
    val textColor = Color.White
    val subTextColor = Color(0xFF94A3B8)
    val accentColor = Color(0xFF3B82F6)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative Handle for Bottom Sheet
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(subTextColor.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("✨", fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nuevo Negocio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Asigna un nombre único y empieza a brillar.",
            style = MaterialTheme.typography.bodyMedium,
            color = subTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nombre del negocio") },
            placeholder = { Text("Ej. Gema Inventory Central") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = subTextColor.copy(alpha = 0.3f),
                focusedLabelColor = accentColor,
                cursorColor = accentColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cancelar", color = subTextColor)
            }

            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 4.dp)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

