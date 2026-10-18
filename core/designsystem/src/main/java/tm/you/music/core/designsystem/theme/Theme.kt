package tm.you.music.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = SageGreenPrimaryDark,
    onPrimary = SageGreenOnPrimaryDark,
    primaryContainer = ForestGreenPrimaryContainerDark,
    onPrimaryContainer = SageGreenOnPrimaryContainerDark,
    inversePrimary = DarkInversePrimary,
    secondary = SageGreenSecondaryDark,
    onSecondary = SageGreenOnSecondaryDark,
    secondaryContainer = SageGreenSecondaryContainerDark,
    onSecondaryContainer = SageGreenOnSecondaryContainerDark,
    tertiary = SageGreenTertiaryDark,
    onTertiary = SageGreenOnTertiaryDark,
    tertiaryContainer = SageGreenTertiaryContainerDark,
    onTertiaryContainer = SageGreenOnTertiaryContainerDark,
    background = DarkSurface,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimaryLight,
    onPrimary = ForestGreenOnPrimaryLight,
    primaryContainer = ForestGreenPrimaryContainerLight,
    onPrimaryContainer = ForestGreenOnPrimaryContainerLight,
    inversePrimary = LightInversePrimary,
    secondary = ForestGreenSecondaryLight,
    onSecondary = ForestGreenOnSecondaryLight,
    secondaryContainer = ForestGreenSecondaryContainerLight,
    onSecondaryContainer = ForestGreenOnSecondaryContainerLight,
    tertiary = ForestGreenTertiaryLight,
    onTertiary = ForestGreenOnTertiaryLight,
    tertiaryContainer = ForestGreenTertiaryContainerLight,
    onTertiaryContainer = ForestGreenOnTertiaryContainerLight,
    background = LightSurface,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    surfaceVariant = LightSurfaceContainerHigh,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

@Composable
fun YouMusicTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
