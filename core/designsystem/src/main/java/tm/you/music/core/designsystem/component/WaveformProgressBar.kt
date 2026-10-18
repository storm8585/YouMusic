package tm.you.music.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
    ExpressiveMusicSlider(
        currentPositionMs = currentPositionMs,
        durationMs = durationMs,
        onSeek = onSeek,
        modifier = modifier,
        activeColor = activeColor,
        inactiveColor = inactiveColor
    )
}
