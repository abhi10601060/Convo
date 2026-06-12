package com.app.ui.theme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme: ColorScheme = lightColorScheme(
    // Primary
    primary                = BrandCore,           // #3D3DE8 – tab pills, CTAs
    onPrimary              = OnBrand,             // #FFFFFF
    primaryContainer       = BrandContainer,      // #C7C7F8 – chip/badge backgrounds
    onPrimaryContainer     = BrandDeep,           // #2525C0 – text inside containers

    // Secondary (same indigo family, lighter – used for outbound arc, secondary chips)
    secondary              = AccentMid,           // #9393F0
    onSecondary            = OnBrand,
    secondaryContainer     = BrandSurface,        // #EEEEFF
    onSecondaryContainer   = BrandDeep,

    // Tertiary (magenta accent – unanswered progress bar)
    tertiary               = ChartUnanswered,     // #D946EF
    onTertiary             = OnBrand,
    tertiaryContainer      = Color(0xFFFAE8FD),
    onTertiaryContainer    = Color(0xFF5B0069),

    // Background / surface
    background             = BgLight,             // #FFFFFF
    onBackground           = TextPrimaryLight,    // #1A1A2E
    surface                = SurfaceLight,        // #F4F5FF – slight blue tint
    onSurface              = TextPrimaryLight,
    surfaceVariant         = CardBgLight,         // #EAEBFF – header area cards
    onSurfaceVariant       = TextSecondaryLight,  // #6B6B8A

    // Error
    error                  = ErrorLight,          // #EF4444
    onError                = OnBrand,
    errorContainer         = Color(0xFFFFDAD6),
    onErrorContainer       = Color(0xFF410002),

    // Outline
    outline                = Color(0xFFB8B8D8),
    outlineVariant         = Color(0xFFDDDDF0),
    scrim                  = Color(0xFF000000),

    // Inverse (used by snackbars, tooltips)
    inverseSurface         = SurfaceDark,
    inverseOnSurface       = TextPrimaryDark,
    inversePrimary         = BrandDark,
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    // Primary – brighter so it reads on #0E0E1C
    primary                = BrandDark,           // #6C6CFF
    onPrimary              = Color(0xFF000000),
    primaryContainer       = BrandDarkContainer,  // #2A2A5E
    onPrimaryContainer     = Color(0xFFBEBEFF),

    // Secondary
    secondary              = AccentDark,          // #5050CC
    onSecondary            = Color(0xFFFFFFFF),
    secondaryContainer     = BrandDarkSurface,    // #1A1A40
    onSecondaryContainer   = Color(0xFFA8A8FF),

    // Tertiary
    tertiary               = Color(0xFFE879F9),
    onTertiary             = Color(0xFF000000),
    tertiaryContainer      = Color(0xFF4A0060),
    onTertiaryContainer    = Color(0xFFF5ABFF),

    // Background / surface
    background             = BgDark,              // #0E0E1C
    onBackground           = TextPrimaryDark,     // #EEEEFF
    surface                = SurfaceDark,         // #16162A
    onSurface              = TextPrimaryDark,
    surfaceVariant         = CardBgDark,          // #1E1E38
    onSurfaceVariant       = TextSecondaryDark,   // #A8A8C8

    // Error
    error                  = ErrorDark,           // #DC2626
    onError                = Color(0xFFFFFFFF),
    errorContainer         = Color(0xFF93000A),
    onErrorContainer       = Color(0xFFFFDAD6),

    // Outline
    outline                = Color(0xFF4A4A6A),
    outlineVariant         = Color(0xFF2A2A44),
    scrim                  = Color(0xFF000000),

    // Inverse
    inverseSurface         = SurfaceLight,
    inverseOnSurface       = TextPrimaryLight,
    inversePrimary         = BrandCore,
)

@Composable
fun ConvoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme ->DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
