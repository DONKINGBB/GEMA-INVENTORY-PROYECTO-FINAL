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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.ui.components.GemaButton
import com.example.gemainventory.ui.components.GemaTextField
import com.example.gemainventory.ui.theme.GemaTheme

@Composable
fun ResetPasswordScreen(
    email: String,
    onResetClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false
) {
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                // Top Bar
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

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Restablecer Contraseña",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enviamos un código a: $email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Verification Code Field
                GemaTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it },
                    label = "Código de verificación",
                    icon = Icons.Default.Verified,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(20.dp))

                // New Password Field
                GemaTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Nueva Contraseña",
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingContent = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = accentBlue)
                } else {
                    GemaButton(
                        text = "Actualizar Contraseña",
                        onClick = { onResetClick(code, newPassword) },
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = accentBlue,
                        enabled = code.length >= 6 && newPassword.isNotEmpty()
                    )
                }
            }
        }
    }
}
