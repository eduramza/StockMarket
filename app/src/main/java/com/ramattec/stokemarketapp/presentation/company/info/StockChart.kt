package com.ramattec.stokemarketapp.presentation.company.info

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import kotlin.math.round
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    infoList: List<IntraDayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green
) {
    val spacing = 100f //px
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val upperValue = remember {
        (infoList.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    }
    val lowerValue = remember {
        infoList.minOfOrNull { it.close }?.toInt() ?: 0
    }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.RED
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Canvas(modifier = modifier) {
        //horizontal axis
        val spacePerHour = (size.width - spacing) / infoList.size
        (0 until infoList.size - 1 step 2).forEach {
            val info = infoList[it]
            val hour = info.date.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${hour}h",
                    spacing + it * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }

        //vertical axis
        val priceStep = (upperValue - lowerValue) / 6f
        (0..6).forEach {
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * it).toString(),
                    30f,
                    size.height - spacing - it * size.height / 6f,
                    textPaint
                )
            }
        }

        //drawing line
        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            infoList.indices.forEach { i ->
                val currentInfo = infoList[i]
                val nextInfo = infoList.getOrNull(i + 1) ?: infoList.last()
                val leftRatio = (currentInfo.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + 1 * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )

            }
        }

        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}