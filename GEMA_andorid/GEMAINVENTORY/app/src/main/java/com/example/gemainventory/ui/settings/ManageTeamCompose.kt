package com.example.gemainventory.ui.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.Usuario
import com.example.gemainventory.ui.components.GemaFloatingActionButton
import com.example.gemainventory.ui.theme.GemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamScreen(
    darkTheme: Boolean,
    userList: List<Usuario>,
    currentUserId: String,
    currentUserIdRol: Int,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onAddUserClick: () -> Unit,
    onEditRoleClick: (Usuario) -> Unit,
    onDeleteClick: (Usuario) -> Unit
) {
    val primaryBlue = Color(0xFF0D2558)
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White

    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Gestión de Equipo", fontWeight = FontWeight.Bold, color = Color.White) },
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
            floatingActionButton = {
                GemaFloatingActionButton(
                    onClick = onAddUserClick,
                    accentColor = Color(0xFF3B82F6),
                    modifier = Modifier.padding(bottom = 90.dp)
                )
            },
            containerColor = backgroundColor
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3B82F6)
                    )
                } else if (userList.isEmpty()) {
                    Text(
                        text = "No hay miembros en el equipo.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "Miembros del Equipo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (darkTheme) Color.White else primaryBlue,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(userList) { user ->
                            UserCard(
                                user = user,
                                currentUserId = currentUserId,
                                currentUserIdRol = currentUserIdRol,
                                cardColor = cardColor,
                                darkTheme = darkTheme,
                                onEditClick = { onEditRoleClick(user) },
                                onDeleteClick = { onDeleteClick(user) }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: Usuario,
    currentUserId: String,
    currentUserIdRol: Int,
    cardColor: Color,
    darkTheme: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val canDelete = currentUserIdRol != -1 && user.idRol != null &&
            user.idUsuario != currentUserId &&
            (currentUserIdRol == 1 || currentUserIdRol < user.idRol!!)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(RetrofitClient.getFullImageUrl(user.imagenUrl))
                        .crossfade(true)
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .build(),
                    contentDescription = "Avatar de ${user.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

                if (user.idRol == 1) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = "Propietario",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nombre ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (darkTheme) Color.White else Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val displayEmail = when {
                    !user.correo.isNullOrBlank() -> user.correo
                    !user.email.isNullOrBlank() -> user.email
                    else -> null
                }
                
                if (displayEmail != null) {
                    Text(
                        text = displayEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RoleBadge(roleId = user.idRol)
            }

            if (canDelete) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Usuario",
                        tint = Color(0xFFEF4444)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver Detalles",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RoleBadge(roleId: Int?) {
    val (backgroundColor, textColor, roleName) = when (roleId) {
        1 -> Triple(Color(0xFF3B82F6), Color.White, "PROPIETARIO")
        2 -> Triple(Color(0xFF10B981), Color.White, "ADMINISTRADOR")
        3 -> Triple(Color(0xFFF59E0B), Color.White, "SUPERVISOR")
        4 -> Triple(Color(0xFFE2E8F0), Color(0xFF334155), "VENDEDOR")
        5 -> Triple(Color(0xFFE2E8F0), Color(0xFF334155), "REPARTIDOR")
        6 -> Triple(Color(0xFFE2E8F0), Color(0xFF334155), "ALMACENISTA")
        else -> Triple(Color(0xFFE2E8F0), Color(0xFF334155), "SIN ROL")
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = roleName,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
