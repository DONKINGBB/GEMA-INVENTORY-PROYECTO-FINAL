package com.example.gemainventory.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gemainventory.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserRoleScreen(
    darkTheme: Boolean,
    user: Usuario?,
    currentUserRole: Int,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onUpdateClick: (Int) -> Unit
) {
    val backgroundColor = if (darkTheme) Color(0xFF121212) else Color(0xFFF8F9FA)
    val cardColor = if (darkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black
    val primaryColor = Color(0xFF3B82F6)

    val roles = listOf(
        "ADMINISTRADOR" to 2,
        "SUPERVISOR" to 3,
        "VENDEDOR" to 4,
        "REPARTIDOR" to 5,
        "ALMACENISTA" to 6
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember(user) { 
        mutableStateOf(roles.find { it.second == user?.idRol } ?: roles.last())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (darkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = textColor
                    )
                }
                
                Text(
                    text = "Editar Rol de Equipo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            if (isLoading || user == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // User Profile Card (Modern/Premium)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(cardColor)
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = if (darkTheme) 
                                        listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                                    else 
                                        listOf(Color.Black.copy(alpha = 0.05f), Color.Transparent)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // User Image with Glow Effect
                            Box(contentAlignment = Alignment.Center) {
                                // Animated Glow
                                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                                val rotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(3000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    ), label = "rotation"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .rotate(rotation)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(primaryColor, Color(0xFF60A5FA), primaryColor)
                                            )
                                        )
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(94.dp)
                                        .clip(CircleShape)
                                        .background(cardColor)
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .background(if (darkTheme) Color.DarkGray else Color.LightGray)
                                ) {
                                    if (!user.imagenUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = user.imagenUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(0.6f).align(Alignment.Center),
                                            tint = textColor.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = user.nombre ?: "Sin nombre",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor
                            )
                            
                            val email = if (!user.correo.isNullOrBlank()) user.correo else if (!user.email.isNullOrBlank()) user.email else null
                            if (email != null) {
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Role Selector
                    Text(
                        text = "Seleccionar Nuevo Rol",
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )

                    val availableRoles = roles.filter { it.second >= currentUserRole }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedRole.first,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { 
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = textColor.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = textColor.copy(alpha = 0.2f),
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )
                        
                        // Overlay to capture clicks reliably
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { expanded = !expanded }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(cardColor)
                        ) {
                            availableRoles.forEach { role ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = role.first,
                                            color = textColor,
                                            fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    onClick = {
                                        selectedRole = role
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Warning Note
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Solo puedes asignar roles iguales o inferiores al tuyo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Save Button
                    Button(
                        onClick = { onUpdateClick(selectedRole.second) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = user.idRol != 1 // Cannot change Owner
                    ) {
                        Text(
                            text = "Actualizar Jerarquía",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    
                    if (user.idRol == 1) {
                        Text(
                            text = "El rol de Propietario no puede ser modificado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}
