package com.example.gemainventory.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import com.example.gemainventory.ui.components.GemaButton
import com.example.gemainventory.ui.components.GemaTextField
import com.example.gemainventory.ui.components.GemaPhoneTextField
import com.example.gemainventory.ui.components.CountryData
import com.example.gemainventory.ui.components.countries
import com.example.gemainventory.ui.theme.GemaTheme

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String, String) -> Unit,
    onBackToLogin: () -> Unit,
    isLoading: Boolean = false
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(countries[0]) }

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
                    .padding(horizontal = 30.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Logo
                Icon(
                    painter = painterResource(id = R.drawable.ic_logo_cuadrado_bb),
                    contentDescription = "GEMA Logo",
                    tint = Color.White,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Únete a la gestión inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Form
                GemaTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre completo",
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                GemaTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                GemaTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = "Contraseña",
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

                Spacer(modifier = Modifier.height(16.dp))

                GemaTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = "Dirección (Opcional)",
                    icon = Icons.Default.LocationOn
                )

                Spacer(modifier = Modifier.height(16.dp))

                GemaPhoneTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    selectedCountry = selectedCountry,
                    onCountryChange = { selectedCountry = it },
                    label = "Teléfono (Opcional)"
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = accentBlue)
                } else {
                    GemaButton(
                        text = "Registrarse",
                        onClick = { 
                            val fullPhone = if (telefono.isNotEmpty()) "${selectedCountry.code}$telefono" else ""
                            onRegisterClick(nombre, correo, contrasena, direccion, fullPhone) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = accentBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onBackToLogin) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "¿Ya tienes cuenta? ",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            "Inicia sesión",
                            color = accentBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
