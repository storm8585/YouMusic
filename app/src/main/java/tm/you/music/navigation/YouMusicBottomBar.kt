package tm.you.music.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tm.you.music.core.designsystem.R
import tm.you.music.core.designsystem.theme.YouMusicMotion

enum class YouMusicTopLevelDestination(
    val route: String,
    val labelRes: Int
) {
    HOME("home_route", R.string.nav_home),
    SEARCH("search_route", R.string.nav_search),
    LIBRARY("library_route", R.string.nav_library)
}

@Composable
fun YouMusicBottomBar(
    currentRoute: String,
    onNavigateToDestination: (YouMusicTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            YouMusicTopLevelDestination.values().forEach { destination ->
                val isSelected = currentRoute == destination.route
                BottomNavItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = { onNavigateToDestination(destination) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: YouMusicTopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = YouMusicMotion.SpatialExpressiveFast,
        label = "NavItemScale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pillColor = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        }

        val iconTint = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 30.dp)
                .clip(CircleShape)
                .background(pillColor),
            contentAlignment = Alignment.Center
        ) {
            when (destination) {
                YouMusicTopLevelDestination.HOME -> HomeIcon(tint = iconTint)
                YouMusicTopLevelDestination.SEARCH -> SearchIcon(tint = iconTint)
                YouMusicTopLevelDestination.LIBRARY -> LibraryIcon(tint = iconTint)
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = stringResource(destination.labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HomeIcon(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.9f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.9f)
            lineTo(w * 0.22f, h * 0.9f)
            lineTo(w * 0.22f, h * 0.45f)
            lineTo(w * 0.1f, h * 0.45f)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Composable
private fun SearchIcon(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
        val strokeWidth = 2.2f.dp.toPx()
        val radius = size.width * 0.34f
        val center = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.42f)

        drawCircle(
            color = tint,
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )

        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(center.x + radius * 0.7f, center.y + radius * 0.7f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
private fun LibraryIcon(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 2.2f.dp.toPx()

        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.2f),
            end = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.85f),
            strokeWidth = stroke,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.2f),
            end = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.85f),
            strokeWidth = stroke,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        val slantPath = Path().apply {
            moveTo(w * 0.72f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.85f)
        }
        drawPath(
            path = slantPath,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}
