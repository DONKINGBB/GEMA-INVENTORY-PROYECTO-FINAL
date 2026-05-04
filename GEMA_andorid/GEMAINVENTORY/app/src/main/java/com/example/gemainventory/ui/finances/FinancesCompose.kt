package com.example.gemainventory.ui.finances

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemainventory.R
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt
import com.example.gemainventory.ui.components.GemaButton
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow


@Composable
fun FinancesView(
    income: Float,
    expenses: Float,
    incomeData: List<Float>,
    expenseData: List<Float>,
    onGenerateReport: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF3B82F6)
    val surfaceColor = Color(0xFF1E293B)
    val glassColor = Color.White.copy(alpha = 0.05f)
    val textColor = Color.White

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = backgroundColor
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
                
                // Consistent Top Bar
                Column {
                    Text(
                        text = "Gestión de Capital",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Finanzas",
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
                        FinanceSectionHeader("Resumen General", accentColor)
                        
                        FinanceCardLarge(
                            title = "Beneficio Neto",
                            value = income - expenses,
                            icon = R.drawable.ic_orders,
                            accentColor = Color.White,
                            glassColor = glassColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FinanceCardSmall(
                                title = "Ingresos",
                                value = income,
                                icon = R.drawable.ic_shopping_cart,
                                modifier = Modifier.weight(1f),
                                accentColor = Color(0xFF10B981),
                                glassColor = glassColor
                            )
                            FinanceCardSmall(
                                title = "Gastos",
                                value = expenses,
                                icon = R.drawable.ic_shopping_cart,
                                modifier = Modifier.weight(1f),
                                accentColor = Color(0xFFEF4444),
                                glassColor = glassColor
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        FinanceSectionHeader("Análisis Anual", accentColor)
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "Balance de Ingresos vs Gastos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                AnimatedLineChart(
                                    incomeData = incomeData,
                                    expenseData = expenseData,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ChartLegendItem(Color(0xFF10B981), "Ingresos")
                                    Spacer(modifier = Modifier.width(32.dp))
                                    ChartLegendItem(Color(0xFFEF4444), "Gastos")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        FinanceSectionHeader("Acciones Rápidas", accentColor)
                        
                        GemaButton(
                            text = "Descargar Reporte PDF",
                            onClick = onGenerateReport,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            accentColor = accentColor
                        )

                        Spacer(modifier = Modifier.height(130.dp)) // Extra padding for Nav Bar
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceSectionHeader(title: String, color: Color) {
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
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FinanceCardLarge(
    title: String,
    value: Float,
    icon: Int,
    accentColor: Color,
    glassColor: Color
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                val valueColor = when {
                    value > 0 -> Color(0xFF10B981)
                    value < 0 -> Color(0xFFEF4444)
                    else -> Color.White
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = currencyFormat.format(value),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor
                )
            }
        }
    }
}

@Composable
fun FinanceCardSmall(
    title: String,
    value: Float,
    icon: Int,
    modifier: Modifier = Modifier,
    accentColor: Color,
    glassColor: Color
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.4f)
            )
            Text(
                text = currencyFormat.format(value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun AnimatedLineChart(
    incomeData: List<Float>,
    expenseData: List<Float>,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    
    var selectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(incomeData, expenseData) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    val maxVal by remember {
        derivedStateOf {
            val highest = (incomeData.maxOrNull() ?: 0f).coerceAtLeast(expenseData.maxOrNull() ?: 0f)
            highest.coerceAtLeast(1f) * 1.3f
        }
    }

    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 10.sp
    )
    
    val primaryColor = Color(0xFF3B82F6)
    val surfaceVariantColor = Color(0xFF1E293B)

    Box(modifier = modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    val spacing = size.width / 11f
                    selectedIndex = (offset.x / spacing).roundToInt().coerceIn(0, 11)
                }
            )
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height - 30.dp.toPx()
            val spacing = width / 11f

            // Grid & Y-Axis Labels
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height - (i * height / gridLines)
                val labelValue = (maxVal / gridLines * i).toDouble()
                
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )

                val formattedLabel = if (labelValue >= 1000) {
                    "${currencyFormat.format(labelValue / 1000).replace(".00", "")}k"
                } else {
                    currencyFormat.format(labelValue).replace(".00", "")
                }

                drawText(
                    textMeasurer = textMeasurer,
                    text = formattedLabel,
                    topLeft = Offset(0f, y - 15.dp.toPx()),
                    style = labelStyle
                )
            }

            // X-Axis Labels (Months)
            val months = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
            months.forEachIndexed { index, month ->
                val x = index * spacing
                drawText(
                    textMeasurer = textMeasurer,
                    text = month,
                    topLeft = Offset(x, height + 8.dp.toPx()),
                    style = labelStyle
                )
            }

            // Draw Data Lines
            drawDataLine(incomeData, Color(0xFF10B981), animationProgress.value, maxVal, height, spacing)
            drawDataLine(expenseData, Color(0xFFEF4444), animationProgress.value, maxVal, height, spacing)

            // Tooltip
            if (selectedIndex != -1) {
                val x = selectedIndex * spacing
                drawLine(
                    color = primaryColor.copy(alpha = 0.4f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                val incomeValue = incomeData.getOrNull(selectedIndex) ?: 0f
                val expenseValue = expenseData.getOrNull(selectedIndex) ?: 0f
                val tooltipText = "I: ${currencyFormat.format(incomeValue)}\nG: ${currencyFormat.format(expenseValue)}"
                val tooltipSize = textMeasurer.measure(tooltipText, labelStyle.copy(color = Color.White, fontWeight = FontWeight.Bold))
                
                val tooltipX = (x + 10.dp.toPx()).coerceAtMost(width - tooltipSize.size.width - 20.dp.toPx())
                
                drawRoundRect(
                    color = surfaceVariantColor,
                    topLeft = Offset(tooltipX, 10.dp.toPx()),
                    size = Size(tooltipSize.size.width.toFloat() + 20f, tooltipSize.size.height.toFloat() + 20f),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = tooltipText,
                    topLeft = Offset(tooltipX + 10f, 10.dp.toPx() + 10f),
                    style = labelStyle.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

private fun DrawScope.drawDataLine(
    data: List<Float>,
    color: Color,
    progress: Float,
    maxVal: Float,
    height: Float,
    spacing: Float
) {
    if (data.isEmpty()) return
    
    val points = (0 until 12).map { i ->
        val value = data.getOrNull(i) ?: 0f
        Offset(i * spacing, height - (value / maxVal * height * progress))
    }

    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            cubicTo(p0.x + (p1.x - p0.x) / 2, p0.y, p0.x + (p1.x - p0.x) / 2, p1.y, p1.x, p1.y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    val fillPath = Path().apply {
        addPath(path)
        lineTo(points.last().x, height)
        lineTo(points.first().x, height)
        close()
    }

    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.2f), Color.Transparent))
    )
}

@Composable
fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
    }
}

