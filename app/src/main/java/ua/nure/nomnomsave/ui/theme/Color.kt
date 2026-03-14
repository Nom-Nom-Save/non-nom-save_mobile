package ua.nure.nomnomsave.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val foregroundColorLight = Color(0xFF2A2A2A)
val foregroundColorDark = Color(0xFFFFFFFF)
val backgroundColorLight = Color(0xFFFFFFFF)
val backgroundColorDark = Color(0xFF1A0D13)

@Immutable
data class AppColors(
    val background: Color = Color.Unspecified,
    val foreground: Color = Color.Unspecified,
    val accent: Color = Color.Unspecified,
    val active: Color = Color.Unspecified,
    val grey: Color = Color.Unspecified,
    val error: Color = Color.Unspecified,
)

internal val LightColors = AppColors(
    background = backgroundColorLight,
    foreground = foregroundColorLight,
    accent = Color(0xFFFFD0E1),
    active = Color(0XFFFF9FC2),
    grey = Color(0xFF8C8C8C),
    error = Color(0xFFB00020),
)

internal val DarkColors = AppColors(
    background = backgroundColorDark,
    foreground = foregroundColorDark,
    accent = Color(0xFF301524),
    active = Color(0XFFEC1380),
    grey = Color(0xFFCECECE),
    error = Color(0xFFCF6679),
)