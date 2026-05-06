package t.me.octopusapps.cinemapulse.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CinemaGoldDark,
    onPrimary = Color(0xFF251A00),
    primaryContainer = Color(0xFF3C2C00),
    onPrimaryContainer = Color(0xFFFFE3A1),
    secondary = CinemaRedDark,
    onSecondary = Color(0xFF4C101A),
    secondaryContainer = Color(0xFF651C27),
    onSecondaryContainer = Color(0xFFFFD9DD),
    tertiary = ProjectorTealDark,
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF104F47),
    onTertiaryContainer = Color(0xFFA9F5E9),
    background = DarkBackground,
    onBackground = Color(0xFFF4F1EA),
    surface = DarkSurface,
    onSurface = Color(0xFFF4F1EA),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC9C4B8),
    surfaceContainer = DarkSurfaceContainer,
    outline = Color(0xFF928B7E),
    outlineVariant = Color(0xFF4F4A42),
    error = CinemaRedDark,
    errorContainer = Color(0xFF681D28),
    onErrorContainer = Color(0xFFFFD9DD),
)

private val LightColorScheme = lightColorScheme(
    primary = CinemaGoldLight,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE2A6),
    onPrimaryContainer = Color(0xFF3E2D00),
    secondary = CinemaRedLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9DD),
    onSecondaryContainer = Color(0xFF3F0711),
    tertiary = ProjectorTealLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB9EEE4),
    onTertiaryContainer = Color(0xFF002F2A),
    background = LightBackground,
    onBackground = Color(0xFF1D1C19),
    surface = LightSurface,
    onSurface = Color(0xFF1D1C19),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF514E47),
    surfaceContainer = LightSurfaceContainer,
    outline = Color(0xFF807970),
    outlineVariant = Color(0xFFC9C5BC),
    error = CinemaRedLight,
    errorContainer = Color(0xFFFFD9DD),
    onErrorContainer = Color(0xFF3F0711),
)

@Composable
internal fun CinemaPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        content = content
    )
}
