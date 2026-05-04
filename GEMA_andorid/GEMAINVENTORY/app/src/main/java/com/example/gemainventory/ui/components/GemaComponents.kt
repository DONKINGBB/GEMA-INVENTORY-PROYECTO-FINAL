package com.example.gemainventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun GemaFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF3B82F6)
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(accentColor.copy(alpha = 0.8f), Color(0xFF2563EB).copy(alpha = 0.8f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun GemaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF3B82F6),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = if (enabled) 
                    listOf(accentColor.copy(alpha = 0.7f), Color(0xFF2563EB).copy(alpha = 0.7f))
                else 
                    listOf(Color.Gray.copy(alpha = 0.5f), Color.DarkGray.copy(alpha = 0.5f))
            ),
            shape = RoundedCornerShape(16.dp)
        )
        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun GemaIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF3B82F6)
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.7f),
                        Color(0xFF2563EB).copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun GemaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    glassColor: Color = Color.White.copy(alpha = 0.05f),
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(glassColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 20.dp, vertical = if (singleLine) 0.dp else 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top) {
            Icon(
                icon, 
                null, 
                tint = Color.White.copy(alpha = 0.4f), 
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = if (singleLine) 0.dp else 4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(label, color = Color.White.copy(alpha = 0.3f))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    enabled = onClick == null,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = singleLine,
                    maxLines = maxLines
                )
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Composable
fun GemaSectionTitle(title: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GemaEntitySelector(
    label: String,
    selectedName: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onAddNew: (() -> Unit)? = null,
    glassColor: Color = Color.White.copy(alpha = 0.05f),
    icon: ImageVector = Icons.Default.ArrowDropDown
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(glassColor)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.White.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                    Text(selectedName, color = if (selectedName.contains("Selecciona")) Color.White.copy(alpha = 0.3f) else Color.White)
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF1E293B))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            if (onAddNew != null) {
                DropdownMenuItem(
                    text = { Text("+ Agregar Nuevo", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold) },
                    onClick = {
                        onAddNew()
                        expanded = false
                    }
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GemaFormHeader(
    title: String,
    onBack: () -> Unit,
    onDelete: (() -> Unit)? = null,
    subtitle: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }

        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(44.dp)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
            }
        }
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
fun GemaPhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCountry: CountryData,
    onCountryChange: (CountryData) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    glassColor: Color = Color.White.copy(alpha = 0.05f)
) {
    var expanded by remember { mutableStateOf(false) }
    val accentColor = Color(0xFF3B82F6)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(glassColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Country Picker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { expanded = true }
            ) {
                Text(selectedCountry.flag, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    selectedCountry.code,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            )

            // Phone Number Input
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(label, color = Color.White.copy(alpha = 0.3f))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = true
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(country.flag, modifier = Modifier.padding(end = 8.dp))
                                Text(country.name, color = Color.White, modifier = Modifier.weight(1f))
                                Text(country.code, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        },
                        onClick = {
                            onCountryChange(country)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
