package com.example.gemainventory.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gemainventory.model.NegocioDto
import com.example.gemainventory.ui.theme.GemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    darkTheme: Boolean,
    currentBusiness: NegocioDto?,
    userRole: Int,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onUpdateName: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(currentBusiness?.nombre ?: "") }

    LaunchedEffect(currentBusiness) {
        tempName = currentBusiness?.nombre ?: ""
    }
    val primaryBlue = Color(0xFF0D2558)
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val context = LocalContext.current
    
    GemaTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mi Negocio", fontWeight = FontWeight.Bold, color = Color.White) },
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
                } else if (currentBusiness != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = currentBusiness.nombre ?: "",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (darkTheme) Color.White else primaryBlue
                                    )
                                    if (userRole == 1) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { showDialog = true }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar nombre",
                                                tint = Color(0xFF3B82F6)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                val infiniteTransition = rememberInfiniteTransition()
                                val scale by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1.03f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1500, easing = EaseInOutSine),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .size(220.dp)
                                        .scale(scale)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${currentBusiness.codigoInvitacion}"
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(qrUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Código QR",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = "CÓDIGO DE INVITACIÓN",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    letterSpacing = 1.5.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF3B82F6).copy(alpha = 0.1f))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Código GEMA", currentBusiness.codigoInvitacion)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = currentBusiness.codigoInvitacion ?: "",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3B82F6),
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copiar",
                                        tint = Color(0xFF3B82F6)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }

            if (showDialog) {
                EditBusinessDialog(
                    initialName = currentBusiness?.nombre ?: "",
                    darkTheme = darkTheme,
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        onUpdateName(it)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun EditBusinessDialog(
    initialName: String,
    darkTheme: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialName) }
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val primaryBlue = Color(0xFF0D2558)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = cardColor,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, if (darkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Editar Empresa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (darkTheme) Color.White else primaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Define el nuevo nombre comercial para tu negocio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Nombre comercial") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = if (darkTheme) Color.Gray else Color.LightGray,
                        focusedLabelColor = Color(0xFF3B82F6)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancelar", color = if (darkTheme) Color.LightGray else Color.Gray)
                    }

                    Button(
                        onClick = { if (text.isNotBlank()) onConfirm(text) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
