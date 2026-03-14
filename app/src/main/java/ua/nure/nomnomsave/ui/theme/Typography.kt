package ua.nure.nomnomsave.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text


val regularTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight(600),

    )

val smallTextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight(600)
)
val largeTextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight(800)
)

@Immutable
data class AppTypography(
    val regular: TextStyle = TextStyle.Default,
    val small: TextStyle = TextStyle.Default,
    val large: TextStyle = TextStyle.Default,
)

internal val LightTypography: AppTypography
    get() = AppTypography(
        regular = regularTextStyle.copy(
            color = foregroundColorLight
        ),
        small = smallTextStyle.copy(
            color = foregroundColorLight
        ),
        large = largeTextStyle.copy(
            color = foregroundColorLight
        )
    )

internal val DarkTypography: AppTypography
    get() = AppTypography(
        regular = regularTextStyle.copy(
            color = foregroundColorDark
        ),
        small = smallTextStyle.copy(
            color = foregroundColorDark
        ),
        large = largeTextStyle.copy(
            color = foregroundColorDark
        )
    )