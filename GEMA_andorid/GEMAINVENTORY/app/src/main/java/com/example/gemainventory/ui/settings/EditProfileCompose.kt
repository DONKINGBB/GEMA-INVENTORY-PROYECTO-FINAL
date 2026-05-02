package com.example.gemainventory.ui.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gemainventory.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    darkTheme: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    address: String,
    onAddressChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    photoUrl: String?,
    onPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPasswordChangeRequest: (String, String) -> Unit, // Updated signature
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean
) {
    val backgroundColor = if (darkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val primaryBlue = Color(0xFF0D2558)
    val accentBlue = Color(0xFF3B82F6)

    var showPasswordDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Logic to detect initial country from phone string if it exists
    var selectedCountry by remember { 
        val country = countries.find { phone.startsWith(it.code) } ?: countries[0]
        mutableStateOf(country)
    }
    
    // Clean phone number without prefix for the display
    val displayPhone = remember(phone, selectedCountry) {
        if (phone.startsWith(selectedCountry.code)) {
            phone.removePrefix(selectedCountry.code).trim()
        } else {
            phone
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            darkTheme = darkTheme,
            oldPassword = oldPassword,
            onOldPasswordChange = { oldPassword = it },
            newPassword = newPassword,
            onNewPasswordChange = { newPassword = it },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { confirmPassword = it },
            onDismiss = { 
                showPasswordDialog = false
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
            },
            onConfirm = {
                onPasswordChangeRequest(oldPassword, newPassword)
                showPasswordDialog = false
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
            }
        )
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            darkTheme = darkTheme,
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                showDeleteDialog = false
                onDeleteAccountClick()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, color = Color.White) },
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Profile Picture Section
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "border")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ), label = "rotation"
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation)
                    ) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(accentBlue, Color.White, accentBlue, Color.Transparent, accentBlue),
                            ),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable { onPhotoClick() }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrl ?: R.drawable.ic_avatar_placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.ic_avatar_placeholder),
                            placeholder = painterResource(R.drawable.ic_avatar_placeholder)
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                    }

                    // Floating Camera Icon
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .offset(x = (-4).dp, y = (-4).dp),
                        color = accentBlue,
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        IconButton(onClick = onPhotoClick) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Information Card
                Text(
                    "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (darkTheme) Color.White else primaryBlue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GemaInputField(
                            value = name,
                            onValueChange = onNameChange,
                            label = "Nombre Completo",
                            icon = Icons.Default.Person,
                            darkTheme = darkTheme
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        GemaInfoItem(
                            label = "Correo Electrónico",
                            value = email,
                            icon = Icons.Default.Email,
                            darkTheme = darkTheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GemaInputField(
                            value = address,
                            onValueChange = onAddressChange,
                            label = "Dirección",
                            icon = Icons.Default.LocationOn,
                            darkTheme = darkTheme
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GemaPhoneInputField(
                            value = displayPhone,
                            onValueChange = { newNumber -> 
                                onPhoneChange(selectedCountry.code + newNumber)
                            },
                            selectedCountry = selectedCountry,
                            onCountryChange = { newCountry ->
                                selectedCountry = newCountry
                                onPhoneChange(newCountry.code + displayPhone)
                            },
                            label = "Teléfono",
                            darkTheme = darkTheme
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security Section
                Text(
                    "Seguridad y Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (darkTheme) Color.White else primaryBlue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column {
                        SecurityItem(
                            title = "Cambiar Contraseña",
                            subtitle = "Actualiza tu clave de acceso",
                            icon = R.drawable.ic_security,
                            onClick = { showPasswordDialog = true },
                            darkTheme = darkTheme
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
                        SecurityItem(
                            title = "Eliminar Cuenta",
                            subtitle = "Esta acción es irreversible",
                            icon = R.drawable.ic_delete_24,
                            color = Color(0xFFEF4444),
                            onClick = { showDeleteDialog = true },
                            darkTheme = darkTheme
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                        Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DeleteAccountDialog(
    darkTheme: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black
    val errorColor = Color(0xFFEF4444)

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(errorColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (step == 1) Icons.Default.Warning else Icons.Default.DeleteForever, 
                        contentDescription = null, 
                        tint = errorColor, 
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (step == 1) {
                    Text(
                        "¿Eliminar tu cuenta?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Esta acción es irreversible. Perderás todos tus inventarios, reportes y configuraciones guardadas permanentemente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar", color = Color.Gray)
                        }
                        Button(
                            onClick = { step = 2 },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = errorColor)
                        ) {
                            Text("Continuar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        "Última Confirmación",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = errorColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "¿Estás absolutamente seguro? No hay marcha atrás. Tu sesión se cerrará y tus datos serán borrados del sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onConfirmDelete,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = errorColor)
                        ) {
                            Text("SÍ, ELIMINAR MI CUENTA", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        }
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("MEJOR NO, VOLVER ATRÁS", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    darkTheme: Boolean,
    oldPassword: String,
    onOldPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF0D2558)
    val accentBlue = Color(0xFF3B82F6)
    val cardColor = if (darkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (darkTheme) Color.White else Color.Black

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(accentBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = accentBlue, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Cambiar Contraseña",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    "Ingresa tu contraseña actual y la nueva para actualizar tu seguridad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                PasswordInputField(
                    value = oldPassword,
                    onValueChange = onOldPasswordChange,
                    label = "Contraseña Actual",
                    visible = oldPasswordVisible,
                    onVisibilityChange = { oldPasswordVisible = it },
                    darkTheme = darkTheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "Nueva Contraseña",
                    visible = newPasswordVisible,
                    onVisibilityChange = { newPasswordVisible = it },
                    darkTheme = darkTheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirmar Contraseña",
                    visible = confirmPasswordVisible,
                    onVisibilityChange = { confirmPasswordVisible = it },
                    darkTheme = darkTheme,
                    isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Text("Cancelar", color = Color.Gray)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White),
                        enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword
                    ) {
                        Text("Actualizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    darkTheme: Boolean,
    isError: Boolean = false
) {
    val focusColor = if (isError) Color(0xFFEF4444) else Color(0xFF3B82F6)
    val containerColor = if (darkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
    val textColor = if (darkTheme) Color.White else Color.Black

    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isError) focusColor else Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = focusColor) },
            trailingIcon = {
                IconButton(onClick = { onVisibilityChange(!visible) }) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (visible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                cursorColor = focusColor
            ),
            singleLine = true,
            isError = isError,
            textStyle = LocalTextStyle.current.copy(color = textColor, fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun GemaInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    darkTheme: Boolean
) {
    val focusColor = Color(0xFF3B82F6)
    val textColor = if (darkTheme) Color.White else Color.Black
    val containerColor = if (darkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)

    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = focusColor) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                cursorColor = focusColor
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = textColor, fontWeight = FontWeight.Medium)
        )
    }
}

data class CountryData(val name: String, val code: String, val flag: String)

val countries = listOf(
    CountryData("México", "+52", "🇲🇽"),
    CountryData("Estados Unidos", "+1", "🇺🇸"),
    CountryData("España", "+34", "🇪🇸"),
    CountryData("Colombia", "+57", "🇨🇴"),
    CountryData("Argentina", "+54", "🇦🇷"),
    CountryData("Chile", "+56", "🇨🇱"),
    CountryData("Perú", "+51", "🇵🇪"),
    CountryData("Ecuador", "+593", "🇪🇨"),
    CountryData("Guatemala", "+502", "🇬🇹"),
    CountryData("Costa Rica", "+506", "🇨🇷")
)

@Composable
fun GemaPhoneInputField(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCountry: CountryData,
    onCountryChange: (CountryData) -> Unit,
    label: String,
    darkTheme: Boolean
) {
    val focusColor = Color(0xFF3B82F6)
    val textColor = if (darkTheme) Color.White else Color.Black
    val containerColor = if (darkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .clickable { expanded = true }
                ) {
                    Text(selectedCountry.flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        selectedCountry.code, 
                        color = focusColor, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.Default.ArrowDropDown, 
                        contentDescription = null, 
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(if (darkTheme) Color(0xFF1E293B) else Color.White)
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(country.flag, modifier = Modifier.padding(end = 8.dp))
                                        Text(country.name, color = textColor, modifier = Modifier.weight(1f))
                                        Text(country.code, color = Color.Gray, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    onCountryChange(country)
                                    expanded = false
                                }
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(1.dp)
                            .height(24.dp)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                cursorColor = focusColor
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = textColor, fontWeight = FontWeight.Medium),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
            )
        )
    }
}

@Composable
fun GemaInfoItem(
    label: String,
    value: String,
    icon: ImageVector,
    darkTheme: Boolean
) {
    val containerColor = if (darkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
    
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(containerColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text(value, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun SecurityItem(
    title: String,
    subtitle: String,
    icon: Int,
    color: Color = Color(0xFF3B82F6),
    onClick: () -> Unit,
    darkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(icon), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (darkTheme) Color.White else Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
