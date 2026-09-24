package com.example.presentation.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DeviceType
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.TertiaryYellow

@Composable
fun EquipmentPieChart(
    distribution: TypeDistribution,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(distribution) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 900))
    }

    val broadcastColor = PrimaryBlue
    val receiverColor = SecondaryGreen
    val modemColor = TertiaryYellow

    val total = distribution.total
    val broadcastAngle = if (total > 0) (distribution.broadcastCount.toFloat() / total) * 360f else 0f
    val receiverAngle = if (total > 0) (distribution.receiverCount.toFloat() / total) * 360f else 0f
    val modemAngle = if (total > 0) (distribution.modemCount.toFloat() / total) * 360f else 0f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(170.dp)
        ) {
            Canvas(modifier = Modifier.size(150.dp)) {
                val strokeWidth = 32.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)
                val topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                val arcSize = Size(radius * 2, radius * 2)

                if (total == 0) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    var currentStartAngle = -90f
                    val currentProgress = animatedProgress.value

                    // Broadcast segment
                    if (broadcastAngle > 0) {
                        val sweep = broadcastAngle * currentProgress
                        drawArc(
                            color = broadcastColor,
                            startAngle = currentStartAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        currentStartAngle += sweep
                    }

                    // Receiver segment
                    if (receiverAngle > 0) {
                        val sweep = receiverAngle * currentProgress
                        drawArc(
                            color = receiverColor,
                            startAngle = currentStartAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        currentStartAngle += sweep
                    }

                    // Modem segment
                    if (modemAngle > 0) {
                        val sweep = modemAngle * currentProgress
                        drawArc(
                            color = modemColor,
                            startAngle = currentStartAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$total",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "جهاز",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PieLegendItem(
                label = DeviceType.BROADCAST.titleAr,
                count = distribution.broadcastCount,
                color = broadcastColor
            )
            PieLegendItem(
                label = DeviceType.RECEIVER.titleAr,
                count = distribution.receiverCount,
                color = receiverColor
            )
            PieLegendItem(
                label = DeviceType.MODEM.titleAr,
                count = distribution.modemCount,
                color = modemColor
            )
        }
    }
}

@Composable
private fun PieLegendItem(
    label: String,
    count: Int,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun WeeklyActivityLineChart(
    modifier: Modifier = Modifier
) {
    val days = listOf("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
    val dataPoints = listOf(14f, 19f, 22f, 18f, 25f, 28f, 31f)

    val primaryColor = PrimaryBlue
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 8.dp)
        ) {
            val maxVal = 35f
            val minVal = 10f
            val range = maxVal - minVal

            val stepX = size.width / (dataPoints.size - 1)

            // Draw horizontal reference guide lines
            for (i in 0..2) {
                val y = size.height * (i / 2f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val path = Path()
            val points = dataPoints.mapIndexed { index, value ->
                val x = index * stepX
                val normalizedY = 1f - ((value - minVal) / range)
                val y = (normalizedY * (size.height - 20f)) + 10f
                Offset(x, y)
            }

            points.forEachIndexed { i, pt ->
                if (i == 0) {
                    path.moveTo(pt.x, pt.y)
                } else {
                    // Smooth curve between points
                    val prev = points[i - 1]
                    val cx = (prev.x + pt.x) / 2
                    path.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                }
            }

            // Draw line
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw dots at each day
            points.forEach { pt ->
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = primaryColor,
                    radius = 3.dp.toPx(),
                    center = pt
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week text labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
