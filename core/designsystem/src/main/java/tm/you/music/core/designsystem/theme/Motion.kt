package tm.you.music.core.designsystem.theme

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

object YouMusicMotion {

    val SpatialExpressiveDefault: SpringSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = 600f
    )

    val SpatialExpressiveFast: SpringSpec<Float> = spring(
        dampingRatio = 0.7f,
        stiffness = 800f
    )

    val SpatialExpressiveSlow: SpringSpec<Float> = spring(
        dampingRatio = 0.8f,
        stiffness = 350f
    )

    val TouchFeedbackSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = 900f
    )

    val EffectExpressive: SpringSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 1000f
    )

    val DpSpatialExpressiveDefault: SpringSpec<Dp> = spring(
        dampingRatio = 0.75f,
        stiffness = 600f
    )

    val DpSpatialExpressiveFast: SpringSpec<Dp> = spring(
        dampingRatio = 0.7f,
        stiffness = 800f
    )

    val OffsetSpatialExpressiveDefault: SpringSpec<IntOffset> = spring(
        dampingRatio = 0.75f,
        stiffness = 600f
    )

    val OffsetSpatialExpressiveFast: SpringSpec<IntOffset> = spring(
        dampingRatio = 0.7f,
        stiffness = 800f
    )
}
