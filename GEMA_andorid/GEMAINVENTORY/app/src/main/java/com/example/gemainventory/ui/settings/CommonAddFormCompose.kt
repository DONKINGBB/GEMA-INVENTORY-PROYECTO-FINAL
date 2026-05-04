package com.example.gemainventory.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.gemainventory.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericAddFormScreen(
    title: String,
    darkTheme: Boolean,
    fields: List<FormFieldData>,
    buttonText: String,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    extraContent: @Composable ColumnScope.() -> Unit = {}
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val glassColor = Color.White.copy(alpha = 0.05f)

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GemaFormHeader(
                title = title,
                onBack = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                fields.forEachIndexed { index, field ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 100 * index)) + 
                                slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600, delayMillis = 100 * index))
                    ) {
                        GemaTextField(
                            value = field.value,
                            onValueChange = field.onValueChange,
                            label = field.label,
                            icon = field.icon,
                            glassColor = glassColor,
                            keyboardType = field.keyboardOptions.keyboardType,
                            singleLine = field.singleLine
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100 * fields.size)) + 
                            slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600, delayMillis = 100 * fields.size))
                ) {
                    Column {
                        extraContent()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 400)) + 
                            expandVertically(expandFrom = Alignment.Bottom)
                ) {
                    GemaButton(
                        text = if (isLoading) "Guardando..." else buttonText,
                        onClick = onSaveClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        accentColor = accentColor,
                        enabled = !isLoading
                    )
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    darkTheme: Boolean,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    GemaTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        icon = icon,
        keyboardType = keyboardOptions.keyboardType
    )
}

data class FormFieldData(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val icon: ImageVector,
    val singleLine: Boolean = true,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default
)

@Composable
fun MapSelectionButton(
    darkTheme: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFEF4444)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Seleccionar en Mapa",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

