package tm.you.music.feature.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import tm.you.music.core.designsystem.component.ExpressivePlayPauseButton
import tm.you.music.core.designsystem.component.WaveformProgressBar
import tm.you.music.core.designsystem.theme.ExtraLargeIncreasedShape
import tm.you.music.core.designsystem.theme.TitleLargeEmphasized
import tm.you.music.core.designsystem.theme.YouMusicMotion
import tm.you.music.core.model.Song

@Composable
fun PlayerRoute(
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PlayerScreen(
        uiState = uiState,
        onDismiss = onDismiss,
        onTogglePlayPause = viewModel::togglePlayPause,
        onSeek = viewModel::seekTo,
        onSkipNext = viewModel::skipNext,
        onSkipPrevious = viewModel::skipPrevious,
        onToggleFavorite = viewModel::toggleFavorite,
        onToggleShuffle = viewModel::toggleShuffle,
        onToggleRepeat = viewModel::toggleRepeat,
        onToggleQueue = viewModel::toggleQueueExpansion,
        modifier = modifier
    )
}

@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong = uiState.playbackState.currentSong ?: return
    val isPlaying = uiState.playbackState.isPlaying

    val albumArtScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.93f,
        animationSpec = YouMusicMotion.SpatialExpressiveDefault,
        label = "AlbumArtScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PlayerTopBar(
            song = currentSong,
            onDismiss = onDismiss
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1f)
                .scale(albumArtScale)
                .clip(ExtraLargeIncreasedShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            if (!currentSong.artworkUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = currentSong.artworkUrl,
                    contentDescription = currentSong.title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        PlayerSongInfoRow(
            song = currentSong,
            isFavorite = uiState.isFavorite,
            onToggleFavorite = onToggleFavorite
        )

        Spacer(modifier = Modifier.height(20.dp))

        WaveformProgressBar(
            currentPositionMs = uiState.playbackState.currentPositionMs,
            durationMs = uiState.playbackState.durationMs,
            onSeek = onSeek,
            barCount = 38
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerControlsSection(
            isPlaying = isPlaying,
            isShuffle = uiState.isShuffleEnabled,
            isRepeat = uiState.isRepeatEnabled,
            onTogglePlayPause = onTogglePlayPause,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            onToggleShuffle = onToggleShuffle,
            onToggleRepeat = onToggleRepeat
        )

        Spacer(modifier = Modifier.height(20.dp))

        QueueDrawerPeek(
            queueSize = uiState.queue.size,
            onClick = onToggleQueue
        )
    }
}

@Composable
private fun PlayerTopBar(
    song: Song,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.2f, size.height * 0.35f)
                    lineTo(size.width * 0.5f, size.height * 0.65f)
                    lineTo(size.width * 0.8f, size.height * 0.35f)
                }
                drawPath(
                    path = path,
                    color = androidx.compose.ui.graphics.Color(0xFFDFE4DE),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.5f.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = androidx.compose.ui.res.stringResource(tm.you.music.core.designsystem.R.string.player_now_playing),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            if (!song.album.isNullOrEmpty()) {
                Text(
                    text = song.album,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⋮",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun PlayerSongInfoRow(
    song: Song,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = TitleLargeEmphasized,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleFavorite),
            contentAlignment = Alignment.Center
        ) {
            val heartColor = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Text(
                text = if (isFavorite) "♥" else "♡",
                color = heartColor,
                fontSize = 26.sp
            )
        }
    }
}

@Composable
private fun PlayerControlsSection(
    isPlaying: Boolean,
    isShuffle: Boolean,
    isRepeat: Boolean,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleShuffle),
            contentAlignment = Alignment.Center
        ) {
            val tint = if (isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Text(text = "⇄", color = tint, fontSize = 20.sp)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onSkipPrevious),
            contentAlignment = Alignment.Center
        ) {
            PreviousIcon(tint = MaterialTheme.colorScheme.onSurface)
        }

        ExpressivePlayPauseButton(
            isPlaying = isPlaying,
            onTogglePlayPause = onTogglePlayPause,
            buttonSize = 72.dp,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onSkipNext),
            contentAlignment = Alignment.Center
        ) {
            NextIcon(tint = MaterialTheme.colorScheme.onSurface)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleRepeat),
            contentAlignment = Alignment.Center
        ) {
            val tint = if (isRepeat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Text(text = "↻", color = tint, fontSize = 20.sp)
        }
    }
}

@Composable
private fun QueueDrawerPeek(
    queueSize: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = androidx.compose.ui.res.stringResource(
                    tm.you.music.core.designsystem.R.string.player_upcoming_tracks,
                    queueSize
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "▲",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun NextIcon(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height

        val trianglePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.72f, h * 0.5f)
            lineTo(0f, h)
            close()
        }
        drawPath(path = trianglePath, color = tint)

        drawRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.8f, 0f),
            size = androidx.compose.ui.geometry.Size(w * 0.2f, h)
        )
    }
}

@Composable
private fun PreviousIcon(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height

        val trianglePath = Path().apply {
            moveTo(w, 0f)
            lineTo(w * 0.28f, h * 0.5f)
            lineTo(w, h)
            close()
        }
        drawPath(path = trianglePath, color = tint)

        drawRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(w * 0.2f, h)
        )
    }
}
