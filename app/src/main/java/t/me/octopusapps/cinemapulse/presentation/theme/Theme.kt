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
    primary = Gold80,
    onPrimary = Color(0xFF1A1A00),
    primaryContainer = Color(0xFF2C2400),
    onPrimaryContainer = Gold80,
    secondary = GoldVariant80,
    onSecondary = Color(0xFF1A1200),
    tertiary = DeepRed80,
    background = DarkBackground,
    onBackground = Color(0xFFF5F5F5),
    surface = DarkSurface,
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    surfaceContainer = DarkSurfaceContainer,
    error = DeepRed80,
)

private val LightColorScheme = lightColorScheme(
    primary = Gold40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3CD),
    onPrimaryContainer = Color(0xFF3D2C00),
    secondary = GoldVariant40,
    onSecondary = Color.White,
    tertiary = DeepRed40,
    background = Color(0xFFFFFBF0),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFBF0),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5EFD6),
    onSurfaceVariant = Color(0xFF4A4540),
    surfaceContainer = Color(0xFFF0EAD2),
)

@Composable
internal fun CinemaPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // off — we want our cinema palette, not wallpaper colors
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