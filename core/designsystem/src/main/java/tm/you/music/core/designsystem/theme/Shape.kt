package tm.you.music.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val LargeIncreasedShape = RoundedCornerShape(20.dp)
val ExtraLargeIncreasedShape = RoundedCornerShape(32.dp)
val ExtraExtraLargeShape = RoundedCornerShape(48.dp)
val FullPillShape = CircleShape
