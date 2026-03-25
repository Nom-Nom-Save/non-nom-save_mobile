package ua.nure.nomnomsave.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val foregroundColorLight = Color(0xFF1B1B1B)
val foregroundColorDark = Color(0xFFE2E2E2)
val backgroundColorLight = Color(0xFFFFFBE6)
val backgroundColorDark = Color(0xFF121212)

@Immutable
data class AppColors(
    val background: Color = Color.Unspecified,
    val cardBackground: Color = Color.Unspecified,
    val foreground: Color = Color.Unspecified,
    val accent: Color = Color.Unspecified,
    val active: Color = Color.Unspecified,
    val grey: Color = Color.Unspecified,
    val red: Color = Color.Unspecified,
    val error: Color = Color.Unspecified,
)

internal val LightColors = AppColors(
    background = backgroundColorLight,
    cardBackground = Color(0xFFFFFBF9),
    foreground = foregroundColorLight,
    accent = Color(0xFF52B788),
    active = Color(0xFF2D6A4F),
    grey = Color(0xFF94A3B8),
    red = Color(0xFFEF4444),
    error = Color(0xFFEF4444),
)

internal val DarkColors = AppColors(
    background = backgroundColorDark,
    cardBackground = Color(0xFF1A1A1A),
    foreground = foregroundColorDark,
    accent = Color(0xFF52B788),
    active = Color(0xFF2D6A4F),
    grey = Color(0xFFA0A0A0),
    red = Color(0xFFEF4444),
    error = Color(0xFFEF4444),
)