package tm.you.music.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import tm.you.music.core.designsystem.theme.TabularTimeStyle
import tm.you.music.core.designsystem.theme.YouMusicMotion
import kotlin.math.sin

@Composable
fun WaveformProgressBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    barCount: Int = 36,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    val progress = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(progress) }

    val effectiveProgress = if (isDragging) dragProgress else progress

    val barHeightScale by animateFloatAsState(
        targetValue = if (isDragging) 1.25f else 1f,
        animationSpec = YouMusicMotion.SpatialExpressiveFast,
        label = "WaveformScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(durationMs) {
                    detectTapGestures { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeek((newProgress * durationMs).toLong())
                    }
                }
                .pointerInput(durationMs) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeek((dragProgress * durationMs).toLong())
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        }
                    )
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val totalSpacing = canvasWidth * 0.25f
            val barWidth = (canvasWidth - totalSpacing) / barCount
            val barSpacing = totalSpacing / (barCount - 1)

            val centerY = canvasHeight / 2f

            for (i in 0 until barCount) {
                val normalizedIndex = i.toFloat() / barCount
                val waveFactor = (sin(normalizedIndex * Math.PI * 4).toFloat() * 0.35f + 0.65f)
                val baseBarHeight = (canvasHeight * 0.7f * waveFactor * barHeightScale).coerceIn(8f, canvasHeight)

                val xOffset = i * (barWidth + barSpacing)
                val yOffset = centerY - (baseBarHeight / 2f)

                val isPlayed = normalizedIndex <= effectiveProgress
                val barColor = if (isPlayed) activeColor else inactiveColor

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xOffset, yOffset),
                    size = Size(barWidth, baseBarHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val displayPositionMs = if (isDragging) {
                (dragProgress * durationMs).toLong()
            } else {
                currentPositionMs
            }

            Text(
                text = formatDuration(displayPositionMs),
                style = TabularTimeStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatDuration(durationMs),
                style = TabularTimeStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
