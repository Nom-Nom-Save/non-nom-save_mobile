package ua.nure.nomnomsave.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalAppColorScheme = staticCompositionLocalOf { AppColors() }
val LocalAppTypography = staticCompositionLocalOf { AppTypography() }
val LocalAppDimension = staticCompositionLocalOf { AppDimension() }
val LocalAppShape = staticCompositionLocalOf { AppShape() }


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val color = if(darkTheme) DarkColors else LightColors
    val typography = if(darkTheme) DarkTypography else LightTypography
    val dimension = AppDimension()
    val shape = AppShape()

    CompositionLocalProvider(
        LocalAppColorScheme provides color,
        LocalAppTypography provides typography,
        LocalAppDimension provides dimension,
        LocalAppShape provides shape,
        content = content
    )
}

object AppTheme {
    val color: AppColors
        @Composable get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current

    val dimension: AppDimension
        @Composable get() = LocalAppDimension.current

    val shape: AppShape
        @Composable get() = LocalAppShape.current
}