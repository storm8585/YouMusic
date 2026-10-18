package tm.you.music.core.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

@Composable
fun ExpressiveMusicSlider(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    thumbColor: Color = MaterialTheme.colorScheme.primary
) {
    val progress = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(progress) }

    val effectiveProgress = if (isDragging) dragProgress else progress

    val trackHeight by animateDpAsState(
        targetValue = if (isDragging) 16.dp else 10.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "M3SliderTrackHeight"
    )

    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 5.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "M3SliderThumbWidth"
    )

    val thumbHeight by animateDpAsState(
        targetValue = if (isDragging) 30.dp else 22.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "M3SliderThumbHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
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
            val centerY = canvasHeight / 2f

            val trackHeightPx = trackHeight.toPx()
            val thumbWidthPx = thumbWidth.toPx()
            val thumbHeightPx = thumbHeight.toPx()
            val trackCornerRadius = CornerRadius(trackHeightPx / 2f, trackHeightPx / 2f)
            val thumbCornerRadius = CornerRadius(thumbWidthPx / 2f, thumbWidthPx / 2f)

            val currentThumbX = effectiveProgress * canvasWidth
            val gapPx = 4.dp.toPx()

            val activeTrackEnd = (currentThumbX - gapPx / 2f).coerceAtLeast(0f)
            if (activeTrackEnd > 0f) {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(0f, centerY - trackHeightPx / 2f),
                    size = Size(activeTrackEnd, trackHeightPx),
                    cornerRadius = trackCornerRadius
                )
            }

            val inactiveTrackStart = (currentThumbX + gapPx / 2f).coerceAtMost(canvasWidth)
            val inactiveTrackWidth = canvasWidth - inactiveTrackStart
            if (inactiveTrackWidth > 0f) {
                drawRoundRect(
                    color = inactiveColor,
                    topLeft = Offset(inactiveTrackStart, centerY - trackHeightPx / 2f),
                    size = Size(inactiveTrackWidth, trackHeightPx),
                    cornerRadius = trackCornerRadius
                )
            }

            val thumbLeft = (currentThumbX - thumbWidthPx / 2f).coerceIn(0f, canvasWidth - thumbWidthPx)
            val thumbTop = centerY - thumbHeightPx / 2f
            drawRoundRect(
                color = thumbColor,
                topLeft = Offset(thumbLeft, thumbTop),
                size = Size(thumbWidthPx, thumbHeightPx),
                cornerRadius = thumbCornerRadius
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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
