package com.example.gemainventory.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.components.GemaButton
import com.example.gemainventory.ui.components.GemaTextField
import com.example.gemainventory.ui.theme.GemaTheme

@Composable
fun ForgotPasswordScreen(
    onSendCodeClick: (String) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    val primaryBlue = Color(0xFF0D2558)
    val accentBlue = Color(0xFF3B82F6)

    GemaTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryBlue, Color(0xFF0F172A))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar / Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.08f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Icon / Illustration Placeholder (Lock)
                Surface(
                    modifier = Modifier.size(100.dp),
                    color = accentBlue.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = accentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ingresa tu correo electrónico para enviarte un código de recuperación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                GemaTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = accentBlue)
                } else {
                    GemaButton(
                        text = "Enviar Código",
                        onClick = { onSendCodeClick(email) },
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = accentBlue,
                        enabled = email.isNotEmpty()
                    )
                }
            }
        }
    }
}
